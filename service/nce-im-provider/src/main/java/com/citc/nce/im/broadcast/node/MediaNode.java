package com.citc.nce.im.broadcast.node;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.client.MediaSendClient;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.citc.nce.im.broadcast.utils.BroadcastConstants.DATE_FORMATTER;
import static com.citc.nce.im.broadcast.utils.BroadcastConstants.MEDIA_BATCH_SIZE;

/**
 * 视频短信节点
 *
 * @author jcrenc
 * @since 2024/4/26 13:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class MediaNode extends AbstractNode {

    @Resource
    private FastGroupMessageApi fastGroupMessageApi;

    @Override
    public List<String> filterSendPhones(BroadcastNode.FilterStrategy filterStrategy) {
        List<Integer> msgStatusList = Collections.singletonList(
                filterStrategy.getCondition() == BroadcastNode.CrowdFilterCondition.FAILED
                        ? DeliveryEnum.FAILED.getCode()
                        : DeliveryEnum.DELIVERED.getCode()
        );
        MsgRecordApi msgRecordApi = SpringUtils.getBean(MsgRecordApi.class);
        String nowDateStr = DATE_FORMATTER.format(LocalDateTime.now());
        return msgRecordApi.queryPhoneList(getId(), MsgTypeEnum.MEDIA_MSG.getCode(), msgStatusList, nowDateStr);
    }

    @Override
    public List<String> send(List<String> sendPhones) {
        List<String> successPhones = new ArrayList<>();
        List<CspVideoSmsAccountDetailResp> sendAccounts = (List<CspVideoSmsAccountDetailResp>) this.getSendAccount();
        MediaSmsTemplatePreviewVo template = (MediaSmsTemplatePreviewVo) this.getTemplate();
        MediaSendClient mediaSendClient = SpringUtils.getBean(MediaSendClient.class);
        for (CspVideoSmsAccountDetailResp sendAccount : sendAccounts) {
            AccountSendDetail detail = this.getAccountSendDetail().get(sendAccount.getAccountId());
            this.setSendNumber(0);
            detail.setSendNumber(0);
            GroupNodeAccountDetailMapper nodeAccountDetailMapper = SpringUtils.getBean(GroupNodeAccountDetailMapper.class);
            GroupNodeAccountDetail groupNodeAccountDetail = nodeAccountDetailMapper.selectOne(Wrappers.<GroupNodeAccountDetail>lambdaUpdate()
                    .eq(GroupNodeAccountDetail::getNodeId, this.getId())
                    .in(GroupNodeAccountDetail::getAccountId, sendAccount.getAccountId()));
            BroadcastPlanUtils.batchSend(sendPhones, MEDIA_BATCH_SIZE, detail.getPreemptedNumber(), batchPhones -> {
                RichMediaResultArray resultArray = mediaSendClient.send(template, sendAccount, this.getPlanId(), this.getId(), batchPhones, groupNodeAccountDetail.getMessageId(), null, null, this.getMessageResourceType(), this.getPaymentType());
                boolean success = Boolean.TRUE.equals(resultArray.getSuccess());
                this.setSendNumber(this.getSendNumber() + batchPhones.size()); //节点尝试发送数量
                if (success) {
                    detail.setSendNumber(detail.getSendNumber() + batchPhones.size());//节点账号发送成功数量
                    successPhones.addAll(batchPhones);
                    detail.getSendPhoneNumbers().addAll(batchPhones);
                } else {
                    if (MessageResourceType.FAST_GROUP.equals(this.getMessageResourceType())) {//如果是快捷群发，并且如果发送失败，则返回发送失败原因
                        fastGroupMessageApi.updateStatus(this.getPlanId(), "发送消息网关失败");
                    }
                }
            });
        }

        return successPhones;
    }
}
