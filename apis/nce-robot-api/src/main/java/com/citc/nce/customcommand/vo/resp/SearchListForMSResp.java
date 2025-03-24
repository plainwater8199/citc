package com.citc.nce.customcommand.vo.resp;

import com.citc.nce.customcommand.vo.CustomCommandItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchListForMSResp {

    @ApiModelProperty("指令列表")
    private List<CustomCommandItem> customCommandItems;
}
