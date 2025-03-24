package com.citc.nce.robot.req;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/5/7 10:55
 */
@Data
@ApiModel
public class RobotGroupPlanShortcutAddReq {
    @ApiModelProperty("消息类型")
    @NotNull(message = "消息类型不能为空")
    private MsgTypeEnum type;

    @ApiModelProperty("发送账号，多个账号使用逗号隔开")
    @NotNull(message = "发送账号不能为空")
    private String account;

    @ApiModelProperty("模板id")
    @NotNull(message = "模板id不能为空")
    private Long templateId;

    @ApiModelProperty("联系人组ID")
    @NotNull(message = "联系人组id不能为空")
    private Long groupId;

    @ApiModelProperty("发送时间")
    @Future(message = "发送时间必须大于当前时间")
    private LocalDateTime sendTime;
}
