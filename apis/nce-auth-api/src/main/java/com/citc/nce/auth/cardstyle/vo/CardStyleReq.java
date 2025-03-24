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
public class CardStyleReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 样式名称
     */
    @ApiModelProperty(value = "样式名称")
    private String styleName;

    /**
     * 样式信息
     */
    @ApiModelProperty(value = "样式信息")
    @NotBlank(message = "样式信息不能为空")
    private String styleInfo;

    /**
     * css样式
     */
    @ApiModelProperty(value = "css样式")
    @NotBlank(message = "css样式不能为空")
    private String styleCss;

    @ApiModelProperty(value = "是否展示(0展示，1不展示)")
    private Integer isShow;
}
