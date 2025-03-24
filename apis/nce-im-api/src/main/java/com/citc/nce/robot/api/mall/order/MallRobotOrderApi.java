package com.citc.nce.robot.api.mall.order;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderDeleteReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderDetailReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderQueryListReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderSaveReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderUpdateReq;
import com.citc.nce.robot.api.mall.order.resp.RobotOrderResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>扩展商城-模板</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 15:35
 */
@FeignClient(value = "im-service", contextId = "MallRobotOrderApi", url = "${im:}")
public interface MallRobotOrderApi {

    @PostMapping("/mall/robotOrder/save")
    int save(@RequestBody @Valid MallRobotOrderSaveReq req);

    @PostMapping("/mall/robotOrder/update")
    int update(@RequestBody @Valid MallRobotOrderUpdateReq req);

    @PostMapping("/mall/robotOrder/delete")
    int delete(@RequestBody @Valid MallRobotOrderDeleteReq req);

    @PostMapping("/mall/robotOrder/queryDetail")
    RobotOrderResp queryDetail(@RequestBody @Valid MallRobotOrderDetailReq req);

    @PostMapping("/mall/robotOrder/queryList")
    PageResult<RobotOrderResp> queryList(@RequestBody MallRobotOrderQueryListReq req);
    @PostMapping("/mall/robotOrder/queryListByIds")
    List<RobotOrderResp> listByIdsDel(@RequestBody List<String> ids);
}
