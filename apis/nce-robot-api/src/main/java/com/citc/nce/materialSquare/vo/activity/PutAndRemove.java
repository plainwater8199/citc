package com.citc.nce.materialSquare.vo.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bydud
 * @since 2024/6/24 9:50
 */
@Data
public class PutAndRemove {
    @ApiModelProperty("活动方案id")
    @NotNull(message = "活动方案id不能为空")
    private Long msActivityContentId;
    @ApiModelProperty("关联作品的id")
    private List<Long> addMssIds;
    @ApiModelProperty("删除活动列表")
    private List<Long> removeMssIds;
}
