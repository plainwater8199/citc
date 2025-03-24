package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.dao.RobotAccountDao;
import com.citc.nce.robot.entity.RobotAccountDo;
import com.citc.nce.robot.service.RobotAccountService;
import com.citc.nce.robot.vo.RobotAccountPageReq;
import com.citc.nce.robot.vo.RobotAccountPageResultResp;
import com.citc.nce.robot.vo.RobotAccountReq;
import com.citc.nce.robot.vo.RobotAccountResp;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 14:36
 * @Version: 1.0
 * @Description:
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class RobotAccountServiceImpl implements RobotAccountService {

    @Resource
    RobotAccountDao robotAccountDao;

    @Resource
    AccountManagementApi accountManagementApi;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Override
    public RobotAccountPageResultResp getRobotAccountList(RobotAccountPageReq robotAccountPageReq) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isNotEmpty(robotAccountPageReq.getAccount())) {
            wrapper.eq("account", robotAccountPageReq.getAccount());
        }
        if (null != robotAccountPageReq.getChannelType()) {
            wrapper.eq("channel_type", robotAccountPageReq.getChannelType());
        }

        if (StringUtils.isNotEmpty(robotAccountPageReq.getMobileNum())) {
            wrapper.like("mobile_num", robotAccountPageReq.getMobileNum());
        }
        if(StringUtils.isNotEmpty(robotAccountPageReq.getConversationId())){
            wrapper.eq("conversation_id", robotAccountPageReq.getConversationId());
        }
        if(StringUtils.isBlank(robotAccountPageReq.getCreate())){
            String userId = SessionContextUtil.getUser().getUserId();
            if(!StringUtils.equals(userId,superAdministratorUserIdConfigure.getSuperAdministrator())){
                wrapper.eq("creator", userId);
            }
        }else {
            wrapper.eq("creator", robotAccountPageReq.getCreate());
        }
        wrapper.orderByDesc("create_time");
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(robotAccountPageReq.getPageNo());
        pageParam.setPageSize(robotAccountPageReq.getPageSize());
        PageResult pageResult = robotAccountDao.selectPage(pageParam, wrapper);
        List<RobotAccountResp> robotAccountResps = BeanUtil.copyToList(pageResult.getList(), RobotAccountResp.class);
        return new RobotAccountPageResultResp(robotAccountResps,pageResult.getTotal(),robotAccountPageReq.getPageNo());
    }

    @Override
    public int saveRobotAccount(RobotAccountReq robotAccountReq) {
        RobotAccountDo robotAccountDo = new RobotAccountDo();
        BeanUtil.copyProperties(robotAccountReq, robotAccountDo);
        robotAccountDo.setCreateTime(new Date());
        AccountManagementResp accountManagementById = accountManagementApi.getAccountManagementById(robotAccountReq.getChatbotAccountId());
        robotAccountDo.setAccountName(accountManagementById.getAccountName());
        return robotAccountDao.insert(robotAccountDo);
    }
}
