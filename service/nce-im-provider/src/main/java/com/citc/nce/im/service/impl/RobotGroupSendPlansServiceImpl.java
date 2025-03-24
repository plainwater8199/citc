package com.citc.nce.im.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.im.mapper.RobotGroupSendPlansDetailDao;
import com.citc.nce.im.service.RobotGroupSendPlansService;
import com.citc.nce.im.util.WrapperUtil;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.robot.req.RobotGroupSendPlansByPlanNameReq;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import com.citc.nce.robot.vo.RobotGroupSendPlans;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndChatbotAccount;
import com.citc.nce.robot.vo.RobotGroupSendPlansPageParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RobotGroupSendPlansServiceImpl extends ServiceImpl<RobotGroupSendPlansDao, RobotGroupSendPlansDo> implements RobotGroupSendPlansService {

    @Resource
    RobotGroupSendPlansDao robotGroupSendPlansDao;

    @Resource
    RobotGroupSendPlansDetailDao detailDao;

    @Resource
    CspVideoSmsAccountApi cspVideoSmsAccountApi;

    @Resource
    CspSmsAccountApi cspSmsAccountApi;

    @Autowired
    private AccountManagementApi accountManagementApi;


    @Value("${userId.superAdministrator}")
    private String superAdministrator;

    @Override
    public RobotGroupSendPlans queryById(Long id) {
        RobotGroupSendPlans robotGroupSendPlans = new RobotGroupSendPlans();
        RobotGroupSendPlansDo robotGroupSendPlansDo = robotGroupSendPlansDao.selectById(id);
        if (null != robotGroupSendPlansDo) {
            BeanUtils.copyProperties(robotGroupSendPlansDo, robotGroupSendPlans);
        }
        return robotGroupSendPlans;
    }

    @Override
    public PageResult queryByPage(RobotGroupSendPlansPageParam robotGroupSendPlansPageParam) {
        QueryWrapper queryWrapper = WrapperUtil.entity2Wrapper(robotGroupSendPlansPageParam.getRobotGroupSendPlans());
        queryWrapper.orderByDesc("create_time");
        queryWrapper.eq("deleted", 0);
        filterUsers(queryWrapper);
        PageResult pageResult = robotGroupSendPlansDao.selectPage(robotGroupSendPlansPageParam, queryWrapper);
        List<RobotGroupSendPlansDo> robotGroupSendPlansDoList = pageResult.getList();
        List<RobotGroupSendPlansReq> robotGroupSendPlansReqList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(robotGroupSendPlansDoList)) {
            for (RobotGroupSendPlansDo robotGroupSendPlansDo : robotGroupSendPlansDoList) {
                RobotGroupSendPlansReq robotGroupSendPlansReq = new RobotGroupSendPlansReq();
                BeanUtils.copyProperties(robotGroupSendPlansDo, robotGroupSendPlansReq);
                String richMediaIds = robotGroupSendPlansReq.getRichMediaIds();
                if (StringUtils.isNotEmpty(richMediaIds)) {
                    List<String> richMediaIdNews = new ArrayList<>();
                    String[] richMediaIdList = richMediaIds.split(",");
                    for (String richMediaId : richMediaIdList) {
                        CspVideoSmsAccountDetailResp cspVideoSmsAccountDetailResp = cspVideoSmsAccountApi.queryDetailInner(richMediaId);
                        if (StringUtils.isNotEmpty(cspVideoSmsAccountDetailResp.getAccountId())) {
                            richMediaIdNews.add(richMediaId);
                        }
                    }
                    robotGroupSendPlansReq.setRichMediaIds(String.join(",", richMediaIdNews));
                }

                String shortMsgIds = robotGroupSendPlansReq.getShortMsgIds();
                if (StringUtils.isNotEmpty(shortMsgIds)) {
                    List<String> shortMsgIdNews = new ArrayList<>();
                    String[] shortMsgIdList = shortMsgIds.split(",");
                    for (String shortMsgId : shortMsgIdList) {
                        CspSmsAccountDetailResp cspSmsAccountDetailResp = cspSmsAccountApi.queryDetailInner(shortMsgId);
                        if (StringUtils.isNotEmpty(cspSmsAccountDetailResp.getAccountId())) {
                            shortMsgIdNews.add(shortMsgId);
                        }
                    }
                    robotGroupSendPlansReq.setShortMsgIds(String.join(",", shortMsgIdNews));
                }
                robotGroupSendPlansReqList.add(robotGroupSendPlansReq);
            }
        }
        pageResult.setList(robotGroupSendPlansReqList);
        return pageResult;
    }

    private void filterUsers(QueryWrapper queryWrapper) {
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministrator)) {
            queryWrapper.likeRight("creator", userId);
        }
    }

    @Override
    public Long insert(RobotGroupSendPlansReq robotGroupSendPlansReq) {
        if (StringUtils.isNotEmpty(robotGroupSendPlansReq.getRichMediaIds())) {
            String[] accountIds = robotGroupSendPlansReq.getRichMediaIds().split(",");
            verifyAccount(accountIds);
        }
        if (StringUtils.isNotEmpty(robotGroupSendPlansReq.getPlanAccount())) {
            String planChatbotAccount = Arrays.stream(robotGroupSendPlansReq.getPlanAccount().split(","))
                    .map(operator -> accountManagementApi.getAccountManagementByAccountType(
                            new AccountManagementTypeReq()
                                    .setCreator(SessionContextUtil.getLoginUser().getUserId())
                                    .setAccountType(operator)
                    ))
                    .map(AccountManagementResp::getChatbotAccount)
                    .collect(Collectors.joining(","));
            robotGroupSendPlansReq.setPlanChatbotAccount(planChatbotAccount);
        }
        RobotGroupSendPlansDo robotGroupSendPlansDo = new RobotGroupSendPlansDo();
        BeanUtils.copyProperties(robotGroupSendPlansReq, robotGroupSendPlansDo);
        if(robotGroupSendPlansDo.getPlanDuration() == null){
            robotGroupSendPlansDo.setPlanDuration("");
        }
        robotGroupSendPlansDao.insert(robotGroupSendPlansDo);
        return robotGroupSendPlansDo.getId();
    }

    /**
     * 移除客户下所有群发计划的关联账号包含了chatbot账号的chatbot信息
     *
     * @param chatbotAccount
     * @param accountType
     */
    @Override
    public void removeChatbotAccount(String chatbotAccount, String accountType,String planChatbotAccountSupplier) {
        QueryWrapper<RobotGroupSendPlansDo> queryWrapper=new QueryWrapper<>();
        filterUsers(queryWrapper);
        queryWrapper.eq("deleted",0);
        //待执行和已暂停的计划取消关联chatbotaccount，其他已执行过的忽略
        queryWrapper.in("plan_status",Arrays.asList(0,3));
        queryWrapper.like("plan_chatbot_account",chatbotAccount).like("plan_account",accountType).like("plan_chatbot_account_supplier",planChatbotAccountSupplier);
        List<RobotGroupSendPlansDo> robotGroupSendPlansDos=robotGroupSendPlansDao.selectList(queryWrapper);
        if(ObjectUtil.isEmpty(robotGroupSendPlansDos))
        {
            log.info("chatbot账号解绑群发计划，未找到记录，userId:{},plan_chatbot_account:{},plan_account:{},plan_chatbot_account_supplier:{}",SessionContextUtil.getUserId(),chatbotAccount,accountType,planChatbotAccountSupplier);
            return;
        }
        for (int i = 0; i < robotGroupSendPlansDos.size(); i++) {
            RobotGroupSendPlansDo robotGroupSendPlansDo=robotGroupSendPlansDos.get(i);
           List<String> chatbotAccounts= new ArrayList<>(Arrays.asList(robotGroupSendPlansDo.getPlanChatbotAccount().split(",")));
            List<String> planAccounts= new ArrayList<>(Arrays.asList(robotGroupSendPlansDo.getPlanAccount().split(",")));
            List<String> planChatbotAccountSuppliers= new ArrayList<>(Arrays.asList(robotGroupSendPlansDo.getPlanChatbotAccountSupplier().split(",")));

            int index=-1;
            for (int i1 = 0; i1 < chatbotAccounts.size(); i1++) {
                if(chatbotAccount.equals(chatbotAccounts.get(i1)))
                {
                    index=i1;
                    break;
                }
            }
            if(index==-1)continue;
            chatbotAccounts.remove(index);
            planAccounts.remove(index);
            planChatbotAccountSuppliers.remove(index);
            robotGroupSendPlansDo.setPlanChatbotAccount(Strings.join(chatbotAccounts).with(","));
            robotGroupSendPlansDo.setPlanAccount(Strings.join(planAccounts).with(","));
            robotGroupSendPlansDo.setPlanChatbotAccountSupplier(Strings.join(planChatbotAccountSuppliers).with(","));
        }
        robotGroupSendPlansDao.updateBatch(robotGroupSendPlansDos);
    }

    private void verifyAccount(String[] accountIds) {
        for (String id : accountIds) {
            CspVideoSmsAccountQueryDetailReq req = new CspVideoSmsAccountQueryDetailReq();
            req.setAccountId(id);
            CspVideoSmsAccountDetailResp accountDetailResp = cspVideoSmsAccountApi.queryDetail(req);
            if (ObjectUtil.isEmpty(accountDetailResp.getStatus())) {
                throw new BizException(820301021, "该计划中视频短信账号已删除，请重新选择");
            }
            if (accountDetailResp.getStatus() == 0) {
                throw new BizException(820301022, "该计划中视频短信账号已禁用，请重新选择");
            }
        }
    }

    @Override
    public void update(RobotGroupSendPlansReq robotGroupSendPlansReq) {
        RobotGroupSendPlansDo robotGroupSendPlansDo = new RobotGroupSendPlansDo();
        BeanUtils.copyProperties(robotGroupSendPlansReq, robotGroupSendPlansDo);
        if (StringUtils.isNotEmpty(robotGroupSendPlansReq.getRichMediaIds())) {
            String[] accountIds = robotGroupSendPlansReq.getRichMediaIds().split(",");
            verifyAccount(accountIds);
        }
        if (StringUtils.isNotEmpty(robotGroupSendPlansReq.getPlanAccount())) {
            String planChatbotAccount = Arrays.stream(robotGroupSendPlansReq.getPlanAccount().split(","))
                    .map(operator -> accountManagementApi.getAccountManagementByAccountType(
                            new AccountManagementTypeReq()
                                    .setCreator(SessionContextUtil.getLoginUser().getUserId())
                                    .setAccountType(operator)
                    ))
                    .map(AccountManagementResp::getChatbotAccount)
                    .collect(Collectors.joining(","));
            robotGroupSendPlansReq.setPlanChatbotAccount(planChatbotAccount);
        }
        robotGroupSendPlansDao.updateById(robotGroupSendPlansDo);
    }

    @Override
    public void deleteById(Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        RobotGroupSendPlans plans = queryById(id);
        if (Objects.isNull(plans)) {
            throw new BizException("计划不存在");
        }
        if (!plans.getCreator().equals(SessionContextUtil.getUser().getUserId())) {
            throw new BizException("该计划不是你的");
        }
        if (plans.getPlanStatus() != SendStatus.TO_BE_SEND.getCode()) {
            throw new BizException(SendGroupExp.DELETE_ERROR);
        }
        robotGroupSendPlansDao.logicDeleteByIds(ids);
    }

    /**
     * 查找所有符合状态的模板id
     *
     * @return 模板id集合
     */
    @Override
    public List<Long> selectAllPlanIds() {
        QueryWrapper<RobotGroupSendPlansDo> wrapper = new QueryWrapper<>();
        //状态为 待启动、执行中、已暂停
        Integer[] arr = {0, 1, 3};
        wrapper.in("plan_status", Arrays.asList(arr));
        List<Long> planIds = robotGroupSendPlansDao.selectList(wrapper).stream().map(RobotGroupSendPlansDo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<RobotGroupSendPlansDetailDo> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(RobotGroupSendPlansDetailDo::getPlanId, planIds);
        List<Long> collect = detailDao.selectList(detailWrapper).stream().map(RobotGroupSendPlansDetailDo::getTemplateId).collect(Collectors.toList());
        return JSONArray.parseArray(collect.toString(), Long.class);
    }

    @Override
    public List<Long> selectIdsByStatus(RobotGroupSendPlansReq robotGroupSendPlansReq) {
        LambdaQueryWrapper<RobotGroupSendPlansDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotGroupSendPlansDo::getPlanStatus, robotGroupSendPlansReq.getPlanStatus());
        List<RobotGroupSendPlansDo> robotGroupSendPlansDos = robotGroupSendPlansDao.selectList(wrapper);
        return robotGroupSendPlansDos.stream().map(RobotGroupSendPlansDo::getId).collect(Collectors.toList());
    }

    @Override
    public List<RobotGroupSendPlansAndChatbotAccount> getByGroupSendIds(List<String> selectPlanIds) {
        List<RobotGroupSendPlansAndChatbotAccount> robotGroupSendPlansAndChatbotAccountList = robotGroupSendPlansDao.getByGroupSendIds(selectPlanIds);
        if (CollectionUtil.isEmpty(robotGroupSendPlansAndChatbotAccountList)) {
            throw new BizException(820301029, "未查询到对应群发计划信息");
        }
//        robotGroupSendPlansAndChatbotAccountList转换成Map
//        Map<Long, RobotGroupSendPlansAndChatbotAccount> map =
//                robotGroupSendPlansAndChatbotAccountList.stream().collect(Collectors.toMap(RobotGroupSendPlansAndChatbotAccount::getId, Function.identity()));
        return robotGroupSendPlansAndChatbotAccountList;
    }

    @Override
    public List<RobotGroupSendPlansAndChatbotAccount> selectByPlanName(RobotGroupSendPlansByPlanNameReq req) {
        LambdaQueryWrapper<RobotGroupSendPlansDo> queryWrapper =  new LambdaQueryWrapper<>();
        queryWrapper.eq(RobotGroupSendPlansDo::getCreator, req.getCustomerId());
        queryWrapper.like(RobotGroupSendPlansDo::getPlanName, req.getPlanName());
        List<RobotGroupSendPlansDo> list = robotGroupSendPlansDao.selectList(queryWrapper);
        return BeanUtil.copyToList(list, RobotGroupSendPlansAndChatbotAccount.class);
    }

    @Override
    public String selectPlanChatbotAccount(RobotGroupSendPlansReq robotGroupSendPlansReq) {
        Long id = robotGroupSendPlansReq.getId();
        LambdaQueryWrapper<RobotGroupSendPlansDo> queryWrapper =  new LambdaQueryWrapper<>();
        queryWrapper.eq(RobotGroupSendPlansDo::getId,id);
        RobotGroupSendPlansDo robotGroupSendPlansDo = robotGroupSendPlansDao.selectOne(queryWrapper);
        if(robotGroupSendPlansDo!=null){
            return robotGroupSendPlansDo.getPlanChatbotAccount();
        }
        return null;
    }
}
