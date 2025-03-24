package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class GetOperatorListReq {
    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "pageNo", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "模糊关键词", dataType = "String", required = false)
    private String keyWord;

    @ApiModelProperty(value = "角色id", dataType = "List", required = false)
    private List<String> roleIdList;

    @ApiModelProperty(value = "状态", dataType = "Integer", required = false)
    private Integer status;
}
