package com.citc.nce.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author bydud
 * @since 2024/4/8
 */
@Data
@Accessors(chain = true)
public class SetUserAvatar {

    @ApiModelProperty(value = "头像")
    @NotBlank(message = "头像不能为空")
    private String userImg;
}
