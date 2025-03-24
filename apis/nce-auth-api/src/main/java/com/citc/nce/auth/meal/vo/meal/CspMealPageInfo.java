package com.citc.nce.auth.meal.vo.meal;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.converters.date.DateStringConverter;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.citc.nce.auth.meal.enums.excel.CspMealStatusConvert;
import com.citc.nce.auth.meal.enums.excel.CspMealTypeConvert;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 套餐查询详情
 * bydud
 * 2024/1/22
 **/
@Data
@ContentRowHeight(25) //内容行高
@HeadRowHeight(35)//表头行高
@ApiModel(value = "CspMealPageInfo对象", description = "csp用户套餐表")
public class CspMealPageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ExcelIgnore
    private Long id;


    @ApiModelProperty("套餐名称")
    @TableField("name")
    @ExcelProperty(value = "套餐名称")
    @ColumnWidth(20)
    private String name;

    @ApiModelProperty("套餐ID,ccp+17位时间戳")
    @TableField("meal_id")
    @ExcelProperty(value = "套餐ID")
    @ColumnWidth(25)
    private String mealId;

    @ApiModelProperty("套餐类型，1普通套餐，2扩容套餐")
    @TableField("type")
    @ExcelProperty(value = "套餐类型" ,converter = CspMealTypeConvert.class)
    private CspMealType type;

    @ApiModelProperty("客户个数")
    @TableField("customer_number")
    @ExcelProperty(value = "客户个数")
    private Integer customerNumber;

    @ApiModelProperty("套餐价格")
    @TableField("price")
    @ExcelProperty(value = "套餐价格")
    private BigDecimal price;

    @ApiModelProperty("上架状态，0下架 1上架")
    @TableField("status")
    @ExcelProperty(value = "上架状态" ,converter = CspMealStatusConvert.class)
    private CspMealStatus status;

    @ApiModelProperty("订购次数，0下架 1上架")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ExcelProperty(value = "订购次数")
    private Long orderCount;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty(value = "创建时间",converter = DateStringConverter.class)
    @ColumnWidth(19)
    private Date createTime;

}
