package com.citc.nce.robot.api.materialSquare.vo.summary.req;

import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OperationOrderPageReq {
    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("模板类型")
    private MsType tempType;

    @ApiModelProperty("支付状态")
    private PayStatus payStatus;

    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
