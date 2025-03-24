package com.citc.nce.authcenter.identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;

/**
 * @author jiancheng
 */
public interface UserEnterPriseIdentificationService extends IService<UserEnterpriseIdentificationDo> {

    /**
     * 校验同一CSP下企业名称是否唯一
     *
     * @return 唯一返回true
     */
    Boolean isUniqueEnterpriseAccountName(String cspId, String enterpriseAccountName);



    /**
     * 企业实名认证申请
     *
     * @param enterpriseIdentificationReq
     */
    void identificationApply(EnterpriseIdentificationReq enterpriseIdentificationReq);

}
