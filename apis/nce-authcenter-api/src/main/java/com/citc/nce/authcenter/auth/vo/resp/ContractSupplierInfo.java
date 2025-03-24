package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ContractSupplierInfo extends ResultResp{

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "数据库主键", dataType = "Long", required = true)
    private Long id;

    @ApiModelProperty("客户企业名称")
    private String enterpriseAccountName;
    //客户信息
    // 合同名称
    @ApiModelProperty(value = "合同名称", dataType = "String", required = true)
    private String contractName;

    // 归属客户
    @ApiModelProperty(value = "归属客户id", dataType = "int", required = true)
    private String customerId;

    // 授权书
    @ApiModelProperty(value = "授权书src", dataType = "String")
    private String authorizationLetterSrc;

    // 业务承诺函
    @ApiModelProperty(value = "业务承诺函src", dataType = "String")
    private String businessCommitmentLetterSrc;

    // 安全保障责任书
    @ApiModelProperty(value = "安全保障责任书src", dataType = "String")
    private String securityResponsibilityLetterSrc;

    // 证明材料
    @ApiModelProperty(value = "证明材料src", dataType = "String")
    private String evidenceSrc;
    @ApiModelProperty(value = "合同文件", dataType = "String")
    private String contractFileSrc;

    @ApiModelProperty(value = "所在省", dataType = "String")
    private String province;

    @ApiModelProperty(value = "所在市", dataType = "String")
    private String city;

    @ApiModelProperty(value = "所在地区", dataType = "String")
    private String region;

    // 企业信息
    // 营业执照
    @ApiModelProperty(value = "营业执照扫描件src", dataType = "String")
    private String businessLicenseSrc;

    // 客户名称
    @ApiModelProperty(value = "客户名称", dataType = "String")
    private String enterpriseName;

    // 办公电话
    @ApiModelProperty(value = "办公电话", dataType = "String")
    private String workPhone;


    // 联系人信息
    // 客户联系人
    @ApiModelProperty(value = "联系人", dataType = "string", required = true)
    private String contactName;

    @ApiModelProperty(value = "联系人手机号", dataType = "Integer")
    private String contactPhoneNumber;

    @ApiModelProperty(value = "联系人邮箱", dataType = "String")
    private String contactMail;

    @ApiModelProperty(value = "联系人身份证正面", dataType = "String", required = true)
    private String contactsIdCardFrontSrc;

    @ApiModelProperty(value = "联系人身份证背面", dataType = "String", required = true)
    private String contactsIdCardReverseSrc;

    // 额外信息
    @ApiModelProperty(value = "归属运营商编码 -1:全部 0：缺省(硬核桃),1：联通,2：移动,3：电信", dataType = "String")
    private Integer operatorCode;


}