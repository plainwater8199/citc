package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendDataVo {

    @ApiModelProperty("账号")
    private String accountId;

    @ApiModelProperty("登陆账号")
    private String customerId;

    @ApiModelProperty("删除时间")
    private LocalDateTime deletedTime;

    @ApiModelProperty("审核状态")
    private Integer audit;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板签名")
    private String templateSign;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer messageType;

    @ApiModelProperty("快捷按钮")
    private String shortcutButton;

    @ApiModelProperty("消息数据JSON")
    private String moduleInformation;
}
