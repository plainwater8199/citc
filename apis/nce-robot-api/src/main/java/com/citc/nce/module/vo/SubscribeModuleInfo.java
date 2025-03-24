package com.citc.nce.module.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubscribeModuleInfo implements Serializable {
    private static final long serialVersionUID = -6525770225160060553L;

    @ApiModelProperty("订阅组件信息ID")
    private Long id;

    @ApiModelProperty("订阅组件表uuid")
    private String subscribeId; //订阅组件表uuid

    @ApiModelProperty("名称")
    @NotNull(message = "名称不能为空！")
    @Length(max = 25, message = "名称长度超过限制(最大25位)")
    private String name;

    @ApiModelProperty("描述")
    @Length(max = 500, message = "描述长度超过限制(最大500位)")
    private String description;

    @ApiModelProperty("模板状态，0：待发送，1：发送中，2：发送完毕")
    private Integer subscribeStatus;

    @ApiModelProperty("发送类型")
    @NotNull(message = "发送类型不能为空！")
    private String sendType; //发送时间类型(DAY每天/First,second,third,fourth,fifth,sixth,seventh(每周第几天))

    @ApiModelProperty(value = "发送时间")
    @NotNull(message = "发送时间不能为空！")
    private String sendTime;

    @ApiModelProperty("订阅成功提示")
    private Integer subsSuccess;

    @ApiModelProperty("订阅成功5G消息模板id")
    private Long success5gMsgId;

    @ApiModelProperty("取消订阅提示")
    private Integer subsCancel;

    @ApiModelProperty("取消订阅5G消息模板id")
    private Long cancel5gMsgId;

    @ApiModelProperty("订阅结束提示")
    private Integer subsEnd;

    @ApiModelProperty("订阅结束5G消息模板id")
    private Long end5gMsgId;

    @ApiModelProperty("订阅量")
    private Long subscribeCount;
}
