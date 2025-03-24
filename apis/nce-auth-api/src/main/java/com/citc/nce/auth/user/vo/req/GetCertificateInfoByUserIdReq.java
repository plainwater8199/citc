package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description
 * @date 2022/8/30 01:50:56
 */
@Data
public class GetCertificateInfoByUserIdReq {
    @ApiModelProperty(value = "用户ID", dataType = "String", required = true)
    private String userId;
    @ApiModelProperty(value = "资历id", dataType = "Integer")
    private Integer certificateId;
}
