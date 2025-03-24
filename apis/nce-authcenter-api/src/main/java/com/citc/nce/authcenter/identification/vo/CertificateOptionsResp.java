package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/07/15
 * @Version: 1.0
 * @Description:
 */
@Data
public class CertificateOptionsResp {
    @ApiModelProperty(value = "表主键", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "资质ID", dataType = "Integer")
    private Integer certificateId;

    @ApiModelProperty(value = "用户ID", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "资质申请状态(1待审核,2审核通过,3审核不通过)", dataType = "Integer")
    private Integer certificateApplyStatus;

    @ApiModelProperty(value = "申请时间", dataType = "Date")
    private Date applyTime;

    @ApiModelProperty(value = "审批通过时间", dataType = "Date")
    private Date approvalTime;

    @ApiModelProperty(value = "资质状态(0开启,1关闭,2无状态)", dataType = "Integer")
    private Integer certificateStatus;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新人", dataType = "String")
    private String updater;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）", dataType = "Integer")
    private Integer deleted;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳", dataType = "Long")
    private Long deletedTime;

    @ApiModelProperty(value = "业务表id", dataType = "Long")
    private Long businessId;
}
