package com.citc.nce.auth.identification.service;

import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.identification.vo.req.ViewRemarkReq;
import com.citc.nce.auth.identification.vo.resp.IdentificationAuditResp;

import java.util.List;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:23
 * @description:
 */
public interface IdentificationAuditRecordService {

    /**
     * 根据认证记录id查询认证审核
     *
     * @param identificationId
     * @return
     */
    List<IdentificationAuditResp> getIdentificationAudits(Long identificationId);

    /**
     * 查看审核历史记录
     *
     * @return
     */
    List<IdentificationAuditResp> viewRemarkHistory(ViewRemarkReq req);

    /**
     * 添加认证记录数据
     *
     * @param recordDo
     */
    void addAuditRecord(IdentificationAuditRecordDo recordDo);

}
