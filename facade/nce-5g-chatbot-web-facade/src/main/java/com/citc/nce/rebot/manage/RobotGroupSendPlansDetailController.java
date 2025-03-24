package com.citc.nce.rebot.manage;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.api.RobotGroupSendPlansDetailApi;
import com.citc.nce.robot.req.DeleteReq;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetailPageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(value = "order",tags = "发送计划--发送计划详情")
@RestController
public class RobotGroupSendPlansDetailController {

    @Resource
    RobotGroupSendPlansDetailApi robotGroupSendPlansDetailApi;

    @ApiOperation(value = "根据id查询")
    @PostMapping("/group/detail/queryById")
    public RobotGroupSendPlansDetail queryById(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        return robotGroupSendPlansDetailApi.queryById(robotGroupSendPlansDetailReq);
    }

    @ApiOperation(value = "根据planId查询所有的盒子信息")
    @PostMapping("/group/detail/selectAllByPlanId")
    public List<RobotGroupSendPlansDetail> selectAllByPlanId(@RequestBody RobotGroupSendPlansDetailReq req) {
        return robotGroupSendPlansDetailApi.selectAllByPlanId(req);
    }

    @ApiOperation(value = "分页查询计划")
    @PostMapping("/group/detail/queryByPage")
    @XssCleanIgnore
    public PageResult queryByPage(@RequestBody RobotGroupSendPlansDetailPageParam robotGroupSendPlansDetailPageParam) {
        return robotGroupSendPlansDetailApi.queryByPage(robotGroupSendPlansDetailPageParam);
    }

    @ApiOperation(value = "保存计划")
    @PostMapping("/group/detail/insert")
    public Long insert(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        return robotGroupSendPlansDetailApi.insert(robotGroupSendPlansDetailReq);
    }

    @ApiOperation(value = "更新计划")
    @PostMapping("/group/detail/update")
    public void update(@RequestBody @Valid RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        robotGroupSendPlansDetailApi.update(robotGroupSendPlansDetailReq);
    }

    @ApiOperation(value = "根据id删除")
    @PostMapping("/group/detail/deleteById")
    public void deleteById(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        robotGroupSendPlansDetailApi.deleteById(robotGroupSendPlansDetailReq);
    }

    @ApiOperation(value = "根据id批量删除")
    @PostMapping("/group/detail/batchDelete")
    public void batchDelete(@RequestBody DeleteReq deleteReq){
        robotGroupSendPlansDetailApi.batchDelete(deleteReq);
    }
}
