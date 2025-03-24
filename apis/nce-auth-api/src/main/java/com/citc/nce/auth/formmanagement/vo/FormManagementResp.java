package com.citc.nce.auth.formmanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class FormManagementResp implements Serializable {

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
     * 表单草稿
     */
    @ApiModelProperty(value = "表单草稿")
    private String formDraft;

    /**
     * 2已发布，1待发布，0未发布
     */
    @ApiModelProperty(value = "发布状态2已发布，1待发布，0未发布，默认0")
    private int formStatus;

    @ApiModelProperty(value = "表单封面")
    private String formCover;

    /**
     * 2已发布，1待发布，0未发布
     */
    @ApiModelProperty(value = "提交条数")
    private Long submitNum;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
