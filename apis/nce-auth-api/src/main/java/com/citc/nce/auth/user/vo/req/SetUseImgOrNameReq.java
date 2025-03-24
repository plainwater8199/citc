package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author huangchong
 * @date 2022/7/15 17:40
 * @describe
 */
@Data
public class SetUseImgOrNameReq {

    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "userId", dataType = "String", required = true)
    private String userId;

    @ApiModelProperty(value = "userImgUuid", dataType = "String", required = false)
    private String userImgUuid;

    @ApiModelProperty(value = "账户名", dataType = "String", required = false)
    private String name;

}
