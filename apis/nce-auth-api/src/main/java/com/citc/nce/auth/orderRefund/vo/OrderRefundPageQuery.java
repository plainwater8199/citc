package com.citc.nce.auth.orderRefund.vo;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author bydud
 * @since 2024/3/12
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 分页参数")
public class OrderRefundPageQuery extends PageParam {
    @ApiModelProperty("客户企业名称")
    private String orderId;

    @ApiModelProperty("客户企业名称")
    private String enterpriseAccountName;

    @ApiModelProperty("套餐类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;

    private String cspId;
}
