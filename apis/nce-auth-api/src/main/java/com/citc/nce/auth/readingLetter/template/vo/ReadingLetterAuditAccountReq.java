package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件名:ReadingLetterAuditAccountReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/11 15:10
 * 描述:
 */
@Data
public class ReadingLetterAuditAccountReq {

    @ApiModelProperty("短信类型   1:5G阅信  2:阅信+")
    private Integer smsType;

    @ApiModelProperty("送审时所使用的外部平台账号")
    private String auditAccountId;
}
