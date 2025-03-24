package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetCertificateListReq {
    @ApiModelProperty(value = "平台", dataType = "Integer")
    private List<Integer> plats;
}
