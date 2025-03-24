package com.citc.nce.auth.invoice.vo;

import com.citc.nce.common.core.pojo.PageParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author bydud
 * @since 2024/3/12
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 查询条件")
public class InvoiceOrderPreQuery extends PageParam {

    @ApiModelProperty("开始时间 yyyy-MM-dd HH:mm:ss 包括")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty("结束时间 yyyy-MM-dd HH:mm:ss 不包含")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    @ApiModelProperty("订单编号")
    private String orderId;
    private String creator;
}
