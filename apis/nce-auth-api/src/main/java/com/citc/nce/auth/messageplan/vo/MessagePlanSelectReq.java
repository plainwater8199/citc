package com.citc.nce.auth.messageplan.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class MessagePlanSelectReq extends PageParam {
    @ApiModelProperty("计划ID")
    private String planId;

    @ApiModelProperty("运营商类型")
    private Integer operator;
}
