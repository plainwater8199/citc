package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.UserEnterpriseIdentificationInfo;
import com.citc.nce.authcenter.auth.vo.UserPersonIdentificationInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientUserDetailsResp {
    @ApiModelProperty(value = "客户端用户user_id")
    private String userId;

    @ApiModelProperty(value = "客户端用户name")
    private String name;

    @ApiModelProperty(value = "客户端用户图像uuid")
    private String userImgUuid;

    @ApiModelProperty(value = "客户端用户手机号")
    private String phone;

    @ApiModelProperty(value = "客户端用户mail")
    private String mail;

    @ApiModelProperty(value = "客户端用户最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer authStatus;

    @ApiModelProperty(value = "客户端用户违规次数")
    private Integer unruleNum;

    @ApiModelProperty(value = "客户端用户账号资质标签")
    private String flags;

    @ApiModelProperty(value = "客户端用户账号个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer personAuthStatus;

    @ApiModelProperty(value = "客户端用户账号企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer enterpriseAuthStatus;
}
