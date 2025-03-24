package com.citc.nce.authcenter.csp.customer.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Query5GAccountListResp {
    @ApiModelProperty("账号列表")
    private List<AccountItem> accountItems;
    @ApiModelProperty("映射关系")
    private Map<String,Map<String,String>> accountMap;
}
