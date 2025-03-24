package com.citc.nce.readingLetter.req;

import lombok.Data;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 收到的短链解析回执
 */
@Data
public class ReadingLetterDailyReportSelectByCspIdReq {

    private String cspId;

    private Integer operatorCode;

    private Integer smsType;

}
