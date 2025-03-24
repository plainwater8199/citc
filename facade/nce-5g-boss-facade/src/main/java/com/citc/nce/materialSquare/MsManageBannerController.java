package com.citc.nce.materialSquare;

import com.citc.nce.materialSquare.entity.MsManageBanner;
import com.citc.nce.materialSquare.vo.banner.MsBannerAdd;
import com.citc.nce.materialSquare.vo.banner.MsBannerChangeOrder;
import com.citc.nce.materialSquare.vo.banner.MsBannerUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author bydud
 * @since 2024/5/9
 */
@RestController
@Api(value = "MsManageBannerController", tags = "素材广场-banner管理")
public class MsManageBannerController {
    @Autowired
    private MsManageBannerApi bannerApi;


    @ApiOperation("新增banner")
    @PostMapping("/msManageBanner/addBanner")
    void addBanner(@RequestBody @Valid MsBannerAdd save) {
        bannerApi.addBanner(save);
    }

    @ApiOperation("修改banner")
    @PostMapping("/msManageBanner/update")
    void update(@RequestBody @Valid MsBannerUpdate update) {
        bannerApi.update(update);
    }

    @ApiOperation("删除banner")
    @GetMapping("/msManageBanner/removeById/{msBannerId}")
    void remove(@PathVariable("msBannerId") Long msBannerId) {
        bannerApi.remove(msBannerId);
    }

    @ApiOperation("list banner")
    @GetMapping("/msManageBanner/list")
    List<MsManageBanner> list() {
        return bannerApi.list();
    }

    @ApiOperation("banner排序")
    @PostMapping("msManageBanner/changeOrderNum")
    void changeOrderNum(@RequestBody @Valid List<MsBannerChangeOrder> bannerList) {
        bannerApi.changeOrderNum(bannerList);
    }
}
