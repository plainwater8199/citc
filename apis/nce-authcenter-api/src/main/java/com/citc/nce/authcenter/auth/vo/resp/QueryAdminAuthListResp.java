package com.citc.nce.authcenter.auth.vo.resp;


import com.citc.nce.authcenter.auth.vo.AuthUrlInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryAdminAuthListResp {
    @ApiModelProperty(value = "权限地址列表", dataType = "Object")
    private AuthUrlInfo authUrl;
}
