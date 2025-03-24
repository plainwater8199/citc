package com.citc.nce.auth.csp.dict.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspDictResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("字典code")
    private String dictCode;

    @ApiModelProperty("字典value")
    private String dictValue;

    @ApiModelProperty("字典类型 1:通道")
    private Integer dictType;
}
