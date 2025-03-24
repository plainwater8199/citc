package com.citc.nce.authcenter.largeModel.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LargeModelReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "页数", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "每页条数", dataType = "Integer", required = true)
    private Integer pageSize;
}
