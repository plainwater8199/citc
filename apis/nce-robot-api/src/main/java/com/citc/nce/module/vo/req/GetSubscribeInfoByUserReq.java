package com.citc.nce.module.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetSubscribeInfoByUserReq {
    @ApiModelProperty("订阅用户手机号")
    @NotNull(message = "手机号不能为空！")
    private String phone;
    @ApiModelProperty("订阅组件ID")
    @NotNull(message = "订阅组件ID不能为空！")
    private String subscribeId;
}
