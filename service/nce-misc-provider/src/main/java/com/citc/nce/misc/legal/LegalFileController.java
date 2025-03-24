package com.citc.nce.misc.legal;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.misc.legal.req.IdReq;
import com.citc.nce.misc.legal.req.PageReq;
import com.citc.nce.misc.legal.req.UpdateReq;
import com.citc.nce.misc.legal.resp.LegalFileResp;
import com.citc.nce.misc.legal.service.LegalFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @BelongsPackage: com.citc.nce.misc.legal
 * @Author: litao
 * @CreateTime: 2023-02-09  16:31
 
 * @Version: 1.0
 */
@RestController
@Slf4j
public class LegalFileController implements LegalFileApi{
    @Resource
    private LegalFileService legalFileService;

    @Override
    public PageResult<LegalFileResp> listByPage(@RequestBody @Valid PageReq req) {
        return legalFileService.listByPage(req);
    }

    @Override
    public LegalFileResp findById(@RequestBody @Valid IdReq req) {
        return legalFileService.findById(req);
    }

    @Override
    public void updateById(@RequestBody @Valid UpdateReq req) {
        legalFileService.updateById(req);
    }

    @Override
    public PageResult<LegalFileResp> historyVersionList(@RequestBody @Valid PageReq req) {
        return legalFileService.historyVersionList(req);
    }
}
