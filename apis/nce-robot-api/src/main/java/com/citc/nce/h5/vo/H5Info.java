package com.citc.nce.h5.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Accessors(chain = true)
public class H5Info {

    @ApiModelProperty("应用id")
    private Long id ;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("用户唯一id")
    private String customerId;

    @ApiModelProperty("描述")
    private String h5Desc;

    @NotNull
    @ApiModelProperty("页面类型：1长页 100翻页")
    private Integer pageDisplay;

    @ApiModelProperty("翻页类型：101常规 102惯性 103连续 104推出 105卡片 106立体 107放大 108交换 109翻书 110掉落 111淡入 112折叠 113螺旋进入 114飞驰 115翻转 116螺旋中心 117画面分割")
    private String flipType;

    @NotNull
    @ApiModelProperty("模版schema-json")
    private String tpl;

    @ApiModelProperty("缩略图")
    private String formCover;

    @NotNull
    @ApiModelProperty("全局样式")
    private String globalStyle;

    @ApiModelProperty("状态  0草稿 1是在线 2是已下线")
    private Integer status;

    @ApiModelProperty("活动ID")
    private Long msActivityId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("素材作品ID")
    private Long mssId;

    @ApiModelProperty("是否有表单： 1有表单列表，0 没有表单列表")
    private Integer hasForm;
}
