package com.citc.nce.keywordsreply.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementOptionVo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.keywordsreply.Constants;
import com.citc.nce.keywordsreply.dao.KeywordsReplyMapper;
import com.citc.nce.keywordsreply.entity.KeywordsReply;
import com.citc.nce.keywordsreply.req.KeywordsReplyAddReq;
import com.citc.nce.keywordsreply.req.KeywordsReplyEditReq;
import com.citc.nce.keywordsreply.req.KeywordsReplySearchReq;
import com.citc.nce.keywordsreply.resp.KeywordsReplyListResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2024/5/29 15:27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordsReplyService extends ServiceImpl<KeywordsReplyMapper, KeywordsReply> implements IService<KeywordsReply> {
    private final MessageTemplateApi messageTemplateApi;
    private final AccountManagementApi accountManagementApi;


    @Transactional(rollbackFor = Exception.class)
    public void add(KeywordsReplyAddReq addReq) {
        checkTemplateAndAccount(addReq.getReplyTemplateId(), Arrays.asList(addReq.getApplyAccounts().split(",")));
        if (lambdaQuery()
                .eq(KeywordsReply::getKeywords, addReq.getKeywords())
                .eq(KeywordsReply::getCreator, SessionContextUtil.getUserId())
                .exists())
            throw new BizException("关键词重复");
        KeywordsReply keywordsReply = new KeywordsReply()
                .setKeywords(addReq.getKeywords())
                .setReplyTemplateId(addReq.getReplyTemplateId())
                .setApplyAccounts(addReq.getApplyAccounts());
        save(keywordsReply);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        LambdaQueryWrapper<KeywordsReply> queryWrapper = new LambdaQueryWrapper<KeywordsReply>()
                .eq(KeywordsReply::getId, id)
                .eq(KeywordsReply::getCreator, SessionContextUtil.getUserId());
        boolean removed = remove(queryWrapper);
        if (!removed)
            throw new BizException("删除失败");
    }


    @Transactional(rollbackFor = Exception.class)
    public void edit(KeywordsReplyEditReq editReq) {
        checkTemplateAndAccount(editReq.getReplyTemplateId(), Arrays.asList(editReq.getApplyAccounts().split(",")));
        if (lambdaQuery()
                .eq(KeywordsReply::getKeywords, editReq.getKeywords())
                .eq(KeywordsReply::getCreator, SessionContextUtil.getUserId())
                .ne(KeywordsReply::getId, editReq.getId())
                .exists())
            throw new BizException("关键词重复");
        boolean updated = lambdaUpdate()
                .eq(KeywordsReply::getId, editReq.getId())
                .eq(KeywordsReply::getCreator, SessionContextUtil.getUserId())
                .set(KeywordsReply::getKeywords, editReq.getKeywords())
                .set(KeywordsReply::getReplyTemplateId, editReq.getReplyTemplateId())
                .set(KeywordsReply::getApplyAccounts, editReq.getApplyAccounts())
                .update(new KeywordsReply());
        if (!updated)
            throw new BizException("编辑失败");
    }


    public PageResult<KeywordsReplyListResp> search(KeywordsReplySearchReq searchReq) {
        Page<KeywordsReply> page = new Page<>(searchReq.getPageNo(), searchReq.getPageSize());
        page.setOrders(Collections.singletonList(OrderItem.desc("create_time")));
        Page<KeywordsReply> keywordsReplyPage = this.lambdaQuery()
                .like(StringUtils.isNotEmpty(searchReq.getApplyAccount()), KeywordsReply::getApplyAccounts, searchReq.getApplyAccount())
                .eq(KeywordsReply::getCreator, SessionContextUtil.getUserId())
                .page(page);
        if (keywordsReplyPage.getTotal() == 0)
            return new PageResult<>(new ArrayList<>(), 0);
        List<KeywordsReplyListResp> records = new ArrayList<>(keywordsReplyPage.getRecords().size());
        Set<Long> templateIds = new HashSet<>();

        // 查询所有账号管理
        Map<String, String> accountMap = obtainAccountMap();

        for (KeywordsReply keywordsReply : keywordsReplyPage.getRecords()) {
            KeywordsReplyListResp item = new KeywordsReplyListResp();
            item.setId(keywordsReply.getId());
            item.setReplTemplateId(keywordsReply.getReplyTemplateId());
            item.setKeywords(keywordsReply.getKeywords());
            item.setApplyAccounts(keywordsReply.getApplyAccounts());
            item.setApplyAccountNames(getAccountNames(accountMap, keywordsReply.getApplyAccounts()));
            item.setCreateTime(keywordsReply.getCreateTime());
            records.add(item);
            templateIds.add(item.getReplTemplateId());
        }
        Map<Long, String> templateNameMap = messageTemplateApi.queryTemplateNameByTemplateIds(new ArrayList<>(templateIds));
        for (KeywordsReplyListResp resp : records) {
            resp.setReplTemplateName(templateNameMap.get(resp.getReplTemplateId()));
        }
        return new PageResult<>(records, keywordsReplyPage.getTotal());
    }

    private String getAccountNames(Map<String, String> accountMap, String applyAccounts) {
        StringBuilder accountNames = new StringBuilder();
        if (!StringUtils.isBlank(applyAccounts)) {
            String[] accounts = applyAccounts.split(",");
            for (String account : accounts) {
                if (accountMap.containsKey(account)) {
                    if (accountNames.length() == 0) {
                        accountNames = new StringBuilder(accountMap.get(account));
                    } else {
                        accountNames.append(",").append(accountMap.get(account));
                    }
                }
            }
        }
        return accountNames.toString();
    }


    private Map<String, String> obtainAccountMap() {
        Map<String, String> accountMap = new HashMap<>();
        List<AccountManagementOptionVo> allAccountManagement = accountManagementApi.getAllAccountManagement(true);
        for (AccountManagementOptionVo item : allAccountManagement) {
            item.getOptions().forEach(option -> accountMap.put(option.getChatbotAccount(), option.getAccountName()));
        }
        return accountMap;
    }


    private void checkTemplateAndAccount(long templateId, List<String> configuredChatbotAccountList) {
        if (messageTemplateApi.getMessageTemplateById(templateId) == null)
            throw new BizException("模板不存在");
        List<String> existChatbotAccountList = accountManagementApi.getListByChatbotAccountList(configuredChatbotAccountList).stream()
                .map(AccountManagementResp::getChatbotAccount)
                .collect(Collectors.toList());
        Set<String> missingChatbotAccounts = configuredChatbotAccountList.stream()
                .filter(chatbotAccount -> !existChatbotAccountList.contains(chatbotAccount) && !Objects.equals(chatbotAccount, Constants.BIND_ALL_ACCOUNT))
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(missingChatbotAccounts)) {
            throw new BizException(String.format("账号%s不存在", String.join(",", missingChatbotAccounts)));
        }
    }

    public Long queryTemplateIdByChatbotAccount(String keywords, String chatbotAccount) {
        return this.lambdaQuery()
                .eq(KeywordsReply::getKeywords, keywords)
                .list()
                .stream()
                .filter(keywordsReply ->
                        Arrays.asList(keywordsReply.getApplyAccounts().split(",")).contains(chatbotAccount)
                                || Objects.equals(keywordsReply.getApplyAccounts(), Constants.BIND_ALL_ACCOUNT)
                )
                .findAny()
                .map(KeywordsReply::getReplyTemplateId)
                .orElse(null);
    }
}
