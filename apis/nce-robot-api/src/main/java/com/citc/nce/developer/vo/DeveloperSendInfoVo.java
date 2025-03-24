package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendInfoVo {

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("唯一标识")
    private String appId;

    @ApiModelProperty("公钥")
    private String appKey;

    @ApiModelProperty("秘钥")
    private String appSecret;

    @ApiModelProperty("回调地址")
    private String callbackUrl;

    @ApiModelProperty("接口地址")
    private String receiveUrl;

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;

    @ApiModelProperty("csp账号")
    private String cspId;
}
