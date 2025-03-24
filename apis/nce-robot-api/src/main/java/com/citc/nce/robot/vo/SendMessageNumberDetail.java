package com.citc.nce.robot.vo;

import com.citc.nce.robot.vo.messagesend.SimpleMessageSendNumberDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2024/3/8 14:29
 */
@Data
@ApiModel("发送消息数量详情")
public class SendMessageNumberDetail {
    @ApiModelProperty("视频短信使用条数")
    private List<SimpleMessageSendNumberDetail> mediaUsage;

    @ApiModelProperty("短信短信使用条数")
    private List<SimpleMessageSendNumberDetail> smsUsage;

    @ApiModelProperty("5G消息-文本使用详情")
    private List<SimpleMessageSendNumberDetail> _5gTextUsage;

    @ApiModelProperty("5G消息-富媒体使用详情")
    private List<SimpleMessageSendNumberDetail> _5gRichUsage;

    @ApiModelProperty("5G消息-会话使用详情")
    private List<SimpleMessageSendNumberDetail> _5gConversationUsage;

    @ApiModelProperty("5G消息-回落使用详情")
    private List<SimpleMessageSendNumberDetail> _5gFallbackUsage;

}
