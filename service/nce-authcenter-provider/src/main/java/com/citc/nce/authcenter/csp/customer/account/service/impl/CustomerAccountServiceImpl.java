package com.citc.nce.authcenter.csp.customer.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.authcenter.csp.customer.account.service.CustomerAccountService;
import com.citc.nce.authcenter.csp.customer.account.vo.AccountItem;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListReq;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListResp;
import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerChatbotAccountMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomerChatbotAccount;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CustomerAccountServiceImpl implements CustomerAccountService {


    @Resource
    private CspCustomerChatbotAccountMapper cspCustomerChatbotAccountMapper;

    /**
     * 查询用户的5G消息列表
     * @param req 请求信息
     * @return  响应结果
     */
    @Override
    public Query5GAccountListResp query5GAccountList(Query5GAccountListReq req) {
        Query5GAccountListResp resp = new Query5GAccountListResp();
        //获取所有的5G账号列表
        List<CspCustomerChatbotAccount> cspCustomerChatbotAccountDos = get5GAccountList(req);
        //数据拼装
        if(CollectionUtils.isNotEmpty(cspCustomerChatbotAccountDos)){
            List<AccountItem> accountItems = new ArrayList<>();
            Map<String, Map<String,String>> accountMap = new HashMap<>();
            AccountItem accountItem;
            Map<String,String> accountInfoMap;
            for(CspCustomerChatbotAccount item :cspCustomerChatbotAccountDos){
                String customerId = item.getCustomerId();
                String account = item.getChatbotAccount();
                String accountType = obtainAccountType(item.getAccountType());
                accountItem  = new AccountItem();
                accountItem.setCustomerId(customerId);
                accountItem.setAccount(account);
                accountItem.setOperatorCode(accountType);
                accountItems.add(accountItem);
                if(accountMap.containsKey(customerId)){
                    accountInfoMap = accountMap.get(customerId);
                    if(accountInfoMap.containsKey(accountType)){
                        log.info("账号异常："+customerId+",运营商："+accountType+",账号："+account);
                    }else{
                        accountInfoMap.put(accountType,account);
                    }
                }else{
                    accountInfoMap = new HashMap<>();
                    accountInfoMap.put(accountType,account);
                    accountMap.put(customerId,accountInfoMap);
                }

            }
            resp.setAccountItems(accountItems);
            resp.setAccountMap(accountMap);
        }
        return resp;
    }

    private List<CspCustomerChatbotAccount> get5GAccountList(Query5GAccountListReq req) {
        LambdaQueryWrapper<CspCustomerChatbotAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CspCustomerChatbotAccount::getDeleted, 0);
        if(!Strings.isNullOrEmpty(req.getCustomerId())){
            wrapper.eq(CspCustomerChatbotAccount::getCustomerId, req.getCustomerId());
        }
        if(!Strings.isNullOrEmpty(req.getCustomerId())){
            wrapper.eq(CspCustomerChatbotAccount::getCspId, req.getCspId());
        }
        return cspCustomerChatbotAccountMapper.selectList(wrapper);
    }

    private String obtainAccountType(String accountType) {
        if("联通".equals(accountType)){
            return "1";
        }else if("移动".equals(accountType)){
            return "2";
        }else if("电信".equals(accountType)){
            return "3";
        }else {
            return "0";
        }
    }
}
