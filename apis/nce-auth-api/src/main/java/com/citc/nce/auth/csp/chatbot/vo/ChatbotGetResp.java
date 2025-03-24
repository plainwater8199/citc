package com.citc.nce.auth.csp.chatbot.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotGetResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "chatbot名称", dataType = "String")
    private String chatbotName;

    @ApiModelProperty(value = "归属客户id", dataType = "int", required = true)
    private Long enterpriseId;

    @ApiModelProperty(value = "客户名称", dataType = "String", required = true)
    private String enterpriseName;

    @ApiModelProperty(value = "服务代码", dataType = "String", required = true)
    private String chatbotServiceCode;

    @ApiModelProperty(value = "服务扩展码")
    private String chatbotServiceExtraCode;

    @ApiModelProperty(value = "机器人Logo文件url", dataType = "String", required = true)
    private String chatbotLogoUrl;

    @ApiModelProperty(value = "所在省", dataType = "String", required = true)
    private String province;

    @ApiModelProperty(value = "所在市", dataType = "String", required = true)
    private String city;

    @ApiModelProperty(value = "所在大区", dataType = "String", required = true)
    private String region;

    @ApiModelProperty(value = "chatbot类型", dataType = "String", required = true)
    private String chatbotType;

    @ApiModelProperty(value = "行业类型 1-党政军,2-民生，3-金融，4-物流，5-游戏，6-电商，7-微商（个人），8-沿街商铺（中小），9-企业（大型）,10-教育培训,11-房地产,12-医疗器械、药店,13-其他", dataType = "int", required = true)
    private String chatbotIndustryType;

    @ApiModelProperty(value = "短信端口", dataType = "String", required = true)
    private String chatbotSmsPort;

    @ApiModelProperty(value = "短信签名", dataType = "String", required = true)
    private String chatbotSmsSign;

    @ApiModelProperty(value = "服务描述", dataType = "String")
    private String chatbotServiceDesc;

    @ApiModelProperty(value = "回叫号码", dataType = "String")
    private String chatbotCallBackNumber;

    @ApiModelProperty(value = "电子邮箱", dataType = "String")
    private String chatbotMail;

    @ApiModelProperty(value = "首页地址", dataType = "String")
    private String chatbotHomepage;

    @ApiModelProperty(value = "气泡颜色 默认0:跟随终端， 1:自定义", dataType = "int")
    private Integer chatbotBubbleColor;

    @ApiModelProperty(value = "气泡颜色RGB编码", dataType = "String")
    private String chatbotBubbleColorRGB;

    @ApiModelProperty(value = "附件Url", dataType = "String")
    private String chatbotFileUrl;

    @ApiModelProperty(value = "附件名", dataType = "String")
    private String annexName;

    @ApiModelProperty(value = "chatbos提供者 0：不显示 1：显示 ", dataType = "int")
    private Integer chatbotISPIsDisplay;

    @ApiModelProperty(value = "白名单手机号(多个时以英文半角逗号,分隔)", dataType = "string")
    private String whiteList;

    private String address;//Chatbot详细地址
    private String serviceWebsite;//Chatbot官网(主页地址)
    private String callBackNumber;//Chatbot服务电话
    private String longitude;//经度
    private String latitude;//纬度
    private String serviceTerm;

    private Integer chatbotStatus;

    @ApiModelProperty(value = "本地机器人Logo文件url", dataType = "String")
    private String localChatbotLogoUrl;

    @ApiModelProperty(value = "本地附件Url", dataType = "String")
    private String localChatbotFileUrl;

    private String customerId;

    private String creator;
}
