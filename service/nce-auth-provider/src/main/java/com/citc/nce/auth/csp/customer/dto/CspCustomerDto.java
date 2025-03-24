package com.citc.nce.auth.csp.customer.dto;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspCustomerDto extends PageParam {

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("用户Id")
    private String userId;

    @ApiModelProperty("CSP启用状态 0:未启用 1：已启用")
    private Integer cspActive;

    private Integer currentPage;

    private String tableName;
}
