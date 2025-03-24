package com.citc.nce.im.broadcast.node;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.im.massSegment.entity.MassSegment;
import com.citc.nce.im.massSegment.mapper.MassSegmentMapper;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.entity.RobotPhoneUplinkResult;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import com.citc.nce.im.mapper.RobotPhoneUplinkResultMapper;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.citc.nce.im.broadcast.utils.BroadcastConstants.*;
import static com.citc.nce.im.broadcast.utils.BroadcastContactUtils.groupByPhoneSegment;

/**
 * 5G消息节点
 *
 * @author jcrenc
 * @since 2024/4/26 13:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Slf4j
public class FifthNode extends AbstractNode {


    @Override
    public List<String> filterSendPhones(BroadcastNode.FilterStrategy strategy) {
        List<String> phoneList = null;
        String btnUuid = strategy.getBtnUuid();
        Long nodeId = this.getId();
        String nowDateStr = DATE_FORMATTER.format(LocalDateTime.now());
        BroadcastNode.CrowdFilterCondition condition = strategy.getCondition();
        RobotPhoneUplinkResultMapper uplinkResultMapper = SpringUtils.getBean(RobotPhoneUplinkResultMapper.class);
        MsgRecordApi msgRecordApi = SpringUtils.getBean(MsgRecordApi.class);
        switch (condition) {
            case CLICK: {
                if (Objects.equals(ANY_BUTTON, btnUuid)) {
                    phoneList = uplinkResultMapper.selectAnyClick(nodeId, nowDateStr);
                } else {
                    LambdaQueryWrapper<RobotPhoneUplinkResult> uplinkWrapper = new LambdaQueryWrapper<>();
                    uplinkWrapper.eq(RobotPhoneUplinkResult::getBtnUuid, btnUuid);
                    uplinkWrapper.eq(RobotPhoneUplinkResult::getPlanDetailId, nodeId);
                    uplinkWrapper.apply("UNIX_TIMESTAMP(receipt_time) <= UNIX_TIMESTAMP('" + nowDateStr + "')");
                    List<RobotPhoneUplinkResult> robotPhoneResults = uplinkResultMapper.selectList(uplinkWrapper);
                    phoneList = robotPhoneResults.stream().map(RobotPhoneUplinkResult::getPhoneNum).collect(Collectors.toList());
                }
                break;
            }
            case NOT_CLICK: {
                phoneList = msgRecordApi.queryNotClick(nowDateStr, nodeId);
                break;
            }
            case UNREAD: {
                phoneList = msgRecordApi.queryUnReadFor5G(nodeId, nowDateStr);
                break;
            }
            case FAILED: {
                List<Integer> msgStatusList = Collections.singletonList(DeliveryEnum.FAILED.getCode());
                phoneList = msgRecordApi.queryPhoneList(nodeId, MsgTypeEnum.M5G_MSG.getCode(), msgStatusList, nowDateStr);
                break;
            }
            case DISPLAYED: {
                List<Integer> msgStatusList = Collections.singletonList(DeliveryEnum.DISPLAYED.getCode());
                phoneList = msgRecordApi.queryPhoneList(nodeId, MsgTypeEnum.M5G_MSG.getCode(), msgStatusList, nowDateStr);
                break;
            }
            case DELIVERED: {
                List<Integer> msgStatusList = Arrays.asList(DeliveryEnum.DELIVERED.getCode(), DeliveryEnum.DISPLAYED.getCode());
                phoneList = msgRecordApi.queryPhoneList(nodeId, MsgTypeEnum.M5G_MSG.getCode(), msgStatusList, nowDateStr);
                break;
            }
        }
        return phoneList;
    }

    @Override
    public List<String> send(List<String> sendPhones) {
        @SuppressWarnings("unchecked")
        List<AccountManagementResp> sendAccount = (List<AccountManagementResp>) this.getSendAccount();
        MessageTemplateResp template = (MessageTemplateResp) this.getTemplate();
        Map<AccountManagementResp, List<String>> groupByPhoneSegment = this.groupByPhoneSegment(sendAccount, sendPhones);
        FifthSendClient sendClient = SpringUtils.getBean(FifthSendClient.class);
        List<String> successPhoneList = new ArrayList<>();

        for (AccountManagementResp account : sendAccount) {
            List<String> operatorPhoneList = groupByPhoneSegment.get(account);
            if (CollectionUtils.isEmpty(operatorPhoneList)) {
                log.info("account:{} 没有找到对应的需要发送的手机号,跳过发送流程 ", account);
                continue;
            }
            AccountSendDetail detail = getAccountSendDetail().get(account.getChatbotAccount());
            if (detail == null) {
                log.info("账号:{} 无需发送", account.getAccountName());
                continue;
            }
            GroupNodeAccountDetailMapper nodeAccountDetailMapper = SpringUtils.getBean(GroupNodeAccountDetailMapper.class);
            GroupNodeAccountDetail groupNodeAccountDetail = nodeAccountDetailMapper.selectOne(Wrappers.<GroupNodeAccountDetail>lambdaUpdate()
                    .eq(GroupNodeAccountDetail::getNodeId, this.getId())
                    .in(GroupNodeAccountDetail::getAccountId, account.getChatbotAccount()));
            detail.setSendNumber(0);
            BroadcastPlanUtils.batchSend(operatorPhoneList, _5G_BATCH_SIZE, detail.getPreemptedNumber(), batchPhones -> {
                MessageData messageData = sendClient.sendWithMessageId(account, template, this.getPlanId(), this.getId(), batchPhones, groupNodeAccountDetail.getMessageId(), null, this.getMessageResourceType(), this.getPaymentType());
                this.setSendNumber(this.getSendNumber() + batchPhones.size()); //节点尝试发送数量
                if (messageData.getCode() == _5G_SENT_CODE_SUCCESS) {
                    detail.setSendNumber(detail.getSendNumber() + batchPhones.size());//节点账号发送成功数量
                    detail.getSendPhoneNumbers().addAll(batchPhones);//节点账号发送成功的手机号
                    successPhoneList.addAll(batchPhones);
                }
            });
        }
        if (CollectionUtils.isEmpty(successPhoneList))
            throw new GroupPlanExecuteException("无可用账号");
        return successPhoneList;
    }


    private Map<AccountManagementResp, List<String>> groupByPhoneSegment(List<AccountManagementResp> chatbotAccounts, List<String> sendPhones) {
        if (CollectionUtils.isEmpty(sendPhones)) {
            throw new GroupPlanExecuteException("余额或套餐不足");
        }

        log.info("开始分段, chatbotAccounts:{}, sendPhones: {}", chatbotAccounts, sendPhones);
        Map<AccountManagementResp, List<String>> groupByOperator = new HashMap<>();
        //硬核桃跳过号段筛选
        if (chatbotAccounts.size() == 1 && Objects.equals(chatbotAccounts.get(0).getAccountTypeCode(), CSPOperatorCodeEnum.DEFAULT.getCode())) {
            groupByOperator.put(chatbotAccounts.get(0), sendPhones);
        } else {
            Map<String, AccountManagementResp> operatorAccountMap = chatbotAccounts.stream()
                    .collect(Collectors.toMap(AccountManagementResp::getAccountType, Function.identity()));
            MassSegmentMapper segmentMapper = SpringUtils.getBean(MassSegmentMapper.class);
            Map<String, String> phoneSegmentMap = segmentMapper.selectList(null).stream()
                    .collect(Collectors.toMap(MassSegment::getPhoneSegment, MassSegment::getOperator, (v1, v2) -> v2));
            for (String phone : sendPhones) {
                String operator = phoneSegmentMap.get(phone.substring(0, 3));
                if (operator == null)
                    log.warn("未知号段:{}", phone);
                AccountManagementResp account = operatorAccountMap.get(operator);
                if (account == null) {
                    log.warn("手机号:{} 归属运营商:{}，没有可发送账号！", phone, operator);
                    continue;
                }
                if (!groupByOperator.containsKey(account)) {
                    groupByOperator.put(account, new ArrayList<>());
                }
                groupByOperator.get(account).add(phone);
            }
        }
        log.info("分段完毕, groupByOperator: {}", groupByOperator);
        return groupByOperator;
    }

}
