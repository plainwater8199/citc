package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 19:14
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ConversationalInteractionvResp {
    @ApiModelProperty(value = "发送量总数", dataType = "Long")
    private Long sendSumNum;

    @ApiModelProperty(value = "上行量总数", dataType = "Long")
    private Long upsideSumNum;

    @ApiModelProperty(value = "会话量总数", dataType = "Long")
    private Long sessionSumNum;

    @ApiModelProperty(value = "有效会话量总数", dataType = "Long")
    private Long effectiveSessionSumNum;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "展示时间")
    private String showTime;
}
