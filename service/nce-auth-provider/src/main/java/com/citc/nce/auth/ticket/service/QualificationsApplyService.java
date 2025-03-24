package com.citc.nce.auth.ticket.service;

import com.citc.nce.auth.ticket.vo.req.GetInfoByIdReq;
import com.citc.nce.auth.ticket.vo.req.GetQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.req.ProcessingStateReq;
import com.citc.nce.auth.ticket.vo.req.SubmitQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.resp.GetQualificationsApplyInfoByIdResp;
import com.citc.nce.common.core.pojo.PageResult;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
public interface QualificationsApplyService {


    void submitQualificationsApply(SubmitQualificationsApplyReq submitQualificationsApplyReq);

    void disposeQualificationsApply(ProcessingStateReq processingStateReq);

    PageResult getQualificationsApply(GetQualificationsApplyReq getQualificationsApplyReq);

    GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(GetInfoByIdReq getInfoByIdReq);

}
