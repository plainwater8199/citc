package com.citc.nce.im.materialSquare.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.materialSquare.service.IOrderService;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.robot.api.materialSquare.OrderAPi;
import com.citc.nce.robot.api.materialSquare.vo.cus.MsCustomerBuyVo;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsSummaryDetailVO;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderPageReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderRemarkReq;
import com.citc.nce.robot.api.tempStore.bean.csp.*;
import com.citc.nce.robot.api.tempStore.bean.manage.ManageOrderPage;
import com.citc.nce.robot.api.tempStore.bean.manage.OrderManagePageQuery;
import com.citc.nce.robot.api.tempStore.domain.Order;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 模板商城 订单表 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-17 11:29:51
 */
@RestController
@Api(tags = "csp-模板商城-订单管理",value = "csp-模板商城-订单管理")
@AllArgsConstructor
@Slf4j
public class MsOrderController implements OrderAPi {
    private final IOrderService orderService;
    private final CspCustomerApi cspCustomerApi;
    private final CspApi cspApi;


    @Override
    public Order getById(@PathVariable("orderId") Long orderId) {
        Order order = orderService.getById(orderId);
        if (Objects.nonNull(order)) {
            String goodsSnapshot = orderService.getOrderSnapshot(orderId);
            order.setGoodsSnapshot(goodsSnapshot);
        }
        return order;
    }

    @ApiOperation("csp我的订单列表查询")
    @Override
    public PageResult<CspOrderPage> cspPage(@RequestBody @Validated OrderPageQuery query) {
        Page<Order> page = PageSupport.getPage(Order.class, query.getPageNo(), query.getPageSize());
        orderService.cspPage(SessionContextUtil.verifyCspLogin(), page, query);
        List<Order> records = page.getRecords();
        return new PageResult<>(fillCustomerPhone(records), page.getTotal());
    }

    @ApiOperation("取消我的订单")
    @Override
    public void cancelMyOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelMyOrder(orderId);
    }

    @ApiOperation("取消订单")
    @Override
    public void cancel(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
    }


    @ApiOperation("支付完成")
    @Override
    public void payComplete(@PathVariable("orderId") Long orderId) {
        orderService.payComplete(orderId);
    }


    @ApiOperation("查询备注信息")
    @Override
    public String getRemake(@PathVariable("orderId") Long orderId) {
        Order order = orderService.getById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException(500, "订单不存在");
        }
        SessionContextUtil.sameCsp(order.getCspId());
        return order.getRemake();
    }

    @ApiOperation("备注")
    @Override
    public void putRemake(@RequestBody @Validated CspOrderRemake remake) {
        orderService.putRemake(remake);
    }


    //================华丽分割线==============customer==========


    @Override
    public void customerBuy(@RequestBody @Valid MsCustomerBuyVo customerBuy) {
        orderService.customerBuy(customerBuy);
    }


    @ApiOperation("我的订单列表查询")
    @Override
    public PageResult<CspOrderPage> customerPage(@RequestBody @Validated OrderPageQuery query) {
        Page<Order> page = PageSupport.getPage(Order.class, query.getPageNo(), query.getPageSize());
        orderService.clientPage(SessionContextUtil.getUserId(), page, query);
        List<CspOrderPage> pageList = page.getRecords().stream()
                .map(s -> {
                    CspOrderPage cspOrderPage = new CspOrderPage();
                    BeanUtils.copyProperties(s, cspOrderPage);
                    return cspOrderPage;
                }).collect(Collectors.toList());
        return new PageResult<>(pageList, page.getTotal());
    }


    @ApiOperation("使用模板")
    @Override
    @XssCleanIgnore
    public OrderUseTemplateResp useTemplate(@RequestBody @Validated OrderUseTemplate req) {
        long theadId = Thread.currentThread().getId();
        try {
            OrderUseTemplateResp resp = orderService.useTemplate(req);
            orderService.commit(theadId);
            return resp;
        } catch (Exception e) {
            //保存撤销送审数据
            log.error("使用模板出错", e);
            orderService.revokeReview(theadId);
            throw e;
        }
    }

    @Override
    public String useTemplateNameCheck(@RequestBody @Validated OrderUseTemplateNameCheckReq req) {
        return orderService.useTemplateNameCheck(req);
    }

    @Override
    public MsSummaryDetailVO getSummaryById(@PathVariable("orderId")Long orderId) {
        return orderService.getSummaryById(orderId);
    }


    //================华丽分割线==============manage==========
    @ApiOperation("订单管理")
    @Override
    public PageResult<ManageOrderPage> managePage(@RequestBody @Valid OrderManagePageQuery query) {
        Page<Order> page = PageSupport.getPage(Order.class, query.getPageNo(), query.getPageSize());
        List<ManageOrderPage> orderPages = orderService.managePage(page, query);
        return new PageResult<>(orderPages, page.getTotal());
    }

    @Override
    public PageResult<ManageOrderPage> orderPage(OperationOrderPageReq req) {
        return orderService.orderPage(req);
    }

    @Override
    public void orderIsPay(Long orderId) {
        orderService.orderIsPay(orderId);
    }

    @Override
    public void orderCancel(Long orderId) {
        orderService.orderCancel(orderId);
    }

    @Override
    public void orderRemark(OperationOrderRemarkReq req) {
        orderService.orderRemark(req);
    }


    /**
     * 给顾客添加手机号
     *
     * @param records 列表
     * @return List<CspOrderPage>
     */
    private List<CspOrderPage> fillCustomerPhone(List<Order> records) {
        Set<String> creatorSet = records.stream().map(Order::getCreator).collect(Collectors.toSet());
        Map<String, CustomerDetailResp> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(creatorSet)) {
            map.putAll(cspCustomerApi.getDetailByCustomerIds(creatorSet).stream()
                    .collect(Collectors.toMap(CustomerDetailResp::getCustomerId, Function.identity(), (a1, a2) -> a2)));
        }
        return records.stream().map(s -> {
            CspOrderPage cspOrderPage = new CspOrderPage();
            BeanUtils.copyProperties(s, cspOrderPage);
            CustomerDetailResp customer = map.get(s.getCreator());
            if (Objects.nonNull(customer)) {
                cspOrderPage.setCreatorName(customer.getName());
                cspOrderPage.setCreatorPhone(customer.getPhone());
            }
            return cspOrderPage;
        }).collect(Collectors.toList());
    }


}

