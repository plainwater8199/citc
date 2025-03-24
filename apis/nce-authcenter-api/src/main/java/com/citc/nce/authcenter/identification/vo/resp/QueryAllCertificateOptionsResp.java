package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.UserCertificateItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryAllCertificateOptionsResp {
    @ApiModelProperty(value = "用户的认证信息")
    private List<UserCertificateItem> userCertificateItems;
}
