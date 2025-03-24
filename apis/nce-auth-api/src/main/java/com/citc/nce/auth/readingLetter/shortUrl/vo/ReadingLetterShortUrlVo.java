package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zjy
 */
@Data
public class ReadingLetterShortUrlVo {
    @ApiModelProperty("Id")
    private Long id;
    @ApiModelProperty("用户Id")
    private String customerId;
    @ApiModelProperty("模板名称")
    private String templateName;
    @ApiModelProperty("阅信模板ID")
    private Long templateId;
    @ApiModelProperty("短链")
    private String shortUrl;
    @ApiModelProperty("签名列表")
    private String signs;
    @ApiModelProperty("申请账号名")
    private String accountName;
    @ApiModelProperty("申请解析数")
    private Integer requestParseNumber;
    @ApiModelProperty("已解析数")
    private Integer resolvedNumber;
    @ApiModelProperty("审核状态  1审核成功  2审核失败")
    private Integer auditStatus;
    @ApiModelProperty("任务状态 1进行中 2 已过期 3 已结束 ")
    private Integer taskStatus;
    @ApiModelProperty("申请日期")
    private Date createTime;
    @ApiModelProperty("有效期")
    private Date validityDate;
    @ApiModelProperty("运营商编码 0：缺省(硬核桃), 1:联通, 2:移动, 3:电信")
    private Integer operatorCode;
    @ApiModelProperty("短链解析资费单价")
    private Integer price;
    @ApiModelProperty("申请创建短链的账号(阅信+账号ID)")
    private String accountId;
}
