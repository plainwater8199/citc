package com.citc.nce.auth.csp.account.service;

import java.util.List;

public interface Csp5GSmsAccountService {
    List<String> queryAccountIdListByCspIds(List<String> userIds);

    List<String> queryAccountIdListByCustomerIds(List<String> customerIds);
}
