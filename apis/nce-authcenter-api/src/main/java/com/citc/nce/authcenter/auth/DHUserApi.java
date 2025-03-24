package com.citc.nce.authcenter.auth;

import com.citc.nce.authcenter.auth.vo.DHExchange;
import com.citc.nce.authcenter.auth.vo.DHVerify;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author bydud
 * @since 2024/7/16 10:33
 */
@Api(tags = "用户--共享秘钥交换")
@FeignClient(value = "authcenter-service", contextId = "dhUserApi", url = "${authCenter:}")
public interface DHUserApi {
    @PostMapping("auth/dh/exchange")
    DHExchange exchange(@RequestBody @Valid DHExchange exchange);
    @PostMapping("auth/dh/verify")
    String verify(@RequestBody DHVerify verify);
}
