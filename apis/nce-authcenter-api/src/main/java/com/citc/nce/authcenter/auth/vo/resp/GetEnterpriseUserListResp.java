package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetEnterpriseUserListResp {
    @ApiModelProperty(value = "用户列表")
    private List<UserInfo> userInfos;
}
