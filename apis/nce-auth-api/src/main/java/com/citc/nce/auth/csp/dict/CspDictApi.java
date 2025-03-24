package com.citc.nce.auth.csp.dict;

import com.citc.nce.auth.csp.dict.vo.CspDictReq;
import com.citc.nce.auth.csp.dict.vo.CspDictResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/8/12 9:17
 */
@FeignClient(value = "auth-service", contextId = "CspDict", url = "${auth:}")
public interface CspDictApi {
    /**
     * 列表查询
     *
     * @param req
     * @return list
     */
    @PostMapping("/csp/cspDict/queryList")
    @NotNull
    PageResult<CspDictResp> queryList(@RequestBody CspDictReq req);
}
