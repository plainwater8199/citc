package com.citc.nce.auth.readingLetter.dataStatistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zjy
 */
@Data
public class DataStatisticsReq {
    @ApiModelProperty("短链")
    private String shortUrl;

    @ApiModelProperty("阅信+账号")
    private String accountName;

    @ApiModelProperty("查询的开始时间")
    @NotNull
    private Date startTime;

    @NotNull
    @ApiModelProperty("查询的结束时间")
    private Date endTime;

}
