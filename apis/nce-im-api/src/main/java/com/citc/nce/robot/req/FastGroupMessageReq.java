package com.citc.nce.robot.req;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/6/25 14:48
 */
@Data
@ApiModel
public class FastGroupMessageReq {

    @ApiModelProperty("id")
    @NotNull(groups = Update.class)
    private Long id;

    @ApiModelProperty("是否为定时发送：0-否，1-是")
    @NotNull(message = "是否为定时发送不能为空")
    private Integer isTiming;

    @ApiModelProperty("消息类型")
    @NotNull(message = "消息类型不能为空")
    private MsgTypeEnum type;

    @ApiModelProperty("发送账号，多个使用\",\"分割")
    @NotEmpty
    private String accounts;

    @ApiModelProperty("模板id")
    @NotNull
    private Long templateId;

    @ApiModelProperty("联系人组id")
    @NotNull
    private Long groupId;

    @ApiModelProperty("定时发送的时间，非定时则为null")
    @Future
    private LocalDateTime settingTime;

    @ApiModelProperty("支付方式 1：余额支付，2：预购套餐支付（默认为1）")
    private Integer paymentTypeCode;

    public interface Update {
    }
}
