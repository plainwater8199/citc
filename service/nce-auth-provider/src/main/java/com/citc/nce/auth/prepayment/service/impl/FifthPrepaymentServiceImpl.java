package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementMapper;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.csp.csp.utils.CspUtils;
import com.citc.nce.auth.messageplan.entity.FifthMessagePlan;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.service.IFifthMessagePlanService;
import com.citc.nce.auth.prepayment.service.IPrepaymentService;
import com.citc.nce.auth.prepayment.vo.MessagePlanDetailDto;
import com.citc.nce.auth.prepayment.vo.MessagePlanInfoDto;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 5g消息预支付实现类
 *
 * @author jiancheng
 */
@Service("fifthPrepaymentServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class FifthPrepaymentServiceImpl implements IPrepaymentService {
    private final AccountManagementMapper accountDao;
    private final IFifthMessagePlanService fifthMessagePlanService;
    private final ObjectMapper objectMapper;


    @Override
    public void verifyAccount(String customerId, String chatbotAccount) {
        boolean exists = accountDao.exists(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .eq(AccountManagementDo::getCustomerId, customerId)
                        .eq(AccountManagementDo::getChatbotAccount, chatbotAccount)
        );
        if (!exists)
            throw new BizException("该5G消息账号不存在:" + chatbotAccount);
    }

    @Override
    public MessagePlanInfoDto getPlanInfo(String customerId, String planId) {
        String cspId = CspUtils.convertCspId(customerId);
        FifthMessagePlan messagePlan = fifthMessagePlanService.lambdaQuery()
                .eq(FifthMessagePlan::getCreator, cspId)
                .eq(FifthMessagePlan::getPlanId, planId)
                .eq(FifthMessagePlan::getStatus, MessagePlanStatus.ON_SHELVES)
                .oneOpt()
                .orElseThrow(() -> new BizException("套餐不存在:" + planId));
        MessagePlanInfoDto infoDto = new MessagePlanInfoDto();
        infoDto.setAmount(messagePlan.getAmount());
        try {
            infoDto.setPlanDetail(objectMapper.writeValueAsString(messagePlan));
        } catch (JsonProcessingException e) {
            log.warn("序列化套餐失败:{}", e.getMessage());
            throw new BizException("序列化套餐快照失败");
        }
        List<MessagePlanDetailDto> detailList = new ArrayList<>();
        if (messagePlan.getTextMessageNumber() != null && messagePlan.getTextMessageNumber() > 0) {
            MessagePlanDetailDto detailDto = new MessagePlanDetailDto();
            detailDto.setMsgType(MsgTypeEnum.M5G_MSG);
            detailDto.setMsgSubType(MsgSubTypeEnum.TEXT);
            detailDto.setLimit(messagePlan.getTextMessageNumber());
            detailList.add(detailDto);
        }
        if (messagePlan.getRichMessageNumber() != null && messagePlan.getRichMessageNumber() > 0) {
            MessagePlanDetailDto detailDto = new MessagePlanDetailDto();
            detailDto.setMsgType(MsgTypeEnum.M5G_MSG);
            detailDto.setMsgSubType(MsgSubTypeEnum.RICH);
            detailDto.setLimit(messagePlan.getRichMessageNumber());
            detailList.add(detailDto);
        }
        if (messagePlan.getConversionNumber() != null && messagePlan.getConversionNumber() > 0) {
            MessagePlanDetailDto detailDto = new MessagePlanDetailDto();
            detailDto.setMsgType(MsgTypeEnum.M5G_MSG);
            detailDto.setMsgSubType(MsgSubTypeEnum.CONVERSATION);
            detailDto.setLimit(messagePlan.getConversionNumber());
            detailList.add(detailDto);
        }
        infoDto.setDetailList(detailList);
        return infoDto;
    }
}
