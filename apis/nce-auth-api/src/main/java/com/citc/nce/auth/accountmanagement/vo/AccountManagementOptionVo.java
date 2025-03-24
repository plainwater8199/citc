package com.citc.nce.auth.accountmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiancheng
 */
@ApiModel("机器人账号vo")
@Data
public class AccountManagementOptionVo {
    @ApiModelProperty("账户类型")
    private String id;
    @ApiModelProperty("账户类型")
    private String accountName;
    @ApiModelProperty("该类型下账号列表")
    private List<AccountManagementTreeResp> options;
}
