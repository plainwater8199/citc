package com.citc.nce.auth.adminUser.vo.resp;

import com.citc.nce.common.core.pojo.BaseUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @authoer:ldy
 * @createDate:2022/7/10 0:54
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "管理端用户登录resp")
public class AdminUserLoginResp extends BaseUser {
    @ApiModelProperty(value = "账户名", dataType = "String")
    private String accountName;
    @ApiModelProperty(value = "用户真实姓名", dataType = "String")
    private String fullName;
    @ApiModelProperty(value = "用户状态(1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;
    @ApiModelProperty(value = "用户token", dataType = "String")
    private String token;
    @ApiModelProperty(value = "用户角色code", dataType = "String")
    private List<String> roleList;
    @ApiModelProperty(value = "菜单资源url", dataType = "String")
    private Map<String, List<String>> url;
}
