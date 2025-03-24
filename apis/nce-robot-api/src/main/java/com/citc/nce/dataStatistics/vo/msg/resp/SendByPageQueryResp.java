package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.dataStatistics.vo.msg.SendByPageQueryItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SendByPageQueryResp {
    @ApiModelProperty(value = "分页查询详情")
    private PageResult<SendByPageQueryItem> sendByPageQueryItems;
}
