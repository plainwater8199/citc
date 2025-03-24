package com.citc.nce.module.service;

import com.citc.nce.module.vo.req.GetSubscribeInfoByUserReq;
import com.citc.nce.module.vo.req.SubscribeNamesReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.resp.GetSubscribeInfoByUserResp;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SubscribeNamesService {

    int saveSubscribeNames(SubscribeNamesReq req);

    int cancelSubscribeNames(SubscribeNamesReq req);

    int updateSubscribeNames(SubscribeNamesReq req);

    PageResult<SubscribeNamesReq> getSubscribeNamesList(SubscribeNamesReq req);

    Long getSubscribeNamesCount(String subscribeId);

    /**
     * 查询订阅指定订阅组件的手机号列表
     * @param subscribeId 订阅组件ID
     * @return Map<advance,Map<phone,chatbotId>>
     */
    Map<String,Map<String,String>> getSendPhone2ChatbotIds(String subscribeId);

    /**
     * 更新发送进度
     * @param phone 手机号
     * @param subscribeId 组件ID
     * @param subContentId 发送的最新进度
     */
    void updateAdvance(String phone, String subscribeId, String subContentId);

    /**
     * 获取指定用户的订阅发送详情
     * @param req 请求信息
     * @return 响应结果
     */
    GetSubscribeInfoByUserResp getSubscribeInfoByPhone(GetSubscribeInfoByUserReq req);


    /**
     * 获取订阅组件的订阅手机列表
     * @param moduleId 组件id
     * @return 手机列表
     */
    Set<String> getSubscribePhones(String moduleId);
}
