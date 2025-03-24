package com.citc.nce.aim;

import com.citc.nce.aim.vo.AimOrderDeleteReq;
import com.citc.nce.aim.vo.AimOrderEditReq;
import com.citc.nce.aim.vo.AimOrderQueryListReq;
import com.citc.nce.aim.vo.AimOrderQueryReq;
import com.citc.nce.aim.vo.AimOrderResp;
import com.citc.nce.aim.vo.AimOrderSaveReq;
import com.citc.nce.aim.vo.AimOrderUpdateStatusReq;
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
@BossAuth("/hang-short-aim/hangup-project")
@RestController
@Slf4j
@Api(value = "aim", tags = "挂短-订单")
public class AimOrderController {

    @Resource
    private AimOrderApi aimOrderApi;

    @ApiOperation(value = "新增订单", notes = "新增订单")
    @PostMapping("/aim/order/save")
    public int save(@RequestBody @Valid AimOrderSaveReq req) {
        return aimOrderApi.save(req);
    }


    @ApiOperation(value = "编辑订单", notes = "编辑订单")
    @PostMapping("/aim/order/edit")
    public int edit(@RequestBody @Valid AimOrderEditReq req) {
        return aimOrderApi.edit(req);
    }


    @ApiOperation(value = "变更订单状态", notes = "变更订单状态")
    @PostMapping("/aim/order/updateStatus")
    public int updateStatus(@RequestBody @Valid AimOrderUpdateStatusReq req) {
        return aimOrderApi.updateStatus(req);
    }

    @ApiOperation(value = "删除订单", notes = "删除订单")
    @PostMapping("/aim/order/delete")
    public int delete(@RequestBody @Valid AimOrderDeleteReq req) {
        return aimOrderApi.delete(req);
    }

    @ApiOperation(value = "查询订单", notes = "查询订单")
    @PostMapping("/aim/order/queryOrderById")
    public AimOrderResp queryOrderById(@RequestBody @Valid AimOrderQueryReq req) {
        return aimOrderApi.queryOrderById(req);
    }

    @ApiOperation(value = "查询已启用订单", notes = "查询已启用订单")
    @PostMapping("/aim/order/queryEnabledOrderByProjectId")
    public AimOrderResp queryEnabledOrderByProjectId(@RequestBody @Valid AimOrderQueryReq req) {
        return aimOrderApi.queryEnabledOrderByProjectId(req);
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表")
    @PostMapping("/aim/order/queryOrderList")
    public PageResult<AimOrderResp> queryOrderList(@RequestBody AimOrderQueryListReq req) {
        return aimOrderApi.queryOrderList(req);
    }
}
