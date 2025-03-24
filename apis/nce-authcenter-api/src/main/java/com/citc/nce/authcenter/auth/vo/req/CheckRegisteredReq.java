package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CheckRegisteredReq implements Serializable {
    @NotBlank(message = "检查值不能为空")
    @ApiModelProperty(value = "检查值", dataType = "String",required = true)
    private String checkValue;
    @NotNull(message = "检查值类型不能为空")
    @ApiModelProperty(value = "检查值类型:1-email,2-name,3-phone,4-email是否激活", dataType = "Integer",required = true)
    private Integer checkType;
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;
    @ApiModelProperty(value = "场景类型:1:添加客户", dataType = "Integer")
    private Integer caseType;
}
