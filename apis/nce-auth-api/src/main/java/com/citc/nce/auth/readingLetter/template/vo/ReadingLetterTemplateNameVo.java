package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateNameVo {

    @ApiModelProperty("模版ID")
    private Long id;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板内容")
    private String platformTemplateId;


}
