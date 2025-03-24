package com.citc.nce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MoveGroupReq {

    @NotNull
    @ApiModelProperty(value = "需要批量处理的图片id",example = "1,2,3")
    private List<Long> pictureIds;

    @ApiModelProperty(value = "需要移动到分组的id",example = "1")
    private  Long groupId;

    @ApiModelProperty(value = "是否彻底删除",example = "1")
    private  Long deleted;
}
