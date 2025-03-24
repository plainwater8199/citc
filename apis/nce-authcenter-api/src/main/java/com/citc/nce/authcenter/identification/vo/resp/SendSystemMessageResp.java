package com.citc.nce.authcenter.identification.vo.resp;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendSystemMessageResp {
    @ApiModelProperty(value = "发送结果", dataType = "Boolean")
    private Boolean result;
}
