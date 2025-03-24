package com.citc.nce.robot.api.tempStore.bean.manage;

import com.citc.nce.common.bean.CspNameBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bydud
 * @since 16:50
 */
@Data
public class GoodsPage extends CspNameBase {

    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("商品名称")
    private String goodsName;
    @ApiModelProperty("商品UUID")
    private String goodsId;
    @ApiModelProperty("商品版本")
    private Integer version;
    @ApiModelProperty(value = "商品类型 0:机器人, 1:5G消息", dataType = "Integer")
    private Integer type;
    @ApiModelProperty(value = "价格", dataType = "String")
    private String price;
    @ApiModelProperty(value = "提交时间", dataType = "Date")
    private Date applyTime;
    @ApiModelProperty(value = "审核状态 0:待审核 1:审核不通过 2:已上架 3:已下架", dataType = "Integer")
    private Integer status;
}
