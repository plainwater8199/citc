package com.citc.nce.auth.ticket.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:分页查询资质申请工单
 */
@Data
public class GetQualificationsApplyReq extends GetInfoByKeyWordReq implements Serializable {
    @ApiModelProperty(value = "处理状态", dataType = "Integer", required = false)
    private Integer processingState;

    @NotNull(message = "当前页不能为空")
    @ApiModelProperty(value = "当前页", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "每页数量不能为空")
    @ApiModelProperty(value = "每页数量", dataType = "Integer", required = true)
    private Integer pageSize;
}
