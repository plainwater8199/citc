package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LogoutReq {
    @NotNull(message = "设备类型不能为空")
    @ApiModelProperty(value = "设备类型 (PC：电脑端，phone：手机)", dataType = "String", required = true)
    private String deviceType;
}
