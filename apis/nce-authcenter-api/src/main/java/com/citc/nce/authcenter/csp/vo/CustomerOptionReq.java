package com.citc.nce.authcenter.csp.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * csp客户下拉框vo
 *
 * @author jiancheng
 */
@Data
public class CustomerOptionReq extends PageParam {

//    @NotNull
    @ApiModelProperty("运营商代码")
    private Integer operatorCode;

    @ApiModelProperty("客户名称")
    private String name;

    @ApiModelProperty("排除的客户ID")
    private List<String> excludeCustomerIds;
}
