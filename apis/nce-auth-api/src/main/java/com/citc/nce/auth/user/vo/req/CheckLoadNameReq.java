package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huangchong
 * @Version: 1.0
 * @Description:
 */
@Data
public class CheckLoadNameReq implements Serializable {
    @ApiModelProperty(value = "检查值", dataType = "String")
    private String checkValue;
}
