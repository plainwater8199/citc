package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/24 18:06
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserIdReq {

    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;
}
