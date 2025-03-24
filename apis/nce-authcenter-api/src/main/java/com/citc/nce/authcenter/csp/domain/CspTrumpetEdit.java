package com.citc.nce.authcenter.csp.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

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
@ApiModel(value = "CspTrumpetEdit对象", description = "csp小号")
public class CspTrumpetEdit extends CspTrumpetAdd {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("小号id")
    @NotNull(message = "小号id不能为空")
    private Long ctuId;

}
