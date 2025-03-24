package com.citc.nce.robot.api.tempStore.bean.images;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author bydud
 * @since 10:22
 */
@Data
@ApiModel(value = "csp-模板商城-资源管理-imgGroup管理-新增")
public class GroupAdd {

    @ApiModelProperty("分组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;

    @ApiModelProperty("分组名称")
    @NotBlank(message = "名称不能为空")
    private String name;

}
