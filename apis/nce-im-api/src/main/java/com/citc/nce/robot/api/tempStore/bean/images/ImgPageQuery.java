package com.citc.nce.robot.api.tempStore.bean.images;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:22
 */
@Data
@ApiModel(value = "csp-模板商城-资源管理-img管理-分页参数")
public class ImgPageQuery {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;
    private String name;
    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
