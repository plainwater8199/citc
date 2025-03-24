package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateAuditReq {

    @NotNull(message = "模板ID不能为空")
    @ApiModelProperty("模板ID")
    private Long id;

    @NotEmpty(message = "送审账号不能为空")
    @ApiModelProperty("模板ID")
    List<ReadingLetterAuditAccountReq> readingLetterAccounts;

}
