package com.citc.nce.auth.csp.sms.account.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public interface CspShortSmsAccountService {
    List<String> queryAccountIdListByCspIds(List<String> cspIds);

    List<String> queryAccountIdListByCustomerIds(List<String> customerIds);
}
