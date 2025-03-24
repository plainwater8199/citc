package com.citc.nce.im.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountQueryDetailReq;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.im.service.RobotGroupSendPlanDescService;
import com.citc.nce.robot.api.RobotGroupSendPlanDescApi;
import com.citc.nce.robot.req.RobotGroupSendPlanDescContainShortUrlReq;
import com.citc.nce.robot.req.RobotGroupSendPlanDescReq;
import com.citc.nce.robot.vo.MediaAccountResp;
import com.citc.nce.robot.vo.RobotGroupSendPlanDesc;
import com.citc.nce.robot.vo.RobotGroupSendPlanDescPageParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.controller
 * @Author:
 * @CreateTime: 2022-07-01  16:03
 * @Description:
 * @Version: 1.0
 */
@RestController
public class RobotGroupSendPlanDescController implements RobotGroupSendPlanDescApi {

    @Resource
    RobotGroupSendPlanDescService robotGroupSendPlanDescService;

    @Resource
    RobotGroupSendPlansDao robotGroupSendPlansDao;

    @Resource
    AccountManagementApi accountManagementApi;

    @Resource
    CspVideoSmsAccountApi cspVideoSmsAccountApi;

    @Resource
    CspSmsAccountApi cspSmsAccountApi;

    @Resource
    CspCustomerApi customerApi;
    @Resource
    private RobotGroupSendPlansDao plansDao;


    @Override
    @PostMapping("/group/desc/queryById")
    public RobotGroupSendPlanDesc queryById(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        plansDao.checkPlanIsOwn(robotGroupSendPlanDescReq.getPlanId());
        return robotGroupSendPlanDescService.queryById(robotGroupSendPlanDescReq.getPlanId());
    }

    @Override
    @PostMapping("/group/desc/queryByPage")
    public PageResult queryByPage(@RequestBody RobotGroupSendPlanDescPageParam robotGroupSendPlanDescPageParam) {
        return robotGroupSendPlanDescService.queryByPage(robotGroupSendPlanDescPageParam);
    }

    @Override
    @PostMapping("/group/desc/insert")
    public void insert(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        robotGroupSendPlanDescService.insert(robotGroupSendPlanDescReq);
    }

    @Override
    @PostMapping("/group/desc/update")
    public void update(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        robotGroupSendPlanDescService.update(robotGroupSendPlanDescReq);
    }

    @Override
    @PostMapping("/group/desc/deleteById")
    public void deleteById(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        robotGroupSendPlanDescService.deleteById(robotGroupSendPlanDescReq.getId());
    }

    @Override
    public MediaAccountResp queryPlanAccount(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        String userId = SessionContextUtil.getUser().getUserId();
        MediaAccountResp accountResp = new MediaAccountResp();
        List<String> accounts = new ArrayList<>();
        List<String> mediaAccounts = new ArrayList<>();
        List<String> shortMsgAccounts = new ArrayList<>();
        Long planId = robotGroupSendPlanDescReq.getPlanId();
        String permissions = customerApi.getUserPermission(userId);
        if (StringUtils.isEmpty(permissions)) {
            return accountResp;
        }
        RobotGroupSendPlansDo plansDo = robotGroupSendPlansDao.selectOne("id", planId);
        if (!plansDo.getCreator().equals(SessionContextUtil.getUser().getUserId())) {
            throw new BizException("该计划不是你的");
        }
        if (StringUtils.isNotEmpty(plansDo.getPlanAccount()) && permissions.contains("5")) {
            List<String> operators = Arrays.asList(plansDo.getPlanAccount().split(","));
            if (CollectionUtil.isNotEmpty(operators)) {
                operators.forEach(
                        o -> {
                            AccountManagementTypeReq req = new AccountManagementTypeReq();
                            req.setCreator(userId);
                            req.setAccountType(o);
                            AccountManagementResp management = accountManagementApi.getAccountManagementByAccountType(req);
                            accounts.add(management.getAccountName());
                        }
                );
            }
            accountResp.setAccounts(accounts);
        }
        if (StringUtils.isNotEmpty(plansDo.getRichMediaIds()) && permissions.contains("3")) {
            List<String> ids = Arrays.asList(plansDo.getRichMediaIds().split(","));
            ids.forEach(
                    o -> {
                        CspVideoSmsAccountQueryDetailReq req = new CspVideoSmsAccountQueryDetailReq();
                        req.setAccountId(o);
                        CspVideoSmsAccountDetailResp cspVideoSmsAccountDetailResp = cspVideoSmsAccountApi.queryDetail(req);
                        mediaAccounts.add(cspVideoSmsAccountDetailResp.getAccountName());
                    }
            );
            accountResp.setMediaAccounts(mediaAccounts);
        }
        if (StringUtils.isNotEmpty(plansDo.getShortMsgIds()) && permissions.contains("4")) {
            List<String> ids = Arrays.asList(plansDo.getShortMsgIds().split(","));
            ids.forEach(
                    o -> {
                        CspSmsAccountDetailResp smsAccountDetailResp = cspSmsAccountApi.queryDetailInner(o);
                        shortMsgAccounts.add(smsAccountDetailResp.getAccountName());
                    }
            );
            accountResp.setShortMsgAccounts(shortMsgAccounts);
        }
        return accountResp;
    }

    /**
     * 查询customer名下的群发计划中是否包含某短链
     *
     * @param req
     * @return 实例对象
     */
    @Override
    public boolean containList(RobotGroupSendPlanDescContainShortUrlReq req) {
        return robotGroupSendPlanDescService.containList(req.getShortUrls());
    }

    /**
     * 查询customer名下的群发计划中是否包含某5G阅信
     *
     * @param one
     * @return 实例对象
     */
    @Override
    public boolean containOne(@RequestParam("one")String one) {
        return robotGroupSendPlanDescService.containOne(one);
    }
}
