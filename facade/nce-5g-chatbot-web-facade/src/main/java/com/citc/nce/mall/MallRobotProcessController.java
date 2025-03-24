package com.citc.nce.mall;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspPermission;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspServiceType;
import com.citc.nce.robot.api.mall.process.MallRobotProcessApi;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessDeleteReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessDetailReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessQueryListReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerDetailReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessUpdateReq;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessResp;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessTriggerDetailResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 19:35
 */
@RestController
@Api(value = "MallRobotProcessController", tags = "扩展商城-流程管理")
public class MallRobotProcessController {

    @Resource
    private MallRobotProcessApi mallRobotProcessApi;

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/save")
    @ApiOperation(value = "新增流程", notes = "新增流程")
    public int save(@RequestBody @Valid MallRobotProcessSaveReq req) {
        return mallRobotProcessApi.save(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/update")
    @ApiOperation(value = "更新流程", notes = "更新流程")
    public int update(@RequestBody @Valid MallRobotProcessUpdateReq req) {
        return mallRobotProcessApi.update(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/delete")
    @ApiOperation(value = "删除流程", notes = "删除流程")
    public int delete(@RequestBody @Valid MallRobotProcessDeleteReq req) {
        return mallRobotProcessApi.delete(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/queryDetail")
    @ApiOperation(value = "流程详情", notes = "流程详情")
    public MallRobotProcessResp queryDetail(@RequestBody @Valid MallRobotProcessDetailReq req) {
        return mallRobotProcessApi.queryDetail(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/queryList")
    @ApiOperation(value = "流程列表查询", notes = "流程列表查询")
    public PageResult<MallRobotProcessResp> queryList(@RequestBody MallRobotProcessQueryListReq req) {
        return mallRobotProcessApi.queryList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/addTrigger")
    @ApiOperation(value = "新增触发器", notes = "新增触发器")
    public int addTrigger(@RequestBody @Valid MallRobotProcessTriggerSaveReq req) {
        return mallRobotProcessApi.addTrigger(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotProcess/queryTriggerDetail")
    @ApiOperation(value = "查询触发器详情", notes = "查询触发器详情")
    public MallRobotProcessTriggerDetailResp queryTriggerDetail(@RequestBody @Valid MallRobotProcessTriggerDetailReq req) {
        return mallRobotProcessApi.queryTriggerDetail(req);
    }
}
