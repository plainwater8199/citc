package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class ChannelInfoResp {
    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "电信通道（1-直连 2-蜂动）")
    private Integer telecomChannel;

    @ApiModelProperty(value = "移动通道（1-直连 2-蜂动）")
    private Integer mobileChannel;

    @ApiModelProperty(value = "联通通道（1-直连 2-蜂动）")
    private Integer unicomChannel;
}
