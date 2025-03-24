package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "管理端 删除client端用户")
public class DeleteClientUserReq {
    /**
     * 客户端用户user_id
     */
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    @NotBlank(message = "客户端用户user_id不能为空")
    private String userId;

}
