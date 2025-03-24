package com.citc.nce.authcenter.auth.vo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhujy
 * @Date: 2024/3/16 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class SupplierChatbotInfoResp implements Serializable {

    private static final long serialVersionUID = 1L;

    //uuid 唯一标识
    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    private String chatbotAccountId;

    // 归属客户
    @ApiModelProperty(value = "归属客户Id", dataType = "String", required = true)
    private String customerId;

    // 归属客户
    @ApiModelProperty(value = "归属客户名", dataType = "String", required = true)
    private String customerName;

    // Chatbot名称
    @ApiModelProperty(value = "chatbot名称", dataType = "String", required = true)
    private String chatbotName;

    // Chatbot Logo
    @ApiModelProperty(value = "ChatbotLogoUrl", dataType = "String", required = true)
    private String chatbotLogoUrl;

    // Chatbot描述
    @ApiModelProperty(value = "Chatbot描述", dataType = "String", required = true)
    private String chatbotServiceDesc;

    // Chatbot签名
    @ApiModelProperty(value = "Chatbot签名", dataType = "String", required = true)
    private String chatbotSmsSign;


    // 行业类型
    // 一级行业
    @ApiModelProperty(value = "", dataType = "int", required = true)
    private Integer chatbotFirstIndustryType;

    // 二级行业
    @ApiModelProperty(value = "", dataType = "int", required = true)
    private Integer chatbotSecondIndustryType;

    // 详细地址
    private String address;//Chatbot详细地址
    private String longitude;//经度
    private String latitude;//纬度

    // 服务条款
    @ApiModelProperty(value = "服务条款", dataType = "String", required = true)
    private String serviceTerm;

    // 官网地址
    @ApiModelProperty(value = "官网地址", dataType = "String", required = true)
    private String serviceWebsite;

    // 回呼号码
    @ApiModelProperty(value = "回呼号码", dataType = "String", required = true)
    private String chatbotCallBackNumber;

    // 联系邮箱
    @ApiModelProperty(value = "联系邮箱", dataType = "String", required = true)
    private String chatbotMail;

    // 背景图片
    @ApiModelProperty(value = "Chatbot背景图片", dataType = "String")
    private String backgroundImgUrl;

    // 测试手机号白名单
    @ApiModelProperty(value = "测试手机号白名单(多个时以英文半角逗号,分隔)", dataType = "string", required = true)
    private String whiteList;

    // 运营商编码
    @ApiModelProperty(value = "运营商编码", dataType = "int", required = true)
    private Integer operatorCode;


}