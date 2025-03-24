package com.citc.nce.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author bydud
 * @since 2024/4/8
 */
@Data
@Accessors(chain = true)
public class SetUserName {
    @NotNull(message = "滑动验证码")
    @ApiModelProperty(value = "滑动验证码")
    private String rotateCheck2Id;

    @ApiModelProperty(value = "账户名")
    @NotBlank(message = "账户名不能为空")
    private String name;
}
