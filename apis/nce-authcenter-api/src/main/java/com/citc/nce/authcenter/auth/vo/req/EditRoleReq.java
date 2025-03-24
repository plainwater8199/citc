package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class EditRoleReq {
    @ApiModelProperty(value = "角色id", dataType = "String", required = false)
    private String roleId;

    @NotBlank(message = "角色名称不能为空")
    @Length(max = 25, message = "角色名称长度超过限制")
    @ApiModelProperty(value = "角色名称", dataType = "String", required = true)
    private String roleName;

    @Length(max = 50, message = "备注长度超过限制")
    @ApiModelProperty(value = "备注", dataType = "String", required = false)
    private String remark;
}
