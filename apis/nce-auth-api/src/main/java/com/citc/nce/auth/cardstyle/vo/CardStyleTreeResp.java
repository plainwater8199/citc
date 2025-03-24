package com.citc.nce.auth.cardstyle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class CardStyleTreeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 样式名称
     */
    @ApiModelProperty(value = "样式名称")
    private String styleName;

    /**
     * 样式信息
     */
    @ApiModelProperty(value = "样式信息")
    private String styleInfo;

    /**
     * css样式
     */
    @ApiModelProperty(value = "css样式")
    private String styleCss;

    private Integer isShow;
}
