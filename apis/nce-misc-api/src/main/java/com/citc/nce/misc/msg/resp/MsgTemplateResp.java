package com.citc.nce.misc.msg.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/18 15:35
 * @Version 1.0
 * @Description:
 */
@Data
public class MsgTemplateResp {
    @ApiModelProperty(value = "id",dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "模板code",dataType = "String")
    private String templdateCode;

    @ApiModelProperty(value = "邮件主题",dataType = "String")
    private String templdateSubject;

    @ApiModelProperty(value = "模板内容",dataType = "String")
    private String templdateContent;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）",dataType = "Integer")
    private Integer isDelete;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳",dataType = "Long")
    private Long deletedTime;

    @ApiModelProperty(value = "消息有效期，配合验证码类型消息使用.0为永久有效。单位分钟",dataType = "Integer")
    private Integer expireTime;

    @ApiModelProperty(value = "创建时间",dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新时间",dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;
}
