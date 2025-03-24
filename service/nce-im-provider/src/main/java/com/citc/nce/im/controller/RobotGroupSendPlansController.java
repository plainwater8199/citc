package com.citc.nce.im.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dataStatistics.MsgDataStatisticsApi;
import com.citc.nce.im.entity.RobotGroupSendPlanDescDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.mapper.RobotGroupSendPlanDescDao;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.im.service.RobotGroupSendPlansService;

import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.req.*;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import com.citc.nce.robot.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.controller
 * @Author:
 * @CreateTime: 2022-07-01  16:03
 * @Description:
 * @Version: 1.0
 */
@RestController
public class RobotGroupSendPlansController implements RobotGroupSendPlansApi {

    @Resource
    RobotGroupSendPlansService robotGroupSendPlansService;
    @Resource
    RobotGroupSendPlansDetailService robotGroupSendPlansDetailService;
    @Resource
    RobotGroupSendPlanDescDao robotGroupSendPlanDescDao;

    @Resource
    RobotGroupSendPlansDao robotGroupSendPlansDao;

    @Resource
    MsgDataStatisticsApi msgDataStatisticsApi;

    @Value("${userId.superAdministrator}")
    private String superAdministrator;

    @Override
    @PostMapping("/group/plans/queryById")
    public RobotGroupSendPlans queryById(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        RobotGroupSendPlans robotGroupSendPlans = checkPlanOwn(robotGroupSendPlansReq);
        return robotGroupSendPlans;
    }

    @Override
    public List<RobotGroupSendPlansAndChatbotAccount> getByGroupSendIds(List<String> selectPlanIds) {
        return robotGroupSendPlansService.getByGroupSendIds(selectPlanIds);
    }

    @Override
    public List<RobotGroupSendPlansAndChatbotAccount> selectByPlanName(RobotGroupSendPlansByPlanNameReq req) {
        return robotGroupSendPlansService.selectByPlanName(req);
    }

    private RobotGroupSendPlans checkPlanOwn(RobotGroupSendPlansReq robotGroupSendPlansReq) {
        RobotGroupSendPlans robotGroupSendPlans = robotGroupSendPlansService.queryById(robotGroupSendPlansReq.getId());
        if (Objects.isNull(robotGroupSendPlans)){
            throw new BizException("计划不存在："+robotGroupSendPlansReq.getId());
        }
        if (robotGroupSendPlans.getCreator() == null){
            throw new BizException("计划异常：创建者为空："+robotGroupSendPlansReq.getId());
        }
        if (!robotGroupSendPlans.getCreator().equals(SessionContextUtil.getUser().getUserId())) {
            throw new BizException("该计划不是你的");
        }
        return robotGroupSendPlans;
    }

    @Override
    @PostMapping("/group/plans/queryByPage")
    public PageResult queryByPage(@RequestBody RobotGroupSendPlansPageParam robotGroupSendPlansPageParam) {
        return robotGroupSendPlansService.queryByPage(robotGroupSendPlansPageParam);
    }

    @Override
    @PostMapping("/group/plans/insert")
    public Long insert(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        return robotGroupSendPlansService.insert(robotGroupSendPlansReq);
    }

    @Override
    @PostMapping("/group/plans/update")
    public void update(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        checkPlanOwn(robotGroupSendPlansReq);
        robotGroupSendPlansService.update(robotGroupSendPlansReq);
    }

    @Override
    @PostMapping("/group/plans/deleteById")
    public void deleteById(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        robotGroupSendPlansService.deleteById(robotGroupSendPlansReq.getId());
    }

