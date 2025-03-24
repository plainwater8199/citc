package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.SendStatusAnalysisItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendStatusAnalysisResp {
    @ApiModelProperty(value = "发送状态分析--5G消息")
    private SendStatusAnalysisItem sendStatusFor5G;
    @ApiModelProperty(value = "发送状态分析--视频短信")
    private SendStatusAnalysisItem sendStatusForMedia;
    @ApiModelProperty(value = "发送状态分析--短信")
    private SendStatusAnalysisItem sendStatusForShort;
}
