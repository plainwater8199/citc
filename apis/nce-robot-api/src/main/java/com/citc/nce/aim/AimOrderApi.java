package com.citc.nce.aim;

import com.citc.nce.aim.vo.AimOrderDeleteReq;
import com.citc.nce.aim.vo.AimOrderEditReq;
import com.citc.nce.aim.vo.AimOrderQueryListReq;
import com.citc.nce.aim.vo.AimOrderQueryReq;
import com.citc.nce.aim.vo.AimOrderResp;
import com.citc.nce.aim.vo.AimOrderSaveReq;
import com.citc.nce.aim.vo.AimOrderUpdateStatusReq;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>挂短-订单</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:35
 */
@FeignClient(value = "rebot-service",contextId="AimOrderApi", url = "${robot:}")
public interface AimOrderApi {

    @PostMapping("/aim/order/save")
    int save(@RequestBody @Valid AimOrderSaveReq req);

    @PostMapping("/aim/order/edit")
    int edit(@RequestBody @Valid AimOrderEditReq req);

    @PostMapping("/aim/order/updateStatus")
    int updateStatus(@RequestBody @Valid AimOrderUpdateStatusReq req);

    @PostMapping("/aim/order/delete")
    int delete(@RequestBody @Valid AimOrderDeleteReq req);

    @PostMapping("/aim/order/queryOrderById")
    AimOrderResp queryOrderById(@RequestBody @Valid AimOrderQueryReq req);

    @PostMapping("/aim/order/queryEnabledOrderByProjectId")
    AimOrderResp queryEnabledOrderByProjectId(@RequestBody @Valid AimOrderQueryReq req);

    @PostMapping("/aim/order/queryOrderList")
    PageResult<AimOrderResp> queryOrderList(@RequestBody AimOrderQueryListReq req);


}
