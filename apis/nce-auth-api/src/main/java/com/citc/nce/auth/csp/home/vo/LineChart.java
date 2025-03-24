package com.citc.nce.auth.csp.home.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 10:43
 */
@Data
public class LineChart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "时间", dataType = "String")
    private String time;
    @ApiModelProperty(value = "数字", dataType = "String")
    private String num;

}
