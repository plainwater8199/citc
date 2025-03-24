package com.citc.nce.auth.certificate.service;

import com.citc.nce.auth.certificate.entity.UserCertificateDo;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.ticket.vo.req.GetCertificateOptionsReq;
import com.citc.nce.auth.user.vo.resp.ListResp;

import java.util.List;

public interface UserCertificateService {

    List<UserCertificateResp> getCertificateOptions(String userId);


    List<UserCertificateDo> getUserCertificate();

    List<UserCertificateResp> getCertificateOptionsAll(String userId);

    ListResp getUserCertificateByUserId(GetCertificateOptionsReq req);

    /**
     * 用户标签申请
     */
    Integer getRemark();

}
