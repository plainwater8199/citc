package com.citc.nce.im.robot.node;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.authcenter.largeModel.LargeModelApi;
import com.citc.nce.authcenter.largeModel.vo.LargeModelPromptSettingResp;
import com.citc.nce.authcenter.largeModel.vo.LargeModelResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.enums.RouteConditionStrategy;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.entity.BaiduWenxinResultDto;
import com.citc.nce.im.session.entity.BaiduWenxinTokenDto;
import com.github.pagehelper.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@Slf4j
public class TextRecognitionNode extends Node {
    /**
     * 是否打开promote优化
     */
    private TextRecognitionSetting textRecognitionSetting;

    /**
     * 规则和示例 (执行时进行查询)
     */
    private LargeModelPromptSettingResp largeModelPromptSetting;

    /**
     * 成功的路由。
     */
    private String sucessId;
    /**
     * 失败的路由。
     */
    private String failId;

    /**
     * 执行结果
     */
    private Boolean execResult = false;

    /**
     * 是否跳过本节点(关闭了大模型设置)
     */
    private Boolean skip = false;

    private static final String CONSTANT_CONTENT = "请输出符合要求的json:\n" + "【要求】 提取 %s ;\n" + "【输入】 %s ;";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    /**
     * 填充userContent
     * 提问回复 1、 触发上行2 、 变量3
     *
     * @param msgDto 上行消息
     */
    @Override
    void beforeExec(MsgDto msgDto) {
        String textValue = textRecognitionSetting.getTextValue();
        //将字符串中系统变量、自定义变量、提问回复翻译成变量值
        String recognitionText = RobotUtils.translateVariable(msgDto.getConversationId(), msgDto.getPhone(), textValue);
        textRecognitionSetting.setTextValue(recognitionText);

        //在节点执行的时候查询 后管大模型配置
        LargeModelApi largeModelApi = ApplicationContextUil.getBean(LargeModelApi.class);
        try {
            largeModelPromptSetting = largeModelApi.getLargeModelPromptSettingByChatbotAccountId();
            log.info("大模型设置为:{}", largeModelPromptSetting);
            log.info("recognitionText为:{}", recognitionText);
            if (Objects.isNull(largeModelPromptSetting) || Objects.isNull(largeModelPromptSetting.getLargeModelResp()) || Objects.isNull(largeModelPromptSetting.getLargeModelResp().getId())) {
                //关闭了大模型设置,直接跳过该节点执行
                log.info("关闭了大模型设置或大模型设置有问题,直接跳过该节点执行");
                skip = true;
            } else {
                if (!textRecognitionSetting.getPromptOptimize()) {
                    textRecognitionSetting.setPromptSetting(largeModelPromptSetting.getSettings());
                    textRecognitionSetting.setPromptExample(largeModelPromptSetting.getExamples());
                }
                textRecognitionSetting.setRuleAndFormats(largeModelPromptSetting.getRuleAndFormats());
            }
        } catch (Exception e) {
            log.error("文字识别节点出现错误:", e);
            skip = true;
        }
    }

