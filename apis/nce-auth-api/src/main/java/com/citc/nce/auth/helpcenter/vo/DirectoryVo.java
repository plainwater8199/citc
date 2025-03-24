package com.citc.nce.auth.helpcenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yy
 * @date 2024-05-06 09:55:22
 * @description 目录
 */
@Data
public class DirectoryVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "目录内容")
    String content;
    @ApiModelProperty(value = "目录状态")
    Integer status;
}
