package com.citc.nce.im.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedListReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.constants.TemplateMessageTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.ConversationContext;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.im.common.HttpURLConnectionUtil;
import com.citc.nce.im.common.SendMsgClient;
import com.citc.nce.im.gateway.SendMessage;
import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.robot.common.RobotConstant;
import com.citc.nce.im.robot.common.RobotException;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.*;
import com.citc.nce.im.robot.node.*;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.service.BaiduWenxinService;
import com.citc.nce.im.robot.service.ProcessService;
import com.citc.nce.im.robot.service.RobotService;
import com.citc.nce.im.robot.service.SaveRecordService;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.robot.util.TemporaryStatisticsUtil;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.im.session.processor.NodeType;
import com.citc.nce.im.session.processor.ProcessorConfig;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.im.session.processor.bizModel.RobotProcessTriggerModel;
import com.citc.nce.im.session.processor.ncenode.impl.Line;
import com.citc.nce.im.util.DateUtils;
import com.citc.nce.keywordsreply.KeywordsReplyApi;
import com.citc.nce.keywordsreply.KeywordsReplyStatisticApi;
import com.citc.nce.keywordsreply.req.KeywordsReplyStatisticsInfo;
import com.citc.nce.msgenum.MsgActionEnum;
import com.citc.nce.robot.*;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.mall.common.Robot;
import com.citc.nce.robot.api.mall.common.RobotProcess;
import com.citc.nce.robot.api.mall.common.RobotTrigger;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.robot.common.ResponsePriority;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.robot.vo.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.citc.nce.robot.enums.ButtonType.SUBSCRIBE_BUTTON_TYPES;

@Service
@Log4j2
public class RobotServiceImpl implements RobotService {

    @Resource
    private ProcessService processService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RobotProcessTriggerNodeApi robotProcessTriggerNodeApi;
    @Resource
    private RobotProcessTreeApi robotProcessTreeApi;
    @Resource
    private RobotSceneNodeApi robotSceneNodeApi;
    @Resource
    private SendMsgClient sendMsgClient;
    @Resource
    private RobotRecordApi robotRecordApi;
    @Resource
    private TemporaryStatisticsUtil temporaryStatisticsUtil;
    @Resource
    private BaiduWenxinService baiduWenxinService;
    @Resource
    private SaveRecordService saveRecordService;

    @Resource
    private MallSnapshotService mallSnapshotService;

    @Autowired
    private RobotProcessSettingNodeApi robotProcessSettingNodeApi;

    @Autowired
    private KeywordsReplyApi keywordsReplyApi;

    @Autowired
    private MessageTemplateApi messageTemplateApi;

    @Autowired
    private AccountManagementApi accountManagementApi;

    @Autowired
    private FifthSendClient fifthSendClient;

    @Resource
    private MessageTemplateApi templateApi;

    @Resource
    private KeywordsReplyStatisticApi keywordsReplyStatisticApi;
    @Resource
    private SendMessage sendMessage;
    public static final String DEFAULT_ACCOUNT = "999999999";
    private final static SecureRandom RANDOM = new SecureRandom();

    /**
     * 主要通过会话Id在redis中查询机器人流程是否存在
     * 1、如果存在则不做任何处理
     * 2、如果不存在则选需要“触发关键字”去数据库中查询。
     * 如果存在多个则需要根据优先级获取，如果优先级高的存在多个，
     * 则以按钮的形式最多返回4个
     * 3、获取到具体的机器人流程后，解析流程并缓存到redis中
     *
     * @param msgDto 消息内容
     * @return boolean 触发兜底时返回true
     */
    @Override
    public boolean processInitialization(MsgDto msgDto) {
        // 获取机器人基础信息
        RobotDto robotDto = RobotUtils.getRobot(msgDto.getConversationId());
        robotDto.setConversationId(msgDto.getConversationId());
        RobotUtils.saveRobot(robotDto);
        RobotRecordResp robotRecordResp = gateWay(msgDto, robotDto, null);
        ButtonType buttonType = ButtonType.getButtonType(msgDto.getBtnType());
        LinkedList<String> processQueue = robotDto.getProcessQueue();
        //当点击打卡订阅按钮时，除非在流程执行中，否则都不触发流程
        if (SUBSCRIBE_BUTTON_TYPES.contains(buttonType) && (processQueue == null || processQueue.isEmpty())) {
            return true;
        }
        if (StringUtils.isEmpty(robotDto.getCurrentProcessId())) {
            // 正常触发流程
            return triggerProcess(msgDto, robotDto, robotRecordResp);
        }
        return false;
    }

