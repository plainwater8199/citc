package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author ping chen
 */
@Data
public class VideoDeveloperCustomerManagerVo {

    @ApiModelProperty("企业名称")
    private String enterpriseAccountName;

    @ApiModelProperty("客户Id")
    private String customerId;

    @ApiModelProperty("调用次数(今天)")
    private Integer callCountToday = 0;

    @ApiModelProperty("调用次数(累计)")
    private Integer callCount = 0;

    @ApiModelProperty("创建时间")
    private Date creatTime;

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;
}
