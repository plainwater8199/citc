package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateSupplierContractReq {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "数据库主键", dataType = "Long", required = true)
    private Long id;
//客户信息
    // 合同名称
    @ApiModelProperty(value = "合同名称", dataType = "String", required = true)
    private String contractName;

//    @ApiModelProperty(value = "归属运营商编码 -1:全部 0：缺省(硬核桃),1：联通,2：移动,3：电信", dataType = "Integer", required = true)
    private Integer operatorCode;
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

    @ApiModelProperty(value = "所在省", dataType = "String", required = true)
    @NotBlank(message = "所在省不能为空")
    private String province;

    @ApiModelProperty(value = "所在市", dataType = "String", required = true)
    @NotBlank(message = "所在市不能为空")
    private String city;

    @ApiModelProperty(value = "所在地区", dataType = "String", required = true)
    @NotBlank(message = "所在地区不能为空")
    private String region;
    // 合同文件
    @ApiModelProperty(value = "合同文件src", dataType = "String")
    private String contractFileSrc;

    @Override
    public String toString() {
        //toString方法内容为中文+value
        return "合同名称='" + contractName +
                "', 归属客户id='" + customerId +
                "', 授权书src='" + authorizationLetterSrc +
                "', 业务承诺函src='" + businessCommitmentLetterSrc +
                "', 安全保障责任书src='" + securityResponsibilityLetterSrc +
                "', 证明材料src='" + evidenceSrc +
                "', 营业执照扫描件src='" + businessLicenseSrc +
                "', 合同文件src='"+contractFileSrc+
                "', 客户名称='" + enterpriseName +
                "', 所在省='" + province +
                "', 所在市='" + city +
                "', 所在地区='" + region +
                "', 办公电话='" + workPhone +
                "', 联系人='" + contactName +
                "', 联系人手机号=" + contactPhoneNumber +
                "', 联系人邮箱='" + contactMail +
                "', 联系人身份证正面Src='" + contactsIdCardFrontSrc +
                "', 联系人身份证背面Src='" + contactsIdCardReverseSrc+"'";
    }

}