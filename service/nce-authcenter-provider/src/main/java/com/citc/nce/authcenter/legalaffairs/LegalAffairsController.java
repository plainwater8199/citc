package com.citc.nce.authcenter.legalaffairs;

import com.citc.nce.authcenter.legalaffairs.service.LegalAffairsService;
import com.citc.nce.authcenter.legalaffairs.vo.req.IdReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.PageReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.UpdateReq;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileNewestResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalRecordResp;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController()
@Slf4j
public class LegalAffairsController implements LegalAffairsApi {

    @Resource
    private LegalAffairsService legalAffairsService;
    @Override
    public PageResult<LegalFileResp> listByPage(@RequestBody @Valid PageReq req) {
        return legalAffairsService.listByPage(req);
    }

    @Override
    public LegalFileResp findById(@RequestBody @Valid IdReq req) {
        return legalAffairsService.findById(req);
    }

    @Override
    public void updateById(@RequestBody @Valid UpdateReq req) {
        legalAffairsService.updateById(req);
    }

    @Override
    public PageResult<LegalFileResp> historyVersionList(@RequestBody @Valid PageReq req) {
        return legalAffairsService.historyVersionList(req);
    }

    @Override
    public List<LegalFileNewestResp> newestList() {
        return legalAffairsService.newestList();
    }

    @Override
    public List<LegalRecordResp> findRecord(@RequestBody @Valid IdReq req) {
        return legalAffairsService.findRecord(req);
    }
}
