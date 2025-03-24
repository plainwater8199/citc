package com.citc.nce.readingLetter.req;

import lombok.Data;

import java.util.List;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 查询某csp旗下某日的各解析记录
 */
@Data
public class TodayDataSelectReq {

    private String customerId;

    private String dayStr;

    private List<String> chatbotAccounts;

    private List<Long> planIds;

    private List<Long> shortUrlIds;

    private Integer smsType;

    private Integer operatorCode;

}
