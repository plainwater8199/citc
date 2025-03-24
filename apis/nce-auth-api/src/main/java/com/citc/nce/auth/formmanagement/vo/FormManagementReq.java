package com.citc.nce.auth.formmanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class FormManagementReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    @NotBlank(message = "表单名称不能为空")
    private String formName;

    /**
     * 表单内容
     */
    @ApiModelProperty(value = "表单内容")
    @NotBlank(message = "表单内容不能为空")
    private String formDetails;

    /**
     * 表单草稿
     */
    @ApiModelProperty(value = "表单草稿")
    @NotBlank(message = "表单草稿不能为空")
    private String formDraft;

    /**
     * 2已发布，1待发布，0未发布
     */
    @ApiModelProperty(value = "发布状态2已发布，1待发布，0未发布，默认0")
    private int formStatus;

    @ApiModelProperty(value = "表单封面")
    private String formCover;
}
