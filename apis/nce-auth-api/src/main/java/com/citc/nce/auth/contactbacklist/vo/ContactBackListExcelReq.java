package com.citc.nce.auth.contactbacklist.vo;



import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
public class ContactBackListExcelReq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 0)
    private String personName;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 1)
    private String phoneNum;
    
}
