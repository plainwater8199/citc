package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author ping chen
 */
@Data
public class DeveloperCustomer5gManagerVo {

    @ApiModelProperty("企业名称")
    private String enterpriseAccountName;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("客户Id")
    private String customerId;

    @ApiModelProperty("调用次数(今天)")
    private Integer callCountToday = 0;

    @ApiModelProperty("调用次数(累计)")
    private Integer callCount = 0;

    @ApiModelProperty("创建时间")
    private Date creatTime;

    @ApiModelProperty("状态，0:启用，1:禁用,2:模板检查中，3:模板异常")
    private Integer state;

    @ApiModelProperty("模板状态:2:模板检查中，3:模板异常,4:审核成功")
    private Integer templateState;

    @ApiModelProperty("唯一Id")
    private String uniqueId;
}
