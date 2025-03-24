package com.citc.nce.module.service;

import com.citc.nce.module.entity.SubscribeModuleDo;
import com.citc.nce.module.vo.SubscribeModuleInfo;
import com.citc.nce.module.vo.req.SubscribeModuleReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.req.SubscribeModuleSaveReq;
import com.citc.nce.module.vo.resp.SubscribeModuleQueryResp;

import java.util.Date;
import java.util.List;

public interface SubscribeModuleService {

    int saveSubscribeModule(SubscribeModuleSaveReq req);

    int deleteSubscribeModule(SubscribeModuleReq req);

    int updateSubscribeModule(SubscribeModuleSaveReq req);

    PageResult<SubscribeModuleInfo> getSubscribeModuleList(SubscribeModuleReq req);

    SubscribeModuleQueryResp getSubscribeModule(SubscribeModuleReq req);

    List<SubscribeModuleReq> getSubscribeModules();

    SubscribeModuleDo getSubscribeModuleInfo(String subscribeId);

    List<SubscribeModuleDo> getSendSubscribeForToday(Date date);

    /**
     * 更新组件的状态，发送完毕
     * @param subscribeModuleInfo 组件信息
     * @param subscribeModuleStatus 组件状态
     */
    void updateSubscribeModuleStatus(SubscribeModuleDo subscribeModuleInfo, Integer subscribeModuleStatus);

    void updateSubscribeModuleStatusById(String subscribeId, int code);
}
