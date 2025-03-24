package com.citc.nce.auth.adminUser.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/22 19:57
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserAndRoleResp extends AdminUserResp {
    @ApiModelProperty(value = "角色数据", dataType = "List")
    private List<RoleResp> roleList;
}
