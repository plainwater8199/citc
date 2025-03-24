package com.citc.nce.mall;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspPermission;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspServiceType;
import com.citc.nce.robot.api.mall.variable.MallRobotVariableApi;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableDeleteReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableDetailReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableQueryListReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableSaveReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableUpdateReq;
import com.citc.nce.robot.api.mall.variable.vo.resp.MallRobotVariableDetailResp;
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
@Api(value = "MallRobotVariableController", tags = "扩展商城-变量管理")
public class MallRobotVariableController {

    @Resource
    private MallRobotVariableApi mallRobotVariableApi;

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotVariable/save")
    @ApiOperation(value = "新增", notes = "新增")
    public int save(@RequestBody @Valid MallRobotVariableSaveReq req) {
        return mallRobotVariableApi.save(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotVariable/update")
    @ApiOperation(value = "更新", notes = "更新")
    public int update(@RequestBody @Valid MallRobotVariableUpdateReq req) {
        return mallRobotVariableApi.update(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotVariable/delete")
    @ApiOperation(value = "删除", notes = "删除")
    public int delete(@RequestBody @Valid MallRobotVariableDeleteReq req) {
        return mallRobotVariableApi.delete(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotVariable/queryDetail")
    @ApiOperation(value = "指令详情", notes = "指令详情")
    public MallRobotVariableDetailResp queryDetail(@RequestBody @Valid MallRobotVariableDetailReq req) {
        return mallRobotVariableApi.queryDetail(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotVariable/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    public PageResult<MallRobotVariableDetailResp> queryList(@RequestBody MallRobotVariableQueryListReq req) {
        return mallRobotVariableApi.queryList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/robotVariable/queryListByIds")
    @ApiOperation(value = "通过ids获取列表", notes = "通过ids获取列表")
    public List<MallRobotVariableDetailResp> queryListByIds(@RequestBody List<String> ids) {
        return mallRobotVariableApi.listByIdsDel(ids);
    }
}
