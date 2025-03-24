package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserEnterpriseIdentificationInfo {
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
     * 企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    @ApiModelProperty(value = "企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer enterpriseAuthStatus;

    /**
     * 企业账户名
     */
    @ApiModelProperty(value = "企业账户名", dataType = "String")
    private String enterpriseAccountName;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称", dataType = "String")
    private String enterpriseName;

    /**
     * 营业执照
     */
    @ApiModelProperty(value = "营业执照", dataType = "String")
    private String enterpriseLicense;

    /**
     * 统一社会信用代码/注册号/组织机构代码
     */
    @ApiModelProperty(value = "统一社会信用代码/注册号/组织机构代码", dataType = "String")
    private String creditCode;

    /**
     * 企业认证申请时间
     */
    @ApiModelProperty(value = "企业认证申请时间", dataType = "Date")
    private Date enterpriseAuthTime;

    /**
     * 企业认证审核时间
     */
    @ApiModelProperty(value = "企业认证审核时间", dataType = "Date")
    private Date enterpriseAuthAuditTime;

    /**
     * 最新审核备注
     */
    @ApiModelProperty(value = "最新审核备注", dataType = "String")
    private String auditRemark;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址", dataType = "String")
    private String address;
    /**
     * 所在省 数据字典服务
     */
    @ApiModelProperty(value = "所在省 数据字典服务", dataType = "String")
    private String province;
    /**
     * 所在市 数据字典服务
     */
    @ApiModelProperty(value = "所在市 数据字典服务", dataType = "String")
    private String city;
    /**
     * 所在地区 数据字典服务
     */
    @ApiModelProperty(value = "所在地区 数据字典服务", dataType = "String")
    private String area;

    @ApiModelProperty(value = "区域", dataType = "String")
    private String zone;
}
