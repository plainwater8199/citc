package com.citc.nce.auth.csp.videoSms.signature.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsSignatureSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "类型 0:视频短信 1:其他")
    private Integer type;

    @ApiModelProperty(value = "签名具体属性", dataType = "List")
    @Valid
    private List<CspVideoSmsSignatureSaveReq> signatureList;
}
