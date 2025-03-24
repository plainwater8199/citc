package com.citc.nce.materialSquare.controller;


import com.citc.nce.materialSquare.MsManageBannerApi;
import com.citc.nce.materialSquare.entity.MsManageBanner;
import com.citc.nce.materialSquare.service.IMsManageBannerService;
import com.citc.nce.materialSquare.vo.banner.MsBannerAdd;
import com.citc.nce.materialSquare.vo.banner.MsBannerChangeOrder;
import com.citc.nce.materialSquare.vo.banner.MsBannerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 素材广场_管理端_banner管理 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-09 10:05:26
 */
@RestController
public class MsManageBannerController implements MsManageBannerApi {

    @Autowired
    private IMsManageBannerService bannerService;

    @Override
    public void addBanner(MsBannerAdd save) {
        bannerService.addBanner(save);
    }

    @Override
    public void update(MsBannerUpdate update) {
        bannerService.updateByEntity(update);
    }

    @Override
    public void remove(Long msBannerId) {
        bannerService.removeById(msBannerId);
    }

    @Override
    public List<MsManageBanner> list() {
        return bannerService.listOrderNum();
    }

    @Override
    public void changeOrderNum(List<MsBannerChangeOrder> bannerList) {
        bannerService.changeOrderNum(bannerList);
    }
}

