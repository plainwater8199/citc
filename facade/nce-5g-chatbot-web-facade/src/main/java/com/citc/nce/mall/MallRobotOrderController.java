package com.citc.nce.mall;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspPermission;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspServiceType;
import com.citc.nce.robot.api.mall.order.MallRobotOrderApi;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderDeleteReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderDetailReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderQueryListReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderSaveReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderUpdateReq;
import com.citc.nce.robot.api.mall.order.resp.RobotOrderResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 19:35
 */
@RestController
@Api(value = "MallRobotOrderController", tags = "扩展商城-指令管理")
public class MallRobotOrderController {

    @Resource
    private MallRobotOrderApi mallRobotOrderApi;

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotOrder/save")
    @ApiOperation(value = "新增", notes = "新增")
    public int save(@RequestBody @Valid MallRobotOrderSaveReq req) {
        return mallRobotOrderApi.save(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotOrder/update")
    @ApiOperation(value = "更新", notes = "更新")
    public int update(@RequestBody @Valid MallRobotOrderUpdateReq req) {
        return mallRobotOrderApi.update(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotOrder/delete")
    @ApiOperation(value = "删除", notes = "删除")
    public int delete(@RequestBody @Valid MallRobotOrderDeleteReq req) {
        return mallRobotOrderApi.delete(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotOrder/queryDetail")
    @ApiOperation(value = "指令详情", notes = "指令详情")
    public RobotOrderResp queryDetail(@RequestBody @Valid MallRobotOrderDetailReq req) {
        return mallRobotOrderApi.queryDetail(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotOrder/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    public PageResult<RobotOrderResp> queryList(@RequestBody MallRobotOrderQueryListReq req) {
        return mallRobotOrderApi.queryList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotOrder/queryListByIds")
    @ApiOperation(value = "通过ids获取列表", notes = "通过ids获取列表")
    public List<RobotOrderResp> queryListByIds(@RequestBody List<String> ids) {
        return mallRobotOrderApi.listByIdsDel(ids);
    }
}
