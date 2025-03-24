package com.citc.nce.auth.csp.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-21  16:09
 
 * @Version: 1.0
 */
@Data
public class IdReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;
}
