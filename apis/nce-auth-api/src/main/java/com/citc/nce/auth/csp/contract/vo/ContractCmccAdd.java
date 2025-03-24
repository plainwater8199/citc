package com.citc.nce.auth.csp.contract.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * bydud
 *
 * @{DATE}
 **/
@Data
public class ContractCmccAdd  implements Serializable {

    private static final long serialVersionUID = 1L;

    //基础信息
    @ApiModelProperty(value = "合同名称", dataType = "String", required = true)
    @NotBlank(message = "合同名称不能为空")
    @Length(max = 25, message = "合同名称长度超过限制(最大25位)")
    private String contractName;
    @ApiModelProperty(value = "归属客户id", dataType = "int", required = true)
    @NotBlank(message = "归属客户不能为空")
    private String customerId;

    //客户信息==============================================================================================================
    @ApiModelProperty(value = "移动客户名称---平台是企业名称", dataType = "String", required = true)
    @Length(max = 100, message = "客户名长度超过限制")
    @NotBlank(message = "客户名称不能为空")
    private String enterpriseName;
    @ApiModelProperty(value = "归属区域-地区", dataType = "String", required = true)
    @NotBlank(message = "所在地区不能为空")
    private String region;
    @ApiModelProperty(value = "归属区域-所在省", dataType = "String", required = true)
    @NotBlank(message = "所在省不能为空")
    private String province;
    @ApiModelProperty(value = "归属区域-所在市", dataType = "String", required = true)
//    @NotBlank(message = "所在市不能为空")
    private String city;
    @ApiModelProperty(value = "行业类型编码前缀", dataType = "String", required = true)
    @NotBlank(message = "行业类型编码前缀不能为空")
    private String industryCodePrefix;
    @ApiModelProperty(value = "行业类型编码后缀", dataType = "String", required = true)
    @NotBlank(message = "行业类型编码后缀不能为空")
    private String industryCodeSuffix;
    @ApiModelProperty(value = "归属代理商id", dataType = "int", required = true)
    @NotNull(message = "归属代理商不能为空")
    private Long agentInfoId;

    @ApiModelProperty(value = "客户联系人", dataType = "string")
    @Length(max = 20, message = "客户联系人长度超过限制(最大11位)")
    private String contactName;
    @ApiModelProperty(value = "手机号")
    private String contactPhoneNumber;
    @ApiModelProperty(value = "邮箱", dataType = "String")
    @Length(max = 50, message = "邮箱长度超过限制(最大50位)")
    private String contactMail;

    @ApiModelProperty(value = "统一社会信用代码/注册号/组织机构代码", dataType = "String", required = true)
    @NotBlank(message = "统一社会信用代码不能为空")
    @Length(max = 18, message = "统一社会信用代码/注册号/组织机构代码长度超过限制(最大18位)")
    private String contractOrginCode;
    @ApiModelProperty(value = "企业责任人姓名", dataType = "String", required = true)
    @NotBlank(message = "企业责任人姓名不能为空")
    @Length(max = 20, message = "企业责任人姓名长度超过限制(最大20位)")
    private String contractLegalPerson;
    @ApiModelProperty(value = "责任人证件类型 01:身份证，02：中国人民解放军军人身份证件，03：中国人民武装警察身份证件 ", dataType = "int", required = true)
    @NotNull(message = "任人证件类型不能为空")
    private Integer contractLegalPersonCardType;
    @ApiModelProperty(value = "企业责任人证件号码", dataType = "String", required = true)
    @NotBlank(message = "责任人证件号码不能为空")
    @Length(max = 18, message = "责任人证件号码长度超过限制(最大18位)")
    private String contractLegalPersonCardNumber;

    @ApiModelProperty(value = "附件url", dataType = "String", required = true)
    @NotBlank(message = "附件不能为空")
    private String contractFileUrl;
    @ApiModelProperty(value = "附件名称", dataType = "String", required = true)
    @NotBlank(message = "附件不能为空")
    private String contractFileName;

    //合同信息==============================================================================================================
    @ApiModelProperty(value = "合同电子扫描件名称", dataType = "String", required = true)
    @NotBlank(message = "合同电子扫描件不能为空")
    private String contractScanningName;
    @ApiModelProperty(value = "合同电子扫描件url", dataType = "String", required = true)
    @NotBlank(message = "合同电子扫描件不能为空")
    private String contractScanningUrl;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @ApiModelProperty(value = "合同生效日期", dataType = "Date")
    private Date contractEffectiveDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @ApiModelProperty(value = "合同失效日期", dataType = "Date")
    private Date contractExpireDate;
    @ApiModelProperty(value = "合同续签状态 0:未续签， 1：已续签", dataType = "int", required = true)
    private Integer contractIsRenewal;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @ApiModelProperty(value = "合同续签日期", dataType = "Date")
    private Date contractRenewalDate;




    //数据库冗余数据
    @ApiModelProperty(value = "归属区域", dataType = "String")
    private String belongAreaStr;
    @ApiModelProperty(value = "行业类型", dataType = "String")
    private String industryTypeStr;
}
