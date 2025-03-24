package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteReq {

    @ApiModelProperty(value = "需要删除的主键id集合",example = "5")
    List<Long> ids;
}
