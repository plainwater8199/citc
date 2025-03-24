package com.citc.nce.robot.api.tempStore.bean.csp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author bydud
 * @since 2023-11-17 11:29:51
 */
@Data
@Accessors(chain = true)
public class EditName {

    @ApiModelProperty(value = "Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "原 变量名称/指令名称")
    private String oldName;

    @ApiModelProperty(value = "新 变量名称/指令名称")
    private String newName;

    @ApiModelProperty(value = "是否是系统变量", dataType = "boolean")
    private String type;

    public boolean isSystemVariable() {
        return "sys".equalsIgnoreCase(type);
    }
}
