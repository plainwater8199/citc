package com.citc.nce.auth.csp.dict;

import com.citc.nce.auth.csp.dict.service.CspDictService;
import com.citc.nce.auth.csp.dict.vo.CspDictReq;
import com.citc.nce.auth.csp.dict.vo.CspDictResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>csp-字典</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 11:27
 */
@RestController
public class CspDictController implements CspDictApi {

    @Autowired
    CspDictService service;

    @Override
    public PageResult<CspDictResp> queryList(CspDictReq req) {
        return service.queryList(req);
    }
}
