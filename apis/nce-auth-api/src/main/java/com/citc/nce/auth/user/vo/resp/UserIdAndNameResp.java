package com.citc.nce.auth.user.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/24 18:08
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserIdAndNameResp {

    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "用户名称", dataType = "String")
    private String name;

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;
}
