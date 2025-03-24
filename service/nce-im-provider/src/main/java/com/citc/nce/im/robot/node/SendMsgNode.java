package com.citc.nce.im.robot.node;

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
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.entity.WsResp;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 消息节点
 *
 * @author jcrenc
 * @since 2023/7/14 10:36
 */
@Data
public class SendMsgNode extends Node {
    //消息发送策略
    private Integer sendType;

    //配置的消息
    private List<JSONObject> msgBody;

    //自定义按钮
    private List<JSONObject> buttons;
    /**
     * 模板ids ,供应商发送时
     */
    private List<Long> templateIds;

    @Override
    void beforeExec(MsgDto msgDto) {
    }

    /**
     * 根据消息配置发送消息
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        SendMsgClient msgClient = ApplicationContextUil.getBean(SendMsgClient.class);
        WsResp msg = new WsResp();
        msg.setConversationId(msgDto.getConversationId());
        msg.setContributionId(msgDto.getContributionId());
        msg.setMsgType(msgDto.getMessageType());
        msg.setUserId(msgDto.getCustomerId());
        Map<String, Object> msgMap = new HashMap<>();

        switch (sendType) {
            //全部发送
            case 0:
                msgMap.put("contentBody", msgBody);
                break;
            //随机发送一条
            case 1:
                msgMap.put("contentBody", Collections.singletonList(msgBody.get(new Random().nextInt(msgBody.size()))));
                break;
        }
        msgMap.put("buttonList", buttons);
        msg.setBody(msgMap);
        msg.setFalg(Integer.parseInt(msgDto.getMessageSource()));
        msg.setPhone(msgDto.getPhone());
        msg.setChatbotAccount(msgDto.getChatbotAccount());
        msg.setChannelType(msgDto.getAccountType());
        //如果是运营商直连  或是 ws debug
        if (SupplierConstant.OWNER.equals(msgDto.getSupplierTag())||
                (Integer.parseInt(msgDto.getMessageSource())==0)) {
            msgClient.sendMsgNew(msg);
        }else {
            //如果是供应商 , 如果是供应商直连
            SendMessage sendMessage = ApplicationContextUil.getBean(SendMessage.class);
            List<MessageTemplateProvedResp> templates = buildFontdoTemplates(this.getTemplateIds(),msgDto.getSupplierTag(), msgDto.getOperator());
            //给蜂动消息填充${detailId}动态参数的值
            msgDto.setDetailId(0L);
            sendMessage.send(msgDto, sendType, templates, msg, null);
        }
    }

    private List<MessageTemplateProvedResp> buildFontdoTemplates(List<Long> templateIds, String SupplierTag, Integer Operator) {
        MessageTemplateProvedListReq messageTemplateProvedListReq = new MessageTemplateProvedListReq();
        ArrayList<MessageTemplateProvedReq> templateProvedReqs = new ArrayList<>();
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
     * 处理节点跳转
     *
     * @param msgDto 上行消息
     */
    @Override
    void afterExec(MsgDto msgDto) {
        RobotDto robot = RobotUtils.getLocalRobot(msgDto.getConversationId());
        List<RouterInfo> outRouters = this.getOutRouters();
        if (!CollectionUtils.isEmpty(outRouters)) {
            if (outRouters.size() > 1)
                throw new BizException(500, "消息节点至多只能有一个出站路由，节点ID:" + this.getNodeId());
            RouterInfo routerInfo = outRouters.get(0);
            String nextNodeId = routerInfo.getTailNodeId();
            robot.setCurrentNodeId(nextNodeId);
        } else {
            robot.setCurrentNodeId(null);
        }
        this.setNodeStatus(NodeStatus.SUCCESS);
        Process process = RobotUtils.getCurrentProcess(robot);
        process.getNodeMap().put(this.getNodeId(), this);
        RobotUtils.saveRobot(robot);
    }

}
