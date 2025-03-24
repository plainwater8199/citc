package com.citc.nce.auth.csp.customer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerGetDetailResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "企业认证状态", dataType = "Integer")
    private Integer enterpriseAuthStatus;

    @ApiModelProperty(value = "个人认证状态", dataType = "Integer")
    private Integer personAuthStatus;
    /**
     *
     */
    @ApiModelProperty(value = "姓名", dataType = "String")
    private String personName;

    @ApiModelProperty(value = "身份证号码", dataType = "String")
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
     * 申请人认证申请时间
     */
    @ApiModelProperty(value = "申请人认证申请时间", dataType = "Date")
    private Date personAuthTime;


    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称", dataType = "String")
    private String enterpriseName;

    /**
     * 企业账户名称
     */
    @ApiModelProperty(value = "企业账户名称", dataType = "String")
    private String enterpriseAccountName;


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
     * 审核备注
     */
    @ApiModelProperty(value = "审核备注", dataType = "String")
    private String auditRemark;

    @ApiModelProperty(value = "地址", dataType = "String")
    private String address;

    @ApiModelProperty(value = "地区", dataType = "String")
    private String area;

    @ApiModelProperty(value = "省份", dataType = "String")
    private String province;

    @ApiModelProperty(value = "城市", dataType = "String")
    private String city;

    private String mail;

    private String phone;

    private String permissions;
}
