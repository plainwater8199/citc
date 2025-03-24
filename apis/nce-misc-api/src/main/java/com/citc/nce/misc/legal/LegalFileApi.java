package com.citc.nce.misc.legal;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.misc.legal.req.IdReq;
import com.citc.nce.misc.legal.req.PageReq;
import com.citc.nce.misc.legal.req.UpdateReq;
import com.citc.nce.misc.legal.resp.LegalFileResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @BelongsPackage: com.citc.nce.misc.legal
 * @Author: litao
 * @CreateTime: 2023-02-09  16:32

 * @Version: 1.0
 */
@FeignClient(value = "misc-service", contextId = "LegalFileApi", url = "${miscServer:}")
public interface LegalFileApi {

    @PostMapping("/legal/listByPage")
    PageResult<LegalFileResp> listByPage(@RequestBody @Valid PageReq req);

    @PostMapping("/legal/findById")
    LegalFileResp findById(@RequestBody @Valid IdReq req);

    @PostMapping("/legal/updateById")
    void updateById(@RequestBody @Valid UpdateReq req);

    @PostMapping("/legal/historyVersionList")
    PageResult<LegalFileResp> historyVersionList(@RequestBody @Valid PageReq req);
}
