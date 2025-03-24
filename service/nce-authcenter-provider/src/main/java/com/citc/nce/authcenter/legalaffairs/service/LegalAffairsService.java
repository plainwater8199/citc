package com.citc.nce.authcenter.legalaffairs.service;

import com.citc.nce.authcenter.legalaffairs.vo.req.IdReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.PageReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.UpdateReq;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileNewestResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalRecordResp;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

public interface LegalAffairsService {
    PageResult<LegalFileResp> listByPage(PageReq req);

    LegalFileResp findById(IdReq req);

    void updateById(UpdateReq req);

    PageResult<LegalFileResp> historyVersionList(PageReq req);


    /**
     *获取最新法务文件
     * @return
     */
    List<LegalFileNewestResp> newestList();

    /**
     * 获取文件操作记录
     * @param req
     * @return
     */
    List<LegalRecordResp> findRecord(IdReq req);
}
