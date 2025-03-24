package com.citc.nce.authcenter.systemmsg.vo.resp;

import com.citc.nce.authcenter.auth.vo.UserNameInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ImportUserByCSVResp {
    @ApiModelProperty(value = "用户列表", dataType = "List")
    private List<UserNameInfo> userNameInfos;
}
