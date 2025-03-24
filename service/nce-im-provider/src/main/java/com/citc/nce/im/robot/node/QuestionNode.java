package com.citc.nce.im.robot.node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedListReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.constants.TemplateMessageTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.common.SendMsgClient;
import com.citc.nce.im.gateway.SendMessage;
import com.citc.nce.im.msgenum.MsgActionEnum;
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.MessageType;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.enums.ProcessStatusEnum;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.MsgUtils;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.robot.enums.ButtonType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

import static com.citc.nce.robot.enums.ButtonType.SUBSCRIBE_BUTTON_TYPES;

/**
 * @author jcrenc
 * @since 2023/7/17 10:29
 */
@Data
@Slf4j
public class QuestionNode extends Node {
    /**
     * 0发送全部1随机发送一条
     */
    private Integer questionType;

    /**
     * 问题列表
     */
    private String questionDetail;

    /**
     * 回复验证规则列表
     */
    private List<Verify> conditionList;

    /**
     * 兜底回复
     */
    private List<JSONObject> nodeLast;

    /**
     * 消息快捷按钮
     */
    private List<JSONObject> buttons;

    /**
     * 模板ids ,供应商发送时
     */
    private List<Long> templateIds;

    /**
     * 结点执行前操作
     *
     * @param msgDto 上行消息
     */
    @Override
    void beforeExec(MsgDto msgDto) {

    }

