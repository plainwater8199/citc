package com.citc.nce.auth.postpay.order.vo;

import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author jcrenc
 * @since 2024/3/12 14:11
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class PostpayOrderNoteVo extends PageParam {
    @ApiModelProperty("主键")
    @NotNull
    private Long id;

    @ApiModelProperty("订单备注")
    private String note;
}
