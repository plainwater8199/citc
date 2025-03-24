package com.citc.nce.auth.postpay;

import com.citc.nce.auth.postpay.scheme.vo.SchemeAddVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeListVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeSearchVo;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jcrenc
 * @since 2024/3/6 9:50
 */
@Api(tags = "后付费方案api")
@FeignClient(value = "auth-service", contextId = "PostpaySchemeApi", url = "${auth:}")
public interface PostpaySchemeApi {

    @PostMapping("/postpay/scheme/add")
    void addScheme(@RequestBody SchemeAddVo addVo);

    @PostMapping("/postpay/scheme/query")
    PageResult<SchemeListVo> queryScheme(@RequestBody SchemeSearchVo searchVo);

    @PostMapping("/postpay/scheme/delete")
    void deleteScheme(@RequestParam("id") Long id);
}
