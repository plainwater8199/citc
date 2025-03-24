package com.citc.nce.auth.adminUser.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 11:32
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class RoleResp {
    @ApiModelProperty(value = "角色id")
    private String roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "角色状态(0启用,1禁用)")
    private Integer roleStatus;
}
