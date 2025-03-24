package com.citc.nce.im.materialSquare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.materialSquare.vo.cus.MsCustomerBuyVo;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsSummaryDetailVO;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderPageReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderRemarkReq;
import com.citc.nce.robot.api.tempStore.bean.csp.*;
import com.citc.nce.robot.api.tempStore.bean.manage.ManageOrderPage;
import com.citc.nce.robot.api.tempStore.bean.manage.OrderManagePageQuery;
import com.citc.nce.robot.api.tempStore.domain.Order;

import java.util.List;

/**
 * <p>
 * 模板商城 订单表 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-17 02:11:15
 */
public interface IOrderService extends IService<Order> {


    /**
     * 查询csp 相关的订单
     *
     * @param cspId cspID
     * @param page  分页对象
     * @param query 查询条件
     */
    void cspPage(String cspId, Page<Order> page, OrderPageQuery query);

    void cancelMyOrder(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId 订单编号
     */
    void cancelOrder(Long orderId);

    /**
     * 完成支付
     *
     * @param orderId 订单编号
     */
    void payComplete(Long orderId);

    /**
     * 添加备注
     *
     * @param remake 备注
     */
    void putRemake(CspOrderRemake remake);


    /**
     * 查询userId的订单信息
     *
     * @param userId 用户的id
     * @param page   分页参数
     * @param query  查询参数
     */
    void clientPage(String userId, Page<Order> page, OrderPageQuery query);

    /**
     * 管理端（后台）查询订单信息
     *
     * @param page  分页参数
     * @param query 查询参数
     */
    List<ManageOrderPage> managePage(Page<Order> page, OrderManagePageQuery query);


    void customerBuy(MsCustomerBuyVo customerBuy);

    /**
     * 查询订单快照
     *
     * @param orderId 订单id
     * @return
     */
    String getOrderSnapshot(Long orderId);


    OrderUseTemplateResp useTemplate(OrderUseTemplate req);

    /**
     * 前端使用模板时名称验证
     *
     * @param
     * @return "1" 有重复 "0" 无重复
     * @author zy.qiu
     * @createdTime 2023/12/5 18:23
     */
    String useTemplateNameCheck(OrderUseTemplateNameCheckReq req);


    /**
     * 使用模板正常情况下删除变量，保持和使用模板在同一个线程
     */
    void commit(Long theadId);

    /**
     * 保存并启动撤销送审，保持和使用模板在同一个线程
     */
    void revokeReview(Long theadId);


    MsSummaryDetailVO getSummaryById(Long orderId);

    PageResult<ManageOrderPage> orderPage(OperationOrderPageReq req);

    void orderIsPay(Long orderId);

    void orderCancel(Long orderId);

    void orderRemark(OperationOrderRemarkReq req);

    void checkModulePermission();
}
