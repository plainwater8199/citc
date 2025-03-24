package com.citc.nce.auth.ticket.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class GetCertificateOptionsReq {

    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank
    private String userId;
}
