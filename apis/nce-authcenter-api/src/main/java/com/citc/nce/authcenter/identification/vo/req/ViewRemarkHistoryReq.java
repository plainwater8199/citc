package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ViewRemarkHistoryReq {
    @ApiModelProperty(value = "客户端用户user_id", dataType = "String")
    private String clientUserId;
    @ApiModelProperty(value = "资质id", dataType = "Integer")
    private Integer identificationId;
}
