package com.citc.nce.module.service;

import com.citc.nce.module.dto.SendContentForSubscribeDto;
import com.citc.nce.module.dto.SubscribeSendInfoForPhoneItemDto;

import java.util.Date;
import java.util.List;

public interface SubscribeContentSendDetailService {

    /**
     * 批量保存订阅的发送记录
     * @param phones 手机号列表
     * @param sendContentForSubscribeInfo 订阅信息
     */
    void saveSubscribeSendRecord(List<String> phones, SendContentForSubscribeDto sendContentForSubscribeInfo);

    /**
     * 对于某个手机号的指定订阅组件的最新进展
     * @param phone 手机号
     * @param subscribeId 订阅组件ID
     * @return 如果没有发送记录则返回”“，如果有则返回”【内容名称】“
     */
    String getRecentAdvances(String phone, String subscribeId);

    /**
     * 根据手机号和订阅组件id查询用户订阅发送内容详情
     * @param phone 手机号
     * @param subscribeId 订阅组件id
     * @return 订阅内容发送详情
     */
    List<SubscribeSendInfoForPhoneItemDto> getSubscribeSendInfoForPhone(String phone, String subscribeId);
}
