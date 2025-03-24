package com.citc.nce.auth.messagetemplate.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableData extends BaseRowModel {

    @ExcelProperty(value = "变量1")
    private String variableOne;

    @ExcelProperty(value = "变量2")
    private String variableTwo;

    @ExcelProperty(value = "变量3")
    private String variableThree;

    @ExcelProperty(value = "变量4")
    private String variableFour;

    @ExcelProperty(value = "变量5")
    private String variableFive;

    @ExcelProperty(value = "手机号")
    private String phone;
}
