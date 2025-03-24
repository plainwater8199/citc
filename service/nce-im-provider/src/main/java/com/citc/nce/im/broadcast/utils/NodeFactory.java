package com.citc.nce.im.broadcast.utils;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.fastgroupmessage.entity.FastGroupMessage;
import com.citc.nce.im.broadcast.node.*;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.service.impl.GroupNodeAccountDetailService;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.robot.enums.FastGroupMessageStatus;
import com.citc.nce.robot.enums.MessageResourceType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.citc.nce.im.broadcast.utils.BroadcastAccountUtils.*;
import static com.citc.nce.im.broadcast.utils.BroadcastTemplateUtils.*;

/**
 * @author jcrenc
 * @since 2024/4/26 15:00
 */
public class NodeFactory {

    /**
     * 创建节点的工厂方法
     */
    public static AbstractNode createNode(RobotGroupSendPlansDetailDo detailDo, BroadcastNode broadcastNode) {
        MsgTypeEnum type = MsgTypeEnum.convertNodeType2MsgType(broadcastNode.getType());
        AbstractNode node;
        switch (type) {
            case M5G_MSG:
                node = new FifthNode();
                node.setTemplateId(detailDo.getTemplateId());
                MessageTemplateApi templateApi = SpringUtils.getBean(MessageTemplateApi.class);
                Integer messageType = templateApi.queryMessageTypeByTemplateId(node.getTemplateId());
                Assert.notNull(messageType, "5G消息模板: " + node.getTemplateId() + " 消息类型为空");
                node.setSubType(MsgSubTypeEnum.convertTemplateType2MsgSubType(messageType));
                break;
            case MEDIA_MSG:
                node = new MediaNode();
                node.setTemplateId(detailDo.getMediaTemplateId());
                break;
            case SHORT_MSG:
                node = new SmsNode();
                node.setTemplateId(detailDo.getShortMsgTemplateId());
                break;
            default:
                throw new IllegalArgumentException("Unsupported node type: " + type);
        }
        node.setType(type);
        node.setMessageResourceType(MessageResourceType.GROUP);
        // 此处可以配置所有节点共有的属性（通用配置已提取出来）
        configureCommonNodeProperties(node, detailDo, broadcastNode);
        return node;
    }

    /**
     * 设置节点公共属性
     */
    private static void configureCommonNodeProperties(AbstractNode node, RobotGroupSendPlansDetailDo detailNode, BroadcastNode broadcastNode) {
        BroadcastNode.NodeParam nodeParam = broadcastNode.getParams();
        node.setId(detailNode.getId());
        node.setPlanId(detailNode.getPlanId());
        node.setCustomerId(detailNode.getCreator());
        node.setStatus(SendStatus.getValue(detailNode.getPlanStatus()));
        node.setLeftPoint(broadcastNode.getLeftPoint());
        node.setParentPoint(broadcastNode.getParentPoint());
        node.setIsTime(nodeParam.getIsTime());
        node.setSendTime(nodeParam.getSendTime());
        node.setFilterRouters(nodeParam.getScreen());
        //设置蜂动5G消息发送回落等信息
        node.setAllowFallback(nodeParam.getAllowFallback());
        //避免空指针问题
        node.setFallbackSmsContent(nodeParam.getFallbackSmsContent() == null ? null : nodeParam.getFallbackSmsContent().getString("value"));
        node.setFallbackReadingLetterTemplateId(nodeParam.getFallbackReadingLetterTemplateId());
        node.setFallbackType(nodeParam.getFallbackType());
        node.setSelectedCarriers(nodeParam.getSelectedCarriers());

        if (StringUtils.hasText(nodeParam.getPlanGroup())) {
            node.setGroupId(Long.parseLong(nodeParam.getPlanGroup()));
        }
    }

