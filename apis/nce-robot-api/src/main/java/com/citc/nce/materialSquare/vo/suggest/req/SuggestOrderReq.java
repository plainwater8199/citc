package com.citc.nce.materialSquare.vo.suggest.req;

import com.citc.nce.materialSquare.vo.suggest.SuggestChangeNum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SuggestOrderReq {

    @NotNull
    @ApiModelProperty("排序方式：0-正序，1-乱序")
    private Integer orderType;

    @ApiModelProperty("作品列表")
    private List<SuggestChangeNum> changeNums;

}
