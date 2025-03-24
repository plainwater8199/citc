package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendVo {

    @ApiModelProperty("客户登录账号")
    private String customerUserId;

    @ApiModelProperty("企业Id")
    private Long enterpriseId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("调用次数(今日)")
    private Integer callCountToday;

    @ApiModelProperty("总调用次数")
    private Long callCount;

    @ApiModelProperty("模板Id")
    private String templateId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;
}
