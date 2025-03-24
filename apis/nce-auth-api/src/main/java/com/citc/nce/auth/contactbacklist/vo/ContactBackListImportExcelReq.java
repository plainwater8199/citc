package com.citc.nce.auth.contactbacklist.vo;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
public class ContactBackListImportExcelReq {

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String personName;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phoneNum;

}
