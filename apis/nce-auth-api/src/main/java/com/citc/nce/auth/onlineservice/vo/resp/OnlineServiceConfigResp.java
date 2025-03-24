package com.citc.nce.auth.onlineservice.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.vo.resp
 * @Author: litao
 * @CreateTime: 2023-01-04  14:57
 
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class OnlineServiceConfigResp {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "是否已启用（0：未启用 1：已启用）")
    private Integer isEnabled;

    @ApiModelProperty(value = "客服状态（0：离线 1：上线）")
    private Integer status;

    @ApiModelProperty(value = "自动回复内容")
    private String autoReplyContent;
}
