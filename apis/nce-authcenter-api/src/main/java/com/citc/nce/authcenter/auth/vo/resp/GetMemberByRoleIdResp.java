package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.AdminUserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetMemberByRoleIdResp {
    @ApiModelProperty(value = "管理员用户列表", dataType = "List")
    private List<AdminUserInfo> adminUserInfos;
}
