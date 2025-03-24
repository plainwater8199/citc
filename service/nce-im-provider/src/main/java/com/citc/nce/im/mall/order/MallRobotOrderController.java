package com.citc.nce.im.mall.order;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.mall.order.service.MallRobotOrderService;
import com.citc.nce.robot.api.mall.order.MallRobotOrderApi;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderDeleteReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderDetailReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderQueryListReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderSaveReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderUpdateReq;
import com.citc.nce.robot.api.mall.order.resp.RobotOrderResp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
public class MallRobotOrderController implements MallRobotOrderApi {

    @Resource
    private MallRobotOrderService service;

    @Override
    public int save(MallRobotOrderSaveReq req) {
        return service.save(req);
    }

    @Override
    public int update(MallRobotOrderUpdateReq req) {
        return service.update(req);
    }

    @Override
    public int delete(MallRobotOrderDeleteReq req) {
        return service.delete(req.getId());
    }

    @Override
    public RobotOrderResp queryDetail(MallRobotOrderDetailReq req) {
        return service.queryDetail(req.getId());
    }

    @Override
    public PageResult<RobotOrderResp> queryList(MallRobotOrderQueryListReq req) {
        return service.queryList(req.getPageNo(), req.getPageSize(), null);
    }

    @Override
    public List<RobotOrderResp> listByIdsDel(List<String> ids) {
        return service.listByIdsDel(ids);
    }
}
