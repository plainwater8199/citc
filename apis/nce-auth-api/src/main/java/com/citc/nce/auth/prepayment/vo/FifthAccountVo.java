package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Data
@ApiModel
@Accessors(chain = true)
public class FifthAccountVo {

    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("appKey")
    private String appKey;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("chatbotAccount")
    private String chatbotAccount;

    @ApiModelProperty("chatbotAccountId")
    private String chatbotAccountId;


}
