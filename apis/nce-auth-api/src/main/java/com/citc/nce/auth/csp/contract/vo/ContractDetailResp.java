package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ContractDetailResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合同Id")
    private Long id;

    @ApiModelProperty(value = "合同名称", dataType = "String", required = true)
    private String contractName;

    @ApiModelProperty(value = "归属代理商id", dataType = "int", required = true)
    private Integer agentInfoId;

    @ApiModelProperty(value = "客户联系人", dataType = "string", required = true)
    private String contactName;

    @ApiModelProperty(value = "手机号", dataType = "Integer")
    private String contactPhoneNumber;

    @ApiModelProperty(value = "邮箱", dataType = "String")
    private String contactMail;

    @ApiModelProperty(value = "客户Id", dataType = "String")
    private String customerId;

    @ApiModelProperty(value = "企业责任人姓名", dataType = "String", required = true)
    private String contractLegalPerson;

    @ApiModelProperty(value = "企业责任人证件类型 01:身份证，02：中国人民解放军军人身份证件，03：中国人民武装警察身份证件 ", dataType = "int", required = true)
    private Integer contractLegalPersonCardType;

    @ApiModelProperty(value = "企业责任人证件号码", dataType = "String", required = true)
    private String contractLegalPersonCardNumber;

    @ApiModelProperty(value = "统一社会信用代码/注册号/组织机构代码", dataType = "String")
    private String contractOrginCode;

    @ApiModelProperty(value = "企业名称", dataType = "String", required = true)
    private String enterpriseName;

    @ApiModelProperty(value = "详细地址", dataType = "String", required = true)
    private String address;

    @ApiModelProperty(value = "所在省", dataType = "String", required = true)
    private String province;

    @ApiModelProperty(value = "所在市", dataType = "String", required = true)
    private String city;

    @ApiModelProperty(value = "所在地区", dataType = "String", required = true)
    private String region;

    @ApiModelProperty(value = "附件url", dataType = "String")
    private String contractFileUrl;

    @ApiModelProperty(value = "附件名称", dataType = "String")
    private String contractFileName;

    @ApiModelProperty(value = "合同电子扫描件url", dataType = "String")
    private String contractScanningUrl;

    @ApiModelProperty(value = "合同电子扫描件名称", dataType = "String")
    private String contractScanningName;

    @ApiModelProperty(value = "合同生效日期", dataType = "Date")
    private Date contractEffectiveDate;

    @ApiModelProperty(value = "合同失效日期", dataType = "Date")
    private Date contractExpireDate;

    @ApiModelProperty(value = "合同续签日期", dataType = "Date")
    private Date contractRenewalDate;

    @ApiModelProperty(value = "合同续签状态 0:未续签， 1：已续签", dataType = "int")
    private Integer contractIsRenewal;

    @ApiModelProperty(value = "合同状态 0:未续签， 1：已续签", dataType = "int")
    private Integer contractStatus;

    @ApiModelProperty(value = "合同id", dataType = "int")
    private Integer contractId;

    @ApiModelProperty(value = "归属区域", dataType = "String")
    private String belongAreaStr;

    @ApiModelProperty(value = "行业类型", dataType = "String")
    private String industryTypeStr;

    @ApiModelProperty(value = "行业类型编码前缀", dataType = "String")
    private String industryCodePrefix;

    @ApiModelProperty(value = "行业类型编码后缀", dataType = "String")
    private String industryCodeSuffix;

    private Integer availableStatus;

    private String corporateIdCardFront;
    private String corporateIdCardReverse;
    private String contactsIdCardFront;
    private String contactsIdCardReverse;

    private String serviceIconSrc;
    private String businessLicenseSrc;
    private String contactsIdCardFrontSrc;
    private String contactsIdCardReverseSrc;
    private String corporateIdCardFrontSrc;
    private String corporateIdCardReverseSrc;


    private Integer customerGrade; //客户等级
    private String introduce; //企业介绍
    private String workPhone; //办公电话
    private String serviceIcon; //企业logo
    private String businessLicense;//营业执照

}
