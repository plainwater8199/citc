package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.UserCertificateItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class GetClientUserCertificateResp {
    @ApiModelProperty(value = "用户认证信息列表")
    private List<UserCertificateItem> userCertificateItems;
}
