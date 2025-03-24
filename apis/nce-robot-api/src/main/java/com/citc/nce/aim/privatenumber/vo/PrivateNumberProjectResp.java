package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class  PrivateNumberProjectResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "int")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "项目名称", dataType = "String")
    private String projectName;

    @ApiModelProperty(value = "客户appKey", dataType = "String")
    private String appKey;

    @ApiModelProperty(value = "通道账号", dataType = "String")
    private String pathKey;

    @ApiModelProperty(value = "通道秘钥", dataType = "String")
    private String secret;

    @ApiModelProperty(value = "短信内容", dataType = "String")
    private String smsTemplate;

    @ApiModelProperty(value = "项目状态 0:禁用 1:启用", dataType = "int")
    private int projectStatus;

    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除", dataType = "int")
    private int deleted;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;

    @ApiModelProperty(value = "订单状态 0:无开启订单 1:有开启订单", dataType = "int")
    private int orderStatus;

    @ApiModelProperty(value = "通道类型 fontdo 蜂动 emay 亿美", dataType = "Integer", required = true)
    private String channelType;

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

    @ApiModelProperty(value = "事件类型", dataType = "String", required = true)
    private String eventType;
}
