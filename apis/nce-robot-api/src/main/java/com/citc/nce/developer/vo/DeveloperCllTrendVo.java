package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperCllTrendVo {

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("调用次数")
    private Integer callCount;

    @ApiModelProperty("成功次数")
    private Integer successCount;

    @ApiModelProperty("失败次数")
    private Integer failCount;

}
