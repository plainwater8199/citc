package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeleteClientUserReq {
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    @NotBlank(message = "客户端用户user_id不能为空")
    private String userId;
}
