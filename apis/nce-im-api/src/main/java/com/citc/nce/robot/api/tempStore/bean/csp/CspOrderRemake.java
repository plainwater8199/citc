package com.citc.nce.robot.api.tempStore.bean.csp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 9:28
 */
@Data
@ApiModel(value = "csp-order-备注信息")
public class CspOrderRemake {
    @ApiModelProperty("主键id")
    @NotNull(message = "orderId不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty("备注信息")
    @Length(message = "备注信息只能是01-100字符")
    private String remake;
}
