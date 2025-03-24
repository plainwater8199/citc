package com.citc.nce.dataStatistics.vo.msg.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class QueryMsgSendTotalRep {
    @ApiModelProperty("查询消息类型 1-5G消息、2-视频短信消息、3-短信消息")
    @NotNull
    private Integer accountType;

    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;

    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;

    @ApiModelProperty("来源 1 群发 2 机器人 3测试发送 51订阅  52打卡")
    private List<Integer> messageResource;

    @ApiModelProperty("是否查询硬核桃，默认为0不查询，1表示查询")
    private Integer isQueryYHT;


}
