package com.citc.nce.postpay;

import com.citc.nce.auth.postpay.PostpaySchemeApi;
import com.citc.nce.auth.postpay.scheme.vo.SchemeAddVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeListVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeSearchVo;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/3/6 10:41
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/postpay/scheme")
@Validated
@Api(tags = "后付费方案")
public class PostpaySchemeController {
    private final PostpaySchemeApi postpaySchemeApi;

    @PostMapping("add")
    @ApiOperation("添加方案")
    public void add(@Valid @RequestBody SchemeAddVo addVo) {
        postpaySchemeApi.addScheme(addVo);
    }

    @PostMapping("query")
    @ApiOperation("查询方案")
    PageResult<SchemeListVo> queryScheme(@RequestBody SchemeSearchVo searchVo) {
        return postpaySchemeApi.queryScheme(searchVo);
    }

    @PostMapping("delete")
    @ApiOperation("删除方案")
    public void deleteScheme(@RequestParam Long id) {
        postpaySchemeApi.deleteScheme(id);
    }
}
