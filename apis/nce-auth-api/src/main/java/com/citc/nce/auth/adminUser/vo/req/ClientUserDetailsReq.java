package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "管理端 获取client端用户详情")
public class ClientUserDetailsReq {
    /**
     * 客户端用户user_id
     */
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String userId;

}
