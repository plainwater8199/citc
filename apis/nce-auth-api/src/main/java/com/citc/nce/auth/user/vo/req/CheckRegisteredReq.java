package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:07
 * @Version: 1.0
 * @Description:
 */
@Data
public class CheckRegisteredReq implements Serializable {
    @ApiModelProperty(value = "检查值", dataType = "String")
    private String checkValue;
    @ApiModelProperty(value = "检查值类型", dataType = "Integer")
    private Integer checkType;
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;
}
