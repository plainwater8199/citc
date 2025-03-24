package com.citc.nce.robot.api.tempStore.bean.customer;

import com.citc.nce.common.bean.CspNameBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 5g消息查询结果
 *
 * @author bydud
 * @since 10:14
 */
@Data
@ApiModel(value = "客户-商品-5g消息商品信息")
public class FiveGGoods extends CspNameBase {
    @ApiModelProperty(value = "id", dataType = "Long")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "商品uuid", dataType = "String")
    private String goodsId;

    @ApiModelProperty(value = "版本号", dataType = "Integer")
    private Integer version;

    @ApiModelProperty(value = "商品名称", dataType = "String")
    private String goodsName;

    @ApiModelProperty(value = "价格", dataType = "String")
    private String price;

    @ApiModelProperty(value = "商品UUID", dataType = "String")
    private String snapshotUuid;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "消息类型(页面上显示文字是模板类型) 1:文本 2:图片 3:视频 4:音频 5:文件 6:单卡 7:多卡 8:位置", dataType = "Integer")
    private Integer messageType;
    @ApiModelProperty(value = "5G消息模板内容")
    private String moduleInformation;
}
