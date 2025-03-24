package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.AdminRoleItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetUserRoleListResp {
    @ApiModelProperty(value = "管理员角色列表")
    private List<AdminRoleItem> adminRoleItems;
}
