package com.citc.nce.auth.invoice.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发票信息管理-客户
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:57
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "InvoiceInfoCustomerVo", description = "发票信息管理-客户")
public class InvoiceInfoCustomerVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long iicusId;

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("发票类型")
    private String type;

    @ApiModelProperty("开票信息")
    private String jsonBody;

    private String updater;

    private Date updateTime;

}
