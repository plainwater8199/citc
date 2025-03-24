package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdateChatbotSupplierReq implements Serializable {

    // id
    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    @NotNull(message = "chatbotAccountId不能为空")
    private String chatbotAccountId;

    // Chatbot名称
    @ApiModelProperty(value = "chatbot名称", dataType = "String", required = true)
    @NotBlank(message = "chatbot名称不能为空")
    @Length(max = 20, message = "chatbot名称长度超过限制(最大20位)")
    private String chatbotName;

    // Chatbot Logo
    @ApiModelProperty(value = "ChatbotLogoUrl", dataType = "String", required = true)
    @NotBlank(message = "Chatbot LOGO不能为空")
    private String chatbotLogoUrl;

    // Chatbot描述
    @ApiModelProperty(value = "Chatbot描述", dataType = "String", required = true)
    @Length(max = 166, message = "Chatbot 描述长度超过限制(最大166位)")
    @NotBlank(message = "Chatbot 描述不能为空")
    private String chatbotServiceDesc;

    // Chatbot签名
    @ApiModelProperty(value = "Chatbot签名", dataType = "String", required = true)
    @NotBlank(message = "Chatbot 签名不能为空")
    @Length(max = 60, message = "Chatbot 签名长度超过限制(最大60位)")
    private String chatbotSmsSign;

    // 行业类型
    // 一级行业
    @ApiModelProperty(value = "", dataType = "int", required = true)
    @NotNull(message = "行业类型不能为空")
    private String chatbotFirstIndustryType;

    // 二级行业
    @ApiModelProperty(value = "", dataType = "int", required = true)
    @NotNull(message = "行业类型不能为空")
    private String chatbotSecondIndustryType;

    // 详细地址
    private String address;//Chatbot详细地址
    private String longitude;//经度
    private String latitude;//纬度

    // 服务条款
    @ApiModelProperty(value = "服务条款", dataType = "String", required = true)
    @NotNull(message = "服务条款不能为空")
    private String serviceTerm;

    // 官网地址
    @ApiModelProperty(value = "官网地址", dataType = "String", required = true)
    @NotNull(message = "官网地址不能为空")
    private String serviceWebsite;

    // 回呼号码
    @ApiModelProperty(value = "回呼号码", dataType = "String", required = true)
    @Length(max = 21, message = "回呼号码长度超过限制(最大21位)")
    @NotNull(message = "回呼号码不能为空")
    private String chatbotCallBackNumber;

    // 联系邮箱
    @ApiModelProperty(value = "联系邮箱", dataType = "String", required = true)
    @Length(max = 50, message = "联系邮箱长度超过限制(最大50位)")
    @NotNull(message = "联系邮箱不能为空")
    private String chatbotMail;

    // 背景图片
    @ApiModelProperty(value = "Chatbot背景图片", dataType = "String")
    private String backgroundImgUrl;

    // 测试手机号白名单
    @ApiModelProperty(value = "测试手机号白名单(多个时以英文半角逗号,分隔)", dataType = "string", required = true)
    @NotBlank(message = "测试手机号白名单不能为空")
    private String whiteList;

    // 运营商编码
    @ApiModelProperty(value = "运营商编码", dataType = "int", required = true)
    @NotNull(message = "运营商编码不能为空")
    private Integer operatorCode;

    @Override
    public String toString() {
        //toString方法内容为中文+value
        return "Chatbot名称='" + chatbotName +
                ", ChatbotLogoUrl='" + chatbotLogoUrl +
                ", Chatbot描述='" + chatbotServiceDesc +
                ", Chatbot签名='" + chatbotSmsSign +
                ", 一级行业='" + chatbotFirstIndustryType +
                ", 二级行业='" + chatbotSecondIndustryType +
                ", 详细地址='" + address +
                ", 经度='" + longitude +
                ", 纬度='" + latitude +
                ", 服务条款='" + serviceTerm +
                ", 官网地址='" + serviceWebsite +
                ", 回呼号码='" + chatbotCallBackNumber +
                ", 联系邮箱='" + chatbotMail +
                ", Chatbot背景图片='" + backgroundImgUrl +
                ", 测试手机号白名单='" + whiteList +
                ", 运营商编码='" + operatorCode ;
    }
}
