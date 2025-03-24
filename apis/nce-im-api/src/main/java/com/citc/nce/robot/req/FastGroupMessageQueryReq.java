package com.citc.nce.robot.req;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.robot.enums.FastGroupMessageStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel
public class FastGroupMessageQueryReq {
    @ApiModelProperty("消息类型: 0-全部，1-5G，2-视频短信，3-短信")
    @NotNull
    private MsgTypeEnum type;

    @ApiModelProperty("发送账号")
    private String account;

    @NotNull
    @ApiModelProperty("发送状态: 0-全部,1-发送中,2-等待发送,3-发送失败,4-发送完毕")
    private FastGroupMessageStatus status;

    @ApiModelProperty("发送开始时间")
    private String sendStartTime;

    @ApiModelProperty("发送结束时间")
    private String sendEndTime;

    @ApiModelProperty("创建开始时间")
    private String createStartTime;

    @ApiModelProperty("创建结束时间")
    private String createEndTime;

    @NotNull(message = "分页大小不能为空")
    private Long pageSize;

    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
