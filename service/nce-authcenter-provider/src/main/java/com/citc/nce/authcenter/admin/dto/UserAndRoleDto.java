package com.citc.nce.authcenter.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserAndRoleDto {

    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "账户名", dataType = "String")
    private String accountName;

    @ApiModelProperty(value = "用户真实姓名", dataType = "String")
    private String fullName;

    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;

    @ApiModelProperty(value = "用户状态(1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "角色数据", dataType = "List")
    private List<RoleDto> roleList;
}
