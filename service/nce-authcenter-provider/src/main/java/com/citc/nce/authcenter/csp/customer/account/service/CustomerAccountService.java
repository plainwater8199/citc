package com.citc.nce.authcenter.csp.customer.account.service;

import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListReq;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListResp;

public interface CustomerAccountService {
    Query5GAccountListResp query5GAccountList(Query5GAccountListReq req);
}
