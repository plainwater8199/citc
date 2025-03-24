package com.citc.nce.robot.api.tempStore.bean.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:19
 */
@Data
public class ManagePageQuery {
    @ApiModelProperty("(商品)归属用户查询字段")
    private String goodsQuery;
    @ApiModelProperty("商品名称")
    private String goodsName;
    @ApiModelProperty(value = "商品类型 0:机器人, 1:5G消息", dataType = "Integer")
    private Integer type;
    @ApiModelProperty(value = "审核状态 0:待审核 1:审核不通过 2:已上架 3:已下架", dataType = "Integer")
    private Integer status;
    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
