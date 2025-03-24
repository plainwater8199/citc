package com.citc.nce.auth.csp.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.account.service
 * @Author: litao
 * @CreateTime: 2023-02-14  15:11

 * @Version: 1.0
 */
public interface AccountManageService extends IService<AccountManageDo> {
    PageResult<AccountResp> queryList(AccountPageReq req);

    int save(AccountSaveReq req);

    AccountDetailResp getDetailByCspAccountId(AccountReq req);

    List<AgentResp> queryAgentList();

    void queryByUserId();

    AccountManageDo getCspAccount(Integer operatorCode, String cspId);
}
