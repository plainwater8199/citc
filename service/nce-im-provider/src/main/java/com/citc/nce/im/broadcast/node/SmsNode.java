package com.citc.nce.im.broadcast.node;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.client.SmsSendClient;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.robot.vo.SmsMessageResponse;
import com.citc.nce.robot.vo.TemplateSmsIdAndMobile;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.citc.nce.im.broadcast.utils.BroadcastConstants.DATE_FORMATTER;

/**
 * 短信节点
 *
 * @author jcrenc
 * @since 2024/4/26 13:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsNode extends AbstractNode {

    @Override
    public List<String> filterSendPhones(BroadcastNode.FilterStrategy filterStrategy) {
        List<Integer> msgStatusList = Collections.singletonList(
                filterStrategy.getCondition() == BroadcastNode.CrowdFilterCondition.FAILED
                        ? DeliveryEnum.FAILED.getCode()
                        : DeliveryEnum.DELIVERED.getCode()
        );
        MsgRecordApi msgRecordApi = SpringUtils.getBean(MsgRecordApi.class);
        String nowDateStr = DATE_FORMATTER.format(LocalDateTime.now());
        return msgRecordApi.queryPhoneList(this.getId(), MsgTypeEnum.SHORT_MSG.getCode(), msgStatusList, nowDateStr);
    }

    @Override
    public List<String> send(List<String> sendPhones) {
        List<String> successPhones = new ArrayList<>();
        final int batchSize = 500;
        List<CspSmsAccountDetailResp> accounts = (List<CspSmsAccountDetailResp>) this.getSendAccount();
        SmsTemplateDetailVo template = (SmsTemplateDetailVo) this.getTemplate();
        SmsSendClient sendClient = SpringUtils.getBean(SmsSendClient.class);
        for (CspSmsAccountDetailResp account : accounts) {
            AccountSendDetail detail = this.getAccountSendDetail().get(account.getAccountId());
            this.setSendNumber(0);
            detail.setSendNumber(0);
            GroupNodeAccountDetailMapper nodeAccountDetailMapper = SpringUtils.getBean(GroupNodeAccountDetailMapper.class);
            GroupNodeAccountDetail groupNodeAccountDetail = nodeAccountDetailMapper.selectOne(Wrappers.<GroupNodeAccountDetail>lambdaUpdate()
                    .eq(GroupNodeAccountDetail::getNodeId, this.getId())
                    .in(GroupNodeAccountDetail::getAccountId, account.getAccountId()));

            BroadcastPlanUtils.batchSend(sendPhones, batchSize, detail.getPreemptedNumber(), batchPhones -> {
                List<TemplateSmsIdAndMobile> mobiles = batchPhones.stream().map(TemplateSmsIdAndMobile::new).collect(Collectors.toList());
                SmsMessageResponse response = sendClient.send(account, template, null, this.getPlanId(), this.getId(), groupNodeAccountDetail.getMessageId(), mobiles, this.getMessageResourceType(), this.getPaymentType());
                this.setSendNumber(this.getSendNumber() + batchPhones.size()); //节点发送数量
                if (response.isSuccess()) {
                    detail.setSendNumber(detail.getSendNumber() + batchPhones.size());//节点账号发送成功的数量
                    successPhones.addAll(batchPhones);
                    detail.getSendPhoneNumbers().addAll(batchPhones);
                }
//            detail.setSendNumber(detail.getSendNumber() + batchPhones.size());//节点账号发送数量
//            successPhones.addAll(batchPhones);
            });
        }

        return successPhones;
    }
}
