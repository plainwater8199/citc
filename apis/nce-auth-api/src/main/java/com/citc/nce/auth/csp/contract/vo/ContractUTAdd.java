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
 * 联通电信
 *
 * @{DATE}
 **/
@Data
public class ContractUTAdd implements Serializable {

    private static final long serialVersionUID = 1L;

    //合同信息
    @ApiModelProperty(value = "合同名称", dataType = "String", required = true)
    @NotBlank(message = "合同名称不能为空")
    @Length(max = 25, message = "合同名称长度超过限制(最大25位)")
    private String contractName;
    @ApiModelProperty(value = "归属客户id", dataType = "int", required = true)
    @NotBlank(message = "归属客户不能为空")
    private String customerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "合同生效日期", dataType = "Date")
    @NotNull(message = "合同生效日期不能为空")
    private Date contractEffectiveDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "合同失效日期", dataType = "Date")
    @NotNull(message = "合同失效日期不能为空")
    private Date contractExpireDate;
    @ApiModelProperty(value = "合同续签状态 0:未续签， 1：已续签", dataType = "int")
    @NotNull(message = "合同续签状态不能为空")
    private Integer contractIsRenewal;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "合同续签日期", dataType = "Date")
    private Date contractRenewalDate;

    @ApiModelProperty(value = "合同电子扫描件名称", dataType = "String")
    @NotBlank(message = "合同电子扫描件不能为空")
    private String contractScanningName;
    @ApiModelProperty(value = "合同电子扫描件url", dataType = "String")
    @NotBlank(message = "合同电子扫描件不能为空")
    private String contractScanningUrl;

    //企业信息
    @ApiModelProperty(value = "营业执照扫描件url", dataType = "String")
    @NotBlank(message = "营业执照扫描件不能为空")
    private String businessLicense;
    @ApiModelProperty(value = "营业执照扫描件id", dataType = "String")
    @NotBlank(message = "营业执照扫描件不能为空")
    private String businessLicenseSrc;

    @ApiModelProperty(value = "客户名称---平台是企业名称", dataType = "String")
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
    @NotBlank(message = "所在市不能为空")
    private String city;
    @ApiModelProperty(value = "归属区域", dataType = "String")
    @NotBlank(message = "归属区域不能为空")
    private String belongAreaStr;

    @ApiModelProperty(value = "详细地址", dataType = "String")
    @Length(max = 100, message = "详细地址长度超过限制")
    @NotBlank(message = "详细地址不能为空")
    private String address;

    @ApiModelProperty(value = "行业类型", dataType = "String")
    @NotBlank(message = "行业类型不能为空")
    private String industryTypeStr;
    @ApiModelProperty(value = "行业类型编码前缀", dataType = "String")
    @NotBlank(message = "行业类型编码前缀不能为空")
    private String industryCodePrefix;
    @ApiModelProperty(value = "客户等级", dataType = "String")
    @NotNull(message = "客户等级不能为空")
    private Integer customerGrade;
    @ApiModelProperty(value = "企业介绍", dataType = "String")
    @NotBlank(message = "企业介绍不能为空")
    private String introduce;
    @ApiModelProperty(value = "办公电话", dataType = "String")
    @NotBlank(message = "办公电话不能为空")
    private String workPhone;

    @ApiModelProperty(value = "企业logo", dataType = "String")
    @NotBlank(message = "企业logo不能为空")
    private String serviceIcon;
    @ApiModelProperty(value = "企业logo", dataType = "String")
    @NotBlank(message = "企业logo不能为空")
    private String serviceIconSrc;

    //法人信息
    @ApiModelProperty(value = "企业责任人姓名", dataType = "String", required = true)
    @NotBlank(message = "企业责任人姓名不能为空")
    @Length(max = 20, message = "企业责任人姓名长度超过限制(最大20位)")
    private String contractLegalPerson;
    @ApiModelProperty(value = "企业责任人证件号码", dataType = "String", required = true)
    @NotBlank(message = "身份证号码不能为空")
    @Length(max = 18, message = "身份证号码长度超过限制(最大18位)")
    private String contractLegalPersonCardNumber;
    @ApiModelProperty(value = "企业责任身份证正面", dataType = "String", required = true)
    @NotBlank(message = "企业责任身份证正面不能为空")
    private String corporateIdCardFrontSrc;
    @NotBlank(message = "企业责任身份证正面不能为空")
    private String corporateIdCardFront;
    @ApiModelProperty(value = "企业责任身份证背面", dataType = "String", required = true)
    @NotBlank(message = "企业责任身份证背面不能为空")
    private String corporateIdCardReverseSrc;
    @NotBlank(message = "企业责任身份证背面不能为空")
    private String corporateIdCardReverse;

    //联系人信息
    @ApiModelProperty(value = "联系人", dataType = "string", required = true)
    @Length(max = 20, message = "联系人长度超过限制(最大11位)")
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    @ApiModelProperty(value = "联系人手机号", dataType = "Integer")
    @NotNull(message = "联系人不能为空")
    private String contactPhoneNumber;
    @ApiModelProperty(value = "联系人邮箱", dataType = "String")
    @Length(max = 50, message = "邮箱长度超过限制(最大50位)")
    @NotBlank(message = "联系人邮箱")
    private String contactMail;
    @ApiModelProperty(value = "联系人身份证正面", dataType = "String", required = true)
    @NotBlank(message = "联系人身份证正面不能为空")
    private String contactsIdCardFrontSrc;
    @NotBlank(message = "联系人身份证正面不能为空")
    private String contactsIdCardFront;
    @ApiModelProperty(value = "联系人身份证背面", dataType = "String", required = true)
    @NotBlank(message = "联系人身份证背面不能为空")
    private String contactsIdCardReverseSrc;
    @NotBlank(message = "联系人身份证背面不能为空")
    private String contactsIdCardReverse;
}
