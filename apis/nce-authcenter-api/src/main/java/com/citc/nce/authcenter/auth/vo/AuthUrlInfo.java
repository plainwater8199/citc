package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class AuthUrlInfo {
    @ApiModelProperty(value = "权限地址列表", dataType = "Object")
    private Map<String, List<AuthUrlItem>> authUrlItemMap;
}
