package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.ClickButtonInfoItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuerySingleMassCountResp {

    @ApiModelProperty(value = "群发数量")
    private Long massSendAmount;

    @ApiModelProperty(value = "发送成功")
    private Long successAmount;

    @ApiModelProperty(value = "发送失败")
    private Long failAmount;

    @ApiModelProperty(value = "未知")
    private Long unknownAmount;

    @ApiModelProperty(value = "已阅")
    private Long readAmount;

    @ApiModelProperty(value = "点击按钮")
    private Long clickButtonAmount;

    @ApiModelProperty(value = "活跃用户数日环比")
    private List<ClickButtonInfoItem> clickButtonInfoItems;


}
