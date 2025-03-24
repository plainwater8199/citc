package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RobotRecordListResp {
    /**
     * 5G账号名称
     */
    @ApiModelProperty("5G账号名称")
    private Long accountName;
    /**
     * 通道名称
     */
    @ApiModelProperty("通道名称")
    private Long channelType;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private Long mobileNum;

    /**
     * 会话id
     */
    @ApiModelProperty("会话id")
    private String conversationId;
}
