package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.ActiveAccountItem;
import com.citc.nce.dataStatistics.vo.msg.ActiveStatisticItem;
import com.citc.nce.dataStatistics.vo.msg.MsgSendAmountItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class YesterdayOverviewResp {
    @ApiModelProperty(value = "活跃用户数")
    private ActiveStatisticItem activeUserItem;
    @ApiModelProperty(value = "活跃账号")
    private ActiveStatisticItem activeAccountItem;
    @ApiModelProperty(value = "活跃消息发送量")
    private MsgSendAmountItem msgSendAmountItem;
}
