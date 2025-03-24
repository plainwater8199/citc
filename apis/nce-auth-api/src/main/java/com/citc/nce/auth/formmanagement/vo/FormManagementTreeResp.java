package com.citc.nce.auth.formmanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class FormManagementTreeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="id")
    private Long id;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    private String formName;

    /**
     * 表单内容
     */
    @ApiModelProperty(value = "表单内容")
    private String formDetails;

    /**
     * 表单分享链接
     */
    @ApiModelProperty(value = "表单分享链接")
    private String formShareUrl;

    /**
     * 短链接
     */
    @ApiModelProperty(value = "短链接")
    private String shortUrl;

}
