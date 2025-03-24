package com.citc.nce.im.broadcast.fastgroupmessage.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.req.FastGroupMessageQueryReq;
import com.citc.nce.robot.req.FastGroupMessageReq;
import com.citc.nce.robot.res.FastGroupMessageItem;
import com.citc.nce.robot.res.FastGroupMessageSelectAllResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author jcrenc
 * @since 2024/7/1 10:15
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class FastGroupMessageController implements FastGroupMessageApi {
    private final FastGroupMessageService fastGroupMessageService;

    @Override
    public void createAndStartFastGroupMessage(FastGroupMessageReq req) {
        fastGroupMessageService.createAndStartFastGroupMessage(req);
    }

    @Override
    public void handleSendStatistic(String messageId, Integer updatedRecordNumber, int stateCode) {
        fastGroupMessageService.handleSendStatistic(messageId, updatedRecordNumber, DeliveryEnum.getValue(stateCode));
    }

    @Override
    public void updateFastGroupMessage(FastGroupMessageReq req) {
        fastGroupMessageService.updateFastGroupMessage(req);
    }

    @Override
    public PageResult<FastGroupMessageItem> queryList(FastGroupMessageQueryReq req) {
        return fastGroupMessageService.queryList(req);
    }

    @Override
    public FastGroupMessageSelectAllResp selectAll() {
        return fastGroupMessageService.selectAll();
    }

    @Override
    public void updateStatus(Long planId, String failedReason) {
        fastGroupMessageService.updateStatus(planId, failedReason);
    }

    @Override
    public Long createStatistics(String customerId, Date startTime, Date endTime) {
        return fastGroupMessageService.createStatistics(customerId, startTime, endTime);
    }

    @Override
    public FastGroupMessageItem findByPlanId(Long planId) {
        return fastGroupMessageService.findByPlanId(planId);
    }
}
