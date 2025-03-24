package com.citc.nce.auth.usermessage.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/8 19:18
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class MsgReq {

    @ApiModelProperty(value = "模板code", dataType = "String", required = true)
    @NotBlank(message = "templdateCode不能为空")
    private String templdateCode;

    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @ApiModelProperty(value = "备注", dataType = "String", required = true)
    private String remark;

}
