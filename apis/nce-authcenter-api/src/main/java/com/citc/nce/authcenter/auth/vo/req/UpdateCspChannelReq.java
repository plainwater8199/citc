package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateCspChannelReq extends GetUserInfoReq {

    @NotNull(message = "电信通道不能为空")
    @ApiModelProperty(value = "电信通道（1-直连 2-蜂动）")
    private Integer telecomChannel;

    @NotNull(message = "移动通道不能为空")
    @ApiModelProperty(value = "移动通道（1-直连 2-蜂动）")
    private Integer mobileChannel;

    @NotNull(message = "联通通道不能为空")
    @ApiModelProperty(value = "联通通道（1-直连 2-蜂动）")
    private Integer unicomChannel;
}
