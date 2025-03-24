package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Admin端 查看client端 userId 用户认证信息
 */
@Data
@Accessors(chain = true)
public class ClientUserIdentificationReq {
    /**
     * 客户端用户user_id
     */
    @NotBlank(message = "客户端用户userId不能为空")
    @ApiModelProperty(value = "客户端用户user_id", dataType = "String", required = true)
    private String userId;

}
