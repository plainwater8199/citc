package com.citc.nce.aim.privatenumber;

import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.annotation.BossAuth;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/9 17:50
 */
@BossAuth("/hang-short-aim/privacy-project")
@RestController
@Slf4j
@Api(value = "aim", tags = "挂短-隐私号订单")
public class PrivateNumberOrderController {

    @Resource
    private PrivateNumberOrderApi privateNumberOrderApi;

    @ApiOperation(value = "新增订单", notes = "新增订单")
    @PostMapping("/privateNumber/order/save")
    public int save(@RequestBody @Valid PrivateNumberOrderSaveReq req) {
        return privateNumberOrderApi.save(req);
    }


    @ApiOperation(value = "编辑订单", notes = "编辑订单")
    @PostMapping("/privateNumber/order/edit")
    public int edit(@RequestBody @Valid PrivateNumberOrderEditReq req) {
        return privateNumberOrderApi.edit(req);
    }


    @ApiOperation(value = "变更订单状态", notes = "变更订单状态")
    @PostMapping("/privateNumber/order/updateStatus")
    public int updateStatus(@RequestBody @Valid PrivateNumberOrderUpdateStatusReq req) {
        return privateNumberOrderApi.updateStatus(req);
    }

    @ApiOperation(value = "删除订单", notes = "删除订单")
    @PostMapping("/privateNumber/order/delete")
    public int delete(@RequestBody @Valid PrivateNumberOrderDeleteReq req) {
        return privateNumberOrderApi.delete(req);
    }

    @ApiOperation(value = "查询订单", notes = "查询订单")
    @PostMapping("/privateNumber/order/queryOrderById")
    public PrivateNumberOrderResp queryOrderById(@RequestBody @Valid PrivateNumberOrderQueryReq req) {
        return privateNumberOrderApi.queryOrderById(req);
    }

    @ApiOperation(value = "查询已启用订单", notes = "查询已启用订单")
    @PostMapping("/privateNumber/order/queryEnabledOrderByProjectId")
    public PrivateNumberOrderResp queryEnabledOrderByProjectId(@RequestBody @Valid PrivateNumberOrderQueryReq req) {
        return privateNumberOrderApi.queryEnabledOrderByProjectId(req);
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表")
    @PostMapping("/privateNumber/order/queryOrderList")
    public PageResult<PrivateNumberOrderResp> queryOrderList(@RequestBody PrivateNumberOrderQueryListReq req) {
        return privateNumberOrderApi.queryOrderList(req);
    }
}
