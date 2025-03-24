package com.citc.nce.aim.privatenumber;


import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>挂短-隐私号订单</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:35
 */
@FeignClient(value = "rebot-service",contextId="PrivateNumberOrderApi", url = "${robot:}")
public interface PrivateNumberOrderApi {

    @PostMapping("/privateNumber/order/save")
    int save(@RequestBody @Valid PrivateNumberOrderSaveReq req);

    @PostMapping("/privateNumber/order/edit")
    int edit(@RequestBody @Valid PrivateNumberOrderEditReq req);

    @PostMapping("/privateNumber/order/updateStatus")
    int updateStatus(@RequestBody @Valid PrivateNumberOrderUpdateStatusReq req);

    @PostMapping("/privateNumber/order/delete")
    int delete(@RequestBody @Valid PrivateNumberOrderDeleteReq req);

    @PostMapping("/privateNumber/order/queryOrderById")
    PrivateNumberOrderResp queryOrderById(@RequestBody @Valid PrivateNumberOrderQueryReq req);

    @PostMapping("/privateNumber/order/queryEnabledOrderByProjectId")
    PrivateNumberOrderResp queryEnabledOrderByProjectId(@RequestBody @Valid PrivateNumberOrderQueryReq req);

    @PostMapping("/privateNumber/order/queryOrderList")
    PageResult<PrivateNumberOrderResp> queryOrderList(@RequestBody PrivateNumberOrderQueryListReq req);


}
