package com.citc.nce.module.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubscribeContentInfo implements Serializable {
    private static final long serialVersionUID = -7279184138751029380L;

    @ApiModelProperty("订阅内容信息ID")
    private Long id;

    @ApiModelProperty("订阅组件表uuid")
    private String subscribeId;

    @ApiModelProperty("订阅内容表uuid")
    private String subContentId;

    @ApiModelProperty("标题")
    @NotNull(message = "标题不能为空！")
    @Length(max = 100, message = "标题长度超过限制(最大100位)")
    private String title;

    @ApiModelProperty("5G消息模板id")
    @NotNull(message = "5G消息模板id不能为空！")
    private Long msg5gId;

    @ApiModelProperty("订阅内容排序")
    private String subscribeContentOrder;

    @ApiModelProperty("订阅内容状态：0：待发送，1：已发送，2：取消发送")
    private String subscribeContentStatus;

    @ApiModelProperty("是否为最后一个订阅内容：0：不是最后一个，1：最后一个订阅内容")
    private String isTheLast;

}
