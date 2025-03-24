package com.citc.nce.auth.csp.sms.account.vo;

import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CspSmsAccountChatbotResp  implements Serializable {


    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("签名")
    private List<CspSmsSignatureResp> signatureList;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("累计充值金额")
    private String accumulatedRechargeAmount;

    @ApiModelProperty("累计退款总额")
    private String accumulatedRefundAmount;

    @ApiModelProperty("充值总条数")
    private String totalRechargeCount;

    @ApiModelProperty("扣除总条数")
    private String totalDifferCount;

    @ApiModelProperty("视频短信剩余量")
    private Long residualCount;
}
