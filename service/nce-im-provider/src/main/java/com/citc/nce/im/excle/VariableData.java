package com.citc.nce.im.excle;

import lombok.Data;

@Data
/**
 * 变量模板参数格式
 * excel的格式 第一行为列头
 * 第一列为手机号，从第二列起变量的值按序号顺序（变量1，变量2，...）从小到大依次填充
 */
public class VariableData{

    private String[] variables;
    private String phone;
}
