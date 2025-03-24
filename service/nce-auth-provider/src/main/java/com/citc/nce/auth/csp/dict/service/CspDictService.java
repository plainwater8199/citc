package com.citc.nce.auth.csp.dict.service;

import com.citc.nce.auth.csp.dict.vo.CspDictReq;
import com.citc.nce.auth.csp.dict.vo.CspDictResp;
import com.citc.nce.common.core.pojo.PageResult;

/**
 * <p></p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface CspDictService {

    PageResult<CspDictResp> queryList(CspDictReq req);
}
