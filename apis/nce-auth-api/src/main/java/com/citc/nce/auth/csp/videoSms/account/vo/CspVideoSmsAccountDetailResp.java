package com.citc.nce.auth.csp.videoSms.account.vo;

import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsAccountDetailResp implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty("customerId")
    private String customerId;

    @ApiModelProperty(value = "签名具体属性", dataType = "List")
    private List<CspVideoSmsSignatureResp> signatureList;
}
