package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.account.vo
 * @Author: litao
 * @CreateTime: 2023-02-14  15:35
 
 * @Version: 1.0
 */
@Data
public class AccountPageReq implements Serializable {
    @ApiModelProperty(value = "页数", dataType = "Integer")
    private Integer pageNo;

    @ApiModelProperty(value = "每页条数", dataType = "Integer")
    private Integer pageSize;
}
