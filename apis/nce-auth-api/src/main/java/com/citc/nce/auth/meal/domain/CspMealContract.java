package com.citc.nce.auth.meal.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.meal.enums.CspMealContractStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * csp套餐合同表
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_meal_contract")
@ApiModel(value = "CspMealContract对象", description = "csp套餐合同表")
public class CspMealContract implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "contract_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;

    @ApiModelProperty("合同编号")
    @TableField("contract_no")
    private String contractNo;

    @ApiModelProperty("cspId")
    @TableField("csp_id")
    private String cspId;

    @ApiModelProperty("合同状态,0待生效 1生效中 2已失效")
    @TableField("status")
    private CspMealContractStatus status;

    @ApiModelProperty("合同生效时长（年）")
    @TableField("valid_time")
    private Integer validTime;

    @ApiModelProperty("生效时间")
    @TableField("effective_time")
    private Date effectiveTime;

    @ApiModelProperty("到期时间")
    @TableField("expire_time")
    private Date expireTime;

    @ApiModelProperty("创建者")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private Date deletedTime;


}
