package com.citc.nce.robot.api.tempStore.bean.manage;

import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bydud
 * @since 10:28
 */
@Data
@ApiModel(value = "后台订单管理查询")
public class OrderManagePageQuery {

    @ApiModelProperty("订购用户查询字段")
    private String customerQuery;
    @ApiModelProperty("(商品)归属用户查询字段")
    private String goodsQuery;
    @ApiModelProperty("模板类型")
    private List<MsType> tempTypes;
    @ApiModelProperty("支付状态")
    private PayStatus payStatus;

    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
