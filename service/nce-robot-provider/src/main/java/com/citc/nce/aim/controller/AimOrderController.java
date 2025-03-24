package com.citc.nce.aim.controller;

import com.citc.nce.aim.AimOrderApi;
import com.citc.nce.aim.service.AimOrderService;
import com.citc.nce.aim.vo.AimOrderDeleteReq;
import com.citc.nce.aim.vo.AimOrderEditReq;
import com.citc.nce.aim.vo.AimOrderQueryListReq;
import com.citc.nce.aim.vo.AimOrderQueryReq;
import com.citc.nce.aim.vo.AimOrderResp;
import com.citc.nce.aim.vo.AimOrderSaveReq;
import com.citc.nce.aim.vo.AimOrderUpdateStatusReq;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>挂短-订单</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:59
 */
@RestController
public class AimOrderController implements AimOrderApi {

    @Resource
    AimOrderService service;


    @Override
    public int save(AimOrderSaveReq req) {
        return service.save(req);
    }

    @Override
    public int edit(AimOrderEditReq req) {
        return service.edit(req);
    }

    @Override
    public int updateStatus(AimOrderUpdateStatusReq req) {
        return service.updateStatus(req);
    }

    @Override
    public int delete(AimOrderDeleteReq req) {
        return service.delete(req);
    }

    @Override
    public AimOrderResp queryOrderById(AimOrderQueryReq req){
        return service.queryOrderById(req);
    }

    @Override
    public AimOrderResp queryEnabledOrderByProjectId(AimOrderQueryReq req){
        return service.queryEnabledOrderByProjectId(req);
    }

    @Override
    public PageResult<AimOrderResp> queryOrderList(AimOrderQueryListReq req) {
        return service.queryOrderList(req);
    }
}
