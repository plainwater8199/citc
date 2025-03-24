package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.UserEnterpriseIdentificationItem;
import com.citc.nce.authcenter.identification.vo.UserPersonIdentificationItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetClientUserIdentificationResp {
    //个人认证
    @ApiModelProperty(value = "个人认证", dataType = "Object")
    private UserPersonIdentificationItem personIdentificationDo;
    //企业认证
    @ApiModelProperty(value = "企业认证", dataType = "Object")
    private UserEnterpriseIdentificationItem enterpriseIdentificatio;
}
