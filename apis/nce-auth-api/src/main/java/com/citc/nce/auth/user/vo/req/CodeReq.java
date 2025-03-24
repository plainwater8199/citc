package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/8 16:31
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CodeReq {
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;
}
