package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientUserDetailsReq {
    /**
     * 客户端用户user_id
     */
    @NotNull(message = "user_id不能为空")
    @ApiModelProperty(value = "客户端用户id", required = true)
    private String userId;
}
