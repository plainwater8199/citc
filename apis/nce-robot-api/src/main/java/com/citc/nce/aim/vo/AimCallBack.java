package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/14 17:22
 */
@Data
public class AimCallBack {
    @ApiModelProperty(value = "订单Id", dataType = "long")
    private long orderId;

    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "被叫号码", dataType = "String")
    private String called;

    @ApiModelProperty(value = "客户号码", dataType = "String")
    private String calling;

    @ApiModelProperty(value = "发送状态 1:成功 2:失败", dataType = "int")
    private int status;

    @ApiModelProperty(value = "详细描述", dataType = "String")
    private String msg;

    @ApiModelProperty(value = "事件时间戳", dataType = "String", example = "YYYY-MM-DD hh:mm:ss")
    private String timestamp;

    @ApiModelProperty(value = "短信时间戳", dataType = "String", example = "YYYY-MM-DD hh:mm:ss")
    private String sendTime;

    @ApiModelProperty(value = "唯一标识", dataType = "String")
    private String recordId;
}
