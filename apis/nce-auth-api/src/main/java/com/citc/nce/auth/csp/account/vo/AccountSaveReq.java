package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * cps 账号
 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class AccountSaveReq implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "csp账户id", dataType = "Long")
    @NotNull(groups = Edit.class)
    private String chatbotCspAccountId;

    @ApiModelProperty(value = "CSP账号", dataType = "String")
    @NotBlank
    @NotNull(groups = Add.class)
    private String cspAccount;

    @ApiModelProperty(value = "CSP密码", dataType = "String")
    @NotBlank
    @NotNull(groups = Add.class)
    private String cspPassword;

    @ApiModelProperty("csp编码")
    @NotBlank
    private String cspCode;

    @ApiModelProperty("代理商客户编码")
    @NotBlank
    @NotNull(groups = Add.class)
    private String agentCustomerNum;

    @ApiModelProperty(value = "代理商信息", dataType = "List")
    private List<AccountAgentInfo> agentInfoList;

    @NotNull(groups = Add.class,message = "运营商类型不能为空")
    private Integer operatorCode;

    private String token;

    private boolean isBoss = false;

    public interface Add{}
    public interface Edit{}
}
