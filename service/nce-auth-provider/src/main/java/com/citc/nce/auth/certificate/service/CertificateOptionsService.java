package com.citc.nce.auth.certificate.service;

import com.citc.nce.auth.adminUser.vo.req.OnOrOffClientUserCertificateReq;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.certificate.vo.req.UserTagLogByCertificateOptionsIdReq;
import com.citc.nce.auth.certificate.vo.resp.UserTagLogResp;
import com.citc.nce.auth.constant.CountNum;

import java.util.List;


/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/14
 * @Version 1.0
 * @Description:
 */
public interface CertificateOptionsService {


    CountNum getPendingReviewNum();

    void onOrOffClientUserCertificate(OnOrOffClientUserCertificateReq req);

    List<UserTagLogResp> getUserTagLogByCertificateOptionsId(UserTagLogByCertificateOptionsIdReq req);

    /**
     * 更具userId查询用户的资历信息
     *
     * @param userId 用户信息
     * @return 资历信息
     */
    List<CertificateOptionsDo> queryUserCertificateByUserId(String userId);

}
