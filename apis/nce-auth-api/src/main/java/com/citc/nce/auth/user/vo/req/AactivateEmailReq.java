package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户邮箱激活req
 *
 * @authoer:huangchong
 * @createDate:2022/7/14 16:46
 * @description:
 */
@Data
@Accessors(chain = true)
public class AactivateEmailReq {
    /**
     * 邮箱
     */
    @NotBlank(message = "mail不能为空")
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    private String mail;
    /**
     * 用户id
     */
    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;
}