    /**
     * * 1 联系人组
     * * 2 模板
     *
     * @return
     */
    @Override
    @PostMapping("/group/plans/checkPlansUserSourceId")
    public Boolean checkPlansUserTemplate(@RequestBody RobotGroupSendPlanIdReq robotGroupSendPlanIdReq) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("deleted", 0);
        switch (robotGroupSendPlanIdReq.getType()) {
            case 1:
                queryWrapper.like("plan_desc", "\"planGroup\":\"" + robotGroupSendPlanIdReq.getId() + "\"");
                break;
            case 2:
                queryWrapper.like("plan_desc", "\"templateId\":" + robotGroupSendPlanIdReq.getId());
                break;
            case 3:
                queryWrapper.like("plan_desc", "\"mediaTemplateId\":" + robotGroupSendPlanIdReq.getId());
                break;
            case 4:
                queryWrapper.like("plan_desc", "\"shortMsgTemplateId\":" + robotGroupSendPlanIdReq.getId());
                break;
            default:
                return true;
        }
        List<Long> planIdList = new ArrayList<>();
        List<RobotGroupSendPlanDescDo> robotGroupSendPlanDescDoList = robotGroupSendPlanDescDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(robotGroupSendPlanDescDoList)) {
            robotGroupSendPlanDescDoList.forEach(robotGroupSendPlanDescDo -> {
                Long planId = robotGroupSendPlanDescDo.getPlanId();
                planIdList.add(planId);
            });
        }
        if (!CollectionUtils.isEmpty(planIdList)) {
            QueryWrapper GroupSendPlansQueryWrapper = new QueryWrapper();
            GroupSendPlansQueryWrapper.eq("deleted", 0);
            GroupSendPlansQueryWrapper.in("id", planIdList);
            /*List<Integer> planStatusList = new ArrayList<>();
            planStatusList.add(1);
            planStatusList.add(3);*/
            // GroupSendPlansQueryWrapper.in("plan_status",planStatusList);
            GroupSendPlansQueryWrapper.eq("plan_status", 1);
            List list = robotGroupSendPlansDao.selectList(GroupSendPlansQueryWrapper);
            if (list.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    @PostMapping("/group/plans/checkPlansUserAccount")
    public Boolean checkPlansUserAccount(RobotGroupSendPlanAccountReq robotGroupSendPlanAccountReq) {
        QueryWrapper queryWrapper = new QueryWrapper();
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministrator)) {
            queryWrapper.eq("creator", userId);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("plan_account", robotGroupSendPlanAccountReq.getAccountType());
        /*List<Integer> planStatusList = new ArrayList<>();
        planStatusList.add(1);
        planStatusList.add(3);
        queryWrapper.in("plan_status",planStatusList);*/
        queryWrapper.eq("plan_status", 1);
        List list = robotGroupSendPlansDao.selectList(queryWrapper);
        return list.size() > 0;
    }

    @Override
    @PostMapping("/group/plans/selectIdsByStatus")
    public List<Long> selectIdsByStatus(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq) {
        return robotGroupSendPlansService.selectIdsByStatus(robotGroupSendPlansReq);

    }

    @Override
    public List<Long> selectAllPlanIds() {
        return robotGroupSendPlansService.selectAllPlanIds();
    }

    @Override
    @PostMapping("/group/plans/selectAll")
    public List<PlanResp> selectAll() {
        String userId = SessionContextUtil.getUser().getUserId();
        SendPageReq sendPageReq = new SendPageReq();
        sendPageReq.setCreator(userId);
        List<PlanResp> planResps = robotGroupSendPlansDao.selectAll(sendPageReq);
        if(!CollectionUtils.isEmpty(planResps)){
            //查询所有计划是否有发送记录，如果没有就删除改计划
            Map<Long,Long> planSendList = msgDataStatisticsApi.queryIsSendPlanId();
            planResps.removeIf(planResp -> !planSendList.containsValue(planResp.getId()));
        }
        return planResps;
    }

    @Override
    public String selectPlanChatbotAccount(RobotGroupSendPlansReq robotGroupSendPlansReq) {
        return robotGroupSendPlansService.selectPlanChatbotAccount(robotGroupSendPlansReq);
    }

    /**
     * 移除客户下所有群发计划关联账号包含了chatbot账号的chatbot信息
     *
     * @param chatbotAccount
     * @param accountType
     * @param planChatbotAccountSupplier
     */
    @Override
    public void removeChatbotAccount(String chatbotAccount, String accountType, String planChatbotAccountSupplier) {
        robotGroupSendPlansService.removeChatbotAccount(chatbotAccount,accountType,planChatbotAccountSupplier);
    }
}
