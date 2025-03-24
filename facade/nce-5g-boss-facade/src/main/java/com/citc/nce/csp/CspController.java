package com.citc.nce.csp;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.CspInfoPage;
import com.citc.nce.authcenter.csp.vo.resp.CspMealCspInfo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiancheng
 */
@Api(tags = "csp 功能")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CspController {

    private final AdminAuthApi adminAuthApi;
    private final CspApi cspApi;

    @PostMapping("/csp/account/allList")
    @ApiOperation("查询所以csp账号")
    public PageResult<CspMealCspInfo> allList(@RequestBody @Validated CspInfoPage query) {
        Page<CspMealCspInfo> page = cspApi.allList(query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @PostMapping("/csp/customer/disableCustomerMeal")
    @ApiOperation("csp异常时禁用csp客户")
    @Log(title = "管理端-csp异常时禁用csp客户", operatorType = OperatorType.MANAGE)
    public void disableCustomerMealCsp(@RequestParam("cspId") String cspId) {
        if (StringUtils.isEmpty(cspId)) {
            throw new BizException("cspId不能为空");
        }
        adminAuthApi.disableCustomerMealCsp(cspId);
    }
}
