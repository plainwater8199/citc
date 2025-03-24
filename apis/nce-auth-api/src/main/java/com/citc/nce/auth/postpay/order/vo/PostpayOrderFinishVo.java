package com.citc.nce.auth.postpay.order.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jcrenc
 * @since 2024/3/12 14:11
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class PostpayOrderFinishVo extends PageParam {
    @ApiModelProperty("主键")
    @NotNull
    private Long id;

    @ApiModelProperty("交易流水号必填，最大20位数字")
    @NotBlank
    @Length(max = 20)
    private String serialNumber;
    @ApiModelProperty("支付金额，最大20位数字")
    @NotBlank
    @Length(max = 64)
    private String payAmount;
}
