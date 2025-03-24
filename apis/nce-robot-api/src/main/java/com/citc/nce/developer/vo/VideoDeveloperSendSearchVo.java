package com.citc.nce.developer.vo;

import com.citc.nce.developer.enums.SendResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class VideoDeveloperSendSearchVo {
    @ApiModelProperty("客户账号Id")
    private String accountId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("回调结果0:成功，1:失败，2:无回调")
    private Integer callbackResult;

    @ApiModelProperty("发送结果")
    private SendResult sendResult;

    @ApiModelProperty("调用开始时间")
    @NotNull(message = "调用开始时间不能为空")
    private String callTimeStart;

    @ApiModelProperty("调用结束时间")
    @NotNull(message = "调用结束时间不能为空")
    private String callTimeEnd;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
