package com.citc.nce.rebot.manage;

import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.api.RobotGroupSendPlanDescApi;
import com.citc.nce.robot.req.RobotGroupSendPlanDescReq;
import com.citc.nce.robot.vo.MediaAccountResp;
import com.citc.nce.robot.vo.RobotGroupSendPlanDesc;
import com.citc.nce.robot.vo.RobotGroupSendPlanDescPageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "order",tags = "发送计划--发送计划画布")
@RestController
public class RobotGroupSendPlanDescController {

    @Resource
    RobotGroupSendPlanDescApi robotGroupSendPlanDescApi;

    @ApiOperation(value = "根据id查询")
    @PostMapping("/group/desc/queryById")
    public RobotGroupSendPlanDesc queryById(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        return robotGroupSendPlanDescApi.queryById(robotGroupSendPlanDescReq);
    }

    @ApiOperation(value = "分页查询计划")
    @PostMapping("/group/desc/queryByPage")
    public PageResult queryByPage(@RequestBody RobotGroupSendPlanDescPageParam robotGroupSendPlanDescPageParam) {
        return robotGroupSendPlanDescApi.queryByPage(robotGroupSendPlanDescPageParam);
    }

    @ApiOperation(value = "保存计划")
    @PostMapping("/group/desc/insert")
    public void insert(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        robotGroupSendPlanDescApi.insert(robotGroupSendPlanDescReq);
    }

    @ApiOperation(value = "更新计划")
    @PostMapping("/group/desc/update")
    public void update(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        robotGroupSendPlanDescApi.update(robotGroupSendPlanDescReq);
    }

    @ApiOperation(value = "根据id删除")
    @PostMapping("/group/desc/deleteById")
    public void deleteById(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        robotGroupSendPlanDescApi.deleteById(robotGroupSendPlanDescReq);
    }

    @ApiOperation(value = "画布账号查询")
    @PostMapping("/group/desc/queryPlanAccount")
    public MediaAccountResp queryPlanAccount(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        return robotGroupSendPlanDescApi.queryPlanAccount(robotGroupSendPlanDescReq);
    }
}
