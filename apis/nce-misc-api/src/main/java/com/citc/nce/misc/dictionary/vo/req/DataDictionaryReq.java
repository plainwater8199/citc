package com.citc.nce.misc.dictionary.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/13 18:39
 * @Version: 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "DataDictionary对象", description = "")
public class DataDictionaryReq {

    @NotBlank
    @ApiModelProperty(value = "子级code", dataType = "String", required = true)
    private String code;

    @NotBlank
    @ApiModelProperty(value = "字典名称", dataType = "String", required = true)
    private String content;

    @NotNull
    @ApiModelProperty(value = "父级code", dataType = "String", required = true)
    private String parentId;

}
