package com.citc.nce.auth;

import com.citc.nce.authcenter.auth.DHUserApi;
import com.citc.nce.authcenter.auth.vo.DHExchange;
import com.citc.nce.authcenter.auth.vo.DHVerify;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author bydud
 * @since 2024/7/16 10:29
 */
@Api(tags = "秘钥交换")
@RestController
@AllArgsConstructor
public class DHController {
    private DHUserApi dhUserApi;

    @PostMapping("/dh/exchange")
    public DHExchange exchange(@RequestBody @Valid DHExchange exchange) {
        SessionContextUtil.getLoginUser();
        return dhUserApi.exchange(exchange);
    }

    @PostMapping("/dh/verify")
    public String verify(@RequestBody DHVerify verify) {
        return dhUserApi.verify(verify);
    }
}
