package com.citc.nce.auth.identification.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserProvinceResp {
    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "数量")
    private Integer quantity;
}
