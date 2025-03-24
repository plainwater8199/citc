package com.citc.nce.authcenter.csp.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * bydud
 * 2024/1/25
 **/
@Data
@ApiModel()
public class PlatformDefinition {
    @ApiModelProperty("cspId")
    private String cspId;

    @ApiModelProperty("登录地址")
    private String loginUrl;
    @ApiModelProperty("平台名称")
    private String name;
    @ApiModelProperty("logo")
    private String logo;
    @ApiModelProperty("轮播图")
    private List<String> carouselChart;
    @ApiModelProperty("主题颜色")
    private String themeColor;
}
