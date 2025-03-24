package com.citc.nce.auth.csp.account;

import com.citc.nce.auth.csp.account.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:47
 */
@FeignClient(value = "auth-service", contextId = "CSPAccount", url = "${auth:}")
public interface AccountApi {
    /**
     * 列表查询
     *
     * @return list
     */
    @PostMapping("/csp/account/queryList")
    PageResult<AccountResp> queryList(@RequestBody AccountPageReq req);

    @PostMapping("/csp/account/save")
    int save(@RequestBody AccountSaveReq req);

    /**
     * 查询csp账号详情
     * @return
     */
    @PostMapping("/csp/account/getDetailByCspAccountId")
    AccountDetailResp getDetailByCspAccountId(@RequestBody AccountReq req);

    /**
     * 查询归属代理商
     * @return
     */
    @PostMapping("/csp/account/queryAgentList")
    List<AgentResp> queryAgentList();

    @PostMapping("/csp/account/queryByUserId")
    void queryByUserId();
}
