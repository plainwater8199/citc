package com.citc.nce.im.broadcast;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.robot.api.BroadcastApi;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2024/4/16 11:16
 */
@RestController
@RequestMapping("/broadcast")
@AllArgsConstructor
public class BroadcastPlanController implements BroadcastApi {
    private BroadcastPlanService broadcastPlanService;
    private AccountManagementApi accountManagementApi;
    private RobotGroupSendPlansDao robotGroupSendPlansDao;

    @RequestMapping("/start")
    public String start(@RequestParam Long id) {
        broadcastPlanService.startPlan(id);
        return "success";
    }

    @RequestMapping("/stop")
    public String stop(@RequestParam Long id) {
        broadcastPlanService.stopPlan(id);
        return "success";
    }

    @RequestMapping("/run")
    public void execute(@RequestParam Long id) {
        broadcastPlanService.runPlan (id);
    }

    @RequestMapping("/plan/chatbotAccount/init")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("初始化robot_group_plan_send表plan_chatbot_account字段为群发计划关联的5G账号")
    public String planChatbotAccountInit() {
        LambdaQueryWrapper<RobotGroupSendPlansDo> qw = new LambdaQueryWrapper<RobotGroupSendPlansDo>()
                .isNull(RobotGroupSendPlansDo::getPlanAccount)
                .or()
                .eq(RobotGroupSendPlansDo::getPlanAccount, "");
        List<RobotGroupSendPlansDo> planList = robotGroupSendPlansDao.selectList(qw);
        for (RobotGroupSendPlansDo plan : planList) {
            String planChatbotAccount = Arrays.stream(plan.getPlanAccount().split(","))
                    .map(operator -> accountManagementApi.getAccountManagementByAccountType(
                            new AccountManagementTypeReq()
                                    .setCreator(plan.getCreator())
                                    .setAccountType(operator)
                    ))
                    .map(AccountManagementResp::getChatbotAccount)
                    .collect(Collectors.joining(","));
            plan.setPlanChatbotAccount(planChatbotAccount);
        }
        robotGroupSendPlansDao.updateBatch(planList);
        return "success";
    }

}
