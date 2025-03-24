package com.citc.nce.aim.vo.resp;

import com.citc.nce.aim.vo.ResponseInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AimProjectTestResp {
    @ApiModelProperty(value = "执行结果", dataType = "String")
    private String result;
    @ApiModelProperty(value = "响应信息", dataType = "Object")
    private ResponseInfo body;
}
