package com.citc.nce.misc.dictionary.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: litao
 * @Contact: taolifr
 * @Date: 2022/10/9 17:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class DeleteReq {
    @NotNull
    @ApiModelProperty(value = "是否删除", dataType = "Integer", required = true)
    private Integer deleted;
}
