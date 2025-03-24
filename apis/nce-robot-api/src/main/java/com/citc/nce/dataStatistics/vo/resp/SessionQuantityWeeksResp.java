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
@ApiModel
@Accessors(chain = true)
public class SessionQuantityWeeksResp {

    @ApiModelProperty(value = "表主键", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "会话量")
    private Long sessionNum;

    @ApiModelProperty(value = "有效会话量")
    private Long effectiveSessionNum;

    @ApiModelProperty(value = "新增用户数")
    private Long newUsersNum;

    @ApiModelProperty(value = "活跃用户数")
    private Long activeUsersNum;

    @ApiModelProperty(value = "时间(每周)")
    private String weeks;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