    private boolean triggerProcess(MsgDto msgDto, RobotDto robotDto, RobotRecordResp robotRecordResp) {
        List<RobotProcessTriggerModel> processList = new ArrayList<>();
        boolean triggerSuccess = false;
        HashMap<Long, RobotProcessTreeResp> processTreeMap = new HashMap<>();
        List<RobotProcessTriggerNodesResp> triggerList = new ArrayList<>();
        // 如果是从扩展商城预览进来
        String snapshotUuid = msgDto.getSnapshotUuid();
        if (StringUtils.isNotEmpty(snapshotUuid)) {
            loadMallProcessInfo(snapshotUuid, triggerList, processTreeMap);
        }
        // 没有流程
        if (null == msgDto.getProcessId()) {
            // (二次上行时)选择了多触发其中的一项时, 会进入此方法，验证回复内容继续触发流程,fontdo不会进入此流程,因为fontdo会在多触发时选择最新的一项
            RobotProcessTriggerModel triggerModel = getProcessIdCaseMultiply(msgDto, robotDto, robotRecordResp);
            if (ObjectUtils.isNotEmpty(triggerModel) && ObjectUtils.isNotEmpty(triggerModel.getProcessId())) {
                processList.add(triggerModel);
                triggerSuccess = true;
            }
        }
        if (!triggerSuccess) {
            // 触发流程时，可能是同时触发了多个流程
            String triggerProcessMsgKey = RedisUtil.getTriggerProcessMsgKey(msgDto.getConversationId());
            // 同一会话多次触发流程，后触发的会覆盖上一次的
            stringRedisTemplate.opsForValue().set(triggerProcessMsgKey, msgDto.getMessageData(), robotDto.getExpireTime(), TimeUnit.MINUTES);
            List<RobotProcessTriggerNodesResp> triggerNodesByCreate = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(triggerList)) {
                triggerNodesByCreate = triggerList;
            } else {
                // 获取触发器
                triggerNodesByCreate = robotProcessTriggerNodeApi.getRobotProcessTriggerNodesByCreate(msgDto.getCustomerId(), msgDto.getChatbotAccount());
            }
            if (CollectionUtils.isNotEmpty(triggerNodesByCreate)) {
                boolean shouldTriggerProcess = false;
                if ((StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource()))) {
                    //终端的按钮回调消息 用于聊天记录保存
                    //     * 0: 非按钮
                    //     * 1： 按钮
                    if (0 == msgDto.getIsDeliveryMessage()) {
                        if (StringUtils.equals(MsgActionEnum.REPLY.getCode(), msgDto.getAction())) {
                            //回复总是可以触发流程
                            shouldTriggerProcess = true;
                        }
                        if (StringUtils.equals(MsgActionEnum.TEXT.getCode(), msgDto.getAction())) {
                            //非位置的文本消息可以触发流程
                            if (MessageType.LOCATION.getCode() != msgDto.getMessageType()) {
                                shouldTriggerProcess = true;
                            }
                        }
                    }
                } else {
                    // 调试窗口正常进行匹配
                    shouldTriggerProcess = true;
                }
                if (shouldTriggerProcess) {
                    //!!!!匹配触发器  如果是供应商渠道, 那么最多只选嘴一个最新的触发器
                    processList = matchTrigger(msgDto, triggerNodesByCreate);
                }
            }
        }
        // 成功触发的流程数
        int triggerSuccessNumber = processList.size();
        if (triggerSuccessNumber == 0) {
            if (triggerDefaultReply(msgDto, robotDto)) {
                log.info("触发兜底成功");
            } else {
                log.warn("!!!!!!!!!!未触发流程和兜底!!!!!!!!!!!!!");
            }
            return true;
        } else if (triggerSuccessNumber == 1) {
            RobotProcessTriggerModel process = processList.get(0);
            setRobotInfo(msgDto, robotDto, process.getProcessId(), process.getSceneId(), processTreeMap);
            return false;
        } else {
            //triggerMultipleProcess(msgDto, robotDto, robotRecordResp);
            //多流程触发,需要用户进行选择触发(包括蜂动)
            triggerMultipleProcessSendMsg(msgDto, processList);
            return true;
        }
    }

    /**
     * 加载模板商城流程信息
     *
     * @param snapshotUuid   快照ID
     * @param triggerList    触发器list，加载到的触发器会添加到list中
     * @param processTreeMap 流程map，加载到的流程会put到map中
     */
    private void loadMallProcessInfo(String snapshotUuid, List<RobotProcessTriggerNodesResp> triggerList, HashMap<Long, RobotProcessTreeResp> processTreeMap) {
        MallCommonContent mallCommonContent = mallSnapshotService.queryContent(snapshotUuid);
        if (ObjectUtils.isEmpty(mallCommonContent)) {
            throw new BizException(MallError.SNAPSHOT_NOT_EXIST);
        }
        if (CollectionUtils.isNotEmpty(mallCommonContent.getRobot())) {
            List<Robot> robot = mallCommonContent.getRobot();
            for (int i = 0; i < robot.size(); i++) {
                Robot robotInfo = robot.get(i);
                // 触发器
                RobotTrigger trigger = robotInfo.getTrigger();
                if (trigger == null)
                    continue;
                RobotProcessTriggerNodesResp triggerNode = new RobotProcessTriggerNodesResp();
                trigger.setId(0L);
                trigger.setProcessId(null);
                BeanUtil.copyProperties(trigger, triggerNode);
                triggerNode.setProcessId(RobotConstant.DEFAULT_COMMON_ID + i);
                triggerNode.setId(RobotConstant.DEFAULT_COMMON_ID + i);
                triggerNode.setSceneId(RobotConstant.DEFAULT_COMMON_ID + i);
                triggerNode.setUpdateTime(DateUtils.obtainDate(robotInfo.getUpdateTime()));
                triggerList.add(triggerNode);

                // 具体流程
                RobotProcess process = robotInfo.getProcess();
                RobotProcessTreeResp processTree = new RobotProcessTreeResp();
                process.setProcessId(null);
                BeanUtil.copyProperties(process, processTree);
                processTree.setProcessId(RobotConstant.DEFAULT_COMMON_ID + i);
                processTreeMap.put(processTree.getProcessId(), processTree);
            }
        }
    }

    /*
     * @describe 触发默认回复
     * @Param
     * @param msgDto
     * @param robotDto
     * @return boolean
     **/
    private boolean triggerDefaultReply(MsgDto msgDto, RobotDto robotDto) {
        // 快捷回复按钮
        if (StringUtils.equals(MsgActionEnum.REPLY.getCode(), msgDto.getAction())
                // 终端主动上行
                || StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())
                // 文本消息
                || StringUtils.equals(MsgActionEnum.TEXT.getCode(), msgDto.getAction())) {
//                  //如果非按钮:   0非按钮  1按钮
            if ((null == msgDto.getIsDeliveryMessage() || msgDto.getIsDeliveryMessage() == 0)) {

                //根据回复优先级去判断是否发送成功，如果发送成功则不再发送默认回复
                boolean replyByResponsePriority = replyByResponsePriority(msgDto, ResponsePriority.CHATBOT);
                // 如果开启了默认回复
                if (!replyByResponsePriority && 1 == robotDto.getRebotSettingModel().getReplySwitch()) {
                    // 回复类型是大型回复
                    boolean enableBaiduAiReply = 1 == robotDto.getRebotSettingModel().getReplyType();
                    log.debug("触发{}兜底", enableBaiduAiReply ? "大模型" : "自定义回复");
                    if (enableBaiduAiReply) {
                        // 开始执行baidu千帆的逻辑
                        baiduWenxinService.exec(robotDto.getRebotSettingModel(), msgDto);
                    } else {
                        if (ButtonType.REPLY.getCode() == msgDto.getBtnType() || ButtonType.SEND_ADDRESS.getCode() == msgDto.getBtnType()) {
                            // 发送兜底消息
                            sendLastReply(msgDto, robotDto);
                        }
                    }
                    // 保存数据统计记录
                    if (Objects.equals("1", msgDto.getMessageSource())) {
                        temporaryStatisticsUtil.lastReply(msgDto);
                    }
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean replyByResponsePriority(MsgDto msgDto, ResponsePriority currentStatus) {
        //获取关键字回复
        RobotDto robotDto = RobotUtils.getRobot(msgDto.getConversationId());
        List<ResponsePriority> responsePriorities = robotDto.getRebotSettingModel().getResponsePriorities();
        if (CollectionUtils.isEmpty(responsePriorities)) {//表示默认的回复顺序，机器人-关键字-默认
            responsePriorities = ResponsePriority.resolve("default");
        }
        ResponsePriority nextPriority = ResponsePriority.getNext(responsePriorities, currentStatus);
        if (ResponsePriority.KEYWORDS.equals(nextPriority)) {
            //发送关键词回复（5G消息模板，不处理机器人的按钮和变量）
            String chatbotAccount = msgDto.getChatbotAccount();
            String msg = msgDto.getMessageData();
            //从redis中获取上行消息内容
            Long keywordsReplyTemplateId = keywordsReplyApi.queryMatchedTemplateId(msg, chatbotAccount);
            if (keywordsReplyTemplateId != null) {
                //发送网关消息
                AccountManagementResp chatbot = accountManagementApi.getAccountManagementByAccountId(chatbotAccount);
                MessageTemplateResp provedTemplate = messageTemplateApi.getProvedTemplate(new MessageTemplateProvedReq(keywordsReplyTemplateId, chatbot.getAccountType()));
                if (null != msgDto.getMessageSource() && "1".equals(msgDto.getMessageSource())) {//网关发送
                    //保存关键词回复的上行消息
                    saveRecordService.saveRecord(msgDto, robotDto);
                    //发送触发的5G消息模板
                    ConversationContext context = ConversationContext.builder()
                            .conversationId(msgDto.getConversationId())
                            .contributionId(msgDto.getContributionId())
                            .build();
                    MessageData messageData = fifthSendClient.deductAndSend(chatbot, provedTemplate, Collections.singletonList(msgDto.getPhone()), null, MessageResourceType.KEY_WORD, null, context);
                    JSONObject msgBody = new JSONObject()
                            .fluentPut("type", provedTemplate.getMessageType())
                            .fluentPut("templateId", provedTemplate.getId())
                            .fluentPut("messageDetail", JSON.parse(messageData.getTemplateReplaceModuleInformation()));
                    WsResp wsResp = convertMsgDtoToWsResp(msgDto, msgBody);
                    //保存关键词回复触发的下行消息
                    sendMsgClient.saveRecord(wsResp, JSON.toJSONString(msgBody));
                    //快捷回复统计
                    List<KeywordsReplyStatisticsInfo> data = new ArrayList<>();
                    KeywordsReplyStatisticsInfo keywordsReplyStatisticsInfo = new KeywordsReplyStatisticsInfo();
                    keywordsReplyStatisticsInfo.setKeywords(msg);
                    keywordsReplyStatisticsInfo.setCustomerId(chatbot.getCustomerId());
                    keywordsReplyStatisticsInfo.setMessageId(messageData.getData().getMessageId());
                    data.add(keywordsReplyStatisticsInfo);
                    keywordsReplyStatisticApi.statisticInsertBatch(data);
                } else {//调试窗口不纳入关键词回复统计信息
                    try {
                        String ip = stringRedisTemplate.opsForValue().get("CHATBOT_SESSION_ROUTER:" + msgDto.getConversationId());
                        String url;
                        // 扩展商城默认账号时发送到扩展商城ws
                        if (org.apache.commons.lang3.StringUtils.equals(DEFAULT_ACCOUNT, msgDto.getChatbotAccount())) {
                            url = "http://" + ip + "/mallWs/pushMsgToClient";
                        } else {
                            url = "http://" + ip + "/ws/pushMsgToClient";
                        }
                        WsResp resp = new WsResp();
                        resp.setConversationId(robotDto.getConversationId());
                        String bodyStr = "{\"msg\":{\"type\":1,\"messageDetail\":{\"input\":{\"names\":[],\"name\":\"关键词回复暂不支持调试窗口，请到终端上重试！\",\"length\":66,\"value\":\"关键词回复暂不支持调试窗口，请到终端上重试！\"}}},\"button\":[]}";
                        resp.setBody(JSON.parse(bodyStr));
                        resp.setMsgType(MessageType.TEXT.getCode());
                        resp.setButtonList(Collections.emptyList());
                        HttpURLConnectionUtil.doPost(url, null, JSON.toJSONString(resp), null);
                    } catch (Exception e) {
                        log.error("发送调试信息失败:{}", e.getMessage(), e);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private RobotRecordResp gateWay(MsgDto msgDto, RobotDto robotDto, RobotRecordResp robotRecordResp) {
        // 网关发的保存聊天记录，调试窗口不保存
        if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())) {
            robotRecordResp = saveRecordService.saveRecord(msgDto, robotDto);
        } else {
            // 调试窗口 且 没有机器人时，设定一个默认值
            setDefaultAccount(msgDto);
        }
        return robotRecordResp;
    }

    private static void setDefaultAccount(MsgDto msgDto) {
        if (StringUtils.isEmpty(msgDto.getChatbotAccount())) {
            msgDto.setChatbotAccount(RobotConstant.DEFAULT_ACCOUNT);
            msgDto.setAccountType(RobotConstant.DEFAULT_ACCOUNT_TYPE);
        }
    }

    private void setRobotInfo(MsgDto msgDto, RobotDto robotDto, Long processId, Long sceneId, HashMap<Long, RobotProcessTreeResp> processTreeMap) {
        log.info("触发流程ID：{}", processId);
        log.info("触发场景ID：{}", sceneId);
        // 如果account是空的时候，通过流程倒推，获取chatbotId设定回去
        // 目前网关过来必定有机器人,调试窗口没选则有默认设置的机器人
        // 模板商城进来的机器人都设定了默认账号
//        setAccountWhenEmpty(msgDto, processList);
        // 处理流程
        Map<String, Process> longProcessMap = dealWithProcess(String.valueOf(processId), RobotConstant.DEFAULT_PARENT_PROCESS_ID, msgDto.getConversationId(), robotDto.getExpireTime(), robotDto, processTreeMap);
        robotDto.setProcessMap(longProcessMap);
        robotDto.setCurrentProcessId(String.valueOf(processId));
        robotDto.setSceneId(String.valueOf(sceneId));
        robotDto.setRobotStatus(RobotStatusEnum.INIT.getCode());
        RobotUtils.saveRobot(robotDto);
        // 数据统计
        saveTemporary(msgDto, robotDto, processId, sceneId);
    }

    private void saveTemporary(MsgDto msgDto, RobotDto robotDto, Long processId, Long sceneId) {
        if (null != processId && null != sceneId) {
            // 流程触发
            temporaryStatisticsUtil.triggerProcess(msgDto, sceneId, processId);
            // 有效会话 todo 改数据统计表，新增 conversationId 字段，有效会话用 这个字段 去重 统计
            String checkEffectiveConversationKey = RedisUtil.getCheckEffectiveConversationKey(msgDto.getConversationId());
            Boolean ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(checkEffectiveConversationKey, msgDto.getConversationId(), robotDto.getExpireTime(), TimeUnit.MINUTES);
            if (ifAbsent) {
                temporaryStatisticsUtil.effectiveConversation(msgDto, sceneId, processId);
            }
            // 刷新过期时间，防止会话时间刷新，重复计入有效会话
            stringRedisTemplate.expire(checkEffectiveConversationKey, robotDto.getExpireTime(), TimeUnit.MINUTES);
        }
    }

    private void setAccountWhenEmpty(MsgDto msgDto, List<RobotProcessTriggerModel> processList) {
        if (StringUtils.isEmpty(msgDto.getChatbotAccount())) {
            RobotSceneNodeResp robotSceneNodeResp = robotSceneNodeApi.getRobotSceneNodeById(processList.get(0).getSceneId());
            // 场景只绑定了一个机器人的情况下，可以直接把绑定的机器人Id设置进去
            if (robotSceneNodeResp.getAccountNum() == 1) {
                List<Map<String, String>> accountInfo = robotSceneNodeResp.getAccountInfo();
                String accountId = accountInfo.get(0).get("accountId");
                msgDto.setChatbotAccount(accountId);
                msgDto.setAccountType(accountInfo.get(0).get("accountType"));
            }
        }
    }

    /**
     * 发送触发的多个流程给用户选择
     */
    private void triggerMultipleProcessSendMsg(MsgDto msgDto, List<RobotProcessTriggerModel> processList) {
        WsResp wsResp = new WsResp();
        wsResp.setContributionId(msgDto.getContributionId());
        wsResp.setConversationId(msgDto.getConversationId());
        wsResp.setBody(processList);
        wsResp.setMsgType(MessageType.TEXT.getCode());
        wsResp.setUserId(msgDto.getCustomerId());
        wsResp.setChatbotAccount(msgDto.getChatbotAccount());
        wsResp.setChannelType(msgDto.getAccountType());
        // 网关过来才有手机号
        wsResp.setPhone(msgDto.getPhone());
        wsResp.setFalg(Integer.parseInt(msgDto.getMessageSource()));
        wsResp.setSupplierTag(msgDto.getSupplierTag());
        sendMsgClient.sendMessageWhenMultipleProcessNew(wsResp);
    }

    /**
     * 根据上行消息的按钮ID匹配是否选择了某个流程
     *
     * @return 匹配成功的流程
     */
    private RobotProcessTriggerModel getProcessIdCaseMultiply(MsgDto msgDto, RobotDto robotDto, RobotRecordResp robotRecordDo) {
        RobotProcessTriggerModel resp = new RobotProcessTriggerModel();
        //postback = ButId
        String triggerMultipleProcessMsgKey = RedisUtil.triggerMultipleProcessMsgKey(msgDto.getConversationId(), msgDto.getButId());
        Object processIdRedis = sendMsgClient.getRedis(triggerMultipleProcessMsgKey, "processId");
        Object sceneName = sendMsgClient.getRedis(triggerMultipleProcessMsgKey, "sceneName");
        Object sceneId = sendMsgClient.getRedis(triggerMultipleProcessMsgKey, "sceneId");
        if (ObjectUtils.isNotEmpty(processIdRedis) && ObjectUtils.isNotEmpty(sceneName)) {
            long i = Long.parseLong(processIdRedis.toString());
            robotDto.setCurrentProcessId(processIdRedis.toString());
            robotDto.setSceneId(sceneId.toString());
            resp.setProcessId(i);
            resp.setSceneId(Long.parseLong(sceneId.toString()));
            stringRedisTemplate.delete(triggerMultipleProcessMsgKey);
            // 网关 发的是一串UUID，这里替换成文本, 调试窗口还是发的文本，不用处理
            if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())) {
                msgDto.setMessageData(sceneName.toString());
                // 更新对应的消息记录
                if (ObjectUtils.isNotEmpty(robotRecordDo)) {
                    robotRecordDo.setMessage(msgDto.getMessageData());
                    robotRecordApi.updateById(robotRecordDo);
                }
            }
        }
        return resp;
    }

    private void sendLastReply(MsgDto msgDto, RobotDto robotDto) {
        log.info("发送兜底消息------------:\nmsg:{}\nrobot:{}", msgDto, robotDto);
        //!!!!如果是fontdo,  选择蜂动的方式进行发送
        if (SupplierConstant.FONTDO.equals(msgDto.getSupplierTag())) {

            String templateIds = robotDto.getRebotSettingModel().getTemplateId();
            //这里可能有多个兜底模板需要发送
            List<String> templateIdList = ListUtil.list(false, templateIds.split(","));
            //如果templateIdList为空,不发送消息
            if (CollectionUtils.isEmpty(templateIdList)) {
                return;
            }
            ////给蜂动消息填充${detailId}动态参数的值
            msgDto.setDetailId(0L);
            //如果只有一个模板id,直接发送
            if (templateIdList.size() == 1) {
                if (StrUtil.isNotBlank(templateIdList.get(0))) {
                    if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())) {
                        doSend(msgDto, templateIdList.get(0), robotDto);
                    } else {
                        sendDebugOne(msgDto, robotDto);
                    }
                }
                return;
            }
            //有多个模板id,判断是发送多个模板还是随机选择一个模板发送
            //兜底回复类型 0代表发送全部 1代表随机发送一条
            int lastReplyType = robotDto.getRebotSettingModel().getLastReplyType();
            //全部发送
            if (lastReplyType == 0) {
                log.info("发送全部兜底消息templateIdList:{}", templateIdList);
                if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())) {
                    for (String templateId : templateIdList) {
                        if (StrUtil.isNotBlank(templateId)) {
                            log.info("发送蜂动兜底消息,templateId:{}", templateId);
                            doSend(msgDto, templateId, robotDto);
                        }
                    }
                } else {
                    log.info("发送debug全部兜底消息");
                    sendDebugAll(msgDto, robotDto);
                }
            }
            //随机选择一个模板发送
            else {
                log.info("发送随机兜底消息");
                if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())) {
                    //随机选择一个模板发送
                    log.info("发送随机 蜂动兜底消息, templateIdList:{}", templateIdList);
                    int index = getRandom(templateIdList.size());
                    log.info("发送蜂动兜底消息:{}", templateIdList.get(index));
                    String templateId = templateIdList.get(index);
                    if (StrUtil.isNotBlank(templateId)) {
                        doSend(msgDto, templateId, robotDto);
                    }
                } else {
                    sendDebugOne(msgDto, robotDto);
                }

            }
        }
        //运营商形式发送
        else {
            RebotSettingModel settingModel = robotDto.getRebotSettingModel();
            JSONArray array = JSONArray.parseArray(settingModel.getLastReply());
            // 获取兜底消息
            List<Object> lastReplyList = getLastReply(settingModel);
            // 默认消息为空不发送消息
            if (null != array && !array.isEmpty()) {
                //兜底回复提问，发送客户消息
                for (Object msg : lastReplyList) {
                    WsResp wsResp = convertMsgDtoToWsResp(msgDto, msg);
                    sendMsgClient.sendMsgNew(wsResp);
                }
            }
        }
    }

    @NotNull
    private static WsResp convertMsgDtoToWsResp(MsgDto msgDto, Object msg) {
        WsResp wsResp = new WsResp();
        BeanUtils.copyProperties(msgDto, wsResp);
        wsResp.setConversationId(msgDto.getConversationId());
        wsResp.setContributionId(msgDto.getContributionId());
        wsResp.setBody(msg);
        wsResp.setMsgType(MessageType.TEXT.getCode());
        wsResp.setPocketBottom(true);
        wsResp.setUserId(msgDto.getCustomerId());
        wsResp.setChannelType(msgDto.getAccountType());
        wsResp.setUserId(msgDto.getCustomerId());
        if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, msgDto.getMessageSource())) {
            wsResp.setFalg(1);
        }
        return wsResp;
    }

    private void sendDebugAll(MsgDto msgDto, RobotDto robotDto) {
        RebotSettingModel settingModel = robotDto.getRebotSettingModel();
        JSONArray array = JSONArray.parseArray(settingModel.getLastReply());
        // 获取兜底消息
        List<Object> lastReplyList = getLastReply(settingModel);
        // 默认消息为空不发送消息
        if (null != array && !array.isEmpty()) {
            //兜底回复提问，发送客户消息
            for (Object msg : lastReplyList) {
                WsResp wsResp = convertMsgDtoToWsResp(msgDto, msg);
                wsResp.setButtonList(getGlobalButtonList(wsResp.getConversationId()));
                String conversationId = wsResp.getConversationId();
                String account = wsResp.getChatbotAccount();
                String originalMsg = JsonUtils.obj2String(wsResp);
                //!!!替换系统参数等占位符
                String translatedMsg = RobotUtils.translateVariable(wsResp.getConversationId(), wsResp.getPhone(), originalMsg);
                log.info("\noriginalMsg:{} \ntranslatedMsg:{}", originalMsg, translatedMsg);
                //发送网关消息
                sendDebugMsg(conversationId, account, translatedMsg);
            }
        }
    }

    private void sendDebugOne(MsgDto msgDto, RobotDto robotDto) {
        RebotSettingModel settingModel = robotDto.getRebotSettingModel();
        JSONArray array = JSONArray.parseArray(settingModel.getLastReply());
        // 获取兜底消息
        List<Object> lastReplyList = getLastReply(settingModel);
        int index = getRandom(lastReplyList.size());
        // 默认消息为空不发送消息
        if (null != array && !array.isEmpty()) {
            //兜底回复提问，发送客户消息
            Object msg = lastReplyList.get(index);
            WsResp wsResp = convertMsgDtoToWsResp(msgDto, msg);
            wsResp.setButtonList(getGlobalButtonList(wsResp.getConversationId()));
            String conversationId = wsResp.getConversationId();
            String account = wsResp.getChatbotAccount();
            String originalMsg = JsonUtils.obj2String(wsResp);
            //!!!替换系统参数等占位符
            String translatedMsg = RobotUtils.translateVariable(wsResp.getConversationId(), wsResp.getPhone(), originalMsg);
            log.info("\noriginalMsg:{} \ntranslatedMsg:{}", originalMsg, translatedMsg);
            //发送网关消息
            sendDebugMsg(conversationId, account, translatedMsg);
        }
    }

    private void sendDebugMsg(String conversationId, String account, String gsonMessage) {
        try {
            String ip = stringRedisTemplate.opsForValue().get("CHATBOT_SESSION_ROUTER:" + conversationId);
            String url;
            // 扩展商城默认账号时发送到扩展商城ws
            if (org.apache.commons.lang3.StringUtils.equals(DEFAULT_ACCOUNT, account)) {
                url = "http://" + ip + "/mallWs/pushMsgToClient";
            } else {
                url = "http://" + ip + "/ws/pushMsgToClient";
            }
            HttpURLConnectionUtil.doPost(url, null, gsonMessage, null);
        } catch (Exception e) {
            log.error("发送调试信息失败:{}", e.getMessage(), e);
        }
    }

    private List<RobotProcessButtonResp> getGlobalButtonList(String conversationId) {
        List<RobotProcessButtonResp> buttonList = new ArrayList<>();
        RebotSettingModel rebotSettingModel = RobotUtils.getRobot(conversationId).getRebotSettingModel();
        //0代表仅在兜底回复显示   1代表在机器人所有回复显示
        if (0 == rebotSettingModel.getGlobalType()) {
            return buttonList;
        }
        buttonList = rebotSettingModel.getButtonList();
        return buttonList;
    }

    //发送蜂动5G消息
    private void doSend(MsgDto msgDto, String templateId, RobotDto robotDto) {
        MessageTemplateProvedReq templateProvedReq = new MessageTemplateProvedReq();
        templateProvedReq.setTemplateId(Long.parseLong(templateId));
        templateProvedReq.setSupplierTag(msgDto.getSupplierTag());
        templateProvedReq.setOperator(msgDto.getOperator());
        List<MessageTemplateProvedReq> templateProvedReqs = new ArrayList<>();
        templateProvedReqs.add(templateProvedReq);
        MessageTemplateProvedListReq messageTemplateProvedListReq = new MessageTemplateProvedListReq();
        messageTemplateProvedListReq.setTemplateProvedReqs(templateProvedReqs);
        //获取平台模板id
        List<MessageTemplateProvedResp> platformTemplates = templateApi.getPlatformTemplateIds(messageTemplateProvedListReq);
        platformTemplates.forEach(template -> template.setTemplateName(TemplateMessageTypeEnum.getRequestMessageTypeEnumByCode(template.getMessageType()).getName()));

        //真正发送

        WsResp msg = new WsResp();
        msg.setConversationId(msgDto.getConversationId());
        msg.setContributionId(msgDto.getContributionId());
        msg.setMsgType(msgDto.getMessageType());
        msg.setUserId(msgDto.getCustomerId());
        msg.setPhone(msgDto.getPhone());
        msg.setChatbotAccount(msgDto.getChatbotAccount());
        msg.setChannelType(msgDto.getAccountType());
        sendMessage.send(msgDto, 0, platformTemplates, msg, robotDto);
    }

    private List<Object> getLastReply(RebotSettingModel model) {
        List<Object> lastReplyList = new ArrayList<>();
        String lastReply = model.getLastReply();
        //解析lastReply，转换为List<String>
        List<JSONObject> lastReplyJsonList = JSON.parseArray(lastReply, JSONObject.class);
        List<RobotProcessButtonResp> buttonList = model.getButtonList();
        List<Object> objectList = new ArrayList<>();
        if (buttonList != null) {
            buttonList.forEach(robotProcessButtonResp -> {
                String buttonDetail = robotProcessButtonResp.getButtonDetail();
                String uuid = robotProcessButtonResp.getUuid();
                Object parse = JSONObject.parse(buttonDetail);
                JSONObject jsonObject = (JSONObject) JSON.toJSON(robotProcessButtonResp);
                jsonObject.put("buttonDetail", parse);
                jsonObject.put("uuid", uuid);
                objectList.add(jsonObject);
            });
        }
        switch (model.getLastReplyType()) {
            // 发送全部
            case 0:
                if (lastReplyJsonList != null && lastReplyJsonList.size() > 0) {
                    lastReplyJsonList.forEach(lastReplyJson -> {
                        setMessageMap(lastReplyList, objectList, lastReplyJson);
                    });
                } else {
                    setMessageMap(lastReplyList, objectList, "");
                }
                break;
            // 随机发送一条
            case 1:
                if (lastReplyJsonList != null && lastReplyJsonList.size() > 0) {
                    int num = getRandom(lastReplyJsonList.size());
                    JSONObject lastReplyJson = lastReplyJsonList.get(num);
                    setMessageMap(lastReplyList, objectList, lastReplyJson);
                } else {
                    setMessageMap(lastReplyList, objectList, "");
                }
                break;
        }
        return lastReplyList;
    }

    private int getRandom(int seed) {
        return RANDOM.nextInt(seed);
    }

    private static void setMessageMap(List<Object> lastReplyList, List<Object> objectList, Object lastReplyJson) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", lastReplyJson);
        map.put("button", objectList);
        lastReplyList.add(map);
    }

    private Map<String, Process> dealWithProcess(String processId, String parentProcessId, String conversationId, Long expireTime, RobotDto robotDto, HashMap<Long, RobotProcessTreeResp> processTreeMap) {
        Map<String, Process> processMap = new HashMap<>();
        // 处理主流程
        Process process = initProcess(processId, parentProcessId, conversationId, expireTime);
        // 处理节点
        Map<String, Node> stringNodeMap = dealWithNodeMap(processId, parentProcessId, processMap, robotDto, processTreeMap);
        process.setNodeMap(stringNodeMap);
        processMap.put(processId, process);
        return processMap;
    }

    private Process initProcess(String processId, String parentProcessId, String conversationId, Long expireTime) {
        Process process = new Process();
        process.setParentProcessId(parentProcessId);
        process.setId(processId);
        process.setConversationId(conversationId);
        process.setExpireTime(expireTime);
        process.setProcessStatus(ProcessStatusEnum.INIT.getCode());
        return process;
    }

    @XssCleanIgnore
    private Map<String, Node> dealWithNodeMap(String processId, String parentProcessId, Map<String, Process> processMap, RobotDto robotDto, HashMap<Long, RobotProcessTreeResp> processTreeMap) {
        RobotProcessTreeResp robotProcessTreeResp = new RobotProcessTreeResp();
        RobotProcessTreeReq processTreeReq = new RobotProcessTreeReq();
        processTreeReq.setProcessId(Long.parseLong(processId));
        if (RobotConstant.DEFAULT_PARENT_PROCESS_ID.equals(parentProcessId)) {
            //初始化流程栈
            LinkedList<String> queue = new LinkedList<>();
            queue.add(processId);
            robotDto.setProcessQueue(queue);
        }
        if (null != processTreeMap && null != processTreeMap.get(Long.parseLong(processId))) {
            robotProcessTreeResp = processTreeMap.get(Long.parseLong(processId));
        } else {
            // 根据流程id去获取流程
            robotProcessTreeResp = robotProcessTreeApi.listReleaseProcessDesById(processTreeReq);
        }

        if (null == robotProcessTreeResp || null == robotProcessTreeResp.getProcessDes()) {
            /**
             * 2023-10-14
             * 1.父流程未发布。提示 流程不存在或未发布
             * 2.父流程发布，里面只有开始节点。流程不执行，不计统计。
             * 3.父流程发布，子流程未发布。执行到子流程时，跳过该子流程
             * 4.父流程发布，子流程发布，子流程只有初始开始节点。子流程不执行，子流程不计统计
             */
            if (!StringUtils.equals(RobotConstant.DEFAULT_PARENT_PROCESS_ID, parentProcessId)) {
                return null;
            } else {
                throw new BizException(RobotException.PROCESS_ERROR);
            }
        } else {
            /**
             * 2023-12-19
             * 流程关闭，跳过关闭的流程不处理
             */
            RobotProcessSettingNodeResp settingById = robotProcessSettingNodeApi.getRobotProcessSettingById(robotProcessTreeResp.getProcessId());
            if (settingById != null && Objects.equals(1, settingById.getDerail())) {
                return null;
            }
        }
        String processDes = robotProcessTreeResp.getProcessDes();

        ProcessorConfig processorConfig = JsonUtils.string2Obj(processDes, ProcessorConfig.class);
        List<Line> lineList = processorConfig.getRobotProcessLineList();
        Map<String, Node> nodeMap = new HashMap<>();
        List<Node> nodeList = NodeUtil.parse(processDes);
        // 正常处理
        if (CollectionUtils.isNotEmpty(nodeList)) {
            nodeMap = whenNormalProcess(processId, parentProcessId, processMap, robotDto, lineList, nodeList);
        } else {
            // 空流程时
            whenEmptyProcess(processId, parentProcessId, processMap);
        }
        return nodeMap;
    }

    private static void whenEmptyProcess(String processId, String parentProcessId, Map<String, Process> processMap) {
        if (!RobotConstant.DEFAULT_PARENT_PROCESS_ID.equals(parentProcessId)) {
            Process emptyProcess = new Process();
            emptyProcess.setId(processId);
            processMap.put(processId, emptyProcess);
        }
    }

    private Map<String, Node> whenNormalProcess(String processId, String parentProcessId, Map<String, Process> processMap, RobotDto robotDto, List<Line> lineList, List<Node> nodeList) {
        Map<String, Node> nodeMap;
        nodeMap = nodeList.stream().collect(Collectors.toMap(Node::getNodeId, Function.identity()));
        // 遍历map，设置节点状态 路由 ,
        List<String> removeNodeIdList = initStatusAndSetRouter(processId, parentProcessId, nodeMap, lineList, processMap, robotDto);
        // 把已经合并进去的分支节点 移除
        if (CollectionUtils.isNotEmpty(removeNodeIdList)) {
            for (String nodeId :
                    removeNodeIdList) {
                nodeMap.remove(nodeId);
            }
            // 空流程时
        } else {
            if (!StringUtils.equals(RobotConstant.DEFAULT_PARENT_PROCESS_ID, parentProcessId)) {
                Process emptyProcess = new Process();
                emptyProcess.setId(processId);
                processMap.put(processId, emptyProcess);
            }
        }
        return nodeMap;
    }

    private static void setProcessQueue(String processId, String parentProcessId, RobotDto robotDto) {
        if (RobotConstant.DEFAULT_PARENT_PROCESS_ID.equals(parentProcessId)) {
            //初始化流程栈
            LinkedList<String> queue = new LinkedList<>();
            queue.add(processId);
            robotDto.setProcessQueue(queue);
        }
    }

    private List<String> initStatusAndSetRouter(String processId, String parentProcessId, Map<String, Node> nodeMap, List<Line> lineList, Map<String, Process> processMap, RobotDto robotDto) {
        Iterator<Map.Entry<String, Node>> iterator = nodeMap.entrySet().iterator();
        // 需要被删除的节点Id
        List<String> removeNodeIdList = new ArrayList<>();
        // 用于分支节点的map
        LinkedHashMap<String, Node> branchNodeMap = new LinkedHashMap<>();
        getBranchNodeMap(nodeMap, lineList, branchNodeMap);
        // 拿到后续节点指向的节点
        HashMap<String, Node> nextNodeMap = new HashMap<>();
        getNextNodeMap(nodeMap, lineList, branchNodeMap, nextNodeMap);
        // 处理开始节点
        Line start = getStart(lineList);
        Assert.notNull(start, "start line is null");
        while (iterator.hasNext()) {
            Map.Entry<String, Node> next = iterator.next();
//            log.info("next k : {}, v : {}", next.getKey(), next.getValue());
            Node node = nodeMap.get(next.getKey());
            node.setNodeStatus(NodeStatus.INIT);
            node.setProcessorId(processId);
            // 获取节点的  验证Id  或者 兜底id
            JSONObject put = new JSONObject();
            put.put("id", node.getNodeId());
            Object id = Arrays.asList(put);
            // 用这个id去匹配line的fromId，匹配到的情况，把routerInfo的下一个节点设置为line的toId
            node = setNodeInfoAndRouterInfo(processId, lineList, processMap, robotDto, removeNodeIdList, branchNodeMap, nextNodeMap, node, id);
            if (StringUtils.equals(node.getNodeId(), start.getToId())) {
                // 处理开始节点
                setBeginNode(start, node, processMap, processId, parentProcessId, robotDto);
            }
            nodeMap.put(next.getKey(), node);
        }
        return removeNodeIdList;
    }

    private Node setNodeInfoAndRouterInfo(String processId, List<Line> lineList, Map<String, Process> processMap, RobotDto robotDto, List<String> removeNodeIdList, LinkedHashMap<String, Node> branchNodeMap, HashMap<String, Node> nextNodeMap, Node node, Object id) {
        List<RouterInfo> outRouterInfo = new ArrayList<>();
        switch (node.getNodeType()) {
            case BLANK_BRANCH_NODE:
                // 分支节点有一个空分支 "nodeType": 1 这个节点的line会指向多个分支节点 "nodeType": 7,
                // 这里需要把这个空分支 和 它指向的节点 合成成为一个节点。类型是 "nodeType": 1,
                // 它的指向节点合并后，还要单独把后面的路由关联到当前空分支的节点id
                List<Node> myBranches = new ArrayList<>();
                for (Node branchNode : branchNodeMap.values()) {
                    for (Line line : lineList) {
                        if (line.getFromId().equals(node.getNodeId()) && branchNode.getNodeId().equals(line.getToId()))
                            myBranches.add(branchNode); //从所有分支中过滤出当前分支节点的分支
                    }
                }
                BranchNode mainBranchNode = dealWithBranchNode(node, outRouterInfo, myBranches, nextNodeMap);
                if (null != mainBranchNode) {
                    node = mainBranchNode;
                }
                break;
            case BRANCH_NODE:
                // BLANK_BRANCH_NODE 会把所有分支节点的路由和判断设置到 BLANK_BRANCH_NODE 节点自己身上，因此多余的分支节点需要删除
                removeNodeIdList.add(node.getNodeId());
                break;
            case QUESTION_NODE:
                // 获取节点的  验证Id  或者 兜底id
                Object nodeLast = NodeUtil.getValue(node, "nodeLast");
                Object conditionList = NodeUtil.getValue(node, "conditionList");
                // 验证类型0关键字1正则2点击按钮3回复类型
                setHeadAndTailNode(lineList, node, nodeLast, outRouterInfo, processMap, processId, robotDto);
                setHeadAndTailNode(lineList, node, conditionList, outRouterInfo, processMap, processId, robotDto);
                break;
            case COMMAND_NODE:
                List<JSONObject> list = new ArrayList<>();
                JSONObject sucessId = new JSONObject();
                Object sucessIdValue = NodeUtil.getValue(node, "sucessId");
                if (ObjectUtils.isNotEmpty(sucessIdValue)) {
                    sucessId.put("id", sucessIdValue.toString());
                    list.add(sucessId);
                }

                JSONObject failId = new JSONObject();
                Object failIdValue = NodeUtil.getValue(node, "failId");
                if (ObjectUtils.isNotEmpty(failIdValue)) {
                    failId.put("id", failIdValue.toString());
                    list.add(failId);
                }
                setHeadAndTailNode(lineList, node, list, outRouterInfo, processMap, processId, robotDto);
                break;
            case TEXT_RECOGNITION_NODE:
                List<JSONObject> list1 = new ArrayList<>();
                JSONObject sucessId1 = new JSONObject();
                Object sucessIdValue1 = NodeUtil.getValue(node, "sucessId");
                if (ObjectUtils.isNotEmpty(sucessIdValue1)) {
                    sucessId1.put("id", sucessIdValue1.toString());
                    list1.add(sucessId1);
                }

                JSONObject failId1 = new JSONObject();
                Object failIdValue1 = NodeUtil.getValue(node, "failId");
                if (ObjectUtils.isNotEmpty(failIdValue1)) {
                    failId1.put("id", failIdValue1.toString());
                    list1.add(failId1);
                }
                setHeadAndTailNode(lineList, node, list1, outRouterInfo, processMap, processId, robotDto);
                break;
            case BEGIN_NODE:
                break;
            default:
                // 处理普通节点
                setHeadAndTailNode(lineList, node, id, outRouterInfo, processMap, processId, robotDto);
        }
        node.setOutRouters(outRouterInfo);
        return node;
    }

    private static Line getStart(List<Line> lineList) {
        Line start = null;
        for (Line line : lineList) {
            if (StringUtils.equals(RobotConstant.DEFAULT_BEGIN_NODE, line.getFromId())) {
                start = line;
                break;
            }
        }
        return start;
    }

    private static void getNextNodeMap(Map<String, Node> nodeMap, List<Line> lineList, HashMap<String, Node> branchNodeMap, HashMap<String, Node> nextNodeMap) {
        if (CollectionUtils.isNotEmpty(branchNodeMap)) {
            Iterator<Map.Entry<String, Node>> branchNodeIterator = branchNodeMap.entrySet().iterator();
            while (branchNodeIterator.hasNext()) {
                Map.Entry<String, Node> next = branchNodeIterator.next();
                Node node = nodeMap.get(next.getKey());
                for (Line line :
                        lineList) {
                    if (StringUtils.equals(node.getNodeId(), line.getFromId())) {
                        Node nextNode = nodeMap.get(line.getToId());
                        nextNodeMap.put(line.getFromId(), nextNode);
                    }
                }
            }
        }
    }

    private static void getBranchNodeMap(Map<String, Node> nodeMap, List<Line> lineList, LinkedHashMap<String, Node> branchNodeMap) {
        for (Map.Entry<String, Node> next : nodeMap.entrySet()) {
            Node node = nodeMap.get(next.getKey());
            if (node.getNodeType() == NodeType.BLANK_BRANCH_NODE) {
                for (Line line :
                        lineList) {
                    // 先把当前节点指向的分支节点拿到
                    if (StringUtils.equals(node.getNodeId(), line.getFromId())) {
                        Node branchNode = nodeMap.get(line.getToId());
                        branchNodeMap.put(branchNode.getNodeId(), branchNode);
                    }
                }
            }
        }
    }

    private BranchNode dealWithBranchNode(Node node, List<RouterInfo> outRouterInfo, List<Node> myBranches, Map<String, Node> nextNodeMap) {
        BranchNode mainBranchNode = new BranchNode();
        if (CollectionUtils.isEmpty(myBranches) && CollectionUtils.isEmpty(nextNodeMap)) {
            return null;
        }
        for (Node branchNode : myBranches) {
            RouterInfo routerInfo = new RouterInfo();
            routerInfo.setLineId(branchNode.getNodeId());
            routerInfo.setHeadNodeId(node.getNodeId());
            if (null != nextNodeMap.get(branchNode.getNodeId())) {
                routerInfo.setTailNodeId(nextNodeMap.get(branchNode.getNodeId()).getNodeId());
            }
            routerInfo.setConditionStrategy(RouteConditionStrategy.NONE);
            Object conditionList = NodeUtil.getValue(branchNode, "conditionList");
            Object executeType = NodeUtil.getValue(branchNode, "executeType");
            if (StringUtils.equals(JsonUtils.obj2String(executeType), RobotConstant.OLD_ROBOT_ALL_MATCH)) {
                routerInfo.setConditionStrategy(RouteConditionStrategy.ALL_MATCH);
            } else {
                routerInfo.setConditionStrategy(RouteConditionStrategy.ANY_MATCH);
            }
            setConditionExpression(routerInfo, conditionList);
            outRouterInfo.add(routerInfo);
        }
        BeanUtils.copyProperties(node, mainBranchNode);
        mainBranchNode.setNodeType(NodeType.BRANCH_NODE);
        return mainBranchNode;
    }

    private static void setConditionExpression(RouterInfo routerInfo, Object conditionList) {
        List<ConditionExpression> execConditions = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(conditionList)) {
            List<JSONObject> objectList = JSONArray.parseArray(JsonUtils.obj2String(conditionList), JSONObject.class);
            for (JSONObject obj :
                    objectList) {
                ConditionExpression conditionExpression = new ConditionExpression();
                int conditionType = Integer.parseInt(obj.get("conditionType").toString());
                conditionExpression.setOperator(ConditionOperator.getValue(conditionType));
                // 左边的变量，用于比较的值
                conditionExpression.setConditionSettingValue(obj.get("conditionContentValue").toString());
                // 右边的变量，用于比较的对象
                conditionExpression.setConditionVariableName(obj.get("conditionCodeValue").toString());
                execConditions.add(conditionExpression);
            }
        }
        routerInfo.setExecConditions(execConditions);
    }

    private void setBeginNode(Line start, Node node, Map<String, Process> processMap, String processId, String parentProcessId, RobotDto robotDto) {
        if (null != start) {
            // 是主流程开始的第一个节点
            if (StringUtils.equals(RobotConstant.DEFAULT_PARENT_PROCESS_ID, parentProcessId) && StringUtils.equals(RobotConstant.DEFAULT_BEGIN_NODE, start.getFromId())) {
                robotDto.setCurrentNodeId(start.getToId());
            }
            if (StringUtils.equals(RobotConstant.DEFAULT_BEGIN_NODE, start.getFromId())) {
                node.setBegin(true);
                node.setNodeName(start.getToName());
                if (CollectionUtils.isEmpty(node.getOutRouters())) {
                    RouterInfo routerInfo = new RouterInfo();
                    routerInfo.setLineId(start.getLineId());
                    routerInfo.setHeadNodeId(node.getNodeId());
//                        routerInfo.setTailNodeId(line.getToId());
                    routerInfo.setConditionStrategy(RouteConditionStrategy.NONE);
                    // 处理不同的节点类型路由数据
                    dealWithQuestionAndSubProcessNode(processMap, processId, robotDto, routerInfo, node);
                    List<RouterInfo> outRouterInfo = new ArrayList<>();
                    outRouterInfo.add(routerInfo);
                    node.setOutRouters(outRouterInfo);
                }
            }
        }
    }

    private void setHeadAndTailNode(List<Line> lineList, Node node, Object object, List<RouterInfo> outRouterInfo, Map<String, Process> processMap,
                                    String processId, RobotDto robotDto) {
        if (ObjectUtils.isNotEmpty(object)) {
            List<JSONObject> objectList = JSONArray.parseArray(JsonUtils.obj2String(object), JSONObject.class);
            Map<String, JSONObject> objectMap = objectList.stream().collect(Collectors.toMap(start -> start.get("id").toString(), Function.identity()));
            boolean isTailNode = true;
            for (Line line :
                    lineList) {
                if (ObjectUtils.isNotEmpty(objectMap.get(line.getFromId()))) {
                    // 验证类型0关键字1正则2点击按钮3回复类型
                    JSONObject jsonObject = objectMap.get(line.getFromId());
                    String relationId = jsonObject.getString("id");
                    if (node instanceof CommandNode) {
                        if (StringUtils.equals(((CommandNode) node).getSucessId(), relationId)) {
                            setRouterInfoAndNode(line.getLineId(), node, line.getToId(), processMap, processId, robotDto, outRouterInfo, relationId, RouteConditionStrategy.EXECUTE_SUCCESS);
                        }
                        if (StringUtils.equals(((CommandNode) node).getFailId(), relationId)) {
                            setRouterInfoAndNode(line.getLineId(), node, line.getToId(), processMap, processId, robotDto, outRouterInfo, relationId, RouteConditionStrategy.EXECUTE_FAIL);
                        }
                    }
                    if (node instanceof TextRecognitionNode) {
                        if (StringUtils.equals(((TextRecognitionNode) node).getSucessId(), relationId)) {
                            setRouterInfoAndNode(line.getLineId(), node, line.getToId(), processMap, processId, robotDto, outRouterInfo, relationId, RouteConditionStrategy.EXECUTE_SUCCESS);
                        }
                        if (StringUtils.equals(((TextRecognitionNode) node).getFailId(), relationId)) {
                            setRouterInfoAndNode(line.getLineId(), node, line.getToId(), processMap, processId, robotDto, outRouterInfo, relationId, RouteConditionStrategy.EXECUTE_FAIL);
                        }
                    } else {
                        setRouterInfoAndNode(line.getLineId(), node, line.getToId(), processMap, processId, robotDto, outRouterInfo, relationId, RouteConditionStrategy.NONE);
                    }
                    isTailNode = false;
                }
            }
            // 尾节点
            if (isTailNode) {
                setRouterInfoAndNode(null, node, null, processMap, processId, robotDto, outRouterInfo, null, RouteConditionStrategy.NONE);
            }
        }
    }

    private void setRouterInfoAndNode(String lineId, Node node, String tail, Map<String, Process> processMap, String processId,
                                      RobotDto robotDto, List<RouterInfo> outRouterInfo, String relationId, RouteConditionStrategy routeConditionStrategy) {
        RouterInfo routerInfo = new RouterInfo();
        routerInfo.setLineId(lineId);
        routerInfo.setHeadNodeId(node.getNodeId());
        routerInfo.setTailNodeId(tail);
        routerInfo.setRelationId(relationId);
        routerInfo.setConditionStrategy(routeConditionStrategy);
        // 处理不同的节点类型路由数据
        dealWithQuestionAndSubProcessNode(processMap, processId, robotDto, routerInfo, node);
        outRouterInfo.add(routerInfo);
    }

    private void dealWithQuestionAndSubProcessNode(Map<String, Process> processMap, String processId, RobotDto robotDto, RouterInfo routerInfo, Node node) {
        if (Objects.requireNonNull(node.getNodeType()) == NodeType.SUB_PROCESSOR_NODE) {
            String subProcessId = (String) NodeUtil.getValue(node, "subProcessId");
            //如果子流程ID为空或者不是数字类型
            if (StringUtils.isEmpty(subProcessId) || !NumberUtils.isParsable(subProcessId)) {
                return;
            }

            // 判断是否已经存过
            Process process = processMap.get(subProcessId);
            if (ObjectUtils.isEmpty(process)) {
                // 先塞个空对象，避免判断的时候一直ture，导致死循环
                processMap.put(subProcessId, new Process());
                // 子流程递归调用自己
                Map<String, Node> stringNodeMap = dealWithNodeMap(subProcessId, processId, processMap, robotDto, null);
                // 保存
                Process subProcess = initProcess(subProcessId, processId, robotDto.getConversationId(), robotDto.getExpireTime());
                subProcess.setNodeMap(stringNodeMap);
                processMap.put(subProcessId, subProcess);
                Map<String, Process> longProcessMap = robotDto.getProcessMap();
                if (null == longProcessMap) {
                    robotDto.setProcessMap(processMap);
                } else {
                    longProcessMap.putAll(processMap);
                    robotDto.setProcessMap(longProcessMap);
                }
            }
        }
    }

    /*
     * @describe 匹配触发器  如果是供应商渠道, 那么最多只选嘴一个最新的触发器
     * @Param
     * @param msgDto
     * @param triggerNodesByCreate
     * @return java.util.List<com.citc.nce.im.session.processor.bizModel.RobotProcessTriggerModel>
     **/
    private List<RobotProcessTriggerModel> matchTrigger(MsgDto msgDto, List<RobotProcessTriggerNodesResp> triggerNodesByCreate) {
        List<RobotProcessTriggerNodesResp> sortedTriggerList = triggerNodesByCreate.stream()
                .sorted(Comparator
                        .comparing(RobotProcessTriggerNodesResp::getUpdateTime)
                        .reversed())
                .collect(Collectors.toList());
        List<RobotProcessTriggerModel> matchedList = new ArrayList<>();
        //优先正则匹配
        for (RobotProcessTriggerNodesResp trigger : sortedTriggerList) {
            if (org.springframework.util.StringUtils.hasText(trigger.getRegularCode())) {
                Pattern pattern = Pattern.compile(trigger.getRegularCode());
                boolean matches = pattern.matcher(msgDto.getMessageData()).matches();
                if (!matches) {
                    matches = pattern.matcher(msgDto.getMessageData()).find();
                }
                if (matches) {
                    RobotProcessTriggerModel robotProcessTriggerModel = new RobotProcessTriggerModel();
                    robotProcessTriggerModel.setProcessId(trigger.getProcessId());
                    robotProcessTriggerModel.setSceneId(trigger.getSceneId());
                    robotProcessTriggerModel.setSceneName(trigger.getProcessName());
                    matchedList.add(robotProcessTriggerModel);
                    if (matchedList.size() == RobotConstant.MAX_TRIGGER_NUM)
                        break;
                }
            }
        }
        if (CollectionUtils.isNotEmpty(matchedList)) {
            return matchedList;
        }
        //匹配关键字
        for (RobotProcessTriggerNodesResp trigger : sortedTriggerList) {
            String primaryCodeList = trigger.getPrimaryCodeList();
            if (org.springframework.util.StringUtils.hasText(primaryCodeList)) {
                String[] keyWordList = primaryCodeList.split(";");
                for (String keyWord : keyWordList) {
                    if (msgDto.getMessageData().contains(keyWord)) {
                        RobotProcessTriggerModel robotProcessTriggerModel = new RobotProcessTriggerModel();
                        robotProcessTriggerModel.setProcessId(trigger.getProcessId());
                        robotProcessTriggerModel.setKeyWord(keyWord);
                        robotProcessTriggerModel.setSceneName(trigger.getProcessName());
                        robotProcessTriggerModel.setSceneId(trigger.getSceneId());
                        matchedList.add(robotProcessTriggerModel);
                        break;
                    }
                }
                if (matchedList.size() == RobotConstant.MAX_TRIGGER_NUM)
                    break;
            }
        }
        return matchedList;
    }

    private static List<RobotProcessTriggerModel> getMaxTriggerSet(List<RobotProcessTriggerModel> robotProcessTriggerModelList) {
        if (robotProcessTriggerModelList.size() > RobotConstant.MAX_TRIGGER_NUM) {
            List<RobotProcessTriggerModel> newList = new ArrayList<>();
            List<RobotProcessTriggerModel> modelList = new ArrayList<>(robotProcessTriggerModelList);
            for (int i = 0; i < RobotConstant.MAX_TRIGGER_NUM; i++) {
                RobotProcessTriggerModel robotProcessTriggerModel = modelList.get(i);
                newList.add(robotProcessTriggerModel);
            }
            return newList;
        }
        return null;
    }


    /**
     * 流程的执行
     * 1、通过conversationId在redis中获取正在执行的机器人
     * 2、获取机器人的当前流程、当前结点和流程栈(要保证栈顶流程是当前流程)
     * 3、如果流程栈不为空，就进行机器人的流程执行
     *
     * @param msgDto 消息内容
     */
    @Override
    public void exec(MsgDto msgDto) {
        String conversationId = msgDto.getConversationId();
        log.info("流程栈开始执行conversationId:{}", conversationId);
        RobotDto robot = RobotUtils.getRobot(conversationId);
        RobotUtils.putLocalRobot(robot); //将机器人添加到本地缓存
        //机器人中找到流程列表(数量为n时 说明有子流程)
        LinkedList<String> processQueue = robot.getProcessQueue();
        Process process = RobotUtils.getCurrentProcess(robot);

        //如果是执行阻塞流程则表示从阻塞中恢复
        if (process.getProcessStatus() == ProcessStatusEnum.BLOCK.getCode()) {
            log.info("阻塞流程恢复执行");
            process.setProcessStatus(ProcessStatusEnum.EXECUTE.getCode());
            RobotUtils.saveRobot(robot);
        }
        //循环流程栈 直至栈清空或当前流程被阻塞
        while (!processQueue.isEmpty()) {
            String last = processQueue.peekLast();
            log.info("流程栈size:{},栈顶流程:{}", processQueue.size(), last);

            processService.processExec(msgDto, robot);

            //流程执行完之后重新从redis中拿最新的信息
            robot = RobotUtils.getRobot(conversationId);
            process = RobotUtils.getCurrentProcess(robot);

            ProcessStatusEnum currentProcessState = ProcessStatusEnum.getValue(process.getProcessStatus());

            String currentProcessId = process.getId();
            if (!Objects.equals(last, currentProcessId)) {
                log.info("栈顶流程发生变更，重新执行新流程:{}", currentProcessId);
                continue;
            }
            if (currentProcessState == null) {
                log.error("当前流程状态异常conversationId:{},currentProcessId:{}，退出执行", conversationId, currentProcessId);
                return;
            }
            switch (currentProcessState) {
                case BLOCK: {
                    RobotUtils.removeLocalRobot(conversationId);
                    log.info("流程阻塞，退出执行");
                    return;
                }
                case EXCEPTION: {
                    RobotUtils.cleanRobot(conversationId);
                    log.info("流程异常，退出执行");
                    return;
                }
                default: {
                    log.info("流程执行完毕 state:{}", currentProcessState);
                    break;
                }
            }

            if (robot.getCurrentNodeId() == null) {
                //如果流程执行返回并且当前节点为空，表示流程执行完毕，执行出栈逻辑
                processQueue = robot.getProcessQueue();
                last = processQueue.removeLast();
                log.info("流程:{}出栈", last);
                robot.setCurrentProcessId(processQueue.peekLast());
                RobotUtils.saveRobot(robot);
                //统计流程完成
                if (process.getProcessStatus() == ProcessStatusEnum.END.getCode()) {
                    int subIdx = last.indexOf("+");
                    temporaryStatisticsUtil.completeProcess(msgDto, robot.getSceneId(), subIdx != -1 ? last.substring(0, subIdx) : last);
                }
            }

            if (!Objects.equals(RobotConstant.DEFAULT_PARENT_PROCESS_ID, process.getParentProcessId())) {
                Process parentProcess = robot.getProcessMap().get(process.getParentProcessId());
                parentProcess.setProcessStatus(ProcessStatusEnum.EXECUTE.getCode());
                robot.setCurrentNodeId(process.getCreatedNodeId());
                log.info("子流程执行完毕，唤醒父流程");
                RobotUtils.saveRobot(robot);
            }
        }
        log.info("流程栈清空conversationId:{}", conversationId);
        RobotUtils.removeLocalRobot(conversationId);
    }
}
