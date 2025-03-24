package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class QueryCertificateOptionsByIdReq {
    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;

    @NotBlank(message = "资历id不能为空")
    @ApiModelProperty(value = "资历id", dataType = "String", required = true)
    private Integer certificateId;
}
