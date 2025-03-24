package com.citc.nce.materialSquare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import com.citc.nce.materialSquare.vo.activity.ContentAdd;
import com.citc.nce.materialSquare.vo.activity.ContentPageQuery;
import com.citc.nce.materialSquare.vo.activity.ContentUpdate;
import com.citc.nce.materialSquare.vo.activity.MsPrice;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 素材广场_后台管理_活动方案 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
public interface IMsManageActivityContentService extends IService<MsManageActivityContent> {

    void addContent(ContentAdd contentAdd);

    void updateContent(ContentUpdate contentUpdate);

    PageResult<MsManageActivityContentResp> pageContent(ContentPageQuery pageQuery, Map<Long, Integer> countMap);

    /**
     * 指定活动方案中，最后一个有效的活动方案最后创建的，切生效中）
     *
     * @param contentIds 指定活动方案
     * @return 一个具体的活动方案 ,不存在返回 0
     */
    MsManageActivityContent getLastEffectActivityContent(List<Long> contentIds);

    MsManageActivityContentResp queryContentById(Long msActivityContentId);


    /**
     * 将待生效待活动刷新到MQ中
     */
    void refreshToMQForMsActivity(Long msActivityId);

    List<MsManageActivityContentResp> activityContentlist();

    /**
     * 获取作品价格
     * @param mssId 作品ID
     * @param msActivityContentId 活动ID
     * @param pType 价格类型
     * @return 商品价格
     */
    MsPrice getPrice(Long mssId, Long msActivityContentId, String pType);
}
