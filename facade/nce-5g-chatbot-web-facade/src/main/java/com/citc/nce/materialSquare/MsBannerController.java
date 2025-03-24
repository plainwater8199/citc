package com.citc.nce.materialSquare;

import com.citc.nce.materialSquare.entity.MsManageBanner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "素材广场-banner")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("ms/banner")
public class MsBannerController {

    @Autowired
    private MsManageBannerApi bannerApi;

    @GetMapping("/list")
    @ApiOperation("获取banner列表")
    public List<MsManageBanner> getBannerList() {
        return bannerApi.list();
    }
}
