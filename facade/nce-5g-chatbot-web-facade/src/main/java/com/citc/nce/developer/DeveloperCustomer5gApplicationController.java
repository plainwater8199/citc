package com.citc.nce.developer;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.xss.core.XssCleanIgnore;
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
@Api(tags = "开发者服务5g应用")
@RequestMapping("/developer/customer/5g/application/")
public class DeveloperCustomer5gApplicationController {
    private final DeveloperCustomer5gApplicationApi developerCustomer5gApplicationApi;

    @PostMapping("save")
    @ApiOperation("保存")
    public void saveApplication(@RequestBody @Valid Developer5gApplicationVo developerSaveApplicationVo) {
        developerCustomer5gApplicationApi.saveApplication(developerSaveApplicationVo);
    }

    @PostMapping("edit")
    @ApiOperation("编辑")
    public void editApplication(@RequestBody @Valid Developer5gApplicationVo developerSaveApplicationVo) {
        developerCustomer5gApplicationApi.editApplication(developerSaveApplicationVo);
    }

    @PostMapping("delete/{uniqueId}")
    @ApiOperation("删除")
    public void deleteApplication(@PathVariable("uniqueId") String uniqueId) {
        developerCustomer5gApplicationApi.deleteApplication(uniqueId);
    }

    @PostMapping("setState")
    @ApiOperation("状态修改")
    public void setStateApplication(@RequestBody @Valid DeveloperSetStateApplicationVo developerSetStateApplicationVo) {
        developerCustomer5gApplicationApi.setStateApplication(developerSetStateApplicationVo);
    }

    @PostMapping("queryApplicationInfo/{uniqueId}")
    @ApiOperation("详情查询")
    public Developer5gApplicationVo queryApplicationInfo(@PathVariable("uniqueId") String uniqueId) {
        return developerCustomer5gApplicationApi.queryApplicationInfo(uniqueId);
    }

    @PostMapping("queryApplicationList")
    @ApiOperation("列表查询")
    @XssCleanIgnore
    public PageResult<Developer5gApplicationVo> queryApplicationList(@RequestBody @Valid DeveloperQueryApplicationVo developerQueryApplicationVo) {
        return developerCustomer5gApplicationApi.queryApplicationList(developerQueryApplicationVo);
    }

    @PostMapping("queryList")
    @ApiOperation("开发者列表")
    public PageResult<DeveloperCustomer5gManagerVo> queryList(@RequestBody @Valid SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo) {
        return developerCustomer5gApplicationApi.queryList(smsDeveloperCustomerReqVo);
    }

    @PostMapping("customerOption")
    @ApiOperation("获取客户下拉框列表")
    PageResult<DeveloperAccountVo> get5gDeveloperCustomerOption(){
        return developerCustomer5gApplicationApi.get5gDeveloperCustomerOption();
    }

}
