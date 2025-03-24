package com.citc.nce.auth.csp.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStatisticInfo {

    @ApiModelProperty(value = "运营商类型： 0：硬核桃，1：联通，2：移动，3：电信", dataType = "Integer")
    private Integer operatorType;
    @ApiModelProperty(value = "用户数量", dataType = "Integer")
    private Long sum;
}
