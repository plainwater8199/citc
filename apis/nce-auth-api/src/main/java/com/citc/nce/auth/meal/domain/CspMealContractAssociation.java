package com.citc.nce.auth.meal.domain;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * csp套餐合同详 关联表
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_meal_contract_association")
@ApiModel(value = "CspMealContractAssociation对象", description = "csp套餐合同详 关联表")
public class CspMealContractAssociation implements Serializable {

    private static final long serialVersionUID = 1L;

    public CspMealContractAssociation() {
    }

    public CspMealContractAssociation(Long contractId, CspMeal meal, Date effectiveTime) {
        this.contractId = contractId;
        this.mealId = meal.getMealId();
        this.name = meal.getName();
        this.type = meal.getType();
        this.customerNumber = meal.getCustomerNumber();
        this.price = meal.getPrice();
        this.mealDetail = JSON.toJSONString(meal);
        this.effectiveTime = effectiveTime;
    }

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("合同ID")
    @TableField("contract_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;

    @ApiModelProperty("套餐ID")
    @TableField("meal_id")
    private String mealId;

    @ApiModelProperty("套餐名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("套餐类型，1普通套餐，2扩容套餐")
    @TableField("type")
    private CspMealType type;

    @ApiModelProperty("客户个数")
    @TableField("customer_number")
    private Integer customerNumber;

    @ApiModelProperty("套餐价格")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty("套餐详情")
    @TableField("meal_detail")
    private String mealDetail;

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
    @TableLogic(value = "null", delval = "now()")
    private Date deletedTime;

    @ApiModelProperty("生效时间")
    @TableField(value = "effective_time")
    private Date effectiveTime;

}
