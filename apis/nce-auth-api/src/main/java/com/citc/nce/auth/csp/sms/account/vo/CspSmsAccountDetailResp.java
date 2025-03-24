package com.citc.nce.auth.csp.sms.account.vo;

import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSaveReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CspSmsAccountDetailResp implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("归属客户id")
    private Long enterpriseId;

    @ApiModelProperty("归属客户名称")
    private String enterpriseName;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("appSecret")
    private String appSecret;

    @ApiModelProperty("通道code")
    private String dictCode;

    @ApiModelProperty("通道value")
    private String dictValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty(value = "签名具体属性", dataType = "List")
    private List<CspSmsSignatureResp> signatureList;
}
