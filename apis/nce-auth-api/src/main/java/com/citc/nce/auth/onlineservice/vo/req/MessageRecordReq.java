package com.citc.nce.auth.onlineservice.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.vo.req
 * @Author: litao
 * @CreateTime: 2023-01-06  10:07
 
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class MessageRecordReq {
    @ApiModelProperty(value = "用户id")
    private String userId;
}
