package com.citc.nce.auth.csp.customer.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("CSP启用状态 0:未启用 1：已启用")
    private Integer cspActive;
}
