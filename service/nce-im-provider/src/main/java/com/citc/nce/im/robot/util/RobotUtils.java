package com.citc.nce.im.robot.util;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.node.Node;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.robot.vo.directcustomer.Parameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.citc.nce.common.core.exception.GlobalErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
public class RobotUtils {

    private final static ConcurrentHashMap<String, RobotDto> robots = new ConcurrentHashMap<>(64);

    /**
     * 根据会话ID从redis中获取机器人信息
     *
     * @param conversationId 会话ID
     * @return 机器人信息
     */
    public static RobotDto getRobot(String conversationId) {
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        ObjectMapper mapper = ApplicationContextUil.getBean(ObjectMapper.class);
        String robotStr = redisTemplate.opsForValue().get(RedisUtil.getRobotKeyByConversation(conversationId));
        if (robotStr == null) {
            throw new BizException(INTERNAL_SERVER_ERROR.getCode(), "robot信息不能为空");
        }
        try {
            return mapper.readValue(robotStr, RobotDto.class);
        } catch (JsonProcessingException e) {
            throw new BizException(500, "反序列化RobotDto失败");
        }
    }


    /**
     * 从本地缓存中获取机器人，如果不存在则先从redis中拿到添加到本地缓存，再返回
     *
     * @param conversationId 会话ID
     * @return 会话ID对应的机器人
     */
    public static RobotDto getLocalRobot(String conversationId) {
        if (robots.containsKey(conversationId))
            return robots.get(conversationId);
        RobotDto robot = getRobot(conversationId);
        robots.put(conversationId, robot);
        return robot;
    }

    /**
     * 向本地缓存中添加一个机器人
     *
     * @param robot 要添加到本地缓存的机器人
     */
    public static void putLocalRobot(RobotDto robot) {
        robots.put(robot.getConversationId(), robot);
    }

    /**
     * 从本地缓存中删除机器人【会话结束时才应该调用】
     *
     * @param conversationId 会话ID
     */
    public static void removeLocalRobot(String conversationId) {
        robots.remove(conversationId);
    }

    /**
     * 将robot信息缓存刷新到redis和本地缓存中
     *
     * @param robot robot
     */
    public static void saveRobot(RobotDto robot) {
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        ObjectMapper mapper = ApplicationContextUil.getBean(ObjectMapper.class);
        String robotKey = RedisUtil.getRobotKeyByConversation(robot.getConversationId());
        try {
            redisTemplate.opsForValue().set(robotKey, mapper.writeValueAsString(robot));
        } catch (JsonProcessingException e) {
            throw new BizException(500, "序列化RobotDto失败");
        }
        Set<String> keys = redisTemplate.keys(robotKey + "*");
        if (!CollectionUtils.isEmpty(keys))
            for (String key : keys)
                redisTemplate.expire(key, Duration.ofMinutes(robot.getExpireTime()));
        putLocalRobot(robot);
    }

    /**
     * 清理会话
     *
     * @param conversationId 会话id
     */
    public static void cleanRobot(String conversationId) {
        robots.remove(conversationId);
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        String robotKey = RedisUtil.getRobotKeyByConversation(conversationId);
        Set<String> keys = redisTemplate.keys(robotKey + "*");
        if (!CollectionUtils.isEmpty(keys))
            for (String key : keys)
                redisTemplate.delete(key);
    }

    /**
     * 获取当前流程
     *
     * @param robot 机器人信息
     * @return 当前流程
     */
    public static Process getCurrentProcess(RobotDto robot) {
        String currentProcessId = robot.getCurrentProcessId();
        Map<String, Process> processMap = robot.getProcessMap();
        if (currentProcessId == null || CollectionUtils.isEmpty(processMap) || !processMap.containsKey(currentProcessId))
            throw new BizException(500, "没有当前流程");
        return processMap.get(currentProcessId);
    }


    /**
     * 根据会话ID获取全部变量
     *
     * @param conversationId 会话ID
     * @return 所有变量
     */
    public static Map<String, String> getVariables(String conversationId) {
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        return redisTemplate.<String, String>opsForHash().entries(RedisUtil.getVariableKey(conversationId));
    }

    /**
     * 将字符串中系统变量、自定义变量、提问回复翻译成变量值
     *
     * @param conversationId 会话ID
     * @param str            要翻译的字符串
     * @return 翻译后的字符串
     */
    public static String translateVariable(String conversationId, String phone, String str) {
        if (StringUtils.isEmpty(str))
            return str;
        str = translateSystemVariable(str, phone, conversationId);
        str = translateCustomVariable(str, conversationId);
        str = translateQuestionReply(str, conversationId);
        return str;
    }


