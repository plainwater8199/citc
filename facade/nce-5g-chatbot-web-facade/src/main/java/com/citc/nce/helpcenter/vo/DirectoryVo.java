package com.citc.nce.helpcenter.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yy
 * @date 2024-05-13 17:11:08
 */
@Data
@ApiModel("帮助中心查询返回对象")
public class DirectoryVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "目录内容")
    String content;
}

