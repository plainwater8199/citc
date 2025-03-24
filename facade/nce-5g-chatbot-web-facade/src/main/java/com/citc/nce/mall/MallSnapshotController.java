package com.citc.nce.mall;

import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspPermission;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspServiceType;
import com.citc.nce.robot.api.mall.snapshot.MallSnapshotApi;
import com.citc.nce.robot.api.mall.snapshot.req.MallRobotOrderSaveOrUpdateReq;
import com.citc.nce.robot.api.mall.snapshot.resp.MallRobotOrderSaveOrUpdateResp;
import com.citc.nce.security.annotation.HasCsp;
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
@Api(value = "MallSnapshotController", tags = "扩展商城-快照管理")
public class MallSnapshotController {

    @Resource
    private MallSnapshotApi mallSnapshotApi;

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/snapshot/saveOrUpdate")
    @ApiOperation(value = "新增或编辑", notes = "新增或编辑")
    @HasCsp
    public MallRobotOrderSaveOrUpdateResp saveOrUpdate(@RequestBody @Valid MallRobotOrderSaveOrUpdateReq req) {
        return mallSnapshotApi.saveOrUpdate(req);
    }
}
