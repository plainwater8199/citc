package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月18日16:15:46
 * @Version: 1.0
 * @Description: BaseResp
 */
@Data
public class AccountResp implements Serializable {
    @ApiModelProperty(value = "账号Id",example = "1")
    private String accountId;

    /**
     * 审核状态  1：通过,2：不通过,3:待审核,4:无状态
     */
    @ApiModelProperty(value = "审核状态",example = "0")
    private Integer status;

    @ApiModelProperty(value = "账号名称",example = "中讯1")
    private String chatbotName;

    @ApiModelProperty(value = "运营商",example = "联通")
    private String operator;

    @ApiModelProperty(value = "chatbotAccount",example = "联通")
    private String chatbotAccount;

    @ApiModelProperty(value = "supplierTag")
    private String supplierTag;

    @ApiModelProperty(value = "机器人状态")
    private Integer chatbotStatus;

}
