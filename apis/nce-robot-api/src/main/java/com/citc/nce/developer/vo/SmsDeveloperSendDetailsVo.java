package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsDeveloperSendDetailsVo {

    @ApiModelProperty("客户登录账号")
    private Long customerUserId;

    @ApiModelProperty("企业Id")
    private Long enterpriseId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("回调地址")
    private String callbackUrl;

    @ApiModelProperty("公钥")
    private String appId;

    @ApiModelProperty("秘钥")
    private String appSecret;

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;
}
