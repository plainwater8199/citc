package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterResp {
    @ApiModelProperty(value = "主键id", dataType = "Long")
    private Long id;
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;
    @ApiModelProperty(value = "用户名", dataType = "String")
    private String name;
    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;
    @ApiModelProperty(value = "电子邮箱", dataType = "String")
    private String mail;

}
