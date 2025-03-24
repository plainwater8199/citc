package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Accessors(chain = true)
public class RoleConfigurationMemberReq {
    @ApiModelProperty(value = "用户id", dataType = "String", required = false)
    private List<String> userIdList;

    @NotBlank(message = "角色id不能为空")
    @ApiModelProperty(value = "角色id", dataType = "String", required = true)
    private String roleId;
}
