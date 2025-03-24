package com.citc.nce.customcommand.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchListForMSReq {
    @ApiModelProperty("指令uuid")
    private Long id;
}