    /**
     * 将字符串中系统变量、自定义变量、提问回复装载到List<Parameter>
     *
     * @param conversationId 会话ID
     * @param str            要翻译的字符串
     * @return 翻译后的字符串
     */
    public static String setVariableParam(String conversationId, String phone, String str, List<Parameter> params) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        str = getSystemVariableParam(str, phone, conversationId, params);
        str = getCustomVariableParam(str, conversationId, params);
        str = getQuestionReplyParam(str, conversationId, params);
        return str;
    }

    /**
     * 翻译系统变量
     * {{sys-time}}    系统时间
     * {{sys-phone}}   手机号
     * {{sys-process}} 触发流程上行
     *
     * @param str   需要翻译的字符串
     * @param phone 手机号
     * @return 翻译后的字符串
     */
    public static String translateSystemVariable(String str, String phone, String conversationId) {
        if (StringUtils.isEmpty(str))
            return str;
        str = str.replace("{{sys-time}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        str = str.replace("{{sys-phone}}", StringUtils.isNotEmpty(phone) ? phone : "");
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        String value = redisTemplate.opsForValue().get(RedisUtil.getTriggerProcessMsgKey(conversationId));
        if (value != null) {
            str = str.replace("{{sys-process}}", value);
        }
        return str;
    }

    /**
     * 翻译系统变量
     * {{sys-time}}    系统时间
     * {{sys-phone}}   手机号
     * {{sys-process}} 触发流程上行
     *
     * @param str   需要翻译的字符串
     * @param phone 手机号
     * @return 翻译后的字符串
     */
    public static String getSystemVariableParam(String str, String phone, String conversationId, List<Parameter> parameters) {
        if (StringUtils.isEmpty(str))
            return str;
        //系统时间
        if (str.contains("{{sys-time}}")) {
            //str = str.replace("{{sys-time}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            Parameter timeParam = new Parameter("STRING", "sys-time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            parameters.add(timeParam);
            str = str.replace("{{sys-time}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        }

        //系统时间
        if (str.contains("{{sys-phone}}")) {
            //str = str.replace("{{sys-phone}}", StringUtils.isNotEmpty(phone) ? phone : "");
            Parameter phoneParam = new Parameter("STRING", "sys-phone", StringUtils.isNotEmpty(phone) ? phone : "");
            parameters.add(phoneParam);
            str = str.replace("{{sys-phone}}", StringUtils.isNotEmpty(phone) ? phone : "");
        }
        if (str.contains("{{sys-process}}")) {
            StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
            String value = redisTemplate.opsForValue().get(RedisUtil.getTriggerProcessMsgKey(conversationId));
            if (value != null) {
                // str = str.replace("{{sys-process}}", value);
                str = str.replace("{{sys-process}}", value);
            }
            Parameter processParam = new Parameter("STRING", "sys-process", value);
            parameters.add(processParam);
        }
        return str;
    }


    /**
     * 翻译系统变量
     *
     * @param str            需要翻译的字符串
     * @param conversationId 会话ID
     * @return 翻译后的字符串
     */
    public static String translateCustomVariable(String str, String conversationId) {
        if (StringUtils.isEmpty(str))
            return str;
        Map<String, String> variables = getVariables(conversationId);
        List<String> variableExpressions = findVariableExpression("\\{\\{custom-[^\\}]+\\}\\}", str);
        for (String variableExpression : variableExpressions) {
            str = str.replace(variableExpression, variables.getOrDefault(variableExpression, ""));
        }
        return str;
    }

    /**
     * 翻译系统变量
     *
     * @param str            需要翻译的字符串
     * @param conversationId 会话ID
     * @return 翻译后的字符串
     */
    public static String getCustomVariableParam(String str, String conversationId, List<Parameter> parameters) {
        if (StringUtils.isEmpty(str))
            return str;
        Map<String, String> variables = getVariables(conversationId);
        //记录参数变量
        log.info("conversationId:{} , variables:{}", conversationId, variables);
        List<String> variableExpressions = findVariableExpression("\\{\\{custom-[^\\}]+\\}\\}", str);
        for (String variableExpression : variableExpressions) {
            log.info("variableExpression:{} ", variableExpression);
            //{{custome-64&美景}}这个查找不到 {{custome-美景}}这个查找得到 ,需要 替换为新key去查找数据
            int and = variableExpression.indexOf("&");
            int strigula = variableExpression.indexOf("-");
            if (and == -1 || strigula == -1) {
                log.info("没有找到&或-");
                continue;
            }
            String substring = variableExpression.substring(strigula+1, and+1);
            String newVariableExpression = variableExpression.replace(substring, "");
            String orDefault = variables.getOrDefault(newVariableExpression, "");
            log.info("orDefault值:{} ", orDefault);
            if (variableExpression.contains("&") && variableExpression.contains("-")) {
                String[] split = variableExpression.split("&");
                String key = split[0].replace("{{custom-", "");
                Parameter customParam = new Parameter("STRING", key, orDefault);
                parameters.add(customParam);
                str = str.replace(variableExpression, orDefault);
                log.info("orDefault值替换key{}:新值为:{} ", key, orDefault);
            }
        }
        log.info("parameters:{} ", parameters);
        return str;
    }

    /**
     * 翻译问题回复
     *
     * @param str
     * @return
     */
    public static String translateQuestionReply(String str, String conversationId) {
        if (StringUtils.isEmpty(str))
            return str;
        List<String> variableExpressions = findVariableExpression("\\{\\{question-\\([^\\}]+\\)\\}\\}", str); //  {{question-([^}]
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        for (String expression : variableExpressions) {
            if (expression.contains("question-(")) {
                int startIdx = expression.indexOf("(");
                int endIdx = expression.indexOf(")");
                if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                    String nodeId = expression.substring(startIdx + 1, endIdx);
                    String questionNodeReplyKey = RedisUtil.getQuestionNodeReplyKey(conversationId, nodeId);
                    String replyContent = Optional.ofNullable(redisTemplate.opsForValue().get(questionNodeReplyKey)).orElse("");
                    str = str.replace(expression, transGeoToAddress(replyContent));
                }
            }
        }
        return str;
    }

    /**
     * 翻译问题回复
     *
     * @param str
     * @return
     */
    public static String getQuestionReplyParam(String str, String conversationId, List<Parameter> parameters) {
        if (StringUtils.isEmpty(str))
            return str;
        List<String> variableExpressions = findVariableExpression("\\{\\{question-\\([^\\}]+\\)\\}\\}", str); //  {{question-([^}]
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        for (String expression : variableExpressions) {
            if (expression.contains("question-(")) {
                int startIdx = expression.indexOf("(");
                int endIdx = expression.indexOf(")");
                if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                    String nodeId = expression.substring(startIdx + 1, endIdx);
                    String questionNodeReplyKey = RedisUtil.getQuestionNodeReplyKey(conversationId, nodeId);
                    String replyContent = Optional.ofNullable(redisTemplate.opsForValue().get(questionNodeReplyKey)).orElse("");
                    // str = str.replace(expression, transGeoToAddress(replyContent));
                    Parameter questionParam = new Parameter("STRING", nodeId, transGeoToAddress(replyContent));
                    parameters.add(questionParam);
                    str = str.replace(expression, transGeoToAddress(replyContent));
                }
            }
        }
        return str;
    }


    /**
     * 将地理坐标翻译成实际位置
     *
     * @param str 需要翻译的字符串
     * @return 翻译后的字符串
     */
    private static String transGeoToAddress(String str) {
        if (str.startsWith("geo:") && str.contains("u=") && str.contains("crs=") && str.contains("rcs-l=")) {
            String[] split = str.split(";");
            StringBuilder addr = new StringBuilder("经度为");
            for (String s : split) {
                if (s.contains("geo:")) {
                    String geo = s.replace("geo:", "");
                    String[] split1 = geo.split(",");
                    addr.append(split1[1]).append(",维度为").append(split1[0]);
                }
                if (s.contains("rcs-l=")) {
                    String rcs = s.replace("rcs-l=", "");
                    try {
                        String decode = URLDecoder.decode(rcs, "utf-8");
                        addr.append(",详细地址为:").append(decode);
                    } catch (UnsupportedEncodingException e) {
                        log.error("地址转换失败", e);
                    }
                }
            }
            str = addr.toString();
        }
        return str;
    }

    public static List<String> findVariableExpression(String pattern, String str) {
        List<String> result = new ArrayList<>();
//        String pattern = "\\{\\{[^\\}]+\\}\\}";
        // 编译正则表达式
        Pattern regex = Pattern.compile(pattern);

        // 创建Matcher对象
        Matcher matcher = regex.matcher(str);

        // 循环查找匹配的子串
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
}