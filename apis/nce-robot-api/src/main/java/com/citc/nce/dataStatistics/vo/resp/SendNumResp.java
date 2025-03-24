package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/1 11:23
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SendNumResp {
    @ApiModelProperty(value = "发送总量", dataType = "Long")
    private Long sendSumNum;

    @ApiModelProperty(value = "发送结果饼图数据", dataType = "list")
    private List<SendAnalysisResp> sendResultList;

    @ApiModelProperty(value = "发送渠道饼图数据", dataType = "list")
    private List<SendAnalysisResp> sendChannelList;
}
