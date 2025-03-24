package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/18 19:49
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UuidReq {

    @NotBlank(message = "uuid不能为空")
    @ApiModelProperty(value = "uuid", dataType = "String", required = true)
    private String uuid;
}
