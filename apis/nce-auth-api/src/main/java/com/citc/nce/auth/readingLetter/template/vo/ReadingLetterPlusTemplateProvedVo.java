package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zjy
 */
@Data
public class ReadingLetterPlusTemplateProvedVo {

    @ApiModelProperty("模版ID")
    private Long id;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("用户Id")
    private String customerId;

    @ApiModelProperty("模板内容")
    private String moduleInformation;

    @ApiModelProperty("缩略图")
    private String thumbnail;

    @ApiModelProperty("模板类型(1 -19分别为图文单卡等)")
    private Integer templateType;
    @ApiModelProperty("各类模板的详细审批状况")
    private List<ReadingLetterTemplateDetailVo> templateDetailList;

}
