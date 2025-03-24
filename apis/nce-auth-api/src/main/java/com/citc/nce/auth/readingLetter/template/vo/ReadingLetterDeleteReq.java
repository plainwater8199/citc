package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zjy
 */
@Data
public class ReadingLetterDeleteReq {

    @NotNull(message = "模板ID不能为空")
    @ApiModelProperty("模板ID")
    private Long id;

}
