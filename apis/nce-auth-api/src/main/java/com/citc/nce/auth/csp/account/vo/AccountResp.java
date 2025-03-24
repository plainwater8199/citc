package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class AccountResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("csp_account_id")
    private String chatbotCspAccountId;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("账号")
    private String cspAccount;

    @ApiModelProperty("密码")
    private String cspPassword;

    @ApiModelProperty("csp编码")
    private String cspCode;

    @ApiModelProperty("公钥")
    private String publicKey;

    @ApiModelProperty("私钥")
    private String privateKey;

    @ApiModelProperty("消息回调地址")
    private String msgCallbackUrl;

    @ApiModelProperty("媒体文件审核回调地址")
    private String mediaCallbackUrl;

    @ApiModelProperty("数据同步接口地址")
    private String dataSyncUrl;

    @ApiModelProperty("ip地址")
    private String ipAddress;

    @ApiModelProperty("代理商客户编码")
    private String agentCustomerNum;


    @ApiModelProperty("运营商")
    private Integer operatorCode;

    private String token;


    @ApiModelProperty("运营商账号ID")
    private String operatorAccountId;

    @ApiModelProperty("cspId")
    private String cspId;

}
