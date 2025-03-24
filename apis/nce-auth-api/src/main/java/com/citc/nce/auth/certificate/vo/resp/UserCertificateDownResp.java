package com.citc.nce.auth.certificate.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 为下拉框提供的resp
 */
@Data
@Accessors(chain = true)
public class UserCertificateDownResp {

    /**
     * 资质id
     */
    @ApiModelProperty(value = "资质id", dataType = "Long")
    private Long id;

    /**
     * 资质名称 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "资质名称", dataType = "String")
    private String certificateName;

    public UserCertificateDownResp() {
    }

}
