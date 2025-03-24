package com.citc.nce.authcenter.csp.customer.account;

import com.citc.nce.authcenter.csp.customer.account.service.CustomerAccountService;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListReq;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
public class CustomerAccountController implements CustomerAccountApi{

    @Autowired
    private CustomerAccountService customerAccountService;
    @Override
    @PostMapping("/customer/account/query5GAccountList")
    public Query5GAccountListResp query5GAccountList(@RequestBody Query5GAccountListReq req) {
        return customerAccountService.query5GAccountList(req);
    }
}