    public static AbstractNode createNodeForFastGroup(FastGroupMessage fastGroupMessage, Integer phoneCount) {
        AbstractNode node;
        Long id = fastGroupMessage.getId();
        switch (fastGroupMessage.getType()) {
            case M5G_MSG:
                node = new FifthNode();
                List<AccountManagementResp> chatbots = getCheckedFifthGenAccount(Arrays.asList(fastGroupMessage.getAccounts().split(",")));
                MessageTemplateResp template = getCheckedFifthGenTemplate(fastGroupMessage.getTemplateId(), chatbots, (FifthNode) node);
                node.setSendAccount(chatbots);
                node.setTemplate(template);
                node.setMaxSendNumber(phoneCount * chatbots.size());
                node.setType(fastGroupMessage.getType());
                node.setSubType(MsgSubTypeEnum.convertTemplateType2MsgSubType(template.getMessageType()));
                node.setTemplateId(fastGroupMessage.getTemplateId());
                node.setGroupId(fastGroupMessage.getGroupId());
                node.setIsFastGroup(1);
                node.setId(id);
                node.setPlanId(id);
                break;
            case MEDIA_MSG:
                node = new MediaNode();
                List<CspVideoSmsAccountDetailResp> mediaSmsAccount = getCheckedMediaSmsAccounts(Arrays.asList(fastGroupMessage.getAccounts().split(",")));
                MediaSmsTemplatePreviewVo mediaTemplate = getCheckedMediaTemplate(fastGroupMessage.getTemplateId());
                node.setSendAccount(mediaSmsAccount);
                node.setTemplate(mediaTemplate);
                node.setMaxSendNumber(phoneCount * mediaSmsAccount.size());
                node.setType(fastGroupMessage.getType());
                node.setTemplateId(fastGroupMessage.getTemplateId());
                node.setGroupId(fastGroupMessage.getGroupId());
                node.setIsFastGroup(1);
                node.setId(id);
                node.setPlanId(id);
                node.setSelectedCarriers("1,2,3");
                break;
            case SHORT_MSG:
                node = new SmsNode();
                List<CspSmsAccountDetailResp> smsAccount = getCheckedSmsAccounts(Arrays.asList(fastGroupMessage.getAccounts().split(",")));
                SmsTemplateDetailVo smsTemplate = getCheckedSmsTemplate(fastGroupMessage.getTemplateId());
                node.setSendAccount(smsAccount);
                node.setTemplate(smsTemplate);
                node.setMaxSendNumber(phoneCount * smsAccount.size());
                node.setType(fastGroupMessage.getType());
                node.setTemplateId(fastGroupMessage.getTemplateId());
                node.setGroupId(fastGroupMessage.getGroupId());
                node.setIsFastGroup(1);
                node.setId(id);
                node.setPlanId(id);
                node.setSelectedCarriers("1,2,3");
                break;
            default:
                throw new IllegalArgumentException("Unsupported message type: " + fastGroupMessage.getType());
        }
        node.setMessageResourceType(MessageResourceType.FAST_GROUP);
        node.setCustomerId(fastGroupMessage.getCustomerId());
        Map<String, AbstractNode.AccountSendDetail> sendDetail = queryDeductedDetail(id);
        node.setAccountSendDetail(sendDetail);
        node.setSendNumber(calculateSendNumber(sendDetail));
        node.setPaymentType(PaymentTypeEnum.ofCode(fastGroupMessage.getPaymentType()));
        node.setStatus(convertFastGroupMessageStatusToSendStatus(fastGroupMessage.getStatus()));
        return node;
    }

    private static Map<String, AbstractNode.AccountSendDetail> queryDeductedDetail(long id) {
        GroupNodeAccountDetailService groupNodeAccountDetailService = SpringUtils.getBean(GroupNodeAccountDetailService.class);
        return groupNodeAccountDetailService.lambdaQuery()
                .eq(GroupNodeAccountDetail::getNodeId, id)
                .list().stream()
                .collect(Collectors.toMap(
                        GroupNodeAccountDetail::getAccountId,
                        detail -> new AbstractNode.AccountSendDetail()
                                .setPreemptedNumber(detail.getPreemptedNumber())
                                .setSendNumber(detail.getActualSendNumber())
                                .setReturnNumber(detail.getReturnNumber())
                ));
    }

    private static SendStatus convertFastGroupMessageStatusToSendStatus(FastGroupMessageStatus status) {
        switch (status) {
            case WAIT:
                return SendStatus.TO_BE_SEND;
            case SENDING:
                return SendStatus.SENDING;
            case SUCCESS:
                return SendStatus.SEND_SUCCESS;
            case FAIL:
                return SendStatus.SEND_FAIL;
            default:
                throw new IllegalArgumentException("Unknown fast group message status: " + status);
        }
    }

    private static int calculateSendNumber(Map<String, AbstractNode.AccountSendDetail> sendDetail) {
        return sendDetail.values().stream()
                .map(AbstractNode.AccountSendDetail::getSendNumber)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }
}
