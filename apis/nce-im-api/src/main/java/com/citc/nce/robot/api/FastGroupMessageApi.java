package com.citc.nce.robot.api;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.req.FastGroupMessageQueryReq;
import com.citc.nce.robot.req.FastGroupMessageReq;
import com.citc.nce.robot.res.FastGroupMessageItem;
import com.citc.nce.robot.res.FastGroupMessageSelectAllResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author jcrenc
 * @since 2024/6/25 14:36
 */
@FeignClient(value = "im-service", contextId = "fastGroupMessageApi", url = "${im:}")
public interface FastGroupMessageApi {

    @PostMapping("/fastGroupMessage/create")
    void createAndStartFastGroupMessage(@RequestBody @Valid FastGroupMessageReq req);


    @PostMapping("/fastGroupMessage/send/statistic")
    void handleSendStatistic(@RequestParam("messageId") String messageId,
                             @RequestParam("updatedRecordNumber") Integer updatedRecordNumber,
                             @RequestParam("stateCode") int stateCode);

    @PostMapping("/fastGroupMessage/edit")
    void updateFastGroupMessage(@RequestBody @Valid FastGroupMessageReq req);

    @PostMapping("/fastGroupMessage/queryList")
    PageResult<FastGroupMessageItem> queryList(@RequestBody @Valid FastGroupMessageQueryReq req);

    @PostMapping("/fastGroupMessage/selectAll")
    FastGroupMessageSelectAllResp selectAll();
    @PostMapping("/fastGroupMessage/updateStatus")
    void updateStatus(@RequestParam("planId")Long planId, @RequestParam("failedReason")String failedReason);
    @PostMapping("/fastGroupMessage/createStatistics")
    Long createStatistics(@RequestParam("customerId")String customerId, @RequestParam("startTime")Date startTime, @RequestParam("endTime")Date endTime);
    @PostMapping("/fastGroupMessage/findByPlanId")
    FastGroupMessageItem findByPlanId(@RequestParam("planId")Long planId);
}
