package com.citc.nce.auth.meal.vo.contract;

import com.citc.nce.auth.meal.enums.CspMealContractStatus;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * bydud
 * 2024/1/23
 **/
@Data
@ApiModel(value = "CspMealContractPageQuery对象", description = "csp用户套餐表")
public class CspMealContractPageQuery extends PageParam {

    @ApiModelProperty("合同状态,0待生效 1生效中 2已失效")
    private CspMealContractStatus status;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("企业账号名称")
    private String enterpriseAccountName;

    @ApiModelProperty("生效时间-开始")
    private Date effectiveStartTime;
    @ApiModelProperty("生效时间-结束")
    private Date effectiveEndTime;

    @ApiModelProperty("到期时间-开始")
    private Date expireStartTime;
    @ApiModelProperty("到期时间-结束")
    private Date expireEndTime;

    @ApiModelProperty("cspId")
    private String cspId;
}
