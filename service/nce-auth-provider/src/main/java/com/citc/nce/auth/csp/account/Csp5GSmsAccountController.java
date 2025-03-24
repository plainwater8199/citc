package com.citc.nce.auth.csp.account;

import com.citc.nce.auth.csp.account.service.Csp5GSmsAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class Csp5GSmsAccountController implements Csp5GSmsAccountApi{

    @Resource
    private Csp5GSmsAccountService csp5GSmsAccountService;
    @Override
    public List<String> queryAccountIdListByCspIds(List<String> userIds) {
        return csp5GSmsAccountService.queryAccountIdListByCspIds(userIds);
    }

    @Override
    public List<String> queryAccountIdListByCustomerIds(List<String> customerIds) {
        return csp5GSmsAccountService.queryAccountIdListByCustomerIds(customerIds);
    }
}
