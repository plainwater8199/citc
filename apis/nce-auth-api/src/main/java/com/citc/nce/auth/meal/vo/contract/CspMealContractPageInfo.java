package com.citc.nce.auth.meal.vo.contract;

import com.citc.nce.auth.meal.enums.CspMealContractStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * bydud
 * 2024/1/23
 **/
@Data
@ApiModel(value = "CspMealContractPageInfo对象", description = "csp用户套餐表")
public class CspMealContractPageInfo {

    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("cspId")
    private String cspId;
    @ApiModelProperty("csp客户名称")
    private String cspUserName;
    @ApiModelProperty("csp客户手机号")
    private String cspPhone;

    @ApiModelProperty("企业名称")
    private String enterpriseName;
    @ApiModelProperty("企业账号名称")
    private String enterpriseAccountName;

    @ApiModelProperty("合同状态,0待生效 1生效中 2已失效")
    private CspMealContractStatus status;

    @ApiModelProperty("套餐客户数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mealNum;

    @ApiModelProperty("合同生效时长（年）")
    private Integer validTime;

    @ApiModelProperty("生效时间")
    private Date effectiveTime;

    @ApiModelProperty("到期时间")
    private Date expireTime;
}
