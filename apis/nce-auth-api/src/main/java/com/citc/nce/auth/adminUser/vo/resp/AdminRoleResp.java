package com.citc.nce.auth.adminUser.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/22 15:50
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AdminRoleResp {

    @ApiModelProperty(value = "表主键")
    private Long id;

    @ApiModelProperty(value = "角色id")
    private String roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "角色状态(1启用,2禁用)")
    private Integer roleStatus;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
