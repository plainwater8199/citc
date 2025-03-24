package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class AccountAgentInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("代理商名称")
    @Length(max = 64, message = "代理商名称长度超过限制(最大64位)")
    private String agentName;

    @ApiModelProperty("代理商编码")
    @Length(max = 30, message = "代理商编码长度超过限制(最大30位)")
    private String agentCode;

    @ApiModelProperty("服务代码")
    @Length(max = 200, message = "服务代码长度超过限制(最大200位)")
    private String serviceCode;

    @ApiModelProperty("运营商账号ID")
    private String operatorAccountId;

}
