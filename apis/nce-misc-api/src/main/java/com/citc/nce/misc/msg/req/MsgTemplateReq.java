package com.citc.nce.misc.msg.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/18 15:35
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class MsgTemplateReq {

    @ApiModelProperty(value = "模板code", dataType = "String", required = true)
    @NotBlank(message = "templdateCode不能为空")
    private String templdateCode;

}
