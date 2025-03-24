package com.citc.nce.authcenter.auth.vo.req;

import com.citc.nce.common.web.validation.Mobile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SaveForRegisterReq {
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", dataType = "String", required = true)
    private String name;

    @Mobile(message = "手机号格式不正确")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private String phone;

    @Email(message = "邮箱格式错误")
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    private String mail;
}
