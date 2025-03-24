package com.citc.nce.auth.csp.videoSms.account.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CspVideoSmsAccountTariffAdd  implements Serializable {
    private static final long serialVersionUID = 1L;

    // accountId
    @ApiModelProperty(value = "accountId", dataType = "String", required = true)
    @NotNull(message = "accountId不能为空")
    @JSONField(serialize = false)
    private String accountId;

    // 视频短信单价
    @ApiModelProperty(value = "视频短信单价", dataType = "int", required = true)
    @NotNull(message = "视频短信单价单价不能为空")
    private Integer videoSmsPrice;
}
