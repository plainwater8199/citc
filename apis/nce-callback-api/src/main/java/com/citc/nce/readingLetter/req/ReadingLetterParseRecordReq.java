package com.citc.nce.readingLetter.req;

import lombok.Data;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 收到的短链解析回执
 */
@Data
public class ReadingLetterParseRecordReq implements java.io.Serializable{
    private static final long serialVersionUID = 1231812312255L;

    private Long templateId;

    private String custFlag;

    private String custId;
    //短链
    private String aimUrl;
    //短码
    private String aimCode;
    //短链类型
    private Integer status;
    //描述
    private String describ;
}
