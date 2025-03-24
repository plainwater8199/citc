package com.citc.nce.authcenter.systemmsg.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddSysMsgManageResp {
    @ApiModelProperty(value = "结果", dataType = "Boolean")
    private Boolean result;
}
