package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CertificateItem {
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

}
