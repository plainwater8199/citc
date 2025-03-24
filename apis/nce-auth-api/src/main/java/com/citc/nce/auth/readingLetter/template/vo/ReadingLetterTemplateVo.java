package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateVo {

    @ApiModelProperty("模版ID")
    private Long id;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板内容")
    private String moduleInformation;

    @ApiModelProperty("模板类型(1 -19分别为图文单卡等)")
    private Integer templateType;

    @ApiModelProperty("缩略图")
    private String thumbnail;

    @ApiModelProperty("是否可以编辑")
    private boolean canEdit;

}
