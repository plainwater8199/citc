package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 17:41
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SessionQuantityResp {
    @ApiModelProperty(value = "会话量总数", dataType = "Long")
    private Long sessionSumNum;

    @ApiModelProperty(value = "有效会话量总数", dataType = "Long")
    private Long effectiveSessionSumNum;

    @ApiModelProperty(value = "新增用户总数", dataType = "Long")
    private Long newUsersSumNum;

    @ApiModelProperty(value = "活跃用户总数", dataType = "Long")
    private Long activeUsersSumNum;

    @ApiModelProperty(value = "折现图数据", dataType = "list")
    private List<?> list;

    @ApiModelProperty(value = "用户分布", dataType = "list")
    private List<?> userList;
}
