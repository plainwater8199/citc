package com.citc.nce.aim.privatenumber.controller;

import com.citc.nce.aim.privatenumber.PrivateNumberOrderApi;
import com.citc.nce.aim.privatenumber.service.PrivateNumberOrderService;
import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class PrivateNumberOrderController implements PrivateNumberOrderApi {

    @Resource
    PrivateNumberOrderService service;


    @Override
    public int save(PrivateNumberOrderSaveReq req) {
        return service.save(req);
    }

    @Override
    public int edit(PrivateNumberOrderEditReq req) {
        return service.edit(req);
    }

    @Override
    public int updateStatus(PrivateNumberOrderUpdateStatusReq req) {
        return service.updateStatus(req);
    }

    @Override
    public int delete(PrivateNumberOrderDeleteReq req) {
        return service.delete(req);
    }

    @Override
    public PrivateNumberOrderResp queryOrderById(PrivateNumberOrderQueryReq req) {
        return service.queryOrderById(req);
    }

    @Override
    public PrivateNumberOrderResp queryEnabledOrderByProjectId(PrivateNumberOrderQueryReq req) {
        return service.queryEnabledOrderByProjectId(req);
    }

    @Override
    public PageResult<PrivateNumberOrderResp> queryOrderList(PrivateNumberOrderQueryListReq req) {
        return service.queryOrderList(req);
    }
}
