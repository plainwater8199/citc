package com.citc.nce.auth.csp.home.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 10:43
 */
@Data
public class HomeLineChartReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", dataType = "String", required = true, example = "2023-10-10")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", dataType = "String", required = true, example = "2023-10-10")
    private String endTime;

    private String creator;
}
