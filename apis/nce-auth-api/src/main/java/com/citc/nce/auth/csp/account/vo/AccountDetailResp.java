package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class AccountDetailResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "CSPId", dataType = "Integer")
    private Long id;

    @ApiModelProperty(value = "AppId", dataType = "Integer")
    private String appId;

    @ApiModelProperty(value = "CSP账号", dataType = "String")
    private String cspAccount;

    @ApiModelProperty(value = "CSP密码", dataType = "String")
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

    private Integer operatorCode;

    private String token;

    @ApiModelProperty(value = "代理商信息", dataType = "List")
    private List<AccountAgentInfo> agentInfoList;
}
