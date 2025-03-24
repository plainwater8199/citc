package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class CertificateOptions {
    @ApiModelProperty(value = "表主键")
    private Long id;

    @ApiModelProperty(value = "资质ID")
    private Integer certificateId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "资质申请状态(1待审核,2审核不通过,3审核通过))")
    private Integer certificateApplyStatus;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "审批通过时间")
    private Date approvalTime;

    @ApiModelProperty(value = "资质状态(0开启,1关闭,2无状态)")
    private Integer certificateStatus;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）")
    private Integer deleted;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳")
    private Long deletedTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "业务表id")
    private Long businessId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String updater;
}
