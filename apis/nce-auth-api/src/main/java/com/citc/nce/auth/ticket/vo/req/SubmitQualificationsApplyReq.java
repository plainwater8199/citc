package com.citc.nce.auth.ticket.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Data
public class SubmitQualificationsApplyReq implements Serializable {

    @NotNull(message = "资质名称不能为空")
    @ApiModelProperty(value = "资质名称", dataType = "Integer", required = true)
    private Integer qualificationName;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", dataType = "String", required = true)
    private String userName;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;

    @Email
    @ApiModelProperty(value = "联系邮箱", dataType = "String", required = true)
    private String email;

}
