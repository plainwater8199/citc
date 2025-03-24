package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author jiancheng
 */
@Data
@ApiModel("5G消息套餐-订单列表对象")
public class FifthPlanOrderListVo {
    @ApiModelProperty("套餐ID")
    private String planId;

    @ApiModelProperty("套餐名称")
    private String planName;

    @ApiModelProperty("订购时间")
    private String createTime;

    @ApiModelProperty("文本消息额度")
    private Long textLimit;

    @ApiModelProperty("富媒体消息额度")
    private Long richLimit;

    @ApiModelProperty("会话额度")
    private Long conversationLimit;



    @ApiModelProperty("文本消息")
    private Long textUsable;

    @ApiModelProperty("富媒体消息额度")
    private Long richUsable;

    @ApiModelProperty("会话额度")
    private Long conversationUsable;


    @ApiModelProperty("文本消息使用数")
    private Long textUsage;

    @ApiModelProperty("富媒体消息使用数")
    private Long richUsage;

    @ApiModelProperty("会话使用数")
    private Long conversationUsage;



    @ApiModelProperty("文本消息失效数")
    private Long textInvalid;

    @ApiModelProperty("富媒体消息失效数")
    private Long richInvalid;

    @ApiModelProperty("会话失效数")
    private Long conversationInvalid;

}
