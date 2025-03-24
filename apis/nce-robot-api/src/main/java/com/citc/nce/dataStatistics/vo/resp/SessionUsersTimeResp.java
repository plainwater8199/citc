package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 17:41
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class SessionUsersTimeResp {

    @ApiModelProperty(value = "账户类型")
    private Integer accountType;

    @ApiModelProperty(value = "新增用户数")
    private Long newUsersNum;

    @ApiModelProperty(value = "新增用户占比")
    private String newUsersPercentage;

    @ApiModelProperty(value = "活跃用户数")
    private Long activeUsersNum;

    @ApiModelProperty(value = "活跃用户占比")
    private String activeUsersPercentage;

    @ApiModelProperty(value = "用户id")
    private String userId;
}
