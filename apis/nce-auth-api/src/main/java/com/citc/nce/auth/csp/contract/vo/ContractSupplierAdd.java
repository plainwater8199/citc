package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ContractSupplierAdd implements Serializable {
    private static final long serialVersionUID = 1L;

    //客户信息
    // 合同名称
    @ApiModelProperty(value = "合同名称", dataType = "String", required = true)
    @NotBlank(message = "合同名称不能为空")
    @Length(max = 25, message = "合同名称长度超过限制(最大25位)")
    private String contractName;

    // 归属客户
    @ApiModelProperty(value = "归属客户id", dataType = "int", required = true)
    @NotBlank(message = "归属客户不能为空")
    private String customerId;

    // 授权书
    @ApiModelProperty(value = "授权书src", dataType = "String")
    @NotBlank(message = "授权书不能为空")
    private String authorizationLetterSrc;

    // 业务承诺函
    @ApiModelProperty(value = "业务承诺函src", dataType = "String")
    @NotBlank(message = "业务承诺函不能为空")
    private String businessCommitmentLetterSrc;

    // 安全保障责任书
    @ApiModelProperty(value = "安全保障责任书src", dataType = "String")
    @NotBlank(message = "安全保障责任书不能为空")
    private String securityResponsibilityLetterSrc;

    // 证明材料
    @ApiModelProperty(value = "证明材料src", dataType = "String")
    @NotBlank(message = "证明材料不能为空")
    private String evidenceSrc;

    // 合同文件
    @ApiModelProperty(value = "合同文件src", dataType = "String")
    private String contractFileSrc;


    // 企业信息
    // 营业执照
    @ApiModelProperty(value = "营业执照扫描件src", dataType = "String")
    @NotBlank(message = "营业执照扫描件不能为空")
    private String businessLicenseSrc;

    // 客户名称
    @ApiModelProperty(value = "客户名称", dataType = "String")
    @Length(max = 34, message = "客户名长度超过限制")
    @NotBlank(message = "客户名称不能为空")
    private String enterpriseName;

    // 办公电话
    @ApiModelProperty(value = "办公电话", dataType = "String")
    @NotBlank(message = "办公电话不能为空")
    private String workPhone;


    // 联系人信息
    // 客户联系人
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

    @ApiModelProperty(value = "联系人身份证背面", dataType = "String", required = true)
    @NotBlank(message = "联系人身份证背面不能为空")
    private String contactsIdCardReverseSrc;

    // 额外信息
    @ApiModelProperty(value = "归属运营商编码 -1:全部 0：缺省(硬核桃),1：联通,2：移动,3：电信", dataType = "String")
    @NotNull(message = "归属运营商编码 -1:全部 0：缺省(硬核桃),1：联通,2：移动,3：电信")
    private Integer operatorCode;

    @ApiModelProperty(value = "归属区域", dataType = "String")
    private String belongAreaStr;

    @ApiModelProperty(value = "所在省", dataType = "String", required = true)
    @NotBlank(message = "所在省不能为空")
    private String province;

    @ApiModelProperty(value = "所在市", dataType = "String", required = true)
    @NotBlank(message = "所在市不能为空")
    private String city;

    @ApiModelProperty(value = "所在地区", dataType = "String", required = true)
    @NotBlank(message = "所在地区不能为空")
    private String region;
}
