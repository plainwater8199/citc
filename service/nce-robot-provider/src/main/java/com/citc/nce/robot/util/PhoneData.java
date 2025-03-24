package com.citc.nce.robot.util;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * excel模板实体类
 */
@Data
public class PhoneData{


    @ExcelProperty(value = "姓名")
    private String name;

    @ExcelProperty(value = "手机号")
    private String phone;

}
