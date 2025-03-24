package com.citc.nce.auth.ticket;

import com.citc.nce.auth.ticket.service.QualificationsApplyService;
import com.citc.nce.auth.ticket.vo.req.GetInfoByIdReq;
import com.citc.nce.auth.ticket.vo.req.GetQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.req.ProcessingStateReq;
import com.citc.nce.auth.ticket.vo.req.SubmitQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.resp.GetQualificationsApplyInfoByIdResp;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/6/28 16:38
 * @Version 1.0
 * @Description:
 */
@RestController
@Slf4j
public class TicketQualificationsApplyController implements TicketQualificationsApplyApi {

    @Autowired
    private QualificationsApplyService qualificationsApplyService;


    @PostMapping("/worksheet/submitQualificationsApply")
    @Override
    public void submitQualificationsApply(SubmitQualificationsApplyReq submitQualificationsApplyReq) {
        qualificationsApplyService.submitQualificationsApply(submitQualificationsApplyReq);
    }

    @PostMapping("/worksheet/disposeQualificationsApply")
    @Override
    public void disposeQualificationsApply(@Valid ProcessingStateReq processingStateReq) {
        qualificationsApplyService.disposeQualificationsApply(processingStateReq);
    }

    @PostMapping("/worksheet/getQualificationsApply")
    @Override
    public PageResult getQualificationsApply(GetQualificationsApplyReq getQualificationsApplyReq) {
        return qualificationsApplyService.getQualificationsApply(getQualificationsApplyReq);
    }

    @PostMapping("/worksheet/getQualificationsApplyInfoById")
    @Override
    public GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(GetInfoByIdReq getInfoByIdReq) {
        return qualificationsApplyService.getQualificationsApplyInfoById(getInfoByIdReq);
    }

}
