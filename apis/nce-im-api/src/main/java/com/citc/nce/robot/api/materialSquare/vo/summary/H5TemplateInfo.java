package com.citc.nce.robot.api.materialSquare.vo.summary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class H5TemplateInfo {
    @ApiModelProperty("主键,唯一值")
    private Long id;

    @ApiModelProperty("表单名称")
    private String formName;

    @ApiModelProperty("表单内容")
    private String formDetails;

    @ApiModelProperty("表单封面")
    private String formCover;

    @ApiModelProperty(value = "表单分享链接")
    private String formShareUrl;

    @ApiModelProperty(value = "短链接")
    private String shortUrl;

    private Date createTime;

}
