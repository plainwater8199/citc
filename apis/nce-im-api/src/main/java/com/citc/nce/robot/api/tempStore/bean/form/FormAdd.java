package com.citc.nce.robot.api.tempStore.bean.form;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author bydud
 * @since 10:22
 */
@Data
@ApiModel(value = "资源管理-form-新增/修改")
public class FormAdd {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @NotBlank(message = "表单封面不能为空")
    private String formCover;
    @NotBlank(message = "表单不能为空")
    private String formDetails;
    @NotBlank(message = "表单名称不能为空")
    private String formName;
}
