package com.citc.nce.auth.csp.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.csp.account.service.Csp5GSmsAccountService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class Csp5GSmsAccountServiceImpl implements Csp5GSmsAccountService {

    @Resource
    private AccountManagementDao accountManagementDao;
    @Override
    public List<String> queryAccountIdListByCspIds(List<String> cspIds) {
        List<String> accountFor5Gs = new ArrayList<>();
        LambdaQueryWrapper<AccountManagementDo> accountManagementDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountManagementDoLambdaQueryWrapper.eq(AccountManagementDo::getDeleted,0);
        accountManagementDoLambdaQueryWrapper.in(AccountManagementDo::getCspId,cspIds);
        List<AccountManagementDo> accountManagementDos = accountManagementDao.selectList(accountManagementDoLambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(accountManagementDos)){
            accountManagementDos.forEach(i->accountFor5Gs.add(i.getChatbotAccount()));
        }
        return accountFor5Gs;
    }

    @Override
    public List<String> queryAccountIdListByCustomerIds(List<String> customerIds) {
        List<String> accountFor5Gs = new ArrayList<>();
        LambdaQueryWrapper<AccountManagementDo> accountManagementDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountManagementDoLambdaQueryWrapper.eq(AccountManagementDo::getDeleted,0);
        accountManagementDoLambdaQueryWrapper.in(AccountManagementDo::getCustomerId,customerIds);
        List<AccountManagementDo> accountManagementDos = accountManagementDao.selectList(accountManagementDoLambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(accountManagementDos)){
            accountManagementDos.forEach(i->accountFor5Gs.add(i.getChatbotAccount()));
        }
        return accountFor5Gs;
    }
}
