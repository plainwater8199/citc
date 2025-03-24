package com.citc.nce.auth.csp.contract.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.entity
 * @Author: litao
 * @CreateTime: 2023-02-13  10:11

 * @Version: 1.0
 */
@Data
@TableName("chatbot_contract_manage_change")
public class ContractManageChangeDo extends BaseDo<ContractManageChangeDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("合同名称")
    @TableField("contract_name")
    private String contractName;

    @ApiModelProperty("合同id")
    @TableField("contract_id")
    private Long contractId;

    @ApiModelProperty("归属客户id")
    @TableField("customer_id")
    private String customerId;

    @ApiModelProperty("归属客户id")
    private Long enterpriseIdentificationId;


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

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField("creator_old")
    private String creatorOld;

    @TableField("updater_old")
    private String updaterOld;

    @ApiModelProperty("删除时间")
    @TableField("deleted_time")
    private Long deletedTime;

    @ApiModelProperty("客户回执id")
    @TableField("customer_num")
    private String customerNum;

    @ApiModelProperty("客户版本号")
    @TableField("version")
    private Integer version;

    @ApiModelProperty("企业身份证正面")
    @TableField("corporate_id_card_front")
    private String corporateIdCardFront;

    @ApiModelProperty("企业身份证反面")
    @TableField("corporate_id_card_reverse")
    private String corporateIdCardReverse;


    @ApiModelProperty("联系人身份证正面")
    @TableField("contacts_id_card_front")
    private String contactsIdCardFront;

    @ApiModelProperty("联系人身份证反面")
    @TableField("contacts_id_card_reverse")
    private String contactsIdCardReverse;

    @ApiModelProperty("客户等级")
    @TableField("customer_grade")
    private Integer customerGrade;

    @ApiModelProperty("企业介绍")
    @TableField("introduce")
    private String introduce;

    @ApiModelProperty("办公电话")
    @TableField("work_phone")
    private String workPhone;

    @ApiModelProperty("企业logo")
    @TableField("service_icon")
    private String serviceIcon;

    @ApiModelProperty("营业执照")
    @TableField("business_license")
    private String businessLicense;


    @ApiModelProperty("详细地址")
    @TableField("address")
    private String address;


    @ApiModelProperty("营业执照显示地址")
    @TableField("business_license_src")
    private String businessLicenseSrc;

    @ApiModelProperty("企业身份证正面显示地址")
    @TableField("corporate_id_card_front_src")
    private String corporateIdCardFrontSrc;

    @ApiModelProperty("企业身份证反面显示地址")
    @TableField("corporate_id_card_reverse_src")
    private String corporateIdCardReverseSrc;

    @ApiModelProperty("联系人身份证正面显示地址")
    @TableField("contacts_id_card_front_src")
    private String contactsIdCardFrontSrc;

    @ApiModelProperty("联系人身份证反面显示地址")
    @TableField("contacts_id_card_reverse_src")
    private String contactsIdCardReverseSrc;

    @ApiModelProperty("企业logo显示地址")
    @TableField("service_icon_src")
    private String serviceIconSrc;
}
