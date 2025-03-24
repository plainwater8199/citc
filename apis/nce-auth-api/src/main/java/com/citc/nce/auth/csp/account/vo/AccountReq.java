package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class AccountReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("csp_account_id")
    private String chatbotCspAccountId;
}
