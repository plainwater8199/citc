package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    @ApiModelProperty(value = "发送失败手机号", dataType = "Object")
    private List<String> failMobiles;
    @ApiModelProperty(value = "发送成功手机号", dataType = "Object")
    private List<String> successMobiles;
}
