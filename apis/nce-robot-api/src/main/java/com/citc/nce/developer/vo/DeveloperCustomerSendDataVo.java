package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ping chen
 */
@Data
public class DeveloperCustomerSendDataVo {

    @ApiModelProperty("公钥")
    @NotBlank(message = "公钥不能为空")
    private String appId;

    @ApiModelProperty("请求时间,时间格式yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "请求时间不能为空")
    private String time;

    @ApiModelProperty("签名md5(appKey+appSecret+AUTH_TIMESTAMP)")
    @NotBlank(message = "签名不能为空")
    private String sign;

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty("平台模板Id")
    @NotNull(message = "模板Id不能为空")
    private String platformTemplateId;

    /**
     * @link -com.citc.nce.common.core.enums.MsgTypeEnum
     */
    @ApiModelProperty("类型 15g消息，2:视屏短信,3:短信")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty("如何是个性模板需要传入变量，变量个数请与模板匹配")
    private List<String> variables;

    private List<DeveloperSendPhoneVo> developerSendPhoneVoList;

    @ApiModelProperty("账号")
    private String accountId;

    @ApiModelProperty("视屏账号AppId")
    private String videoAppId;

    @ApiModelProperty("视屏账号appSecret")
    private String videoAppSecret;

    @ApiModelProperty("账号模板Id")
    private Long templateId;

    @ApiModelProperty("5G消息类型所属应用Id")
    private String applicationUniqueId;

    @ApiModelProperty("cspId")
    private String cspId;
}
