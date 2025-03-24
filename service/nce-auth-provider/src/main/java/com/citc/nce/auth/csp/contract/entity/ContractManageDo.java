package com.citc.nce.auth.csp.contract.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同管理
 * @BelongsPackage: com.citc.nce.auth.csp.contract.entity
 * @Author: litao
 * @CreateTime: 2023-02-13  10:11
 
 * @Version: 1.0
 */
@Data
@TableName("chatbot_contract_manage")
public class ContractManageDo extends BaseDo<ContractManageDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("合同名称")
    private String contractName;

    @ApiModelProperty("客户Id")
    private String customerId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("归属运营商编码：0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("大区")
    private String region;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("行业类型编码前缀")
    private String industryCodePrefix;

    @ApiModelProperty("行业类型编码后缀")
    private String industryCodeSuffix;

    @ApiModelProperty("归属代理商Id")
    private Long agentInfoId;

    @ApiModelProperty("客户联系人")
    private String contactName;

    @ApiModelProperty("联系人手机号")
    private String contactPhoneNumber;

    @ApiModelProperty("联系人邮箱")
    private String contactMail;

    @ApiModelProperty("附件url")
    private String contractFileUrl;

    @ApiModelProperty(value = "附件名称", dataType = "String")
    private String contractFileName;

    @ApiModelProperty("统一社会信用代码")
    private String contractOrginCode;

    @ApiModelProperty("企业责任人姓名")
    private String contractLegalPerson;

    @ApiModelProperty("责任人证件类型 1:身份证，2：中国人民解放军军人身份证件，3：中国人民武装警察身份证件 ")
    private Integer contractLegalPersonCardType;

    @ApiModelProperty("责任人证件号")
    private String contractLegalPersonCardNumber;

    @ApiModelProperty("合同电子扫描件url")
    private String contractScanningUrl;

    @ApiModelProperty("合同电子扫描件名称")
    private String contractScanningName;

    @ApiModelProperty("合同生效日期")
    private Date contractEffectiveDate;

    @ApiModelProperty("合同失效日期")
    private Date contractExpireDate;

    @ApiModelProperty("合同续签状态 0:未续签， 1：已续签")
    private Integer contractIsRenewal;

    @ApiModelProperty("合同续签日期")
    private Date contractRenewalDate;

    /**
     * 60 新增待审核
     * 61 变更待审核
     * 62 注销待审核
     * 63 新增审核不通过
     * 64 变更审核不通过
     * 65 注销审核不通过
     * 66 正常
     * 67 已注销
     * 68 已过期
     */
    @ApiModelProperty("合同状态 11:新增审核不通过， 12：变更审核不通过，20：新增审核中，22：待管理平台新增审核，23：待管理平台变更审核，30：正常，40：暂停")
    private Integer contractStatus;

    @ApiModelProperty("服务代码")
    private String contractServiceCode;

    @ApiModelProperty("服务扩展码")
    private String contractServiceExtraCode;

    @ApiModelProperty(value = "归属区域", dataType = "String")
    private String belongAreaStr;

    @ApiModelProperty(value = "行业类型", dataType = "String")
    private String industryTypeStr;
    //客户回执id
    private String customerNum;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer actualState;

    private String failureReason;

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

    private Integer deleted;

    private Date deletedTime;

    // 蜂动新增
    private Integer channel; // 通道（1-直连，2-蜂动）
    private String evidenceSrc; // 证明材料
    private String securityResponsibilityLetterSrc; // 安全保障责任书
    private String businessCommitmentLetterSrc; // 业务承诺函
    private String authorizationLetterSrc; // 授权书
    private String contractFileSrc; // 合同文件
}
