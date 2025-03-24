package com.citc.nce.auth.postpay.scheme.controller;

import com.citc.nce.auth.postpay.PostpaySchemeApi;
import com.citc.nce.auth.postpay.scheme.service.PostpaySchemeService;
import com.citc.nce.auth.postpay.scheme.vo.SchemeAddVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeListVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeSearchVo;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jcrenc
 * @since 2024/3/6 9:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class PostpaySchemeController implements PostpaySchemeApi {
    private final PostpaySchemeService postpaySchemeService;
    @Override
    public void addScheme(SchemeAddVo addVo) {
        postpaySchemeService.addScheme(addVo);
    }

    @Override
    public PageResult<SchemeListVo> queryScheme(SchemeSearchVo searchVo) {
        return postpaySchemeService.queryScheme(searchVo);
    }

    @Override
    public void deleteScheme(Long id) {
        postpaySchemeService.deleteScheme(id);
    }
}
