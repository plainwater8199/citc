package com.citc.nce.authcenter.systemmsg.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
@Accessors(chain = true)
public class SysMsgManageInfo {
    @NotBlank(message = "标题不能为空")
    @ApiModelProperty(value = "标题", dataType = "String")
    private String title;
    @NotBlank(message = "内容不能为空")
    @ApiModelProperty(value = "内容", dataType = "String")
    private String content;
    @NotNull(message = "是否立即发送不能为空")
    @ApiModelProperty(value = "是否立即发送（0：否 1：是）", dataType = "String")
    private Integer isSend;
    @ApiModelProperty(value = "发送时间", dataType = "String")
    private Date sendTime;
    @NotNull(message = "接收对象类型不能为空")
    @ApiModelProperty(value = "接收对象类型，1-userId，2-用户标签", dataType = "String")
    private Integer receiveType;
    @NotBlank(message = "接受用户对象不能为空")
    @ApiModelProperty(value = "接受用户对象:标签：0-全部,10001-企业用户，10002-实名用户，10003-入驻用户，10004-能力供应商，10005-解决方案商，100006-csp用户,100007-GSMA用户,100008-5G消息工作组会员", dataType = "String")
    private String receiveObjects;
}
