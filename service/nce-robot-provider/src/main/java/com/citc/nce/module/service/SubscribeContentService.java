package com.citc.nce.module.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.dto.SendContentForSubscribeDto;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.module.vo.SubscribeContentInfo;
import com.citc.nce.module.vo.req.SubscribeContentDeleteReq;
import com.citc.nce.module.vo.req.SubscribeContentQueryListReq;
import com.citc.nce.module.vo.req.SubscribeContentSaveReq;

import java.util.List;

public interface SubscribeContentService {

    String saveSubscribeContent(SubscribeContentSaveReq req);

    int deleteSubscribeContent(SubscribeContentDeleteReq req);

    int updateSubscribeContent(SubscribeContentSaveReq req);

    PageResult<SubscribeContentInfo> getSubscribeContentList(SubscribeContentQueryListReq req);

    /**
     * 根据订阅组件id发送最新需发送的内容
     * @param subscribeIds 订阅主键id列表
     */
    List<SendContentForSubscribeDto> getSendContentForSubscribeIds(List<String> subscribeIds);

    SubscribeContentDo getSubscribeContentInfoById(String subscribeContentId);

    void updateSubscribeContentStatus(SubscribeContentDo subscribeContentDo, Integer subscribeContentStatus);

    SubscribeContentDo getSubContentInfoById(String subscribeContentId);

    List<SubscribeContentDo> findListById(String subscribeId);
}
