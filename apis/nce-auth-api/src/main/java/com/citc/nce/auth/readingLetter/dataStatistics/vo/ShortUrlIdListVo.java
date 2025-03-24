package com.citc.nce.auth.readingLetter.dataStatistics.vo;

import lombok.Data;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 查询某csp旗下某日的各解析记录
 */
@Data
public class ShortUrlIdListVo {

    private Long shortUrlId;

    private String shortUrl;

    private String dayStr;

    private Integer successNumber;

    private Integer operatorCode;

    private String accountName;

    private String templateName;

}
