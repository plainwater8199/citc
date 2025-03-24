package com.citc.nce.robot.api.tempStore.bean.images;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bydud
 * @since 14:18
 */
@Data
public class GroupInfo {

    @ApiModelProperty("分组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;

    @ApiModelProperty("分组名")
    private String name;

    @ApiModelProperty("图片数量")
    private Integer count = 0;
}
