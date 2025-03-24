package com.citc.nce.robot.api.mall.snapshot.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MallRobotOrderSaveOrUpdateResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "快照uuid", dataType = "String")
    private String snapshotUuid;
}
