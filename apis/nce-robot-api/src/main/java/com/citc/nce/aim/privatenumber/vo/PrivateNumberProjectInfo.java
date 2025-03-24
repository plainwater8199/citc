package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class PrivateNumberProjectInfo {
    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "订单ID", dataType = "Long")
    private Long orderId;

    @ApiModelProperty(value = "appKey", dataType = "String")
    private String appKey;

    @ApiModelProperty(value = "短信模版", dataType = "String")
    private String smsTemplate;

    @ApiModelProperty(value = "通道类型 fontdo 蜂动 emay 亿美", dataType = "Integer", required = true)
    private String type;

    @ApiModelProperty(value = "通道账号pathKey", dataType = "String")
    private String key;

    @ApiModelProperty(value = "通道秘钥secret", dataType = "String")
    private String pw;

    @ApiModelProperty(value = "是否包含阅信链接 0 不包含 1包含", dataType = "Integer", required = true)
    private Integer yuexinLinkEnable;

    @ApiModelProperty(value = "代理商id", dataType = "String")
     private String openId;

    @ApiModelProperty(value = "租户id", dataType = "String", required = true)
    private String ecId;

    @ApiModelProperty(value = "appId", dataType = "String", required = true)
    private String appId;

    @ApiModelProperty(value = "appSecret", dataType = "String", required = true)
    private String appSecret;

    @ApiModelProperty(value = "电信短链", dataType = "String", required = true)
    private String ctccYxLink;

    @ApiModelProperty(value = "联通短链", dataType = "String", required = true)
    private String cuccYxLink;

    @ApiModelProperty(value = "移动短链", dataType = "String", required = true)
    private String cmccYxLink;
}
