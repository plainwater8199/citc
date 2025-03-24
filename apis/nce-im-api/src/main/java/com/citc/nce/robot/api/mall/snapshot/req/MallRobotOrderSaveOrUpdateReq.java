package com.citc.nce.robot.api.mall.snapshot.req;

import com.citc.nce.robot.api.mall.common.MallCommonContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class MallRobotOrderSaveOrUpdateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板id", dataType = "String", required = true)
    @NotBlank(message = "模板id不能为空")
    private String templateId;

    @ApiModelProperty(value = "快照uuid", dataType = "String")
    private String snapshotUuid;

    @ApiModelProperty(value = "快照内容", dataType = "String")
    private MallCommonContent mallCommonContent;

    @ApiModelProperty(value = "快照类型 0:默认 1:模板 2:商品 3:订单", dataType = "String")
    private Integer snapshotType;
}
