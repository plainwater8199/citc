package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateSearchReq {
    @ApiModelProperty("模板名称，模糊查找")
    private String templateName;

    @ApiModelProperty("短信类型 1:5G阅信 2:阅信+")
    private Integer smsType;

    @ApiModelProperty("运营商编码 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("审核状态 0等待送审 1审核中 2审核通过 3审核被拒")
    private Integer status;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
