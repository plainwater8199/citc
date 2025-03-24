package com.citc.nce.materialSquare.vo.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/5/15 9:32
 */
@Data
public class MssForLiReq {
    @ApiModelProperty("活动方案id")
    @NotNull
    private Long msActivityContentId;

    @ApiModelProperty("排序方式，1：创建时间升序，2：创建时间降序，3：浏览量降序，4：点赞量降序")
    private Integer sortType;
}
