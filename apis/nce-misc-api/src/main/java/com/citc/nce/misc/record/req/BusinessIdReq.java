package com.citc.nce.misc.record.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BusinessIdReq {
    @NotNull
    @ApiModelProperty(value = "业务主键id", required = true)
    private String businessId;

    @ApiModelProperty(value = "业务类型")
    private Integer businessType;

    @ApiModelProperty(value = "业务类型列表")
    private List<Integer> businessTypeList;
}
