package com.citc.nce.auth.csp.statistics.vo.resp;

import com.citc.nce.auth.csp.statistics.vo.UserStatisticInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class QueryUserStatisticByOperatorResp {

    @ApiModelProperty(value = "新增用户统计", dataType = "List")
    private List<UserStatisticInfo> newUserStatisticInfoList;
    @ApiModelProperty(value = "活跃用户统计", dataType = "List")
    private List<UserStatisticInfo> activeUserStatisticInfoList;
}
