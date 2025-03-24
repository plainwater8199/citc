package com.citc.nce.misc.dictionary.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/13 18:39
 * @Version: 1.0
 * @Description:
 */
@Data
public class DictionaryResp {

    /**
     * 字典项id
     */
    @ApiModelProperty(value = "字典项id", dataType = "Long")
    private Long id;

    /**
     * 字典值
     */
    @ApiModelProperty(value = "字典值", dataType = "String")
    private String code;

    /**
     * 字典文本
     */
    @ApiModelProperty(value = "字典文本", dataType = "String")
    private String content;

    /**
     * 字典分类
     */
    @ApiModelProperty(value = "字典分类id", dataType = "Long")
    private Long dicTypeId;

    /**
     * 字典分类
     */
    @ApiModelProperty(value = "字典分类", dataType = "String")
    private String typeCode;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", dataType = "Integer")
    private Integer sort;

    /**
     * 父级字典项id
     */
    @ApiModelProperty(value = "父级字典项id", dataType = "String")
    private String parentId;

}
