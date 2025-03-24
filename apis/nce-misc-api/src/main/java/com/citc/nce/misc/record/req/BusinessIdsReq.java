package com.citc.nce.misc.record.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BusinessIdsReq {
    @NotNull
    @ApiModelProperty(value = "业务主键id", required = true)
    private List<String> businessIds;

    @NotNull
    @ApiModelProperty(value = "业务类型", required = true)
    private Integer businessType;
    @ApiModelProperty(value = "业务类型集合")
    private List<Integer> businessTypeList;
}
