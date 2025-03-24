package com.citc.nce.robot.api.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.materialSquare.vo.cus.MsCustomerBuyVo;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsSummaryDetailVO;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderPageReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderRemarkReq;
import com.citc.nce.robot.api.tempStore.bean.csp.*;
import com.citc.nce.robot.api.tempStore.bean.manage.ManageOrderPage;
import com.citc.nce.robot.api.tempStore.bean.manage.OrderManagePageQuery;
import com.citc.nce.robot.api.tempStore.domain.Order;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 素材广场 订单api
 *
 * @author bydud
 * @since 14:33
 */

@FeignClient(value = "im-service", contextId = "orderAPi", url = "${im:}")
public interface OrderAPi {

    @GetMapping("/tempStore/order/getById/{orderId}")
    public Order getById(@PathVariable("orderId") Long orderId);

    @PostMapping("/tempStore/order/csp/page")
    @ApiOperation("csp我的订单列表查询")
    public PageResult<CspOrderPage> cspPage(@RequestBody @Validated OrderPageQuery query);

    @PostMapping("/tempStore/order/cancel/{orderId}")
    @ApiOperation("取消订单")
    public void cancel(@PathVariable("orderId") Long orderId);

    @PostMapping("/tempStore/order/csp/payComplete/{orderId}")
    @ApiOperation("支付完成")
    public void payComplete(@PathVariable("orderId") Long orderId);

    @GetMapping("/tempStore/order/csp/remake/{orderId}")
    @ApiOperation("查询备注信息")
    public String getRemake(@PathVariable("orderId") Long orderId);

    @PostMapping("/tempStore/order/csp/remake")
    @ApiOperation("备注")
    public void putRemake(@RequestBody @Validated CspOrderRemake remake);


    //================华丽分割线==============customer==========


    /**
     * 商品主键
     *
     * @param id 主键id
     * @return
     */
    @ApiOperation("下订单,素材汇总表mssId")
    @PostMapping("/tempStore/order/customer/buy")
    public void customerBuy(@RequestBody @Valid MsCustomerBuyVo id);

    @PostMapping("/tempStore/order/customer/page")
    @ApiOperation("我的订单列表查询")
    public PageResult<CspOrderPage> customerPage(@RequestBody @Validated OrderPageQuery query);

    @PostMapping("/tempStore/order/customer/cancel/{orderId}")
    @ApiOperation("取消我的订单")
    public void cancelMyOrder(@PathVariable("orderId") Long orderId);

    @PostMapping("/tempStore/order/customer/useTemplate")
    @ApiOperation("使用模板")
    public OrderUseTemplateResp useTemplate(@RequestBody @Validated OrderUseTemplate req);

    @PostMapping("/tempStore/order/customer/useTemplateNameCheck")
    @ApiOperation("使用模板名称验证")
    String useTemplateNameCheck(@RequestBody @Validated OrderUseTemplateNameCheckReq req);

    @GetMapping("/order/getSummaryById/{orderId}")
    @ApiOperation("根据订单id，查询作品详情")
    MsSummaryDetailVO getSummaryById(@PathVariable("orderId")Long orderId);

    //================华丽分割线==============manage==========
    @PostMapping("/tempStore/order/manage/page")
    @ApiOperation("订单管理")
    PageResult<ManageOrderPage> managePage(@RequestBody @Valid OrderManagePageQuery query);

    @PostMapping("/tempStore/operation/order/page")
    @ApiOperation("运营-订单管理-分页查询")
    PageResult<ManageOrderPage> orderPage(@RequestBody @Valid OperationOrderPageReq req);

    @PostMapping("/tempStore/operation/order/isPay/{orderId}")
    @ApiOperation("运营-订单管理-支付完成")
    void orderIsPay(@PathVariable("orderId") Long orderId);

    @PostMapping("/tempStore/operation/order/cancel/{orderId}")
    @ApiOperation("运营-订单管理-取消订单")
    void orderCancel(@PathVariable("orderId") Long orderId);

    @PostMapping("/tempStore/operation/order/remark")
    @ApiOperation("运营-订单管理-备注")
    void orderRemark(@RequestBody @Valid OperationOrderRemarkReq req);
}
