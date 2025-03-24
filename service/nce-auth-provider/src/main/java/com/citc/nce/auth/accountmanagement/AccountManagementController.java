package com.citc.nce.auth.accountmanagement;

import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.*;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.prepayment.vo.FifthAccountVo;
import com.citc.nce.auth.prepayment.vo.FifthMessageAccountListVo;
import com.citc.nce.auth.prepayment.vo.FifthPlanOrderListVo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 账户管理
 */
@RestController()
@Slf4j
public class AccountManagementController implements AccountManagementApi {
    @Resource
    private AccountManagementService accountManagementService;


    /**
     * 账户管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "账户管理列表分页获取", notes = "账户管理列表分页获取")
    @PostMapping("/account/management/pageList")
    @Override
    public PageResultAccountResp<?> getAccountManagements(@RequestBody @Valid PageParam pageParam) {
        return accountManagementService.getAccountManagements(pageParam);
    }

    /**
     * 修改账户管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改账户管理", notes = "修改账户管理")
    @PostMapping("/account/management/edit")
    @Override
    public void updateAccountManagement(@RequestBody @Valid AccountManagementEditReq accountManagementEditReq) {
        accountManagementService.updateAccountManagement(accountManagementEditReq, true);
    }

    /**
     * 删除账户管理
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chatbotAccountId", value = "chatbotAccountId", dataType = "String", required = true)
    })
    @ApiOperation(value = "删除账户管理", notes = "删除账户管理")
    @PostMapping("/account/management/delete")
    @Override
    public int delAccountManagementById(@RequestParam("chatbotAccountId") String chatbotAccountId) {
        return accountManagementService.delAccountManagementById(chatbotAccountId);
    }

    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chatbotAccountId", value = "chatbotAccountId", dataType = "String", required = true)
    })
    @ApiOperation(value = "获取单个账户管理", notes = "获取单个账户管理")
    @PostMapping("/account/management/getOne")
    @Override
    public AccountManagementResp getAccountManagementById(@RequestParam("chatbotAccountId") String chatbotAccountId) {
        return accountManagementService.getAccountManagementById(chatbotAccountId);
    }

    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountId", value = "accountId", dataType = "String", required = true)
    })
    @PostMapping("/account/management/getAccountManagementByAccountId")
    @Override
    public AccountManagementResp getAccountManagementByAccountId(@RequestParam("accountId") String accountId) {
        return accountManagementService.getAccountManagementByAccountId(accountId);
    }

    /**
     * 获取单个账户管理
     *
     * @param
     * @return 不存在 return null
     */
    @Override
    public AccountManagementResp getAccountManagementByChatbotAccountId(String chatbotAccountId) {
        return accountManagementService.getAccountManagementByChatbotAccountId(chatbotAccountId);
    }

    /**
     * 获取所有账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/list")
    @Override
    public List<AccountManagementResp> getAccountManagementlist(@RequestBody String customerId) {
        return accountManagementService.getAccountManagementlist(customerId);
    }

    /**
     * 获取所有账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getTreeList")
    @Override
    public List<Object> getTreeList(@RequestParam("isHide") Integer isHide) {
        return accountManagementService.getAllAccountManagement(1 == isHide).stream().map(o -> (Object) o).collect(Collectors.toList());
    }

    @Override
    public List<Object> getAccountManagementByIdIncludeDeleted(@RequestParam("isHide") Integer isHide) {
        return accountManagementService.getAccountManagementByIdIncludeDeleted(1 == isHide).stream().map(o -> (Object) o).collect(Collectors.toList());
    }

    @PostMapping("/account/management/getProvedTreeList")
    public List<Object> getProvedTreeList(@RequestBody AccountManagementForProvedTreeReq accountManagementForProvedTreeReq) {
        return accountManagementService.getProvedTreeList(accountManagementForProvedTreeReq).stream().map(o -> (Object) o).collect(Collectors.toList());
    }

    /**
     * @param channelAvailability 通道可用性
     * @return 全部机器人账号列表
     */
    @Override
    public List<AccountManagementOptionVo> getAllAccountManagement(Boolean channelAvailability) {
        return accountManagementService.getAllAccountManagement(channelAvailability);
    }

    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getAccountManagementByAccountType")
    @Override
    public AccountManagementResp getAccountManagementByAccountType(@RequestBody AccountManagementTypeReq accountManagementTypeReq) {
        return accountManagementService.getAccountManagementByAccountType(accountManagementTypeReq);
    }
    @PostMapping("/account/management/getAccountManagementByAccountTypes")
    @Override
    public List<AccountManagementResp> getAccountManagementByAccountTypes(@RequestBody AccountManagementTypeReq accountManagementTypeReq) {
        return accountManagementService.getAccountManagementByAccountTypes(accountManagementTypeReq);

    }

        /**
         * 获取单个账户管理
         *
         * @param
         * @return
         */
    @PostMapping("/account/management/getAccountManagementByAccountTypeAndSupplier")
    @Override
    public AccountManagementResp getAccountManagementByAccountTypeAndSupplier(@RequestBody AccountManagementTypeReq accountManagementTypeReq) {
        return accountManagementService.getAccountManagementByAccountTypeAndSupplier(accountManagementTypeReq);
    }

    @PostMapping("/account/management/selectCountAll")
    @Override
    public long selectCountAll() {
        return accountManagementService.selectCountAll();
    }

    @Override
    public List<AccountManagementResp> getListByChatbotAccounts(AccountChatbotAccountQueryReq accountChatbotAccountQueryReq) {
        return accountManagementService.getListByChatbotAccounts(accountChatbotAccountQueryReq);
    }

    @Override
    public Boolean checkChatBotStatus(@RequestBody AccountManagementTypeReq accountManagementTypeReq) {
        return accountManagementService.checkChatBotStatus(accountManagementTypeReq);
    }

    @Override
    public List<String> getChatbotAccountIdsByCustomerId(String customerId) {
        return accountManagementService.getChatbotAccountIdsByCustomerId(customerId);
    }

    @Override
    public PageResult<FifthMessageAccountListVo> selectFifthMessageAccountByCustomer(MessageAccountSearchVo searchVo) {
        return accountManagementService.selectFifthMessageAccountByCustomer(searchVo);
    }

    @Override
    public FifthAccountVo queryFifthAccount(String chatbotAccount) {
        return accountManagementService.queryFifthAccount(chatbotAccount);
    }

    @Override
    public List<AccountManagementResp> getListByChatbotAccountList(List<String> chatbotAccountList) {
        return accountManagementService.getListByChatbotAccountList(chatbotAccountList);
    }

    @Override
    public List<AccountManagementResp> getChatbotAccountInfoByCustomerId(String customerId) {
        return accountManagementService.getChatbotAccountInfoByCustomerId(customerId);
    }

    @Override
    public List<AccountManagementResp> getListByCreators(@RequestParam("creators") List<String> creators) {
        return accountManagementService.getListByCreators(creators);
    }

}
