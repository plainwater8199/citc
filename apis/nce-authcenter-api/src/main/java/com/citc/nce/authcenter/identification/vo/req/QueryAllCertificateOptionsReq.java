package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryAllCertificateOptionsReq {
    @ApiModelProperty(value = "是否有效，1有效，0无效", dataType = "Integer")
    private Integer isActive;
}
