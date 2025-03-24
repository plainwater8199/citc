package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class GetReviewLogListReq {
    @NotBlank(message = "uuid不能为空")
    @ApiModelProperty(value = "uuid", dataType = "String", required = true)
    private String uuid;
}
