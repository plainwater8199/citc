package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/1 11:34
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SendAnalysisResp {
    @ApiModelProperty(value = "发送量", dataType = "Long")
    private Long sendNum;
    @ApiModelProperty(value = "发送结果", dataType = "Integer")
    private Integer sendResult;
    @ApiModelProperty(value = "发送渠道", dataType = "Integer")
    private Integer sendChannel;
}
