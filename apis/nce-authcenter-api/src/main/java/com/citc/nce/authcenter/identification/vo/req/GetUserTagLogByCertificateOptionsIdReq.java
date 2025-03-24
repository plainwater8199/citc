package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GetUserTagLogByCertificateOptionsIdReq {
    @ApiModelProperty(value = "用户账号资质信息表id")
    private Long certificateOptionsId;
}
