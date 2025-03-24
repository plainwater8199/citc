package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.auth.user.vo.req
 * @Author: weilanglang
 * @CreateTime: 2022-09-01  17:55
 
 * @Version: 1.0
 */
@Data
public class UserInformationReq implements Serializable {

    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer", required = true)
    @NotNull(message = "ptotalType不能为空")
    private Integer protalType;

}
