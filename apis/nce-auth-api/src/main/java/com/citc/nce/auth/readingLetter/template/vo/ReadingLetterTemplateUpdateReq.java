package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateUpdateReq {

    @NotNull(message = "模板ID不能为空")
    @ApiModelProperty("模板ID")
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    @ApiModelProperty("模板名称")
    private String templateName;

    @NotBlank(message = "模板的Json数据")
    @ApiModelProperty("模板的Json数据")
    private String moduleInformation;

    @NotNull(message = "模板类型")
    @ApiModelProperty("模板类型")
    // 1图文单卡 2视频单卡 3图片视频单卡 4视频图片单卡 5长交本国文卡片 6图片轮酒卡片 7国文多卡 8行程卡片
    // 9一般通知卡片 10增强通知卡片 11新闻卡片 12单商品卡片 13字商品卡片 14单卡学卡片 15字卡学卡片 16红包卡片 17短剧视频 18短剧图片 19海报模板
    private Integer templateType;

    @NotBlank(message = "缩略图")
    @ApiModelProperty("缩略图")
    private String thumbnail;

    @NotNull(message = "是否送审")
    @ApiModelProperty("是否送审")
    private Boolean needAudit;
}
