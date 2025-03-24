package com.citc.nce.materialSquare;

import com.citc.nce.materialSquare.entity.MsManageBanner;
import com.citc.nce.materialSquare.vo.banner.MsBannerAdd;
import com.citc.nce.materialSquare.vo.banner.MsBannerChangeOrder;
import com.citc.nce.materialSquare.vo.banner.MsBannerUpdate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 素材广场-后台-banner-api
 * @author bydud
 * @since 2024/5/9
 */
@FeignClient(value = "rebot-service", contextId = "MsManageBannerApi", url = "${robot:}")
public interface MsManageBannerApi {

    @PostMapping("/msManageBanner/addBanner")
    void addBanner(@RequestBody @Valid MsBannerAdd save);

    @PostMapping("/msManageBanner/update")
    void update(@RequestBody @Valid MsBannerUpdate update);

    @GetMapping("/msManageBanner/removeById/{msBannerId}")
    void remove(@PathVariable("msBannerId") Long msBannerId);

    @GetMapping("/msManageBanner/list")
    List<MsManageBanner> list();

    @PostMapping("msManageBanner/changeOrderNum")
    void changeOrderNum(@RequestBody @Valid List<MsBannerChangeOrder> bannerList);
}
