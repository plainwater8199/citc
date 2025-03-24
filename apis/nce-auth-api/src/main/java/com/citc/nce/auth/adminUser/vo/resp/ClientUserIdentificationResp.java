package com.citc.nce.auth.adminUser.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Admin端 查看client端 userId 用户认证信息resp
 */
@Data
@Accessors(chain = true)
public class ClientUserIdentificationResp {
    //个人认证
    UserPersonIdentificationResp personIdentificationDo;
    //企业认证
    UserEnterpriseIdentificationResp enterpriseIdentificatio;

}
