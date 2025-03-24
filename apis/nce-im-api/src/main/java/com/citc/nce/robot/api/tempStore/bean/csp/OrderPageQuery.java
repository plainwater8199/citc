package com.citc.nce.robot.api.tempStore.bean.csp;

import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2023-11-17 11:29:51
 */
@Data
@ApiModel(value = "csp-order-分页查询参数")
public class OrderPageQuery {

    @ApiModelProperty("支付状态:0待支付1订购成功9已取消")
    private PayStatus payStatus;

    @ApiModelProperty("模板类型")
    private MsType tempType;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
