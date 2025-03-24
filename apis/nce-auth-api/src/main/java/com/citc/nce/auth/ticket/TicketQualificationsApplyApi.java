package com.citc.nce.auth.ticket;

import com.citc.nce.auth.ticket.vo.req.GetInfoByIdReq;
import com.citc.nce.auth.ticket.vo.req.GetQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.req.ProcessingStateReq;
import com.citc.nce.auth.ticket.vo.req.SubmitQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.resp.GetQualificationsApplyInfoByIdResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "auth-service", contextId = "TicketQualificationsApplyApi", url = "${auth:}")
public interface TicketQualificationsApplyApi {

    /**
     * 提交资质申请
     *
     * @param submitQualificationsApplyReq
     */
    @PostMapping("/worksheet/submitQualificationsApply")
    void submitQualificationsApply(@RequestBody @Valid SubmitQualificationsApplyReq submitQualificationsApplyReq);

    /**
     * 处理资质申请
     *
     * @param processingStateReq
     */
    @PostMapping("/worksheet/disposeQualificationsApply")
    void disposeQualificationsApply(@RequestBody @Valid ProcessingStateReq processingStateReq);


    /**
     * 查询资质申请
     *
     * @param getQualificationsApplyReq
     */
    @PostMapping("/worksheet/getQualificationsApply")
    PageResult getQualificationsApply(@RequestBody @Valid GetQualificationsApplyReq getQualificationsApplyReq);


    /**
     * 查询资质申请详情页
     *
     * @param getInfoByIdReq
     */
    @PostMapping("/worksheet/getQualificationsApplyInfoById")
    GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(@RequestBody @Valid GetInfoByIdReq getInfoByIdReq);

}
