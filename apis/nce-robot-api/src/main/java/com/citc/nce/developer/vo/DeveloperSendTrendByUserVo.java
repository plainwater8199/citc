package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendTrendByUserVo {

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("成功次数")
    private Integer successCount;

    @ApiModelProperty("失败次数")
    private Integer failCount;

    @ApiModelProperty("未知次数")
    private Integer unknownCount;

    @ApiModelProperty("已阅次数")
    private Integer displayedCount;

}
