package com.citc.nce.developer;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.developer.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "开发者服务短信")
@RequestMapping("/sms/developer/customer/")
public class DeveloperCustomerSmsController {
    private final SmsDeveloperCustomerApi smsDeveloperCustomerApi;
    private final  ECDHService ecdhService;

    @PostMapping("generate")
    @ApiOperation("鉴权信息生成")
    public boolean generate() {
        smsDeveloperCustomerApi.generate();
        return true;
    }

    @PostMapping("details/{customerId}")
    @ApiOperation("详情查询")
    public SmsDeveloperAuthVo details(@PathVariable("customerId") String customerId) {
        if (!SessionContextUtil.verifyCspLogin().equals(customerId.substring(0, 10))) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        return smsDeveloperCustomerApi.details(customerId);
    }

    @PostMapping("cus/details")
    @ApiOperation("详情查询")
    public SmsDeveloperAuthVo details() {
        return smsDeveloperCustomerApi.details(SessionContextUtil.getLoginUser().getUserId());
    }

    @PostMapping("saveCallbackUrl")
    @ApiOperation("设置回调地址")
    public void saveCallbackUrl(@RequestBody @Valid SmsDeveloperAuthCallbackUrlVo smsDeveloperAuthCallbackUrlVo) {
        smsDeveloperCustomerApi.saveCallbackUrl(smsDeveloperAuthCallbackUrlVo);
    }

    @PostMapping("search/callback/list")
    @ApiOperation("回调明细列表查询")
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(@RequestBody @Valid SmsDeveloperSendSearchVo smsDeveloperSendSearchVo) {
        PageResult<DeveloperCustomerVo> page = smsDeveloperCustomerApi.searchDeveloperSend(smsDeveloperSendSearchVo);
        for (DeveloperCustomerVo body : page.getList()) {
            body.setPhone(ecdhService.encode(body.getPhone()));
        }
        return page;
    }

    @PostMapping("setSwitch")
    @ApiOperation("禁用/启用")
    public void setSwitch(@RequestBody @Valid SmsDeveloperSwitchVo smsDeveloperSwitchVo) {
        if (!SessionContextUtil.verifyCspLogin().equals(smsDeveloperSwitchVo.getCustomerUserId().substring(0, 10))) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        smsDeveloperCustomerApi.setSwitch(smsDeveloperSwitchVo);
    }

    @PostMapping("queryList")
    @ApiOperation("开发者列表")
    public PageResult<SmsDeveloperCustomerManagerVo> queryList(@RequestBody @Valid SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo) {
        return smsDeveloperCustomerApi.queryList(smsDeveloperCustomerReqVo);
    }

    @PostMapping("customerOption")
    @ApiOperation("获取客户下拉框")
    PageResult<DeveloperAccountVo> getSmsDeveloperCustomerOption() {
        return smsDeveloperCustomerApi.getSmsDeveloperCustomerOption();
    }


}
