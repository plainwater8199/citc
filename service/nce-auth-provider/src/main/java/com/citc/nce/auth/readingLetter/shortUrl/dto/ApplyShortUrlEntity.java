package com.citc.nce.auth.readingLetter.shortUrl.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 文件名:ApplyShortUrlEntity
 * 创建者:zhujinyu
 * 创建时间:2024/7/15 16:40
 * 描述:
 */
@Data
@Builder
public class ApplyShortUrlEntity {

    private Long templateId;
    private String codeType;
    private List<String> smsSigns;
    private List<Params> paramList;
    private Integer showTimes;
    private Integer expireTime;
    //自定义域名
    private String domain;

/*    {    "templateId" : 518640803064553594,
            "codeType" : "GROUP_SEND",
            "smsSigns" : ["签名名称"],
        "paramList":[{
        "custFlag" : "15050505555",
                "custId" : "11111111",
                "customShortCode" : "abc106",
                "customUrl" : "www.example.com"
    }],
        "batchId" : "",
            "showTimes" : 1,
            "expireTime" : 1}*/

}