    /**
     * 执行指令
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        //跳过后,执行结果默认是false
        if (!skip) {
            String result = "";
            LargeModelResp largeModelResp = largeModelPromptSetting.getLargeModelResp();
            StringRedisTemplate stringRedisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
            try {
                //拼出完整的发送内容
                String format = String.format(CONSTANT_CONTENT, textRecognitionSetting.getRecognitions().stream().map(TransferParam::getContent).collect(Collectors.joining(",")), textRecognitionSetting.getTextValue());
                String fullContext = textRecognitionSetting.getPromptSetting() + "\n" + textRecognitionSetting.getRuleAndFormats() + "\n" + "示例:\n" + textRecognitionSetting.getPromptExample() + "\n" + format;
                log.info("需要发送给大模型的文字内容:\n{}", fullContext);
                // 先尝试redis获取baidu的token
                String baiduWenxinImTokenKey = RedisUtil.getBaiduwenxinImTokenKey(largeModelPromptSetting.getLargeModelResp().getId());
                String baiduWenxinImToken = stringRedisTemplate.opsForValue().get(baiduWenxinImTokenKey);
                //调用大模型接口,获取文字内容
                String token = getBaiduWenxinImToken(largeModelResp, baiduWenxinImToken, stringRedisTemplate);
                //调用大模型
                result = connectionBaidu(largeModelResp.getApiUrl(), fullContext, token);
            } catch (Exception e) {
                log.error("大模型调用流程出错", e);
                execResult = false;
            }
            try {
                //系统变量
                log.info("准备开始查询variables");
                Map<String, String> variables = RobotUtils.getVariables(msgDto.getConversationId());
                log.info("variables:{}", variables);
                log.info("是否会进行文字识别后的参数设置:{}", StrUtil.isNotBlank(result));

                //请求成功
                if (StrUtil.isNotBlank(result)) {
                    List<String> variableExpressions = getJsonVariableExpressions(result);
                    log.info("大模型回复中需要提取的内容为:{}", variableExpressions);
                    for (String variableExpression : variableExpressions) {
                        JSONObject jsonObject = JSON.parseObject(variableExpression);
                        for (TransferParam transferParam : textRecognitionSetting.getRecognitions()) {
                            Object valueObject = jsonObject.get(transferParam.getContent());
                            String value = "";
                            if (valueObject instanceof String) {
                                value = (String) (jsonObject.get(transferParam.getContent()));
                            }
                            if (valueObject instanceof JSONArray) {
                                value = ((JSONArray) (jsonObject.get(transferParam.getContent()))).stream().map(Object::toString).filter(Objects::nonNull).collect(Collectors.joining(","));
                            }
                            if (valueObject instanceof JSONObject) {
                                value = ((JSONObject) (jsonObject.get(transferParam.getContent()))).entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(","));
                            }
                            //json中的值不为空  并且  系统变量中存在当前变量名
                            if (StrUtil.isNotBlank(value) && Objects.nonNull(variables.get("{{custom-" + transferParam.getVariableName() + "}}"))) {
                                variables.put("{{custom-" + transferParam.getVariableName() + "}}", value);
                                log.info("重新在系统中设置变量的值:{{custom-" + transferParam.getVariableName() + "}},value=" + value);
                                execResult = true;
                            } else {
                                log.info("变量:{{custom-" + transferParam.getVariableName() + "}}不存在,或者是对应值为空");
                            }
                        }
                    }
                    //重新在系统中设置变量的值
                    String variableKey = RedisUtil.getVariableKey(msgDto.getConversationId());
                    RobotDto robot = RobotUtils.getLocalRobot(msgDto.getConversationId());
                    stringRedisTemplate.<String, String>opsForHash().putAll(variableKey, variables);
                    stringRedisTemplate.expire(variableKey, Duration.ofMinutes(robot.getExpireTime()));
                    log.info("重新在系统中设置变量的值");
                }
            } catch (Exception e) {
                log.error("文字识别节点内容处理出错", e);
                execResult = false;
            }
        }
    }

    public static void main(String[] args) {
        List<String> variableExpressions = getJsonVariableExpressions("根据您给出的【要求】和【输入】，需要提取的信息是数字个数和天气。但是在您给出的【输入】中，并没有明确的“天气”信息，所以我将只提取并返回数字个数。如果您能提供包含天气信息的输入，我可以相应地修改JSON以包含该信息。\n\n以下是一个符合您要求的JSON输出，其中包含了数字个数的信息：\n\n\n```json\n{\n  \"数字个数\": 8\n}\n```\n这个JSON表示在输入的字符串中找到了8个数字。\n\n如果您想要同时包含天气信息，您可能需要提供一个包含天气描述的输入字符串。例如：\n\n【输入】\"39218撒大声地390281893291832啊啊今天天气真晴朗大萨达是\"\n\n对于这个包含天气信息的输入，我们可以生成以下JSON：\n\n\n```json\n{\n  \"数字个数\": 8,\n  \"天气\": \"晴朗\"\n}\n```\n请注意，为了从输入中提取天气信息，您可能需要使用更复杂的文本处理方法，如正则表达式或自然语言处理技术，具体取决于输入的复杂性和格式。在上面的示例中，我简单地搜索了“天气”一词后面的形容词作为天气描述。在实际应用中，您可能需要根据具体情况调整这种方法。");
        for (String variableExpression : variableExpressions) {
            JSONObject jsonObject = JSON.parseObject(variableExpression);

            Object valueObject = jsonObject.get("各指数涨跌情况");
            String value = "";
            if (valueObject instanceof String) {
                value = (String) (jsonObject.get("各指数涨跌情况"));
            }
            if (valueObject instanceof JSONArray) {
                value = ((JSONArray) (jsonObject.get("各指数涨跌情况"))).stream().map(Object::toString).filter(Objects::nonNull).collect(Collectors.joining(","));
            }
            if (valueObject instanceof JSONObject) {
                value = ((JSONObject) (jsonObject.get("各指数涨跌情况"))).entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(","));
            }
            System.out.println(value);

        }
    }

    private static List<String> getJsonVariableExpressions(String textValue) {
        List<String> variableExpressions = new ArrayList<>();
        int index = 0;
        int quantityDifference = 0;
        int jsonStartIndex = 0;

        while (index < textValue.length()) {
            char c = textValue.charAt(index);
            if (c == '{' && quantityDifference == 0) {
                jsonStartIndex = index;
            }
            if (c == '{') {
                quantityDifference++;
            }
            if (c == '}') {
                quantityDifference--;
                if (quantityDifference == 0) {
                    String variableExpression = textValue.substring(jsonStartIndex, index + 1);
                    variableExpressions.add(variableExpression);

                }
            }
            index++;
        }
        return variableExpressions;
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getBaiduWenxinImToken(LargeModelResp rebotSettingModel, String baiduWenxinToken, StringRedisTemplate stringRedisTemplate) {
        if (StringUtil.isEmpty(baiduWenxinToken)) {
            StringBuffer tokenUrl = new StringBuffer();
            tokenUrl.append("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=")
                    .append(rebotSettingModel.getApiKey())
                    .append("&client_secret=")
                    .append(rebotSettingModel.getSecretKey());
            // 获取token时实际不需要传jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grant_type", "client_credentials");
            jsonObject.put("client_id", rebotSettingModel.getApiKey());
            jsonObject.put("client_secret", rebotSettingModel.getSecretKey());
            log.info("###### baiduWenxin ###### 获取token的url : {}", tokenUrl);
            log.info("###### baiduWenxin ###### 获取token的条件 : {}", rebotSettingModel);

            HttpResponse execute = HttpUtil.createPost(tokenUrl.toString())
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toJSONString())
                    .execute();
            int status = execute.getStatus();
            if (status != 200) {
                //请求出错
                log.error("百度大模型调用异常:{}", execute.body());
                return "";
            }
            String resultString = execute.body();
            if (StringUtil.isEmpty(resultString)) {
                return null;
            }
            log.info("###### baiduWenxin ###### 获取token的结果 : {}", resultString);
            BaiduWenxinTokenDto tokenDto = null;
            if (StringUtil.isNotEmpty(resultString)) {
                tokenDto = JsonUtils.string2Obj(resultString, BaiduWenxinTokenDto.class);
            }
            if (tokenDto != null && StringUtils.isNotEmpty(tokenDto.getError())) {
                return null;
            }
            if (Objects.nonNull(tokenDto)) {
                stringRedisTemplate.opsForValue().set(RedisUtil.getBaiduwenxinImTokenKey(largeModelPromptSetting.getLargeModelResp().getId()), tokenDto.getAccessToken(), Long.valueOf(tokenDto.getExpiresIn()), TimeUnit.SECONDS);
                baiduWenxinToken = tokenDto.getAccessToken();
            } else {
                throw new BizException(81001235, "百度token获取失败");
            }
        }
        return baiduWenxinToken;
    }


    private String connectionBaidu(String apiUrl, String fullContext, String baiduWenxinToken) {
        JSONObject messagesJsonObject = new JSONObject();
        messagesJsonObject.put("content", fullContext);
        messagesJsonObject.put("role", "user");

        JSONArray messages = new JSONArray();
        messages.add(messagesJsonObject);

        JSONObject body = new JSONObject();
        body.put("messages", messages);
        body.put("stream", false);
        StringBuffer url = new StringBuffer();
        url.append(apiUrl)
                .append("?access_token=")
                .append(baiduWenxinToken);
        log.info("###### baiduWenxin ###### 获取百度回复的url : {}", url);
        HttpResponse execute = HttpUtil.createPost(url.toString())
                .header("Content-Type", "application/json")
                .body(body.toJSONString())
                .execute();
        int status = execute.getStatus();
        if (status != 200) {
            //请求出错
            log.error("百度大模型调用异常:{}", execute.body());
            return "";
        }
        String resultString = execute.body();
//        String resultString = getBaiduWenxinResult(url.toString(), jsonObject.toJSONString());
        log.info("###### baiduWenxin ###### 获取百度回复的结果 : {}", resultString);
        if (StringUtil.isNotEmpty(resultString)) {
            BaiduWenxinResultDto resultDto = new BaiduWenxinResultDto();
            // 如果报错了
            if (resultString.contains("error_code")) {
                /**
                 * 需求，大模型未返回，或者返回异常，则不再下发消息
                 */
                throw new BizException("大模型返回异常，停止执行");
            }
            resultDto = JsonUtils.string2Obj(resultString, BaiduWenxinResultDto.class);
            Assert.notNull(resultDto, "大模型返回值不能为空");
            log.info("###### baiduWenxin ###### 获取百度回复resultDto.result : {}", resultDto.getResult());
            return resultDto.getResult();
        } else {
            log.error("大模型回复为空");
            throw new BizException("大模型返回异常，大模型回复为空");
        }
    }

    @Override
    void afterExec(MsgDto msgDto) {
        RobotDto robot = RobotUtils.getLocalRobot(msgDto.getConversationId());
        Process process = RobotUtils.getCurrentProcess(robot);
        process.getNodeMap().put(this.getNodeId(), this);
        List<RouterInfo> outRouters = this.getOutRouters();
        log.info("文字识别节点执行结果:{}", execResult);
        log.info("outRouters:{}", outRouters);
        if (!CollectionUtils.isEmpty(outRouters)) {
            if (outRouters.size() > 2) {
                log.warn("文字识别节点至多只能有两个出站路由，节点ID:{}", this.getNodeId());
            }
            String nextNodeId = null;
            for (RouterInfo outRouter : outRouters) {
                RouteConditionStrategy strategy = outRouter.getConditionStrategy();
                if (strategy == RouteConditionStrategy.EXECUTE_SUCCESS && this.execResult) {
                    log.info("文字识别节点执行成功，执行出站路由，节点ID:{}", this.getNodeId());
                    nextNodeId = outRouter.getTailNodeId();
                    break;
                }
                if (strategy == RouteConditionStrategy.EXECUTE_FAIL && !this.execResult) {
                    log.info("文字识别节点执行失败，执行出站路由，节点ID:{}", this.getNodeId());
                    nextNodeId = outRouter.getTailNodeId();
                    break;
                }
            }
            robot.setCurrentNodeId(nextNodeId);
        } else {
            robot.setCurrentNodeId(null);
            log.info("出站路由为空,将不继续执行");
        }
        this.setNodeStatus(this.execResult ? NodeStatus.SUCCESS : NodeStatus.ERROR);
        RobotUtils.saveRobot(robot);
    }


//    {
//        "id": 148,
//            "variableName": "变量1",
//            "variableValue": "123",
//            "creator": "896995996239571",
//            "createTime": "2024-05-17T02:12:06.000+00:00",
//            "updater": "896995996239571",
//            "updateTime": "2024-05-17T02:12:37.000+00:00",
//            "deleted": 0
//    }

/*    {
        "executeType": 1,
            "conditionList": [
            {
                "conditionCode": "{{question-ab39a72ad-ac56-4300-99e3-2b29e667de79&提问1}}",
                    "conditionType": 2,
                    "conditionCodeValue": "{{question-(ab39a72ad-ac56-4300-99e3-2b29e667de79)}}",
                    "conditionContent": "111",
                    "conditionContentValue": "111"
            }
			],
            "nodeType": 7,
            "i": 1,
            "nodeName": "分支1：分支",
            "id": "a99734b8c-38a5-4e46-8cbe-47a05e32f8fc",
            "top": "274px",
            "left": "1421px",
            "width": "218px"
    }*/
}
