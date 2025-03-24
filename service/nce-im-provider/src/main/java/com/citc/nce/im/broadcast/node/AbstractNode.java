package com.citc.nce.im.broadcast.node;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.contactlist.vo.ContactListResp;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.enums.MessageResourceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.citc.nce.im.broadcast.utils.BroadcastPlanUtils.EXECUTABLE_NODE_STATUS;
import static com.citc.nce.msgenum.SendStatus.EXPIRED;
import static com.citc.nce.msgenum.SendStatus.TO_BE_SEND;

/**
 * @author jcrenc
 * @since 2024/4/26 13:41
 */
@Getter
@Setter
@ToString(exclude = "parent")
@Slf4j
@Accessors(chain = true)
public abstract class AbstractNode {
    private Long id;
    private Long planId;
    private MsgTypeEnum type;
    private MsgSubTypeEnum subType;
    private String customerId;
    private SendStatus status;
    private String failMsg;
    private Integer maxSendNumber; //预估的节点最大发送量
    private Integer sendNumber; //实际尝试发送数量
    private Integer unknownNumber;
    private Integer failNumber;
    private String leftPoint;
    private String parentPoint;
    private Long templateId;
    private Boolean isTime;
    private LocalDateTime sendTime;
    private Long groupId;
    private List<BroadcastNode.FilterStrategy> filterRouters;
    private List<AbstractNode> children;
    @JsonIgnore
    private transient AbstractNode parent;
    private List<ContactListResp> contactList;
    private Object sendAccount;
    private Object template;
    private Map<String, AccountSendDetail> accountSendDetail;
    //是否允许回落( 0 不允许, 1允许)
    private int allowFallback;
    //回落类型 1:短信 ,  2:5g阅信
    private Integer fallbackType;
    //回落短信内容(allowFallback = 1时, 必填)
    private String fallbackSmsContent;
    //允许回落阅读信模板id(fallbackType = 2时, 必填)
    private Long fallbackReadingLetterTemplateId;
    //选择需要发送的运营商,英文逗号拼接 (1,2,3)
    private String selectedCarriers;

    private List<String> realSendPhone;

    private Integer isFastGroup;

    private MessageResourceType messageResourceType;

    /*节点扣费类型 冗余plan的字段*/
    private PaymentTypeEnum paymentType;

    /**
     * 根据人群筛选策略筛选获取当前节点筛选后的手机号列表
     *
     * @param filterStrategy 筛选侧罗
     * @return 筛选结果
     */
    public abstract List<String> filterSendPhones(BroadcastNode.FilterStrategy filterStrategy);

    /**
     * 调用平台发送消息
     *
     * @return 发送状态为成功的手机号列表
     */
    public abstract List<String> send(List<String> sendPhones);

    //检查节点是否过期或者是否可执行，过期不扣费，可执行且不过期才扣费
    public boolean nodeIsNeedDeduct() {
        int compared = -1;
        if (isTime) {
            LocalDateTime timingTime = sendTime.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime nowTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            compared = timingTime.compareTo(nowTime);
            log.info("定时执行时间:{},当前时间{}", timingTime, nowTime);
        } else {
            compared = 1;
        }
        return compared >= 0 && EXECUTABLE_NODE_STATUS.contains(this.status);

    }

    public boolean isExpired() {
        if (isTime) {
            LocalDateTime timingTime = sendTime.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime nowTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            int compared = timingTime.compareTo(nowTime);
            return compared < 0;
        }
        return false;

    }

    public void timingCheck() {
        if (isTime) {
            LocalDateTime timingTime = sendTime.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime nowTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            int compared = timingTime.compareTo(nowTime);
            log.info("定时执行时间:{},当前时间{}", timingTime, nowTime);
            SendStatus state = compared < 0 ? EXPIRED : TO_BE_SEND;
            if (compared < 0) {
                log.warn("节点已过期，取消执行");
                throw new GroupPlanExecuteException(state, "节点已过期");
            } else if (compared > 0) {
                log.warn("未到设置时间，继续等待");
                throw new GroupPlanExecuteException(TO_BE_SEND, "未到设置时间，继续等待");
            } else {
                log.info("定时节点时间匹配成功");
            }
        }
    }

    public void timingCheckIfExpired() {
        if (isTime) {
            LocalDateTime timingTime = sendTime.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime nowTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            int compared = timingTime.compareTo(nowTime);
            log.info("定时执行时间:{},当前时间{}", timingTime, nowTime);
            SendStatus state = compared < 0 ? EXPIRED : TO_BE_SEND;
            if (compared < 0) {
                log.warn("节点已过期，取消执行");
                throw new GroupPlanExecuteException(state, "节点已过期");
            }
        }
    }


    public AbstractNode syncDb() {
        log.info("同步节点信息 id:{} status:{} sendNumber:{} unknownNumber:{} failNumber:{}", id, status, sendNumber, unknownNumber, failNumber);
        RobotGroupSendPlansDetailService nodeService = SpringUtils.getBean(RobotGroupSendPlansDetailService.class);
        nodeService.lambdaUpdate()
                .eq(BaseDo::getId, id)
                .set(RobotGroupSendPlansDetailDo::getPlanStatus, status.getCode())
                .set(StringUtils.hasText(failMsg), RobotGroupSendPlansDetailDo::getFailedMsg, failMsg)
                .set(sendTime != null, RobotGroupSendPlansDetailDo::getSendTime, sendTime)
                .set(sendNumber != null && sendNumber > 0, RobotGroupSendPlansDetailDo::getSendAmount, sendNumber)
                .setSql(unknownNumber != null && unknownNumber > 0, "unknow_amount = unknow_amount + {0}", unknownNumber)
                .setSql(failNumber != null && failNumber > 0, "fail_amount = fail_amount + {0}", failNumber)
                .update(new RobotGroupSendPlansDetailDo());
        return this;
    }

    public void syncAccountSendDetail() {
        GroupNodeAccountDetailMapper nodeAccountDetailMapper = SpringUtils.getBean(GroupNodeAccountDetailMapper.class);
        for (Map.Entry<String, AbstractNode.AccountSendDetail> entry : getAccountSendDetail().entrySet()) {
            String accountId = entry.getKey();
            AbstractNode.AccountSendDetail detail = entry.getValue();
            nodeAccountDetailMapper.update(null, Wrappers.<GroupNodeAccountDetail>lambdaUpdate()
                    .eq(GroupNodeAccountDetail::getNodeId, id)
                    .eq(GroupNodeAccountDetail::getAccountId, accountId)
                    .set(GroupNodeAccountDetail::getMaxSend, getMaxSendNumber()) //最大发送量
                    .set(GroupNodeAccountDetail::getPreemptedNumber, detail.preemptedNumber)
                    .set(GroupNodeAccountDetail::getActualSendNumber, detail.sendNumber)
                    .set(GroupNodeAccountDetail::getReturnNumber, detail.returnNumber)
            );
            log.info("同步节点发送账号明细 nodeId:{} accountId:{} maxSend:{} preempted:{} actualSend:{} return:{}", id, accountId, getMaxSendNumber(), detail.preemptedNumber, detail.sendNumber, detail.returnNumber);
        }
    }

    /**
     * 账号发送详情
     */
    @Data
    public static class AccountSendDetail {
        //尝试发送数量
        private Integer preemptedNumber;
        //发送成功数量
        private Integer sendNumber;
        private Integer returnNumber;
        //发送成功的短信
        private List<String> sendPhoneNumbers = new ArrayList<>();
        //发送条数
        private Integer chargeNum;
    }

}
