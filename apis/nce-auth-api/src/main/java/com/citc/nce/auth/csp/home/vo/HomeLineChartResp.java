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
public class HomeLineChartResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据类型 1:小时 2:天 3:周", dataType = "Int")
    private Long dataType;

    @ApiModelProperty(value = "用户折线图数据", dataType = "List")
    private List<LineChart> userLineChart;

    @ApiModelProperty(value = "chatbot折线图数据", dataType = "List")
    private List<LineChart> chatbotLineChart;
}
