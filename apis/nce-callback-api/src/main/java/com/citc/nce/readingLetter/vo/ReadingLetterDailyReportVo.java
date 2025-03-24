package com.citc.nce.readingLetter.vo;

import lombok.Data;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 收到的短链解析回执
 */
@Data
public class ReadingLetterDailyReportVo implements java.io.Serializable{
    private static final long serialVersionUID = 1231812312255L;

    private String templateName;

    private String shortUrl;

    private String accountName;
    //短链
    private String operatorCode;
    //短链类型
    private Integer successNumber;

    private Long shortUrlId;
}
