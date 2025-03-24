package com.citc.nce.auth.csp.sms.account;

import com.citc.nce.auth.csp.sms.account.service.CspShortSmsAccountService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CspShortSmsAccountController implements CspShortSmsAccountApi {

    @Resource
    private CspShortSmsAccountService cspShortSmsAccountService;
    @Override
    public List<String> queryAccountIdListByCspIds(List<String> cspIds) {
        return cspShortSmsAccountService.queryAccountIdListByCspIds(cspIds);
    }

    @Override
    public List<String> queryAccountIdListByCustomerIds(List<String> customerIds) {
        return cspShortSmsAccountService.queryAccountIdListByCustomerIds(customerIds);
    }
}
