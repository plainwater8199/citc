package com.citc.nce.readingLetter.vo;

import lombok.Data;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 收到的短链解析回执
 */
@Data
public class CspReadingLetterNumVo implements java.io.Serializable{
    private static final long serialVersionUID = 1231812312255L;

    private Integer num;
    //短链
    private Integer operatorCode;

}
