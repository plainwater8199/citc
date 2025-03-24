package com.citc.nce.auth.csp.recharge.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RechargeTariffAdd implements Serializable {

    private static final long serialVersionUID = 1L;

    // accountId
    @ApiModelProperty(value = "accountId", dataType = "String", required = true)
    @NotNull(message = "accountId不能为空")
    @JSONField(serialize = false)
    private String accountId;

    /** 账号类型 1:5g消息,2:视频短信,3:短信，4 阅信+账号 */
    @ApiModelProperty(name = "账号类型 1:5g消息,2:视频短信,3:短信，4 阅信+账号",notes = "")
    @NotNull(message = "accountType不能为空")
    @JSONField(serialize = false)
    private Integer accountType ;

    // 文本消息
    @ApiModelProperty(value = "文本消息", dataType = "int", required = true)
    private Integer textMsgPrice;

    // 富媒体消息
    @ApiModelProperty(value = "富媒体消息", dataType = "int", required = true)
    private Integer richMsgPrice;

    // 会话消息
    @ApiModelProperty(value = "会话消息", dataType = "int", required = true)
    private Integer sessionMsgPrice;

    // 回落资费类型
    @ApiModelProperty(value = "回落资费类型", dataType = "int", required = true)
    @JSONField(serialize = false)
    private Integer fallbackType;

    // 回落短信
    @ApiModelProperty(value = "回落短信", dataType = "int", required = true)
    private Integer fallbackSmsPrice;

    // 5g阅信解析
    @ApiModelProperty(value = "5g阅信解析", dataType = "int", required = true)
    private Integer yxAnalysisPrice;

    // 视频短信单价
    @ApiModelProperty(value = "视频短信单价", dataType = "int", required = true)
    private Integer videoSmsPrice;

    // 短信单价
    @ApiModelProperty(value = "短信单价", dataType = "int", required = true)
    private Integer smsPrice;

    // 阅信+解析
    @ApiModelProperty(value = "阅信+解析", dataType = "int", required = true)
    private Integer yxPlusAnalysisPrice;
}
