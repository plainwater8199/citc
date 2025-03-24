package com.citc.nce.auth.onlineservice.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.vo.resp
 * @Author: litao
 * @CreateTime: 2023-01-06  10:07
 
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class MessageRecordResp {
    @ApiModelProperty(value = "unReadCount")
    private Integer unReadCount;
}
