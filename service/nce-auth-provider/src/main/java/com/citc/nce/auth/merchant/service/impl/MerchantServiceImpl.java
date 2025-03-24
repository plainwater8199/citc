package com.citc.nce.auth.merchant.service.impl;


import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.certificate.entity.UserCertificateDo;
import com.citc.nce.auth.certificate.service.CertificateOptionsService;
import com.citc.nce.auth.certificate.service.UserCertificateService;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.service.UserEnterpriseIdentificationService;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.auth.merchant.service.MerchantService;
import com.citc.nce.auth.merchant.vo.req.QueryMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.req.UpdateMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.resp.MerchantInfo;
import com.citc.nce.auth.merchant.vo.resp.QueryMerchantInfoResp;
import com.citc.nce.auth.merchant.vo.resp.UpdateMerchantInfoResp;
import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.user.vo.CertificateInfo;
import com.citc.nce.common.core.exception.BizException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description 商户中心-实现
 * @date 2022/8/27 09:24:42
 */
@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Resource
    private UserService userService;
    @Resource
    private UserCertificateService userCertificateService;
    @Resource
    private CertificateOptionsService certificateOptionsService;
    @Resource
    private UserEnterpriseIdentificationService userEnterpriseIdentificationService;

    @Override
    public QueryMerchantInfoResp queryMerchantInfo(QueryMerchantInfoReq req) {
        QueryMerchantInfoResp resp = new QueryMerchantInfoResp();
        String userId = req.getUserId();
        if (!Strings.isNullOrEmpty(userId)) {
            //获取商户的基本信息
            MerchantInfo merchantInfo = getMerchantInfo(userId);

            resp.setMerchantInfo(merchantInfo);
        } else {
            throw BizException.build(AuthError.ACCOUNT_USER_ABSENT);
        }
        return resp;
    }

    /**
     * 根据商户UserID查询商户的基本信息
     *
     * @param userId 商户userId
     * @return 商户基本信息
     */
    private MerchantInfo getMerchantInfo(String userId) {
        MerchantInfo merchantInfo = new MerchantInfo();
        if (!Strings.isNullOrEmpty(userId)) {
            //1、查询商户的基本信息
            UserDo userDo = userService.userInfoDetailByUserId(userId);
            WebEnterpriseIdentificationResp userEnterpriseIdentificationInfo = userEnterpriseIdentificationService.getIdentificationInfo(userId);
            //2、查询商户的资历信息
            List<CertificateOptionsDo> userCertificateOptionsDoList = certificateOptionsService.queryUserCertificateByUserId(userId);
            List<CertificateInfo> certificateInfoList = new ArrayList<>();
            if (!userCertificateOptionsDoList.isEmpty()) {
                //3、查询认证名称
                List<UserCertificateDo> userCertificateDoList = userCertificateService.getUserCertificate();
                Map<Long, String> certificateNameMap = userCertificateDoList.stream().collect(Collectors.toMap(UserCertificateDo::getId, UserCertificateDo::getCertificateName));
                CertificateInfo certificateInfo;
                for (CertificateOptionsDo item : userCertificateOptionsDoList) {
                    certificateInfo = new CertificateInfo();
                    certificateInfo.setCertificateId(item.getCertificateId());
                    certificateInfo.setCertificateName(certificateNameMap.get(Long.valueOf(item.getCertificateId() + "")));//优化
                    certificateInfo.setCertificateApplyStatus(item.getCertificateApplyStatus());
                    certificateInfoList.add(certificateInfo);
                }
            }
            if (userDo != null) {
                merchantInfo.setName(userEnterpriseIdentificationInfo.getEnterpriseName());
                merchantInfo.setSpTel(userDo.getSpTel());
                merchantInfo.setSpEmail(userDo.getSpEmail());
                merchantInfo.setSpLogo(userDo.getSpLogo());
                merchantInfo.setEnterpriseAuthStatus(userDo.getEnterpriseAuthStatus());
            }
            merchantInfo.setUserId(userId);
            merchantInfo.setCertificateInfoList(certificateInfoList);
        }
        return merchantInfo;

    }

    @Override
    public UpdateMerchantInfoResp updateMerchantInfo(UpdateMerchantInfoReq req) {
        UpdateMerchantInfoResp resp = new UpdateMerchantInfoResp();
        String userId = req.getUserId();
        String spTel = req.getSpTel();
        String spEmail = req.getSpEmail();
        String spLogo = req.getSpLogo();
        if (!Strings.isNullOrEmpty(userId)) {
            if (!Strings.isNullOrEmpty(spTel) && !Strings.isNullOrEmpty(spEmail)) {
                UserDo userDo = new UserDo();
                userDo.setSpTel(spTel);
                userDo.setSpEmail(spEmail);
                userDo.setSpLogo(spLogo);
                //更新用户基本信息
                userService.updateUserInfo(userId, userDo);
                MerchantInfo merchantInfo = getMerchantInfo(userId);
                resp.setMerchantInfo(merchantInfo);
            } else {
                throw BizException.build(AuthError.MERCHANT_INFO_IS_NULL);
            }
        } else {
            throw BizException.build(AuthError.ACCOUNT_USER_ABSENT);
        }
        return resp;
    }
}
