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
public class SessionUsersResp {

    @ApiModelProperty(value = "新增用户总数")
    private Long newUsersSumNum;

    @ApiModelProperty(value = "活跃用户总数")
    private Long activeUsersSumNum;

    @ApiModelProperty(value = "饼状图数据", dataType = "list")
    private List<?> list;
}
