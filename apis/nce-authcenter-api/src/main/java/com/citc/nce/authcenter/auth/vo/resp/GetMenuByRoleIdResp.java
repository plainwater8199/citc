package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.RoleAndMenuItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetMenuByRoleIdResp {
    @ApiModelProperty(value = "角色菜单列表", dataType = "List")
    private List<RoleAndMenuItem> roleAndMenuItems;
}
