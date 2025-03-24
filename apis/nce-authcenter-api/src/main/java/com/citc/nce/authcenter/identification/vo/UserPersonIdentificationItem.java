package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserPersonIdentificationItem {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    /**
     * 资质code
     */
    @ApiModelProperty(value = "资质code", dataType = "Integer")
    private Integer certificateId;

    /**
     * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    @ApiModelProperty(value = "个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer personAuthStatus;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", dataType = "String")
    private String personName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", dataType = "String")
    private String idCard;

    /**
     * 身份证正面照片
     */
    @ApiModelProperty(value = "身份证正面照片", dataType = "String")
    private String idCardImgFront;

    /**
     * 身份证反面照片
     */
    @ApiModelProperty(value = "身份证反面照片", dataType = "String")
    private String idCardImgBack;

    /**
     * 个人实名认证申请时间
     */
    @ApiModelProperty(value = "个人实名认证申请时间", dataType = "Date")
    private Date personAuthTime;

    /**
     * 个人认证审核时间
     */
    @ApiModelProperty(value = "个人认证审核时间", dataType = "Date")
    private Date personAuthAuditTime;

    /**
     * 最新审核备注
     */
    @ApiModelProperty(value = "最新审核备注", dataType = "String")
    private String auditRemark;
}
