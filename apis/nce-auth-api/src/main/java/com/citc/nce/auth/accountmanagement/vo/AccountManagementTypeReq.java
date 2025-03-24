package com.citc.nce.auth.accountmanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AccountManagementTypeReq implements Serializable {

    /**
     * 账户类型，1联通2硬核桃
     */
    @ApiModelProperty(value="账户类型，联通、硬核桃")
    private String accountType;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;
    /**
     * 所属供应商  fontdo 蜂动 ;owner csp自己
     */
    @ApiModelProperty("所属供应商")
    @SuppressWarnings("all")
    private String supplier_tag;
    /**
     * 所属供应商  fontdo 蜂动 ;owner csp自己
     */
    @ApiModelProperty("所属供应商")
    private String chatbotAccount;

    @ApiModelProperty("账号名称")
    private String chatbotName;

}
