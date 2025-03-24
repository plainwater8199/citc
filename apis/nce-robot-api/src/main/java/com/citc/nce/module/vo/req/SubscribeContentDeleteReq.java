package com.citc.nce.module.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubscribeContentDeleteReq implements Serializable {
    private static final long serialVersionUID = -7279184138751029380L;


    @ApiModelProperty("订阅组件表uuid")
    private String subscribeId;

    @ApiModelProperty("订阅内容表uuid")
    @NotNull
    private String subContentId; // 订阅内容表uuid


}
