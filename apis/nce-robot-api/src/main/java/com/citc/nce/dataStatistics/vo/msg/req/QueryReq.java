package com.citc.nce.dataStatistics.vo.msg.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel
public class QueryReq {
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
    @ApiModelProperty(value = "渠道编码", dataType = "String")
    private String accountDictCode;
    @ApiModelProperty(value = "运营商编码,移动=2、联通=1、电信=3", dataType = "String")
    private String operatorCode;
    @ApiModelProperty(value = "账号ID", dataType = "String")
    private String accountId;
    @ApiModelProperty("查询消息类型 1-5G消息、2-视频短信消息、3-短信消息")
    @NotNull
    private Integer accountType;
    @ApiModelProperty("来源 1 群发 2 机器人 3测试发送")
    private List<Integer> messageResource;
    @ApiModelProperty(value = "计划Id",example = "123")
    private Long planId;

}
