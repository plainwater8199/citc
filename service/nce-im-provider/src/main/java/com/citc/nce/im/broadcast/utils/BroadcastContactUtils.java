package com.citc.nce.im.broadcast.utils;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.auth.contactlist.vo.ContactListPageReq;
import com.citc.nce.auth.contactlist.vo.ContactListResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import com.citc.nce.im.massSegment.service.IMassSegmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2024/6/27 9:43
 */
@Slf4j
public class BroadcastContactUtils {


    public static Map<Long, List<ContactListResp>> getContactPhones(List<Long> groupIds) {
        ContactListApi contactListApi = SpringUtils.getBean(ContactListApi.class);
        return contactListApi.getContactListByGroupIds(groupIds);
    }

    public static List<String> mapToPhoneAndFilterBlack(List<ContactListResp> contactList) {
        List<String> phoneNumbers = contactList.stream()
                .filter(contact -> {
                    if (contact.getBlacklist() == 0)
                        return true;
                    else {
                        log.warn("过滤黑名单联系人 {}:{}", contact.getPersonName(), contact.getPhoneNum());
                        return false;
                    }
                })
                .map(ContactListResp::getPhoneNum)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(phoneNumbers))
            throw new GroupPlanExecuteException("联系人组不存在或者可发送联系人为空");
        return phoneNumbers;
    }


    /**
     * 根据联系人组获取过滤黑名单后的手机号列表
     */
    public static List<String> getCheckedContactPhones(String customerId, Long groupId) {
        ContactListApi contactListApi = SpringUtils.getBean(ContactListApi.class);
        List<ContactListResp> contactListRespList = contactListApi.getContactListAlls(
                new ContactListPageReq()
                        .setGroupId(groupId)
                        .setPageParam(new PageParam(1, Integer.MAX_VALUE, true))
                        .setUserId(customerId)
        );
        return mapToPhoneAndFilterBlack(contactListRespList);
    }


    public static Map<AccountManagementResp, List<String>> groupByPhoneSegment(List<AccountManagementResp> chatbotAccounts, List<String> sendPhones) {
        Map<AccountManagementResp, List<String>> groupByOperator = new HashMap<>();
        //硬核桃跳过号段筛选
        if (chatbotAccounts.size() == 1 && Objects.equals(chatbotAccounts.get(0).getAccountTypeCode(), CSPOperatorCodeEnum.DEFAULT.getCode())) {
            groupByOperator.put(chatbotAccounts.get(0), sendPhones);
        } else {
            Map<String, AccountManagementResp> operatorAccountMap = chatbotAccounts.stream()
                    .collect(Collectors.toMap(AccountManagementResp::getAccountType, Function.identity()));
            IMassSegmentService segmentService = SpringUtils.getBean(IMassSegmentService.class);
            Map<String, CSPOperatorCodeEnum> operatorByPhone = segmentService.getOperatorByPhone(sendPhones);
            operatorByPhone.forEach((phone, operator) -> {
                AccountManagementResp account = operatorAccountMap.get(operator.getName());
                if (account == null) {//展示发送失败的数据--群发 TODO
                    log.warn("手机号:{} 归属运营商:{}，没有可发送账号！", phone, operator);
                    return;
                }
                if (!groupByOperator.containsKey(account)) {
                    groupByOperator.put(account, new ArrayList<>());
                }
                groupByOperator.get(account).add(phone);
            });
        }
        return groupByOperator;
    }
}
