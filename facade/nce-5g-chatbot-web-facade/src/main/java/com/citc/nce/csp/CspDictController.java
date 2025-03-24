package com.citc.nce.csp;

import com.citc.nce.auth.csp.dict.CspDictApi;
import com.citc.nce.auth.csp.dict.vo.CspDictReq;
import com.citc.nce.auth.csp.dict.vo.CspDictResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@RequestMapping("/csp")
@Api(value = "CspDictController", tags = "CSP--字典")
public class CspDictController {

    @Resource
    CspDictApi dictApi;

    @PostMapping("/cspDict/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    public PageResult<CspDictResp> queryList(@RequestBody CspDictReq req) {
        return dictApi.queryList(req);
    }

}
