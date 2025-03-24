package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class FifthPlanOrderQueryVo extends PageParam {
    @NotEmpty
    @ApiModelProperty("5g消息账号")
    private String chatbotAccount;
}
