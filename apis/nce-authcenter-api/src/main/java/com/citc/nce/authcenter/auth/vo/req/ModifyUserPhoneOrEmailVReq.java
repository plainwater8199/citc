package com.citc.nce.authcenter.auth.vo.req;


import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyUserPhoneOrEmailVReq {
    @NotBlank(message = "验证码信息不能为空")
    @ApiModelProperty(value = "验证码信息", dataType = "Object", required = true)
    private CaptchaCheckReq captchaInfo;

    @NotBlank(message = "新手机号或邮箱不能为空")
    @ApiModelProperty(value = "newAccount", dataType = "String", required = true)
    private String newAccount;

    @NotNull(message = "账号类型不能为空")
    @ApiModelProperty(value = "账号类型(1--EMAIL,3--PHONE)", dataType = "Integer", required = true)
    private Integer accountType;

    @ApiModelProperty(value = "userId", dataType = "String", required = false)
    private String userId;

    @NotBlank(message = "用户身份必传")
    private String myVerifyCaptchaV2;
}
