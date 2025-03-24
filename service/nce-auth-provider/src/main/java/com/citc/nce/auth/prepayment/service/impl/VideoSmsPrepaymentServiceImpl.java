package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.csp.csp.utils.CspUtils;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.messageplan.entity.VideoSmsPlan;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.service.IVideoSmsPlanService;
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
 * 视频短信预支付实现类
 *
 * @author jiancheng
 */
@Service("videoSmsPrepaymentServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class VideoSmsPrepaymentServiceImpl implements IPrepaymentService {
    private final CspVideoSmsAccountDao accountDao;
    private final IVideoSmsPlanService videoSmsPlanService;
    private final ObjectMapper objectMapper;

    @Override
    public void verifyAccount(String customerId, String account) {
        boolean exists = accountDao.exists(
                Wrappers.<CspVideoSmsAccountDo>lambdaQuery()
                        .eq(CspVideoSmsAccountDo::getCustomerId, customerId)
                        .eq(CspVideoSmsAccountDo::getAccountId, account)
        );
        if (!exists)
            throw new BizException("该视频短信账号不存在:" + account);
    }

    @Override
    public MessagePlanInfoDto getPlanInfo(String customerId, String planId) {
        String cspId = CspUtils.convertCspId(customerId);
        VideoSmsPlan messagePlan = videoSmsPlanService.lambdaQuery()
                .eq(VideoSmsPlan::getCreator, cspId)
                .eq(VideoSmsPlan::getPlanId, planId)
                .eq(VideoSmsPlan::getStatus, MessagePlanStatus.ON_SHELVES)
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
                .setMsgType(MsgTypeEnum.MEDIA_MSG)
                .setLimit(messagePlan.getNumber());
        infoDto.setDetailList(Collections.singletonList(detailDto));
        return infoDto;
    }
}
