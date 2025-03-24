package com.citc.nce.developer.vo;

import com.citc.nce.developer.enums.SendResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class SmsDeveloperSendSearchVo {
    @ApiModelProperty("客户账号Id")
    private String accountId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("回调结果0:成功，1:失败，2:其它")
    private Integer callbackResult;

    @ApiModelProperty("发送结果:0-全部，1-调用失败，2-发送失败，3-发送中（未知），4-发送成功")
    private SendResult sendResult;

    @ApiModelProperty("调用开始时间")
    private String callTimeStart;

    @ApiModelProperty("调用结束时间")
    private String callTimeEnd;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
