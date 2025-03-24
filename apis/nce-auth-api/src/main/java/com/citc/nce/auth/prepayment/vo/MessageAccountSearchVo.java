package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class MessageAccountSearchVo extends PageParam {
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("通道")
    private String dictCode;

    @ApiModelProperty("状态 -1:全部 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("账号名")
    private String accountName;
}
