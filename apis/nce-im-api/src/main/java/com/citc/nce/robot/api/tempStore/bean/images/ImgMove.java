package com.citc.nce.robot.api.tempStore.bean.images;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bydud
 * @since 14:46
 */
@Data
@ApiModel(value = "csp-模板商城-资源管理-img管理-移动")
public class ImgMove {
    @NotEmpty(message = "图片不同为空")
    private List<Long> imgIds;
    @NotNull(message = "分组不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;
}
