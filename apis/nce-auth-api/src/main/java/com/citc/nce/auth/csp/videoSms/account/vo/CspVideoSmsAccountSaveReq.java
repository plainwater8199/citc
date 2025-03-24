package com.citc.nce.auth.csp.videoSms.account.vo;

import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSaveReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSaveReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsAccountSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "通道Code")
    @NotBlank(message = "通道不能为空")
    private String dictCode;

    @ApiModelProperty("通道value")
    private String dictValue;

    @ApiModelProperty(value = "账户名称")
    @NotBlank(message = "账户名称不能为空")
    @Length(max = 25, message = "账户名称长度超过限制")
    private String accountName;

    @ApiModelProperty(value = "appId")
    @NotBlank(message = "appId不能为空")
    @Length(max = 50, message = "appId长度超过限制")
    private String appId;

    @ApiModelProperty(value = "appSecret")
    @NotBlank(message = "appSecret不能为空")
    @Length(max = 50, message = "appSecret长度超过限制")
    private String appSecret;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "签名具体属性", dataType = "List")
    @Valid
    private List<CspVideoSmsSignatureSaveReq> signatureList;
}
