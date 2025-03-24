package com.citc.nce.helper;

import cn.hutool.crypto.digest.DigestUtil;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;

/**
 * 接口验签和授权验证
 */
public class WalnutAuthHelper {

    @Resource
    AccountManagementApi accountManagementApi;

    public boolean checkSignature(String chatbotId,String signature,String nonce,String timestamp) {
        //根据chatbot
        AccountManagementResp account = accountManagementApi.getAccountManagementByAccountId(chatbotId);
        if(account==null || StringUtils.isBlank(account.getToken())){
            return false;
        }
        String sign = DigestUtil.sha256Hex(account.getToken()+timestamp+nonce);
        if(!sign.equals(signature)){
            return false;
        }
        return true;
    }



}
