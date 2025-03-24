package com.citc.nce.misc.dictionary.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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
@ApiModel(value = "DataDictionaryType对象", description = "")
public class DataDictionaryTypeReq {

    @NotBlank
    @ApiModelProperty(value = "类型名称", dataType = "String", required = true)
    private String typeName;

    @NotEmpty
    @ApiModelProperty(value = "数据字典", dataType = "List", required = true)
    private List<DataDictionaryReq> dataDictionaryReqList;
}
