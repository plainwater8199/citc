package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.MsgStatusItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class SendTrendsByStatusResp {
    @ApiModelProperty(value = "分页查询详情")
    private List<MsgStatusItem> msgStatusItems;
}
