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
@Api(tags = "开发者服务视屏短信")
@RequestMapping("/video/developer/customer/")
public class DeveloperCustomerVideoController {
    private final VideoDeveloperCustomerApi videoDeveloperCustomerApi;
    private final  ECDHService ecdhService;

    @PostMapping("generate")
    @ApiOperation("鉴权信息生成")
    public boolean generate() {
        videoDeveloperCustomerApi.generate();
        return true;
    }

    @PostMapping("details/{customerId}")
    @ApiOperation("详情查询")
    public VideoDeveloperAuthVo details(@PathVariable("customerId") String customerId) {
        if (!SessionContextUtil.verifyCspLogin().equals(customerId.substring(0, 10))) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        return videoDeveloperCustomerApi.details(customerId);
    }

    @PostMapping("cus/details")
    @ApiOperation("详情客户查询")
    public VideoDeveloperAuthVo details() {
        return videoDeveloperCustomerApi.details(SessionContextUtil.getLoginUser().getUserId());
    }

    @PostMapping("saveCallbackUrl")
    @ApiOperation("设置回调地址")
    public void saveCallbackUrl(@RequestBody @Valid VideoDeveloperAuthCallbackUrlVo videoDeveloperAuthCallbackUrlVo) {
        videoDeveloperCustomerApi.saveCallbackUrl(videoDeveloperAuthCallbackUrlVo);
    }

    @PostMapping("search/callback/list")
    @ApiOperation("回调明细列表查询")
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(@RequestBody @Valid VideoDeveloperSendSearchVo videoDeveloperSendSearchVo) {
        PageResult<DeveloperCustomerVo> page = videoDeveloperCustomerApi.searchDeveloperSend(videoDeveloperSendSearchVo);
        for (DeveloperCustomerVo body : page.getList()) {
            body.setPhone(ecdhService.encode(body.getPhone()));
        }
        return page;
    }

    @PostMapping("setSwitch")
    @ApiOperation("禁用/启用")
    public void setSwitch(@RequestBody @Valid VideoDeveloperSwitchVo videoDeveloperSwitchVo) {
        if (!SessionContextUtil.verifyCspLogin().equals(videoDeveloperSwitchVo.getCustomerUserId().substring(0, 10))) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        videoDeveloperCustomerApi.setSwitch(videoDeveloperSwitchVo);
    }

    @PostMapping("queryList")
    @ApiOperation("开发者列表")
    public PageResult<VideoDeveloperCustomerManagerVo> queryList(@RequestBody @Valid VideoDeveloperCustomerReqVo videoDeveloperCustomerReqVo) {
        return videoDeveloperCustomerApi.queryList(videoDeveloperCustomerReqVo);
    }


    @PostMapping("customerOption")
    @ApiOperation("获取客户下拉框")
    public PageResult<DeveloperAccountVo> getVideoDeveloperCustomerOption() {
        return videoDeveloperCustomerApi.getVideoDeveloperCustomerOption();
    }


}
