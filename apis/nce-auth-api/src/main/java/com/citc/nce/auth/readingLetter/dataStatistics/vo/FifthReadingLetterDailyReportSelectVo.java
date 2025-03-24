package com.citc.nce.auth.readingLetter.dataStatistics.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class FifthReadingLetterDailyReportSelectVo implements Serializable {
    private static final long serialVersionUID = 1231812312255L;

    @ExcelProperty(value = "模板名称", index = 0)
    private String templateName;
    @ExcelIgnore
    private String shortUrl;

    @ExcelProperty(value = "来源", index = 1)
    private String from = "群发";

    @ExcelProperty(value = "来源名称", index = 2)
    private String planName;
    @ExcelProperty(value = "发送账号", index = 3)
    private String chatbotName;
    @ExcelProperty(value = "运营商", index = 4)
    private String operatorName;
    //供应商群发任务id
    @ExcelProperty(value = "任务ID", index = 5)
    private String taskId;
    @ExcelProperty(value = "消息类型", index = 6)
    private String messageType = "群发";
    //群发ID 或者是 开发者服务应用ID
    @ExcelIgnore
    private Long groupSendId;
    @ExcelIgnore
    private String chatbotAccount;
    @ExcelIgnore
    private Integer operatorCode;
    @ExcelProperty(value = "成功解析数量", index = 7)
    private Integer successNumber;
    //这是蜂动模板平台id
    @ExcelIgnore
    private Long platformTemplateId;
    @ExcelIgnore
    private Integer sourceType;
}