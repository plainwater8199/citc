package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Data
@ApiModel
@Accessors(chain = true)
public class MessagePlanDetailDto {
    @ApiModelProperty("消息类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0:文本消息,1:富媒体消息,2:会话消息}")
    private MsgSubTypeEnum msgSubType;

    @ApiModelProperty("额度")
    private Long limit;
}
