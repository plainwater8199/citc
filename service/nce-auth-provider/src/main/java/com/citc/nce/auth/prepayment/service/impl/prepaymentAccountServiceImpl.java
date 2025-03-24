package com.citc.nce.auth.prepayment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementMapper;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.prepayment.service.IPrepaymentAccountService;
import com.citc.nce.auth.prepayment.vo.*;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import com.citc.nce.robot.vo.RobotGroupSendPlans;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class prepaymentAccountServiceImpl implements IPrepaymentAccountService {

    private final AccountManagementMapper accountManagementMapper;
    private final CspSmsAccountDao cspSmsAccountDao;
    private final CspVideoSmsAccountDao cspVideoSmsAccountDao;
    private final RobotGroupSendPlansApi robotGroupSendPlansApi;

    @Override
    public List<CustomerAccountPrepaymentListVo> selectCustomerAccount(PrepaymentAccountReq req) {
        String account = req.getAccount();
        if (req.getMsgType() == MsgTypeEnum.M5G_MSG) {
            return findChatbotAccountPrepayment(account);
        }
        // 查询短信
        if (req.getMsgType() == MsgTypeEnum.SHORT_MSG) {
            return findSmsAccountPrepayment(account);
        }
        // 查询视频短信
        if (req.getMsgType() == MsgTypeEnum.MEDIA_MSG) {
            return findVideoSmsAccountPrepayment(account);
        }

        return Collections.emptyList();
    }


    @Override
    public List<CustomerAccountPrepaymentListVo> selectCustomerAllAccount(Long planId) {
        List<CustomerAccountPrepaymentListVo> accountPrepaymentListVoList = new ArrayList<>();
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        // 先查询出当前计划下所有的账号
        RobotGroupSendPlansReq req = new RobotGroupSendPlansReq();
        req.setId(planId);
        RobotGroupSendPlans plan = robotGroupSendPlansApi.queryById(req);
        // 查询5G消息
        if (StringUtils.isNotEmpty(plan.getPlanChatbotAccount())) {
            accountPrepaymentListVoList.addAll(findChatbotAccountPrepayment(plan.getPlanChatbotAccount()));
        }
        // 查询短信
        if (StringUtils.isNotEmpty(plan.getShortMsgIds())) {
            accountPrepaymentListVoList.addAll(findSmsAccountPrepayment(plan.getShortMsgIds()));
        }
        // 查询视频短信
        if (StringUtils.isNotEmpty(plan.getRichMediaIds())) {
            accountPrepaymentListVoList.addAll(findVideoSmsAccountPrepayment(plan.getRichMediaIds()));
        }
        return accountPrepaymentListVoList;
    }


    private List<CustomerAccountPrepaymentListVo> findChatbotAccountPrepayment(String chatbotAccount) {
        // 如果查询到的5G消息账号为空，表明客户没有预购套餐的账号，需要将预购套餐账号列表设置为初始值
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        return accountManagementMapper.selectPlanAccountByCustomerId(customerId, chatbotAccount.split(","))
                .stream()
                .map(fifthMessageAccountListVo -> new CustomerAccountPrepaymentListVo()
                        .setAccountType(MsgTypeEnum.M5G_MSG.getCode())
                        .setAccountName(fifthMessageAccountListVo.getAccountName())
                        .setOperator(fifthMessageAccountListVo.getOperator())
                        .setAvailableAmount(JSONObject.toJSONString(new FifthMessageAccountListVo()
                                .setMenuStatus(null)
                                .setTotalUsableTextMessageNumber(fifthMessageAccountListVo.getTotalUsableTextMessageNumber())
                                .setTotalUsableRichMessageNumber(fifthMessageAccountListVo.getTotalUsableRichMessageNumber())
                                .setTotalUsableConversationNumber(fifthMessageAccountListVo.getTotalUsableConversationNumber())
                        ))
                )
                .collect(Collectors.toList());
    }

    private List<CustomerAccountPrepaymentListVo> findSmsAccountPrepayment(String smsAccount) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        return cspSmsAccountDao.selectPlanSmsAccount(customerId, smsAccount)
                .stream()
                .map(smsMessageAccountListVo -> new CustomerAccountPrepaymentListVo()
                        .setAccountType(MsgTypeEnum.SHORT_MSG.getCode())
                        .setAccountName(smsMessageAccountListVo.getAccountName())
                        .setAvailableAmount(smsMessageAccountListVo.getTotalUsable() == null ? "0" : String.valueOf(smsMessageAccountListVo.getTotalUsable()))
                )
                .collect(Collectors.toList());
    }

    private List<CustomerAccountPrepaymentListVo> findVideoSmsAccountPrepayment(String videoSmsAccount) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        return cspVideoSmsAccountDao.selectPlanVideoSmsAccount(customerId, videoSmsAccount)
                .stream()
                .map(videoSmsMessageAccountListVo -> new CustomerAccountPrepaymentListVo()
                        .setAccountType(MsgTypeEnum.MEDIA_MSG.getCode())
                        .setAccountName(videoSmsMessageAccountListVo.getAccountName())
                        .setAvailableAmount(videoSmsMessageAccountListVo.getTotalUsable() == null ? "0" : String.valueOf(videoSmsMessageAccountListVo.getTotalUsable()))
                )
                .collect(Collectors.toList());
    }

}
