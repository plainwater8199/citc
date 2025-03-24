package com.citc.nce.im.mall.order.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderSaveReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderUpdateReq;
import com.citc.nce.robot.api.mall.order.resp.RobotOrderResp;

import java.util.List;

public interface MallRobotOrderService {

    int save(MallRobotOrderSaveReq req);

    int update(MallRobotOrderUpdateReq req);

    int delete(Long req);

    RobotOrderResp queryDetail(Long req);

    PageResult<RobotOrderResp> queryList(Integer pageNo, Integer pageSize, String userId);

    List<RobotOrderResp> listByIdsDel(List<String> ids);
}
