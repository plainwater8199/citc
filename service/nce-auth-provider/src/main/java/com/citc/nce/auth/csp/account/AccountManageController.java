package com.citc.nce.auth.csp.account;

import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.account.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.Key;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.account
 * @Author: litao
 * @CreateTime: 2023-02-14  15:12
 * @Version: 1.0
 */
@RestController
@Slf4j
public class AccountManageController implements AccountApi {
    @Resource
    private AccountManageService accountManageService;
    @Autowired
    private CommonKeyPairConfig commonKeyPairConfig;

    @Override
    public PageResult<AccountResp> queryList(AccountPageReq req) {
        return accountManageService.queryList(req);
    }

    @Override
    public int save(AccountSaveReq req) {
        return accountManageService.save(req);
    }

    @Override
    public AccountDetailResp getDetailByCspAccountId(AccountReq req) {
        return accountManageService.getDetailByCspAccountId(req);
    }

    @Override
    public List<AgentResp> queryAgentList() {
        return accountManageService.queryAgentList();
    }

    @Override
    public void queryByUserId() {
        accountManageService.queryByUserId();
    }

    @RequestMapping("/csp/account/init")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("第三方csp秘钥字段加密存储，初始化历史数据")
    public void init() {
        List<AccountManageDo> cspAccounts = accountManageService.list();
        Key publicKey = RsaUtil.getPublicKey(commonKeyPairConfig.getPublicKey());
        for (AccountManageDo cspAccount : cspAccounts) {
            if (cspAccount.getCspPassword().length() > 100)
                continue;
            cspAccount.setCspPassword(RsaUtil.encryptByAsymmetric(cspAccount.getCspPassword(), publicKey));
        }
        accountManageService.updateBatchById(cspAccounts);
    }
}
