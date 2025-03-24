package com.citc.nce.readingLetter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 文件名:ReadingLetterParseRecordDo
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:45
 * 描述:
 */
@TableName("reading_letter_parse_record")
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ReadingLetterParseRecordDo extends BaseDo<ReadingLetterParseRecordDo> {
    private static final long serialVersionUID = 4213624912358724L;
    private Long id;

    private Long platformTemplateId;

    private String customerId;

    private Integer operatorCode;
    //短链id
    private Long shortUrlId;
    //发送结果 0成功  其他失败
    private Integer sendResult;
    //描述
    private String describ;
    //接收时间
    private Date receiptTime;
    //日期 : 例如 20240718
    private String dayStr;

    private Integer smsType;
    //来源(1:群发  2:开发者服务)
    private Integer sourceType;
    //群发ID 或者是 开发者服务应用ID
    private Long groupSendId;
    //蜂动发送任务id
    private String taskId;
    //短链
    private String shortUrl;
    //收信人手机号
    private String phoneNum;
    //蜂动ChatbotAccount
    private String chatbotAccount;
}
