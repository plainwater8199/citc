package com.citc.nce.auth.csp.relationship.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:18
 
 * @Version: 1.0
 */
@Data
public class RelationshipReq {


    @ApiModelProperty("用户Id")
    private String userId;
}
