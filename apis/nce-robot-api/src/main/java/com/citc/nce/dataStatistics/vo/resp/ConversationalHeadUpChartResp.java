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
public class ConversationalHeadUpChartResp {
    @ApiModelProperty(value = "联通总数", dataType = "Long")
    private Long unicomSumNum;

    @ApiModelProperty(value = "上行量总数", dataType = "Long")
    private Long upsideSumNum;

    @ApiModelProperty(value = "移动总数", dataType = "Long")
    private Long moveSumNum;

    @ApiModelProperty(value = "电信总数", dataType = "Long")
    private Long telecomSumNum;

    @ApiModelProperty(value = "硬核桃总数", dataType = "Long")
    private Long walnutSumNum;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "展示时间")
    private String showTime;

}
