package com.citc.nce.module.vo.resp;

import com.citc.nce.module.vo.SubscribeInfoItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetSubscribeInfoByUserResp {
    @ApiModelProperty("用户订阅发送列表")
    private List<SubscribeInfoItem> subscribeInfoItemList;
}
