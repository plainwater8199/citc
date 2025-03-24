package com.citc.nce.auth.cardstyle.accountmanagement;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.*;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 账户管理
 */
@RestController
@Slf4j
@Api(value = "auth", tags = "账户管理")
public class AccountManagementController {
    @Resource
    private AccountManagementApi accountManagementApi;


    /**
     * 账户管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "账户管理列表分页获取", notes = "账户管理列表分页获取")
    @PostMapping("/account/management/pageList")
    public PageResultAccountResp getAccountManagements(@RequestBody @Valid PageParam pageParam) {
        return accountManagementApi.getAccountManagements(pageParam);
    }
    @ApiOperation(value = "获取模板审核通过的chatbot账号", notes = "获取模板审核通过的chatbot账号")
    @PostMapping("/account/management/getProvedTreeList")
    public List<Object> getProvedTreeList(@RequestBody AccountManagementForProvedTreeReq accountManagementForProvedTreeReq)
    {
        return accountManagementApi.getProvedTreeList(accountManagementForProvedTreeReq).stream().map(o -> (Object) o).collect(Collectors.toList());
    }

    /**
     * 修改账户管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改账户管理", notes = "修改账户管理")
    @PostMapping("/account/management/edit")
    public void updateAccountManagement(@RequestBody @Valid AccountManagementEditReq accountManagementEditReq) {
        accountManagementApi.updateAccountManagement(accountManagementEditReq);
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
    @SkipToken
    @ApiOperation(value = "删除账户管理", notes = "删除账户管理")
    @PostMapping("/account/management/delete")
    public int delAccountManagementById(@RequestParam("chatbotAccountId") String chatbotAccountId) {
        return accountManagementApi.delAccountManagementById(chatbotAccountId);
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
    public AccountManagementResp getAccountManagementById(@RequestParam("chatbotAccountId") String chatbotAccountId) {
        return accountManagementApi.getAccountManagementById(chatbotAccountId);
    }

    /**
     * 获取所有账户管理按类型分组
     *
     * @param
     * @return
     */

    @ApiOperation(value = "获取账户管理树", notes = "获取账户管理树")
    @PostMapping("/account/management/getTreeList")
    public List<Object> getAccountManagementById(@RequestParam("isHide") Integer isHide) {
        return accountManagementApi.getTreeList(isHide);
    }

    /**
     * 获取所有账户管理按类型分组
     *
     * @param
     * @return
     */

    @ApiOperation(value = "获取账户管理树,包括已删除chatbot账号", notes = "获取账户管理树")
    @PostMapping("/account/management/getTreeListIncludeDeleted")
    public List<Object> getAccountManagementByIdIncludeDeleted(@RequestParam("isHide") Integer isHide) {
        return accountManagementApi.getAccountManagementByIdIncludeDeleted(isHide);
    }

}
