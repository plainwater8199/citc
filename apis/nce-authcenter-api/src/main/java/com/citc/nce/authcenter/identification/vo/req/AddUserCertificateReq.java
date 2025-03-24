package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class AddUserCertificateReq {

    @ApiModelProperty(value = "client端用户id")
    @NotBlank(message = "客户端用户user_id不能为空")
    private String clientUserId;


    @ApiModelProperty(value = "资质code：10003-入驻用户，10007-GSMA会员，10008-5G消息工作组会员")
    @NotNull(message = "资质code不能为空")
    private Integer certificateOptionsCode;
}
