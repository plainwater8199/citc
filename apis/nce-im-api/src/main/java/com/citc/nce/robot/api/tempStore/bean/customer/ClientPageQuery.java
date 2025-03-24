package com.citc.nce.robot.api.tempStore.bean.customer;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:19
 */
@Data
@ApiModel(value = "客户-订单管理查询参数")
public class ClientPageQuery {

    private String goodsName;
    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
