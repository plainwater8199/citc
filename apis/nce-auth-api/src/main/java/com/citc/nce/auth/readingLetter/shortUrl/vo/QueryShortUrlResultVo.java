package com.citc.nce.auth.readingLetter.shortUrl.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名:ApplyShortUrlResultVo
 * 创建者:zhujinyu
 * 创建时间:2024/7/15 17:32
 * 描述:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryShortUrlResultVo {

//    短链Id
    private Long aimUrlId;
    //客户标识
    private String custFlag;
    //自定义ID
    private String custId;
    private String aimCode;
    private String aimUrl;
    //总解析数
    private Integer totalShowTimes;
    //已解析数
    private Integer showTimes;
    //0：成功
    private Integer status;

}
