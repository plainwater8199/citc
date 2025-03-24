package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class GetClientUserIdentificationReq {
    /**
     * 客户端用户user_id
     */
    @NotBlank(message = "客户端用户userId不能为空")
    @ApiModelProperty(value = "客户端用户user_id", dataType = "String", required = true)
    private String userId;
}
