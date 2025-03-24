package com.citc.nce.csp;

import com.citc.nce.auth.csp.account.AccountApi;
import com.citc.nce.auth.csp.account.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.Examine;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@Api(value = "AccountController",tags = "CSP--账号管理")
public class AccountController {

    @Resource
    AccountApi accountApi;

    @PostMapping("/csp/account/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @HasCsp
    public PageResult<AccountResp> queryList(@RequestBody AccountPageReq req){
        return accountApi.queryList(req);
    }

    @Examine
    @PostMapping("/csp/account/save")
    @ApiOperation(value = "新增变更", notes = "新增变更")
    @HasCsp
    @Log(title = "新增客户账号")
    public int save(@RequestBody AccountSaveReq req){
        return accountApi.save(req);
    }

    @PostMapping("/csp/account/getDetailByCspAccountId")
    @ApiOperation(value = "查看详情", notes = "查看详情")
    @HasCsp
    public AccountDetailResp getDetailByCspAccountId(@RequestBody AccountReq req){
        return accountApi.getDetailByCspAccountId(req);
    }

    @GetMapping("/csp/account/queryAgentList")
    @ApiOperation(value = "查询归属代理商", notes = "查询归属代理商")
    @HasCsp
    public List<AgentResp> queryAgentList(){
        return accountApi.queryAgentList();
    }

    @GetMapping("/csp/account/queryByUserId")
    @HasCsp
    @ApiOperation(value = "查询是否有可用CSP账号", notes = "查询是否有可用CSP账号")
    public void queryByUserId(){
        accountApi.queryByUserId();
    }
}
