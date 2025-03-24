package com.citc.nce.auth.postpay.order.vo;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author jcrenc
 * @since 2024/3/12 14:11
 */
@ApiModel
@Data
@Accessors(chain = true)
public class PostpayOrderDetailReq extends PageParam {
    @ApiModelProperty("订单ID,关联到后付费订单表的订单id")
    @NotBlank
    private String orderId;
}
