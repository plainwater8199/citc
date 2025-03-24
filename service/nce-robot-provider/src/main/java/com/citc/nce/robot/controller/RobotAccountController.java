package com.citc.nce.robot.controller;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountChatbotAccountQueryReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.RobotAccountApi;
import com.citc.nce.robot.dao.RobotAccountDao;
import com.citc.nce.robot.entity.RobotAccountDo;
import com.citc.nce.robot.service.RobotAccountService;
import com.citc.nce.robot.vo.RobotAccountPageReq;
import com.citc.nce.robot.vo.RobotAccountPageResultResp;
import com.citc.nce.robot.vo.RobotAccountReq;
import com.citc.nce.robot.vo.RobotAccountResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RobotAccountController implements RobotAccountApi {

    @Resource
    RobotAccountService robotAccountService;

    @Autowired
    private RobotAccountDao robotAccountDao;
    @Autowired
    private AccountManagementApi accountManagementApi;

    /**
     * 查询聊天会话账户
     *
     * @return
     */
    @ApiOperation(value = "查询聊天会话账户", notes = "查询聊天会话账户")
    @PostMapping("/robot/account/pagelist")
    @Override
    public RobotAccountPageResultResp getRobotAccountList(@RequestBody @Valid RobotAccountPageReq robotAccountPageReq) {
        return robotAccountService.getRobotAccountList(robotAccountPageReq);
    }

    /**
     * 新增聊天会话账户
     *
     * @return
     */
    @ApiOperation(value = "新增聊天会话账户", notes = "新增聊天会话账户")
    @PostMapping("/robot/account/save")
    @Override
    public int saveRobotAccount(@RequestBody @Valid RobotAccountReq robotAccountReq) {
        return robotAccountService.saveRobotAccount(robotAccountReq);
    }


    /**
     * 初始化robot_account表新增的account_name为该账号机器人当前的名称
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/robot/account/initRobotAccountAccountName")
    public String initRobotAccountAccountName(){
        List<RobotAccountDo> robotAccountDos = robotAccountDao.selectList();
        List<String> chatbotAccounts = robotAccountDos.stream().map(RobotAccountDo::getAccount).collect(Collectors.toList());
        AccountChatbotAccountQueryReq accountChatbotAccountQueryReq=new AccountChatbotAccountQueryReq();
        accountChatbotAccountQueryReq.setChatbotAccountList(chatbotAccounts);
        accountChatbotAccountQueryReq.setCreator(SessionContextUtil.getUser().getUserId());
        Map<String, String> chatbotNameMap = accountManagementApi.getListByChatbotAccounts(accountChatbotAccountQueryReq)
                .stream()
                .collect(Collectors.toMap(AccountManagementResp::getChatbotAccount, AccountManagementResp::getAccountName));
        robotAccountDos.forEach(robotAccountDo -> robotAccountDo.setAccountName(chatbotNameMap.get(robotAccountDo.getAccount())));
        robotAccountDao.updateBatch(robotAccountDos);
        return "ok";
    }
}
