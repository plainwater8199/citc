package com.citc.nce.auth.invoice.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 申请开票
 *
 * @author bydud
 * @since 2024/2/28
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "InvoiceApply对象", description = "申请开票，")
public class InvoiceApply implements Serializable {

    private static final long serialVersionUID = 1L;

    private String create;

    @NotBlank(message = "付费方式不能为空")
    private String type;

    @NotNull
    @Size(min = 1, message = "订单不能为空")
    private List<Long> orderIds;

}

