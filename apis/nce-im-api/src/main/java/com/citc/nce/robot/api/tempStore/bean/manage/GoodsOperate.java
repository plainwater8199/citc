package com.citc.nce.robot.api.tempStore.bean.manage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:15
 */
@Data
public class GoodsOperate {
    @ApiModelProperty(value = "主键id")
    @NotNull(message = "id不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mssId;

    @Length(max = 100, message = "备注长度超过限制")
    @ApiModelProperty(value = "备注")
    private String remark;
}
