package com.citc.nce.auth.readingLetter.dataStatistics.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ReadingLetterDailyReportSelectVo implements Serializable {
    private static final long 允许服务商代理登录serialVersionUID = 1231812312255L;
    @ExcelProperty(value = "模板名称", index = 0)
    private String templateName;
    @ExcelProperty(value = "短链", index = 1)
    private String shortUrl;
    @ExcelIgnore
    private Long shortUrlId;
    @ExcelProperty(value = "阅信+账号", index = 2)
    private String accountName;
    @ExcelIgnore
    private Integer operatorCode;
    @ExcelProperty(value = "运营商", index = 3)
    private String operatorName;
    @ExcelProperty(value = "成功解析数量", index = 4)
    private Integer successNumber;

}