package com.citc.nce.materialSquare.vo.suggest.resp;

import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SuggestListResp {

    @ApiModelProperty("排序方式：0-正序，1-乱序")
    private Integer orderType;

    @ApiModelProperty("作品列表")
    private List<SuggestListOrderNum> changeNums;

}
