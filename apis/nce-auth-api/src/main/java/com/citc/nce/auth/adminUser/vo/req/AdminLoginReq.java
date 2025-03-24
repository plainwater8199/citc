package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/28 17:08
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AdminLoginReq extends DyzOnlyIdentificationReq {

    @NotBlank(message = "phone不能为空")
    @ApiModelProperty(value = "phone", dataType = "String", required = true)
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "多因子验证码", dataType = "String", required = true)
    private String code;

    @NotBlank(message = "验证码关联key不能为空")
    @ApiModelProperty(value = "验证码关联key", dataType = "String", required = true)
    private String captchaKey;
}
