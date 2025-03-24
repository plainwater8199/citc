package com.citc.nce.readingLetter.vo;

import lombok.Data;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 查询某csp旗下某日的各解析记录
 */
@Data
public class DailyReportListVo {

    private Integer successNumber;

    private String taskId;

    private String shortUrl;

    private String chatbotAccount;
    //群发ID 或者是 开发者服务应用ID
    private Long groupSendId;

    private Long platformTemplateId;

    private Integer sourceType;
}
