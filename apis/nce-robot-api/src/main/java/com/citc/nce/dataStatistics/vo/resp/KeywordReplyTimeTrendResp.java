package com.citc.nce.dataStatistics.vo.resp;

import com.citc.nce.dataStatistics.vo.KeywordReplyTimeItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel
public class KeywordReplyTimeTrendResp {
    @ApiModelProperty(value = "关键字列表", dataType = "List")
    private List<KeywordReplyTimeItem> timeItems;

}
