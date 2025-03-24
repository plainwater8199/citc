package com.citc.nce.materialSquare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.materialSquare.entity.MsManageBanner;
import com.citc.nce.materialSquare.vo.banner.MsBannerAdd;
import com.citc.nce.materialSquare.vo.banner.MsBannerChangeOrder;
import com.citc.nce.materialSquare.vo.banner.MsBannerUpdate;

import java.util.List;

/**
 * <p>
 * 素材广场_管理端_banner管理 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-09 10:05:26
 */
public interface IMsManageBannerService extends IService<MsManageBanner> {

    void updateByEntity(MsBannerUpdate update);

    /**
     * 查询全部，并根据orderNum排序
     *
     * @return MsManageBanner
     */
    List<MsManageBanner> listOrderNum();

    void changeOrderNum(List<MsBannerChangeOrder> bannerList);

    List<MsManageBanner> listByMsActivityId(Long msActivityId);

    void removeMsActivityId(Long msActivityId);

    void addBanner(MsBannerAdd save);

    List<MsManageBanner> listByMsActivityIds(List<Long> activityIds);
}
