package com.citc.nce.auth.invoice.service;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.csp.readingLetter.dao.CspReadingLetterAccountDao;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author bydud
 * @since 2024/3/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceAccountService {

    private final AccountManagementDao fiveAccountMapper;
    private final CspVideoSmsAccountDao videoSmsAccountMapper;
    private final CspSmsAccountDao smsAccountMapper;
    private final CspReadingLetterAccountDao cspReadingLetterAccountDao;
    public String getAccountName(Map<String, String> map, String accountId, MsgTypeEnum type) {
        Assert.notEmpty(accountId, "账号不能为空");
        Assert.notNull(type, "类型不能为空");
        if (Objects.isNull(map)) {
            return getAccountName(type, accountId);
        }
        String key = getKey(type, accountId);
        String accountName = map.get(key);
        if (StringUtil.isEmpty(accountName)) {
            accountName = getAccountName(type, accountId);
            map.put(key, accountName);
        }
        return accountName;
    }

    private String getAccountName(MsgTypeEnum msgType, String accountId) {
        switch (msgType.getCode()) {
            case 1:
                return fiveAccountMapper.selectNameByAccountId(accountId);
            case 2:
                return videoSmsAccountMapper.selectNameByAccountId(accountId);
            case 3:
                return smsAccountMapper.selectNameByAccountId(accountId);
            case 4:
                return cspReadingLetterAccountDao.selectNameByAccountId(accountId);
            default:
        }
        return "";
    }

    private String getKey(MsgTypeEnum msgType, String accountId) {
        return msgType + "_" + accountId;
    }
}
