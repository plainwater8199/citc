package com.citc.nce.robot.api.tempStore.bean.customer;

import com.citc.nce.common.bean.CspNameBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author bydud
 * @since 10:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "客户-商品-机器人商品查询信息")
public class ChatBootGoods extends CspNameBase {
    @ApiModelProperty(value = "id", dataType = "Long")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "商品uuid", dataType = "String")
    private String goodsId;

    @ApiModelProperty(value = "版本号", dataType = "Integer")
    private Integer version;

    @ApiModelProperty(value = "商品名称", dataType = "String")
    private String goodsName;

    @ApiModelProperty(value = "商品描述", dataType = "String")
    private String goodsDesc;

    @ApiModelProperty(value = "价格", dataType = "String")
    private String price;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;


}
