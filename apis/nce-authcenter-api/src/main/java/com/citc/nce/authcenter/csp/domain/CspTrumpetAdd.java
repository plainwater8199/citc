package com.citc.nce.authcenter.csp.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * csp小号
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 10:01:59
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "CspTrumpetAdd对象", description = "csp小号")
public class CspTrumpetAdd implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("cspId")
    private String cspId;

    @ApiModelProperty("账号")
    @NotEmpty(message = "账号不能为空")
    private String accountName;

    @ApiModelProperty("手机号")
    @NotEmpty(message = "手机号不能为空")
    private String phone;
}
