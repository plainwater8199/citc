package com.citc.nce.misc.dictionary.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/29 17:19
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DictionaryReq {
    @ApiModelProperty(value = "参数code", dataType = "String", required = true)
    @NotNull
    private String code;
}
