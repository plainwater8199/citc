package com.citc.nce.materialSquare;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.materialSquare.OrderAPi;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderPageReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderRemarkReq;
import com.citc.nce.robot.api.tempStore.bean.manage.ManageOrderPage;
import com.citc.nce.robot.api.tempStore.bean.manage.OrderManagePageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author bydud
 * @since 10:03
 */
@BossAuth({"/chatbot-view/shop/roder","/chatbot-view/shop/roderall"})
@Api(tags = "后台管理-模板商城-订单管理",value = "后台管理-模板商城-订单管理")
@RestController
@Slf4j
@AllArgsConstructor
public class MsOrderController {


    private OrderAPi orderAPi;

    @PostMapping("/tempStore/order/manage/page")
    @ApiOperation("订单管理分页查询")
    public PageResult<ManageOrderPage> managePage(@RequestBody @Validated OrderManagePageQuery query) {
        return orderAPi.managePage(query);
    }

    @PostMapping("/tempStore/operation/order/page")
    @ApiOperation("运营-订单管理-分页查询")
    public PageResult<ManageOrderPage> orderPage(@RequestBody @Validated OperationOrderPageReq req) {
        return orderAPi.orderPage(req);
    }

    @PostMapping("/tempStore/operation/order/isPay/{orderId}")
    @ApiOperation("运营-订单管理-支付完成")
    public void orderIsPay(@PathVariable("orderId") Long orderId) {
        orderAPi.orderIsPay(orderId);
    }

    @PostMapping("/tempStore/operation/order/cancel/{orderId}")
    @ApiOperation("运营-订单管理-取消订单")
    public void orderCancel(@PathVariable("orderId") Long orderId) {
        orderAPi.orderCancel(orderId);
    }

    @PostMapping("/tempStore/operation/order/remark")
    @ApiOperation("运营-订单管理-备注")
    public void orderRemark(@RequestBody @Valid OperationOrderRemarkReq req) {
        orderAPi.orderRemark(req);
    }

}
