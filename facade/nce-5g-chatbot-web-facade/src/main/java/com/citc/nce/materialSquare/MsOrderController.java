package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.modulemanagement.ModuleManagementApi;
import com.citc.nce.robot.api.materialSquare.OrderAPi;
import com.citc.nce.robot.api.materialSquare.vo.cus.MsCustomerBuyVo;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsSummaryDetailVO;
import com.citc.nce.robot.api.tempStore.bean.csp.*;
import com.citc.nce.robot.api.tempStore.domain.Order;
import com.citc.nce.security.annotation.HasCsp;
import com.citc.nce.security.annotation.IsCustomer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @author bydud
 * @since 10:03
 */

@Api(tags = "模板商城-订单管理")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("ms")
public class MsOrderController {

    private OrderAPi orderAPi;
    private final ECDHService ecdhService;
    private final ModuleManagementApi moduleManagementApi;

//    ===========CSP===============

    @GetMapping("/order/getById/{orderId}")
    @ApiOperation("根据订单id，查询订单详情")
    public Order getById(@PathVariable("orderId") Long orderId) {
        return orderAPi.getById(orderId);
    }

    @GetMapping("/order/getSummaryById/{orderId}")
    @ApiOperation("根据订单id，查询作品详情")
    public MsSummaryDetailVO getSummaryById(@PathVariable("orderId") Long orderId) {
        return orderAPi.getSummaryById(orderId);
    }

    @PostMapping("/order/csp/page")
    @ApiOperation("csp我的订单列表查询")
    public PageResult<CspOrderPage> cspPage(@RequestBody @Validated OrderPageQuery query) {
        PageResult<CspOrderPage> result = orderAPi.cspPage(query);
        result.getList().forEach(s -> s.setCreatorPhone(ecdhService.encode(s.getCreatorPhone())));
        return result;
    }

    @PostMapping("/order/cancel/{orderId}")
    @ApiOperation("取消订单")
    @HasCsp
    public void cancel(@PathVariable("orderId") Long orderId) {
        orderAPi.cancel(orderId);
    }

    @PostMapping("/order/csp/payComplete/{orderId}")
    @ApiOperation("支付完成")
    @HasCsp
    public void payComplete(@PathVariable("orderId") Long orderId) {
        orderAPi.payComplete(orderId);
    }

    @GetMapping("/order/csp/remake/{orderId}")
    @ApiOperation("查询备注信息")
    @HasCsp
    public RestResult<String> getRemake(@PathVariable("orderId") Long orderId) {
        return RestResult.success(orderAPi.getRemake(orderId));
    }

    @PostMapping("/order/csp/remake")
    @ApiOperation("备注")
    @HasCsp
    public void putRemake(@RequestBody @Validated CspOrderRemake remake) {
        orderAPi.putRemake(remake);
    }


    //================华丽分割线==============cus==========
    @IsCustomer
    @ApiOperation("下订单,素材汇总表mssId")
    @PostMapping("/order/customer/buy")
    public void customerBuy(@RequestBody @Valid MsCustomerBuyVo customerBuy) {
        orderAPi.customerBuy(customerBuy);
    }

//    @GetMapping("/tempStore/order/customer/orderView/{orderId}")
//    @ApiOperation("预览订单数据")
//    @XssCleanIgnore
//    public OrderView orderView(@PathVariable("orderId") Long orderId) {
//        return orderAPi.orderView(orderId);
//    }

    @IsCustomer
    @PostMapping("/order/customer/page")
    @ApiOperation("我的订单列表查询")
    public PageResult<CspOrderPage> customerPage(@RequestBody @Validated OrderPageQuery query) {
        return orderAPi.customerPage(query);
    }
    @IsCustomer
    @PostMapping("/order/customer/cancel/{orderId}")
    @ApiOperation("取消我的订单")
    public void cancelMyOrder(@PathVariable("orderId") Long orderId) {
        orderAPi.cancelMyOrder(orderId);
    }

    @IsCustomer
    @PostMapping("/order/customer/useTemplateNameCheck")
    @ApiOperation("使用模板名称验证")
    public String useTemplateNameCheck(@RequestBody @Validated OrderUseTemplateNameCheckReq req) {
        return orderAPi.useTemplateNameCheck(req);
    }
    @IsCustomer
    @PostMapping("/order/customer/useTemplate")
    @ApiOperation("使用模板")
    @XssCleanIgnore
    public OrderUseTemplateResp useTemplate(@RequestBody @Validated OrderUseTemplate req) {
        //不要系统变量，是否是系统变量由前端传递
        req.setVariableEditList(req.getVariableEditList().stream().filter(s -> !s.isSystemVariable()).collect(Collectors.toList()));
        return orderAPi.useTemplate(req);
    }
    @IsCustomer
    //客户侧
    @GetMapping("/moduleManagement/queryUsedPermissionsById/{moduleId}")
    @ApiOperation("组件查询--根据id-获取登录用户是否有权限")
    public Boolean queryUsedPermissionsById(@PathVariable("moduleId") Long moduleId) {
        return moduleManagementApi.queryUsedPermissionsById(moduleId);
    }

}
