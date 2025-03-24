package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class VideoSmsPlanOrderListVo {
    @ApiModelProperty("套餐ID")
    private String planId;

    @ApiModelProperty("套餐名称")
    private String planName;

    @ApiModelProperty("订购时间")
    private String createTime;

    @ApiModelProperty("额度")
    private Long limit;

    @ApiModelProperty("使用量")
    private Long usage;

    @ApiModelProperty("失效量")
    private Long invalid;

    @ApiModelProperty("剩余量")
    private Long usable;

}
