package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class PrivateNumberProjectEditReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id", dataType = "Long", required = true)
    @NotNull(message = "主键id不能为空")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String", required = true)
    @NotBlank(message = "项目Id不能为空")
    private String projectId;

    @ApiModelProperty(value = "项目名称", dataType = "String")
    @Length(max = 20, message = "项目名称长度超过限制(最大20位)")
    private String projectName;

    @ApiModelProperty(value = "客户appKey", dataType = "String")
    @NotBlank(message = "客户appKey不能为空")
    @Length(max = 256, message = "客户appKey长度超过限制(最大256位)")
    private String appKey;

    @ApiModelProperty(value = "通道账号", dataType = "String")
    @Length(max = 100, message = "通道账号长度超过限制(最大100位)")
    private String pathKey;

    @ApiModelProperty(value = "通道秘钥", dataType = "String")
    @Length(max = 100, message = "通道秘钥长度超过限制(最大100位)")
    private String secret;

    @ApiModelProperty(value = "短信内容", dataType = "String")
    @Length(max = 70, message = "短信内容长度超过限制(最大70位)")
    private String smsTemplate;

    @ApiModelProperty(value = "通道类型 fontdo 蜂动 emay 亿美", dataType = "Integer", required = true)
    @NotNull(message = "通道类型不能为空")
    private String channelType;
    @ApiModelProperty(value = "是否包含阅信链接 0 不包含 1包含", dataType = "Integer", required = true)
    @NotNull(message = "是否包含阅信链接不能为空")
    private Integer yuexinLinkEnable;

    @ApiModelProperty(value = "appId", dataType = "String", required = true)
    @Length(max = 100, message = "appId长度超过限制(最大100位)")
    private String appId;

    @ApiModelProperty(value = "appSecret", dataType = "String", required = true)
    @Length(max = 100, message = "appSecret长度超过限制(最大100位)")
    private String appSecret;

    @ApiModelProperty(value = "电信短链", dataType = "String", required = true)
    @Length(max = 100, message = "电信短链长度超过限制(最大100位)")
    private String ctccYxLink;

    @ApiModelProperty(value = "联通短链", dataType = "String", required = true)
    @Length(max = 100, message = "联通短链长度超过限制(最大100位)")
    private String cuccYxLink;

    @ApiModelProperty(value = "移动短链", dataType = "String", required = true)
    @Length(max = 100, message = "移动短链长度超过限制(最大70位)")
    private String cmccYxLink;

    @ApiModelProperty(value = "事件类型", dataType = "String", required = true)
    @Length(max = 100, message = "事件类型超过限制(最大100位)")
    private String eventType;
}
