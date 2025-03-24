package com.citc.nce.authcenter.identification.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "管理端用户管理 用户标签统计")
public class DashboardUserStatisticsResp {
    //待处理的用户认证个数
    @ApiModelProperty(value = "待处理的用户认证个数")
    private Long unauthCount;
    //待处理的用户标签申请个数
    @ApiModelProperty(value = "待处理的用户标签申请个数")
    private Long unAuditCount;
}
