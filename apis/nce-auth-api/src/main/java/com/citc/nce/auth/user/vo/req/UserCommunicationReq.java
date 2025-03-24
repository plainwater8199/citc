package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/29 10:07
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserCommunicationReq {

    @ApiModelProperty(value = "手机或邮箱", dataType = "String", required = true)
    @NotBlank(message = "参数communication不能为空")
    private String communication;

    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank(message = "用户id不能为空")
    private String userId;
}