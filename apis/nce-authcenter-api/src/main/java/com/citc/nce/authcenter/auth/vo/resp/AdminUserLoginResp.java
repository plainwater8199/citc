package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.MenuSortInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class AdminUserLoginResp {
    @ApiModelProperty(value = "用户ID", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "用户名", dataType = "String")
    private String userName;

    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;

    @ApiModelProperty(value = "用户平台类型： 1 核能商城客户端，2 chatbot客户端，3 硬核桃社区，4 管理平台", dataType = "Integer")
    private Integer platformType;
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
    @ApiModelProperty(value = "菜单顺序", dataType = "List")
    private List<MenuSortInfo> menuSortInfos;
}
