package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.MsgSendByChannelItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class QueryMsgSendTotalResp {
    @ApiModelProperty("群发总数")
    private Long totalSendNum;

    @ApiModelProperty("发送成功总数")
    private Long totalSuccessSendNum;

    @ApiModelProperty("消息类型")
    private String accountType;

    @ApiModelProperty("每个渠道统计")
    private List<MsgSendByChannelItem> msgSendByChannelItem;
}
