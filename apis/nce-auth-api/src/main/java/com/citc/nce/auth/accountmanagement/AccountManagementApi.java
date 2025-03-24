package com.citc.nce.auth.accountmanagement;

import com.citc.nce.auth.accountmanagement.vo.*;
import com.citc.nce.auth.prepayment.vo.FifthAccountVo;
import com.citc.nce.auth.prepayment.vo.FifthMessageAccountListVo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:53
 * @Version: 1.0
 * @Description:账户管理
 */

@FeignClient(value = "auth-service", contextId = "AccountManagement", url = "${auth:}")
public interface AccountManagementApi {
    /**
     * 账户管理列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/pageList")
    PageResultAccountResp<?> getAccountManagements(@RequestBody @Valid PageParam pageParam);

    /**
     * 修改账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/edit")
    void updateAccountManagement(@RequestBody @Valid AccountManagementEditReq accountManagementEditReq);

    /**
     * 删除账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/delete")
    int delAccountManagementById(@RequestParam("chatbotAccountId") String chatbotAccountId);

    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getOne")
    AccountManagementResp getAccountManagementById(@RequestParam("chatbotAccountId") String chatbotAccountId);

    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getAccountManagementByAccountId")
    AccountManagementResp getAccountManagementByAccountId(@RequestParam("accountId") String accountId);

    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getAccountManagementByChatbotAccountId")
    AccountManagementResp getAccountManagementByChatbotAccountId(@RequestParam("chatbotAccountId") String chatbotAccountId);

    /**
     * 获取所有账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/list")
    List<AccountManagementResp> getAccountManagementlist(@RequestBody String creator);

    /**
     * 获取所有账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getTreeList")
    List<Object> getTreeList(@RequestParam("isHide") Integer isHide);
    @PostMapping("/account/management/getProvedTreeList")
    List<Object> getProvedTreeList(@RequestBody AccountManagementForProvedTreeReq accountManagementForProvedTreeReq);


    /**
     * 获取所有机器人账号
     *
     * @param channelAvailability 通道可用性
     * @return
     */
    @GetMapping("/account/management/getAllAccountManagement")
    List<AccountManagementOptionVo> getAllAccountManagement(@RequestParam(required = false, value = "channelAvailability") Boolean channelAvailability);


    /**
     * 获取单个账户管理
     *
     * @param
     * @return
     */
    @PostMapping("/account/management/getAccountManagementByAccountType")
    AccountManagementResp getAccountManagementByAccountType(@RequestBody AccountManagementTypeReq accountManagementTypeReq);

    @PostMapping("/account/management/getAccountManagementByAccountTypes")
     List<AccountManagementResp> getAccountManagementByAccountTypes(@RequestBody AccountManagementTypeReq accountManagementTypeReq) ;

        /**
         * 获取单个账户管理
         *
         * @param
         * @return
         */
    @PostMapping("/account/management/getAccountManagementByAccountTypeAndSupplier")
    AccountManagementResp getAccountManagementByAccountTypeAndSupplier(@RequestBody AccountManagementTypeReq accountManagementTypeReq);

    @PostMapping("/account/management/selectCountAll")
    long selectCountAll();

    @PostMapping("/account/management/getListByChatbotAccounts")
    List<AccountManagementResp> getListByChatbotAccounts(@RequestBody AccountChatbotAccountQueryReq accountChatbotAccountQueryReq);

    @PostMapping("/account/management/getListByCreators")
    List<AccountManagementResp> getListByCreators(@RequestParam("creators") List<String> creators);


    @PostMapping("/account/management/checkChatBotStatus")
    Boolean checkChatBotStatus(@RequestBody AccountManagementTypeReq accountManagementTypeReq);

    /**
     * 根据客户ID查询机器人账号ID
     */
    @GetMapping("/account/management/getChatbotAccountIdsByCustomerId")
    List<String> getChatbotAccountIdsByCustomerId(@RequestParam("customerId") String customerId);

    /**
     * 根据用户信息查询用户的chatbot账号信息
     * @param customerId 用户信息
     * @return chatbot的账号信息
     */
    @PostMapping("/account/management/getChatbotAccountInfoByCustomerId")
    List<AccountManagementResp> getChatbotAccountInfoByCustomerId(@RequestParam("customerId") String customerId);

    @PostMapping("/account/management/selectFifthMessageAccountByCustomer")
    PageResult<FifthMessageAccountListVo> selectFifthMessageAccountByCustomer(@RequestBody MessageAccountSearchVo searchVo);

    @GetMapping("/account/management/queryFifthAccount")
    FifthAccountVo queryFifthAccount(@RequestParam("chatbotAccount") String chatbotAccount);

    @PostMapping("/account/management/getListByChatbotAccountList")
    List<AccountManagementResp> getListByChatbotAccountList(@RequestBody List<String> chatbotAccountList);

    @ApiOperation(value = "获取账户管理树,包括已删除chatbot账号", notes = "获取账户管理树")
    @PostMapping("/account/management/getTreeListIncludeDeleted")
    List<Object> getAccountManagementByIdIncludeDeleted(@RequestParam("isHide")Integer isHide);
}
