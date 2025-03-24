package com.citc.nce.auth.cardstyle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CardStyleOneReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;
}
