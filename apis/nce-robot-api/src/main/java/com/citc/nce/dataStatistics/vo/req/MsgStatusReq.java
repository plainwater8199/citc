package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@ApiModel("MsgStatusReq")
@Data
public class MsgStatusReq extends StartAndEndTimeReq {
    @ApiModelProperty("查询消息类型 1-5G消息、2-视频短信消息、3-短信消息")
    @NotNull
    private String accountType;

    @ApiModelProperty("消息来源 1 群发 2 机器人")
    private String messageResource;

    @ApiModelProperty("按账号筛选")
    private String accountId;

}
