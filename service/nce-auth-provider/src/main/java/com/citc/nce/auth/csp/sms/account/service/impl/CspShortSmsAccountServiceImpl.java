package com.citc.nce.auth.csp.sms.account.service.impl;

import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.sms.account.service.CspShortSmsAccountService;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CspShortSmsAccountServiceImpl implements CspShortSmsAccountService {
    @Resource
    private CspSmsAccountDao cspSmsAccountDao;
    @Resource
    private CspCustomerApi cspCustomerApi;
    @Override
    public List<String> queryAccountIdListByCspIds(List<String> cspIds) {
        if (CollectionUtils.isNotEmpty(cspIds)) {
            List<String> customerIdList = new ArrayList<>();
            for (String cspId: cspIds) {
                List<String> customerIdListByCsp = cspCustomerApi.queryCustomerIdsByCspId(cspId);
                if (CollectionUtils.isNotEmpty(customerIdListByCsp)) {
                    customerIdList.addAll(customerIdListByCsp);
                }
            }
            return queryAccountIdListByCustomerIds(customerIdList);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> queryAccountIdListByCustomerIds(List<String> customerIds) {
        List<String> accountIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(customerIds)) {
            LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspSmsAccountDo::getCustomerId, customerIds)
                    .eq(CspSmsAccountDo::getDeleted, 0);
            List<CspSmsAccountDo> accountDos = cspSmsAccountDao.selectList(queryWrapperX);

            if (CollectionUtils.isNotEmpty(accountDos)) {
                accountDos.forEach(account -> accountIds.add(account.getAccountId()));
            }
        }
        return accountIds;
    }
}