    /**
     * 根据节点状态选择执行逻辑
     * 1、当节点为初始状态时：根据配置规则向客户端发送提问并跳转到阻塞状态
     * 2、当节点为阻塞状态时：根据回复验证情况和兜底配置选择出线路由
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        String conversationId = msgDto.getConversationId();
        RobotDto robot = RobotUtils.getLocalRobot(conversationId);
        Process process = RobotUtils.getCurrentProcess(robot);
        JSONArray questions = JSONArray.parseArray(this.questionDetail);
        //如果执行时没有问题或者已经是阻塞状态，直接到执行后方法去路由
        if (CollectionUtils.isEmpty(questions) || this.getNodeStatus() == NodeStatus.BLOCK) {
            this.setNodeStatus(NodeStatus.EXECUTE);
            process.getNodeMap().put(this.getNodeId(), this);
            RobotUtils.saveRobot(robot);
            return;
        }
        /**
         * 2023-10-27
         * 修复提问节点发送消息，发送消息后马上又回到原来的提问节点，导致死循环
         */
        if (this.getNodeStatus() == NodeStatus.INIT || this.getNodeStatus() == NodeStatus.SUCCESS) {
            sendQuestion(msgDto, questions);
            this.setNodeStatus(NodeStatus.BLOCK);
            process.setProcessStatus(ProcessStatusEnum.BLOCK.getCode());
            process.getNodeMap().put(this.getNodeId(), this);
            RobotUtils.saveRobot(robot);
        }
    }

    /**
     * 发送问题
     */
    private void sendQuestion(MsgDto msgDto, JSONArray questions) {
        //运营商发送或者是调试窗口

        JSONArray msgArray = new JSONArray();
        if (questionType == 1) {
            Object question = questions.get(new Random().nextInt(questions.size()));
            questions = new JSONArray();
            questions.add(question);
        }
        for (Object question : questions) {
            JSONObject msg = new JSONObject();
            msg.put("msg", question);

            msg.put("button", JSONArray.parse(JSON.toJSONString(buttons)));
            msgArray.add(msg);
        }
        WsResp wsResp = new WsResp();
        BeanUtils.copyProperties(msgDto, wsResp);
        //sendMsgNode: map {"contentBody":[] , "buttonList":[]}
        //questionNode: List [ JSONObject {"msg":{JSONObject} , "button":[JSONObject]}  ]
        wsResp.setBody(msgArray);
        wsResp.setUserId(msgDto.getCustomerId());
        wsResp.setFalg(Integer.parseInt(msgDto.getMessageSource()));
        wsResp.setQuestion(true);
        wsResp.setChannelType(msgDto.getAccountType());
        wsResp.setContributionId(msgDto.getContributionId());
        wsResp.setMsgType(msgDto.getMessageType());
        //运营商或者是ws debug
        if (SupplierConstant.OWNER.equals(msgDto.getSupplierTag()) ||
                (Integer.parseInt(msgDto.getMessageSource()) == 0)) {
            SendMsgClient sendMsgClient = ApplicationContextUil.getBean(SendMsgClient.class);
            sendMsgClient.sendMsgNew(wsResp);
        } else {
            //供应商发送
            SendMessage sendMessage = ApplicationContextUil.getBean(SendMessage.class);
            List<MessageTemplateProvedResp> templates = buildFontdoTemplates(this.getTemplateIds(), msgDto.getSupplierTag(), msgDto.getOperator());
            //给蜂动消息填充${detailId}动态参数的值
            msgDto.setDetailId(0L);
            sendMessage.send(msgDto, questionType, templates, wsResp, null);
        }
    }

    private List<MessageTemplateProvedResp> buildFontdoTemplates(List<Long> templateIds, String SupplierTag, Integer Operator) {
        List<MessageTemplateProvedReq> templateProvedReqs = new ArrayList<>();
        MessageTemplateProvedListReq messageTemplateProvedListReq = new MessageTemplateProvedListReq();
        for (Long templateId : templateIds) {
            MessageTemplateProvedReq templateProvedReq = new MessageTemplateProvedReq();
            templateProvedReq.setTemplateId(templateId);
            templateProvedReq.setSupplierTag(SupplierTag);
            templateProvedReq.setOperator(Operator);
            templateProvedReqs.add(templateProvedReq);
        }
        messageTemplateProvedListReq.setTemplateProvedReqs(templateProvedReqs);
        MessageTemplateApi templateApi = ApplicationContextUil.getBean(MessageTemplateApi.class);
        List<MessageTemplateProvedResp> messageTemplateProvedResp = templateApi.getPlatformTemplateIds(messageTemplateProvedListReq);
        messageTemplateProvedResp.forEach(template -> template.setTemplateName(TemplateMessageTypeEnum.getRequestMessageTypeEnumByCode(template.getMessageType()).getName()));
        return messageTemplateProvedResp;
    }

    /**
     * 处理问题回复
     *
     * @return 返回路由ID
     */
    private String handleReply(MsgDto msgDto) {
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        //如果上行的类型是一个action或者上行位置
        MsgActionEnum action = MsgActionEnum.byCode(msgDto.getAction());
        Integer btnType = msgDto.getBtnType();
        //如果点击了按钮，只有命中回复按钮验证才往下执行
        if (MsgActionEnum.ACTION == action && ButtonType.REPLY.getCode() != btnType) {
            if (conditionList.stream()
                    .filter(verify -> verify.verifyType == 2)
                    .map(Verify::getButId)
                    .noneMatch(verifyReplyButtonId -> Objects.equals(verifyReplyButtonId, msgDto.getButId()))) {
                log.info("点击非回复按钮，未能命中提问节点验证条件，继续等待");
                throw new BizException("提问节点继续等待处理。");
            }
        }
        //
        boolean isGeoMsg = MsgUtils.isGeoMsg(action, msgDto.getMessageData());
        if (isGeoMsg) {
            if (conditionList.stream()
                    .filter(verify -> verify.verifyType == 3)
                    .noneMatch(verify -> Objects.equals(verify.verifyContent, "4"))) {
                return null;
            }
        }
        //将回复内容缓存起来，某些地方可以通过提问节点引用此回复内容(普通按钮不缓存)
        if (MsgActionEnum.REPLY == action || MsgActionEnum.ACTION == action || (MsgActionEnum.TEXT == action && !isGeoMsg)) {
            String questionNodeReplyKey = RedisUtil.getQuestionNodeReplyKey(msgDto.getConversationId(), this.getNodeId());
            redisTemplate.opsForValue().set(questionNodeReplyKey, msgDto.getMessageData(), Duration.ofMinutes(30));
        }
        log.info("提问节点验证条件列表：{}", conditionList);
        for (Verify verify : conditionList) {
            if (verify.verified(msgDto)) {
                log.info("提问节点验证条件命中，验证条件：{}", verify);
                return verify.id;
            }
        }
        log.info("提问节点未命中");
        return null;
    }


    /**
     * 提问节点的路由只能走回复验证或者节点兜底，如果两个都不匹配不上则结束
     *
     * @param msgDto 上行消息  -- 可不用
     */
    @Override
    void afterExec(MsgDto msgDto) {
        if (this.getNodeStatus() == NodeStatus.BLOCK) {
            log.info("节点等待问题回复,执行后方法跳过执行");
            return;
        }
        String conversationId = msgDto.getConversationId();
        RobotDto robot = RobotUtils.getLocalRobot(conversationId);
        Process process = RobotUtils.getCurrentProcess(robot);
        String relationId = null;
        try {
            relationId = handleReply(msgDto);
        } catch (BizException e) {
            this.setNodeStatus(NodeStatus.BLOCK);
            process.setProcessStatus(ProcessStatusEnum.BLOCK.getCode());
            process.getNodeMap().put(this.getNodeId(), this);
            RobotUtils.saveRobot(robot);
            return;
        }
        robot.setCurrentNodeId(null);
        this.setNodeStatus(NodeStatus.SUCCESS);
        String nextNodeId = null;
        if (relationId != null) {
            for (RouterInfo outRouter : this.getOutRouters()) {
                if (Objects.equals(outRouter.getRelationId(), relationId)) {
                    nextNodeId = outRouter.getTailNodeId();
                    break;
                }
            }
            if (nextNodeId == null)
                log.info("匹配成功的验证条件未配置出口路由");
        }
        if (nextNodeId == null) {
            log.info("尝试走节点兜底");
            if (CollectionUtils.isNotEmpty(this.getNodeLast())) {
                for (RouterInfo outRouter : this.getOutRouters()) {
                    String routerRelationId = outRouter.getRelationId();
                    if (StringUtils.equals(routerRelationId, this.getNodeLast().get(0).getString("id"))) {
                        nextNodeId = outRouter.getTailNodeId();
                        break;
                    }
                }
            } else {
                log.info("未配置节点兜底");
            }
        }
        robot.setCurrentNodeId(nextNodeId);
        process.getNodeMap().put(this.getNodeId(), this);
        RobotUtils.saveRobot(robot);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Verify {
        /**
         * 验证类型0关键字1正则2点击按钮3回复类型
         */
        private Integer verifyType;
        /**
         * 关键字类型验证用；分割
         */
        private String verifyContent;
        /**
         * 锚点id
         */
        private String id;
        private String butId;


        public boolean verified(MsgDto msg) {
            switch (verifyType) {
                case 2:
                    log.info("验证回复按钮id：{}，当前按钮id：{}", butId, msg.getButId());
                    return Objects.equals(butId, msg.getButId());
                case 3:
                    if (StringUtils.equals("0", verifyContent)) {
                        return MessageType.TEXT.getCode() == msg.getMessageType();
                    }
                    if (StringUtils.equals("1", verifyContent)) {
                        return MessageType.PICTURE.getCode() == msg.getMessageType();
                    }
                    if (StringUtils.equals("2", verifyContent)) {
                        return MessageType.AUDIO.getCode() == msg.getMessageType();
                    }
                    if (StringUtils.equals("3", verifyContent)) {
                        return MessageType.VIDEO.getCode() == msg.getMessageType();
                    }
                    if (StringUtils.equals("4", verifyContent)) {
                        return MessageType.LOCATION.getCode() == msg.getMessageType();
                    }
                case 0:
                    if (MessageType.TEXT.getCode() == msg.getMessageType()
                            && !StringUtils.equals(MsgActionEnum.ACTION.getCode(), msg.getAction())
                            && !SUBSCRIBE_BUTTON_TYPES.contains(ButtonType.getButtonType(msg.getBtnType()))) {
                        String[] split = verifyContent.split(";");
                        for (String s : split) {
                            if (msg.getMessageData().contains(s)) {
                                return true;
                            }
                        }
                        return false;
                    }
                case 1:
                    if (MessageType.TEXT.getCode() == msg.getMessageType()
                            && !StringUtils.equals(MsgActionEnum.ACTION.getCode(), msg.getAction())
                            && !SUBSCRIBE_BUTTON_TYPES.contains(ButtonType.getButtonType(msg.getBtnType()))) {
                        Pattern pattern = Pattern.compile(verifyContent);
                        boolean matches = pattern.matcher(msg.getMessageData()).matches();
                        if (!matches) {
                            matches = pattern.matcher(msg.getMessageData()).find();
                        }
                        return matches;
                    }
                default:
                    return false;
            }
        }
    }
}
