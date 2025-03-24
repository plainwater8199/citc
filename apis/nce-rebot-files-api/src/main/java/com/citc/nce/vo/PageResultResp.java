package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
@Data
public class PageResultResp<T> implements Serializable {

    private static final long serialVersionUID = 6175999778747535591L;

    @ApiModelProperty(value = "总条数",example = "10")
    private Long total;

    @ApiModelProperty(value = "返回的具体对象参数")
    private List<T>  list;
}
