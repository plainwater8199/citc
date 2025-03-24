package com.citc.nce.auth.readingLetter.dataStatistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zjy
 */
@Data
public class FifthDataStatisticsReq {
    @ApiModelProperty("发送计划名")
    private String planName;

    @ApiModelProperty("Chatbot账号名")
    private String chatbotName;

    @ApiModelProperty("运营商代码")
    private Integer operatorCode;

    @ApiModelProperty("查询的开始时间")
    @NotNull(message = "查询的开始时间不能为空")
    private Date startTime;

    @NotNull(message = "查询的结束时间不能为空")
    @ApiModelProperty("查询的结束时间")
    private Date endTime;

}
