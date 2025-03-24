package com.citc.nce.rebot.manage;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.req.RobotGroupSendPlanAccountReq;
import com.citc.nce.robot.req.RobotGroupSendPlanIdReq;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import com.citc.nce.robot.vo.PlanResp;
import com.citc.nce.robot.vo.RobotGroupSendPlans;
import com.citc.nce.robot.vo.RobotGroupSendPlansPageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(value = "order", tags = "发送计划")
@RestController
public class RobotGroupSendPlansController {

    @Resource
    RobotGroupSendPlansApi robotGroupSendPlansApi;

    @ApiOperation(value = "根据id查询")
    @PostMapping("/group/plans/queryById")
    public RobotGroupSendPlans queryById(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        return robotGroupSendPlansApi.queryById(robotGroupSendPlansReq);
    }

    @ApiOperation(value = "分页查询计划")
    @PostMapping("/group/plans/queryByPage")
    public PageResult queryByPage(@RequestBody RobotGroupSendPlansPageParam robotGroupSendPlansPageParam) {
        return robotGroupSendPlansApi.queryByPage(robotGroupSendPlansPageParam);
    }

    @ApiOperation(value = "保存计划")
    @PostMapping("/group/plans/insert")
    public Long insert(@RequestBody @Valid RobotGroupSendPlansReq robotGroupSendPlansReq) {
        String fiveAccount = robotGroupSendPlansReq.getPlanAccount();
        if (StringUtils.isNotBlank(fiveAccount) && fiveAccount.contains("硬核桃") && fiveAccount.indexOf(",") > 0) {
            throw new BizException("三大运营商和硬核桃的消息账号互斥");
        }
        return robotGroupSendPlansApi.insert(robotGroupSendPlansReq);
    }

    @ApiOperation(value = "更新计划")
    @PostMapping("/group/plans/update")
    public void update(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        String fiveAccount = robotGroupSendPlansReq.getPlanAccount();
        if (StringUtils.isNotBlank(fiveAccount) && fiveAccount.contains("硬核桃") && fiveAccount.indexOf(",") > 0) {
            throw new BizException("三大运营商和硬核桃的消息账号互斥");
        }
        robotGroupSendPlansApi.update(robotGroupSendPlansReq);
    }

    @ApiOperation(value = "根据id删除")
    @PostMapping("/group/plans/deleteById")
    public void deleteById(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        robotGroupSendPlansApi.deleteById(robotGroupSendPlansReq);
    }

    @GetMapping("/group/plans/selectAll")
    public List<PlanResp> selectAll() {
        return robotGroupSendPlansApi.selectAll();
    }

    /**
     * 检查计划是否有使用模板
     *
     *  type 1 联系人组
     *      2 模板
     * @return 是否成功
     */
    @ApiOperation(value = "检验联系人组或消息模板是否在群发发送中")
    @PostMapping("/group/plans/checkPlansUserSourceId")
    public Boolean checkPlansUserTemplate(@RequestBody RobotGroupSendPlanIdReq robotGroupSendPlanIdReq) {
        return robotGroupSendPlansApi.checkPlansUserTemplate(robotGroupSendPlanIdReq);
    }

    /**
     * 检查计划是否有使用账号
     *
     * @return 是否成功
     */
    @ApiOperation(value = "检验5G账户是否在群发发送中")
    @PostMapping("/group/plans/checkPlansUserAccount")
    public Boolean checkPlansUserAccount(@RequestBody RobotGroupSendPlanAccountReq robotGroupSendPlanAccountReq) {
        return robotGroupSendPlansApi.checkPlansUserAccount(robotGroupSendPlanAccountReq);
    }
}
