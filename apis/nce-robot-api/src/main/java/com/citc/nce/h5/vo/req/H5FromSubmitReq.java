package com.citc.nce.h5.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class H5FromSubmitReq {

    @NotBlank(message = "h5Id不能为空")
    @ApiModelProperty(value = "h5Id")
    private Long h5Id;

    @ApiModelProperty(value = "表单内容")
    private String content;

}
