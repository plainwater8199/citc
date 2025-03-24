package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ping chen
 */
@Data
public class DeveloperYesterdayOverviewVo {


    @ApiModelProperty("调用次数")
    private Integer callCount = 0;

    @ApiModelProperty("成功次数")
    private Integer successCount = 0;

    @ApiModelProperty("失败次数")
    private Integer failCount = 0 ;

    @ApiModelProperty("成功率")
    private BigDecimal successRate;

    @ApiModelProperty("发送成功次数")
    private Integer sendSuccessCount = 0;

    @ApiModelProperty("发送失败次数")
    private Integer sendFailCount = 0;

    @ApiModelProperty("发送未知次数")
    private Integer sendUnknownCount = 0;

    @ApiModelProperty("发送已阅次数")
    private Integer sendDisplayedCount = 0;

}
