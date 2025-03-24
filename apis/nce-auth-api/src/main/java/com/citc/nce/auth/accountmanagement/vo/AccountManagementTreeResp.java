package com.citc.nce.auth.accountmanagement.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class AccountManagementTreeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "账号名称")
    private String accountName;

    /**
     * 账号id
     */
    @ApiModelProperty(value = "账号id")
    private String chatbotAccount;

    /**
     * 账户类型，1联通2硬核桃
     */
    @ApiModelProperty(value = "账户类型，联通、硬核桃")
    private String accountType;

    @ApiModelProperty(value = "账号ID(唯一标识)")
    private String chatbotAccountId;

    private String supplierTag;

    @ApiModelProperty(value = "机器人状态")
    private Integer chatbotStatus;

    // 用来判断通道可用性 不返回
    @JsonIgnore
    private Integer isAddOther;
    @JsonIgnore
    private Integer state;
    @JsonIgnore
    private Integer actualState;


}
