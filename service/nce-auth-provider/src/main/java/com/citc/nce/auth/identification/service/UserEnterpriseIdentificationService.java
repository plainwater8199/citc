package com.citc.nce.auth.identification.service;

import com.citc.nce.auth.adminUser.vo.resp.UserEnterpriseIdentificationResp;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.GetEnterpriseInfoByUserIdsReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.identification.vo.resp.UserProvinceResp;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;

import java.util.List;
import java.util.Map;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:57
 * @description:
 */
public interface UserEnterpriseIdentificationService {
    /**
     * 企业实名认证申请
     *
     * @param enterpriseIdentificationReq
     */
    void enterpriseIdentificationApply(EnterpriseIdentificationReq enterpriseIdentificationReq);


    /**
     * 查看企业认证信息
     *
     * @param usedId
     * @return
     */
    WebEnterpriseIdentificationResp getIdentificationInfo(String usedId);

    WebEnterpriseIdentificationResp getEnterpriseIdentificationByUserId(String usedId);
    WebEnterpriseIdentificationResp getEnterpriseIdentificationByEnterpriseId(Long id);

    /**
     * check 企业名是否重复
     *
     * @param enterpriseAccountName
     * @return
     */
    Boolean checkEnterpriseAccountNameUnique(String enterpriseAccountName);

    /**
     * 企业使用权限申请
     * wll
     */
    void applyPlatformPermission(IdentificationPlatformPermissionReq req);

    /**
     * 获取用户的企业信息
     *
     * @param req
     * @return
     */
    Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(GetEnterpriseInfoByUserIdsReq req);

    List<UserEnterpriseIdentificationResp> getEnterpriseInfoByIds(List<String> ids);

    Map<String, String> getEnterpriseIdentificationInfoByUserIds(List<String> customerIds);

    /**
     * 查询用户分布省份
     * @return
     */
//    List<UserProvinceResp> queryUserProvince();
}
