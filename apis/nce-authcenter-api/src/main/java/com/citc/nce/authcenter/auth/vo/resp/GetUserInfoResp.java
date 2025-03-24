package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.UserIdAndNameInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetUserInfoResp {
    @ApiModelProperty(value = "用户基本信息", dataType = "List")
    private List<UserIdAndNameInfo> userIdAndNameInfos;
}
