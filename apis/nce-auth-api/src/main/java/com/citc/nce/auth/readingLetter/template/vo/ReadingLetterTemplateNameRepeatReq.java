package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateNameRepeatReq {

    @NotBlank(message = "模板名称不能为空")
    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板id")
    private Long templateId;
}
