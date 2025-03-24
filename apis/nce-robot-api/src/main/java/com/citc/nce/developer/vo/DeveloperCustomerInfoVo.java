package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperCustomerInfoVo {

    @ApiModelProperty("回调地址")
    private String callbackUrl;

    @ApiModelProperty("接口地址")
    private String receiveUrl;

    @ApiModelProperty("唯一键")
    private String uniqueId;

}
