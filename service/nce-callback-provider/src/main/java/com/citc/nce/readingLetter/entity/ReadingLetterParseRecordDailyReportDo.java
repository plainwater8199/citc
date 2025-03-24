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
@TableName("reading_letter_parse_record_daily_report")
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ReadingLetterParseRecordDailyReportDo extends BaseDo<ReadingLetterParseRecordDailyReportDo> {
    private static final long serialVersionUID = 4213624912351234L;

    private String customerId;
    //短链id
    private Long shortUrlId;
    //短链
    private String shortUrl;
    //短链类型
    private Integer successNumber;
    //短链类型
    private Integer operatorCode;
    //日报表时间
    private Date dayTime;
    //阅信信息类型 1:5G阅信  2:阅信+
    private Integer smsType;
    //5G阅信 蜂动群发任务id
    private String taskId;
    //5G阅信 群发id
    private Long groupSendId;
    //fontdo chatbotAccount
    private String chatbotAccount;
    //5G阅信的fontdo平台模板id
    private Long platformTemplateId;
    //来源(1:群发  2:开发者服务)
    private Integer sourceType;
}
