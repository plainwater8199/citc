package com.citc.nce.misc.legal.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.misc.legal.req.IdReq;
import com.citc.nce.misc.legal.req.PageReq;
import com.citc.nce.misc.legal.req.UpdateReq;
import com.citc.nce.misc.legal.resp.LegalFileResp;

/**
 * @BelongsPackage: com.citc.nce.misc.leagal.service
 * @Author: litao
 * @CreateTime: 2023-02-09  16:04

 * @Version: 1.0
 */
public interface LegalFileService {
    PageResult<LegalFileResp> listByPage(PageReq req);

    LegalFileResp findById(IdReq req);

    void updateById(UpdateReq req);

    PageResult<LegalFileResp> historyVersionList(PageReq req);
}
