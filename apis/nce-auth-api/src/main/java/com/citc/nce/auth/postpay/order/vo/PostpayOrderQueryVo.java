package com.citc.nce.auth.postpay.order.vo;

import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/3/12 14:11
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class PostpayOrderQueryVo extends PageParam {

    @ApiModelProperty("账期 4位年份+2位月份")
    private String paymentDays;

    @ApiModelProperty("客户企业名称")
    private String enterpriseAccountName;

    @ApiModelProperty("订单状态 0无需支付 1待支付 2已支付")
    private PostpayOrderStatus status;

    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

}
