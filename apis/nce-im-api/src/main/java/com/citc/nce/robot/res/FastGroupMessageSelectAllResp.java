package com.citc.nce.robot.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class FastGroupMessageSelectAllResp {

    @ApiModelProperty("快捷群发信息")
    private Map<Long,String> fastGroupMessageInfo;
}
