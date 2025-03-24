package com.citc.nce.csp;

import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.req.LoginReq;
import com.citc.nce.authcenter.auth.vo.req.thirdAuthReq;
import com.citc.nce.authcenter.auth.vo.req.thirdLoginReq;
import com.citc.nce.authcenter.auth.vo.resp.LoginResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiancheng
 */
@Api(tags = "csp customer")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CspCustomerApi cspCustomerApi;
    private final AdminAuthApi adminAuthApi;

    /**
     * csp 客户登录
     * @param encoded cspId编码
     * @param req 登录请求对象
     */
    @PostMapping("/user/login/{encoded}")
    @SkipToken
    @Log(title = "客户侧-用户登录", operatorType = OperatorType.CUS, isSaveRequestData = false, isSaveResponseData = false)
    public LoginResp login(@PathVariable String encoded, @Valid @RequestBody LoginReq req) {
        return cspCustomerApi.login(encoded, req);
    }

    /**
     * 客户第三方登录
     * @param req 登录请求对象
     */
    @PostMapping("/customer/thirdLogin")
    @SkipToken
    @Log(title = "客户侧-第三方登录", operatorType = OperatorType.CUS, isSaveRequestData = false, isSaveResponseData = false)
    public LoginResp thirdLogin(@Valid @RequestBody thirdLoginReq req) {
        return cspCustomerApi.thirdLogin(req);
    }

    /**
     * 客户第三方验证
     * @param req 登录请求对象
     */
    @PostMapping("/customer/thirdAuth")
    @SkipToken
    @Log(title = "客户侧-第三方验证", operatorType = OperatorType.CUS, isSaveRequestData = false, isSaveResponseData = false)
    public LoginResp thirdAuth(@Valid @RequestBody thirdAuthReq req) {
        return cspCustomerApi.thirdAuth(req);
    }

    /**
     * 转正
     *
     * @param customerId
     * @return
     */
    @GetMapping("/csp/changeCustomerToFormal")
    @HasCsp
    @Log(title = "changeCustomerToFormal", operatorType = OperatorType.CSP)
    @ApiOperation("客户转正")
    void changeCustomerToFormal(@RequestParam("customerId") String customerId) {
        String cspId = SessionContextUtil.getLoginUser().getCspId();
        if (!customerId.contains(cspId)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        cspCustomerApi.changeCustomerToFormal(customerId);
    }

    @PostMapping("/csp/customer/disableCustomerMeal")
    @ApiOperation("csp异常时禁用csp客户")
    @HasCsp
    @Log(title = "CPS-csp异常时禁用csp客户", operatorType = OperatorType.CSP)
    public void disableCustomerMealCsp() {
        adminAuthApi.disableCustomerMealCsp(SessionContextUtil.verifyCspLogin());
    }

}
