package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.csp.csp.utils.CspUtils;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.messageplan.entity.SmsPlan;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.service.ISmsPlanService;
import com.citc.nce.auth.prepayment.service.IPrepaymentService;
import com.citc.nce.auth.prepayment.vo.MessagePlanDetailDto;
import com.citc.nce.auth.prepayment.vo.MessagePlanInfoDto;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 短信预支付实现类
 *
 * @author jiancheng
 */
@Service("smsPrepaymentServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class SmsPrepaymentServiceImpl implements IPrepaymentService {
    private final CspSmsAccountDao cspSmsAccountDao;
    private final ISmsPlanService smsPlanService;
    private final ObjectMapper objectMapper;

    @Override
    public void verifyAccount(String customerId, String account) {
        boolean exists = cspSmsAccountDao.exists(
                Wrappers.<CspSmsAccountDo>lambdaQuery()
                        .eq(CspSmsAccountDo::getCustomerId, customerId)
                        .eq(CspSmsAccountDo::getAccountId, account)
        );
        if (!exists)
            throw new BizException("该短信账号不存在:" + account);
    }

    @Override
    public MessagePlanInfoDto getPlanInfo(String customerId, String planId) {
        String cspId = CspUtils.convertCspId(customerId);
        SmsPlan messagePlan = smsPlanService.lambdaQuery()
                .eq(SmsPlan::getCreator, cspId)
                .eq(SmsPlan::getPlanId, planId)
                .eq(SmsPlan::getStatus, MessagePlanStatus.ON_SHELVES)
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
        MessagePlanDetailDto detailDto = new MessagePlanDetailDto()
                .setMsgType(MsgTypeEnum.SHORT_MSG)
                .setLimit(messagePlan.getNumber());
        infoDto.setDetailList(Collections.singletonList(detailDto));
        return infoDto;
    }
}
