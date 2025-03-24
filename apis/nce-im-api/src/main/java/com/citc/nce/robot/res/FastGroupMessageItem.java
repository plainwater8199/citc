package com.citc.nce.robot.res;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.robot.enums.FastGroupMessageStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel
public class FastGroupMessageItem {

    @ApiModelProperty("快捷群发ID")
    private Long id;

    @ApiModelProperty("是否为定时发送：0-否，1-是")
    private Integer isTiming;

    @ApiModelProperty("设置发送时间")
    private LocalDateTime settingTime;

    @ApiModelProperty("发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty("模板id")
    private Long templateId;

    @ApiModelProperty("模板内容")
    private String templateContent;

    @ApiModelProperty("消息类型")
    private MsgTypeEnum type;

    @ApiModelProperty("发送账号名称")
    private String account;

    @ApiModelProperty("发送账号")
    private String accounts;

    @ApiModelProperty("联系人组id")
    private Long groupId;

    @ApiModelProperty("联系人组名称")
    private String groupName;

    @ApiModelProperty("发送状态:群发状态：1-发送中，2-待发送，3-发送失败，4-发送成功")
    private Integer status;

    @ApiModelProperty("发送结果")
    private String failureReason;

    @ApiModelProperty("发送量")
    private Integer sendNumber;

    @ApiModelProperty("成功数")
    private Integer successNumber;

    @ApiModelProperty("失败数")
    private Integer failedNumber;

    @ApiModelProperty("未知数")
    private Integer unknownNumber;

    @ApiModelProperty("回落数")
    private Integer fallbackNumber;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
