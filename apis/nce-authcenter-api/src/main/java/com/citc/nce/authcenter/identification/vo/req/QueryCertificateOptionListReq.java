package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryCertificateOptionListReq {
    @ApiModelProperty(value = "是否查询所有 1-是，0-否", dataType = "Integer")
    private Integer isAll;

}
