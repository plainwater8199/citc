package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EnterpriseIdentificationApplyReq {
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @ApiModelProperty(value = "姓名", dataType = "String", required = true)
    @NotBlank(message = "姓名不能为空")
    @Length(max = 64, message = "姓名长度超过限制")
    private String personName;

    @ApiModelProperty(value = "身份证号", dataType = "String", required = true)
    @NotBlank(message = "身份证号不能为空")
    @Length(max = 18, message = "身份证号长度超过限制")
    private String idCard;

    @ApiModelProperty(value = "身份证正面照片", dataType = "String", required = true)
    @NotBlank(message = "身份证正面照片不能为空")
    private String idCardImgFront;

    @ApiModelProperty(value = "身份证反面照片", dataType = "String", required = true)
    @NotBlank(message = "身份证反面照片不能为空")
    private String idCardImgBack;

    @ApiModelProperty(value = "企业账户名", dataType = "String", required = true)
    @NotBlank(message = "企业账户名不能为空")
    private String enterpriseAccountName;

    @ApiModelProperty(value = "营业执照", dataType = "String", required = true)
    @NotBlank(message = "营业执照不能为空")
    private String enterpriseLicense;

    @ApiModelProperty(value = "统一社会信用代码/注册号/组织机构代码", dataType = "String", required = true)
    @NotBlank(message = "统一社会信用代码/注册号/组织机构代码不能为空")
    @Length(max = 32, message = "统一社会信用代码/注册号/组织机构代码长度超过限制")
    private String creditCode;

    @ApiModelProperty(value = "企业名称", dataType = "String", required = true)
    @NotBlank(message = "企业名称不能为空")
    @Length(max = 100, message = "企业名称长度超过限制")
    private String enterpriseName;

    @ApiModelProperty(value = "详细地址", dataType = "String", required = true)
    @NotBlank(message = "详细地址不能为空")
    @Length(max = 100, message = "详细地址长度超过限制")
    private String address;

    @ApiModelProperty(value = "所在省", dataType = "String", required = true)
    @NotBlank(message = "所在省不能为空")
    private String province;

    @ApiModelProperty(value = "所在市", dataType = "String", required = true)
    @NotBlank(message = "所在市不能为空")
    private String city;

    @ApiModelProperty(value = "所在地区", dataType = "String", required = true)
    @NotBlank(message = "所在地区不能为空")
    private String area;

    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer", required = true)
    @NotNull(message = "平台信息不能为空")
    private Integer protal;
}
