package com.citc.nce.authcenter.UserDataSyn.service.impl;

import com.citc.nce.authcenter.UserDataSyn.dao.UserDataSynDao;
import com.citc.nce.authcenter.UserDataSyn.entity.UserDataSynDo;
import com.citc.nce.authcenter.UserDataSyn.service.UserDataSynService;
import com.citc.nce.authcenter.identification.dao.UserCertificateOptionsDao;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.authcenter.identification.dao.UserPersonIdentificationDao;
import com.citc.nce.authcenter.identification.entity.UserCertificateOptionsDo;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.authcenter.identification.entity.UserPersonIdentificationDo;
import com.citc.nce.authcenter.identification.entity.UserPlatformPermissionsDo;
import com.citc.nce.authcenter.user.dao.UserDao;
import com.citc.nce.authcenter.user.dao.UserViolationDao;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.entity.UserViolationDo;
import com.citc.nce.authcenter.userDataSyn.vo.*;
import com.citc.nce.common.util.JsonUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 硬核桃社区用户数据同步功能实现
 */
@Service
@Slf4j
@RefreshScope
public class UserDataSynServiceImpl implements UserDataSynService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserPersonIdentificationDao userPersonIdentificationDao;

    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;

    @Resource
    private UserViolationDao userViolationDao;

    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;

    @Resource
    private UserDataSynDao userDataSynDao;


    @Override
    @Transactional
    public UserDataSynResp userDataSyn(UserDataSynReq req) {
        UserDataSynResp resp = new UserDataSynResp();
        CommunityUserInfo communityUserInfo = obtainCommunityUser(req);
        obtainUserInfo(communityUserInfo, resp);
        return resp;

    }

    private void obtainUserInfo(CommunityUserInfo communityUserInfo, UserDataSynResp resp) {
        if (communityUserInfo != null) {
            if (communityUserInfo.getStatus() != null && communityUserInfo.getStatus() == 200) {
                UserData data = communityUserInfo.getData();
                List<UserDataInfo> userDataInfos = data.getDocs();
                if (!CollectionUtils.isEmpty(userDataInfos)) {
                    List<UserDo> userDos = new ArrayList<>();
                    List<UserPlatformPermissionsDo> userPlatformPermissionsDos = new ArrayList<>();
                    List<UserPersonIdentificationDo> userPersonIdentificationDos = new ArrayList<>();
                    List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDos = new ArrayList<>();
                    List<UserViolationDo> userViolationDos = new ArrayList<>();
                    List<UserCertificateOptionsDo> userCertificateOptionsDos = new ArrayList<>();
                    UserCertificateOptionsDo certificateOptionsDo;
                    UserPlatformPermissionsDo userPlatformPermissionsDo;
                    UserViolationDo violationDo;
                    UserEnterpriseIdentificationDo userEnterpriseIdentificationDo;
                    UserPersonIdentificationDo userPersonIdentificationDo;
                    UserDo userDo;
                    /**
                     * 2023-5-16
                     * 新增用户数据同步信息
                     */
                    List<UserDataSynDo> userDataSynDos = new ArrayList<>();
                    UserDataSynDo userDataSynDo;

                    List<String> saveUsers = new ArrayList<>();
                    List<String> unSaveUsers = new ArrayList<>();
                    for (UserDataInfo item : userDataInfos) {
                        if (Strings.isNullOrEmpty(item.getPhoneNum())) {
                            System.out.println("--------未同步用户-------：" + item.getPhoneNum() + "---" + item.getUserName());
                            unSaveUsers.add(item.getPhoneNum() + "---" + item.getUserName());
                            continue;
                        }

                        //用户信息
                        userDo = new UserDo();
                        String userId = item.getUserCode() + "00";
                        System.out.println("-------------------------------------userId---------------------------------:" + userId);
                        CertificationInfo certificationInfo = item.getCertification();
                        EnterpriseInfo enterprise = certificationInfo.getEnterprise();
                        ApplierInfo applier = certificationInfo.getApplier();
                        userDo.setUserId(userId);
                        userDo.setName(item.getUserName());
                        userDo.setUserImgUuid(getPictureId(item.getLogo(), userId + "-LOGO.jpg"));
                        userDo.setPhone(item.getPhoneNum());
                        userDo.setMail(item.getEmail());
                        userDo.setEmailActivated(0);
                        userDo.setUserType("个人账号".equals(item.getUserType()) ? 0 : 1);
                        userDo.setPersonAuthStatus(obtainAuthStatus(certificationInfo.getApplierState()));
                        userDo.setEnterpriseAuthStatus(obtainAuthStatus(certificationInfo.getEnterpriseState()));
                        userDo.setAuthStatus(obtainAuthStatus(certificationInfo.getApplierState()));
                        userDo.setCreator(userId);
                        userDo.setCreateTime(getTime(item.getDate()));
                        userDo.setUpdater(userId);
                        userDo.setDeleted("1".equals(item.getState()) ? 0 : 1);
                        userDo.setUnruleNum(0);
                        userDo.setSpLogo("");
                        userDo.setSpTel("");
                        userDo.setSpEmail("");
                        userDos.add(userDo);

                        /**
                         * 2023-5-16
                         * 新增用户数据同步信息
                         */
                        userDataSynDo = new UserDataSynDo();
                        userDataSynDo.setUserId(userId);
                        userDataSynDo.setCreator(userId);
                        userDataSynDo.setCreateTime(userDo.getCreateTime());
                        userDataSynDo.setUpdater(userId);
                        userDataSynDo.setDeleted(userDo.getDeleted());
                        userDataSynDos.add(userDataSynDo);


                        //权限
                        userPlatformPermissionsDo = new UserPlatformPermissionsDo();
                        userPlatformPermissionsDo.setUserId(userId);
                        userPlatformPermissionsDo.setUserStatus(0);
                        userPlatformPermissionsDo.setApplyTime(new Date());
                        userPlatformPermissionsDo.setApprovalStatus(0);
                        userPlatformPermissionsDo.setProtal(1);
                        userPlatformPermissionsDo.setApprovalLogId("-1");
                        userPlatformPermissionsDo.setCreator("0000000000");
                        userPlatformPermissionsDo.setCreateTime(new Date());
                        userPlatformPermissionsDo.setUpdater("0000000000");
                        userPlatformPermissionsDo.setUpdateTime(new Date());
                        userPlatformPermissionsDo.setDeleted(0);
                        userPlatformPermissionsDo.setDeletedTime((new Date()).getTime());
                        userPlatformPermissionsDos.add(userPlatformPermissionsDo);

                        userPlatformPermissionsDo = new UserPlatformPermissionsDo();
                        userPlatformPermissionsDo.setUserId(userId);
                        userPlatformPermissionsDo.setUserStatus(item.getEnable() ? 1 : 2);
                        userPlatformPermissionsDo.setApplyTime(getTime(item.getDate()));
                        userPlatformPermissionsDo.setApprovalStatus(3);
                        userPlatformPermissionsDo.setProtal(2);
                        userPlatformPermissionsDo.setApprovalLogId("-1");
                        userPlatformPermissionsDo.setCreator("0000000000");
                        userPlatformPermissionsDo.setCreateTime(getTime(item.getDate()));
                        userPlatformPermissionsDo.setUpdater("0000000000");
                        userPlatformPermissionsDo.setUpdateTime(getTime(item.getDate()));
                        userPlatformPermissionsDo.setDeleted(0);
                        userPlatformPermissionsDo.setDeletedTime((new Date()).getTime());
                        userPlatformPermissionsDos.add(userPlatformPermissionsDo);


                        userPlatformPermissionsDo = new UserPlatformPermissionsDo();
                        userPlatformPermissionsDo.setUserId(userId);
                        userPlatformPermissionsDo.setUserStatus(0);
                        userPlatformPermissionsDo.setApplyTime(new Date());
                        userPlatformPermissionsDo.setApprovalStatus(0);
                        userPlatformPermissionsDo.setProtal(3);
                        userPlatformPermissionsDo.setApprovalLogId("-1");
                        userPlatformPermissionsDo.setCreator("0000000000");
                        userPlatformPermissionsDo.setCreateTime(new Date());
                        userPlatformPermissionsDo.setUpdater("0000000000");
                        userPlatformPermissionsDo.setUpdateTime(new Date());
                        userPlatformPermissionsDo.setDeleted(0);
                        userPlatformPermissionsDo.setDeletedTime((new Date()).getTime());
                        userPlatformPermissionsDos.add(userPlatformPermissionsDo);

                        if (applier != null) {
                            //个人认证信息
                            userPersonIdentificationDo = new UserPersonIdentificationDo();
                            userPersonIdentificationDo.setUserId(userId);
                            userPersonIdentificationDo.setPersonAuthStatus(obtainAuthStatus(certificationInfo.getApplierState()));
                            userPersonIdentificationDo.setPersonName(applier.getName());
                            userPersonIdentificationDo.setIdCard(applier.getCardNo());
                            userPersonIdentificationDo.setIdCardImgFront(getPictureId(applier.getCardNoFrontPhoto(), userId + "-IDF.jpg"));
                            userPersonIdentificationDo.setIdCardImgBack(getPictureId(applier.getCardNoBackPhoto(), userId + "-IDB.jpg"));
                            userPersonIdentificationDo.setPersonAuthTime(getTime(certificationInfo.getApplierCreateDate()));
                            userPersonIdentificationDo.setPersonAuthAuditTime(getTime(certificationInfo.getApplierVerifiedDate()));
                            userPersonIdentificationDo.setCreator(userId);
                            userPersonIdentificationDo.setCreateTime(getTime(certificationInfo.getApplierCreateDate()));
                            userPersonIdentificationDo.setUpdater("0000000000");
                            userPersonIdentificationDo.setDeleted(0);
                            userPersonIdentificationDo.setDeletedTime((new Date()).getTime());
                            userPersonIdentificationDo.setAuditRemark(certificationInfo.getApplierReason());
                            userPersonIdentificationDo.setProtal(2);
                            userPersonIdentificationDos.add(userPersonIdentificationDo);

                            if (certificationInfo.getApplierState() != null && !"3".equals(certificationInfo.getApplierState())) {
                                certificateOptionsDo = new UserCertificateOptionsDo();
                                certificateOptionsDo.setCertificateId(10002);
                                certificateOptionsDo.setUserId(userId);
                                certificateOptionsDo.setCertificateApplyStatus(obtainAuthStatus(certificationInfo.getApplierState()));
                                certificateOptionsDo.setApplyTime(getTime(certificationInfo.getApplierCreateDate()));
                                certificateOptionsDo.setApprovalTime(getTime(certificationInfo.getApplierVerifiedDate()));
                                certificateOptionsDo.setCertificateStatus("1".equals(certificationInfo.getApplierState()) ? 0 : 2);
                                certificateOptionsDo.setCreator(userId);
                                certificateOptionsDo.setCreateTime(getTime(certificationInfo.getApplierCreateDate()));
                                certificateOptionsDo.setDeleted(0);
                                certificateOptionsDo.setDeletedTime((new Date()).getTime());
                                userCertificateOptionsDos.add(certificateOptionsDo);
                            }
                        }

                        if (enterprise != null) {
                            //企业认证
                            userEnterpriseIdentificationDo = new UserEnterpriseIdentificationDo();
                            userEnterpriseIdentificationDo.setUserId(userId);
                            userEnterpriseIdentificationDo.setEnterpriseAuthStatus(obtainAuthStatus(certificationInfo.getEnterpriseState()));
                            userEnterpriseIdentificationDo.setEnterpriseAccountName(enterprise.getOrgName());
                            userEnterpriseIdentificationDo.setEnterpriseName(enterprise.getOrgName());
                            userEnterpriseIdentificationDo.setEnterpriseLicense(getPictureId(enterprise.getCertificate(), userId + "-ec.jpg"));
                            userEnterpriseIdentificationDo.setCreditCode(enterprise.getOrgCode());
                            userEnterpriseIdentificationDo.setEnterpriseAuthTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            userEnterpriseIdentificationDo.setEnterpriseAuthAuditTime(getTime(certificationInfo.getEnterpriseVerifiedDate()));
                            userEnterpriseIdentificationDo.setAddress(enterprise.getAddress());
                            userEnterpriseIdentificationDo.setProvince(enterprise.getProvince());
                            userEnterpriseIdentificationDo.setCity(enterprise.getCity());
                            userEnterpriseIdentificationDo.setArea(enterprise.getArea());
                            userEnterpriseIdentificationDo.setCreator(userId);
                            userEnterpriseIdentificationDo.setCreateTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            userEnterpriseIdentificationDo.setUpdater("0000000000");
                            userEnterpriseIdentificationDo.setUpdateTime(getTime(certificationInfo.getApplierVerifiedDate()));
                            userEnterpriseIdentificationDo.setDeleted(0);
                            userEnterpriseIdentificationDo.setDeletedTime((new Date()).getTime());
                            userEnterpriseIdentificationDo.setAuditRemark(certificationInfo.getEnterpriseReason());
                            userEnterpriseIdentificationDo.setProtal(2);
                            userEnterpriseIdentificationDo.setCspActive(0);
                            userEnterpriseIdentificationDos.add(userEnterpriseIdentificationDo);


                            if (certificationInfo.getEnterpriseState() != null && !"3".equals(certificationInfo.getEnterpriseState())) {
                                certificateOptionsDo = new UserCertificateOptionsDo();
                                certificateOptionsDo.setCertificateId(10001);
                                certificateOptionsDo.setUserId(userId);
                                certificateOptionsDo.setCertificateApplyStatus(obtainAuthStatus(certificationInfo.getEnterpriseState()));
                                certificateOptionsDo.setApplyTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                                certificateOptionsDo.setApprovalTime(getTime(certificationInfo.getEnterpriseVerifiedDate()));
                                certificateOptionsDo.setCertificateStatus("1".equals(certificationInfo.getApplierState()) ? 0 : 2);
                                certificateOptionsDo.setCreator(userId);
                                certificateOptionsDo.setCreateTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                                certificateOptionsDo.setDeleted(0);
                                certificateOptionsDo.setDeletedTime((new Date()).getTime());
                                userCertificateOptionsDos.add(certificateOptionsDo);
                            }
                        }


                        //违规
                        Integer ruleTimes = item.getBreakRuleTimes();
                        if (ruleTimes != null && ruleTimes > 0) {
                            violationDo = new UserViolationDo();
                            violationDo.setUserId(userId);
                            violationDo.setPlate(2);
                            violationDo.setViolationType(0);
                            violationDo.setCreator("0000000000");
                            violationDo.setCreateTime(new Date());
                            violationDo.setDeleted(0);
                            userViolationDos.add(violationDo);
                        }

                        //入驻
                        String isSettled = item.getIsSettled();
                        if (!Strings.isNullOrEmpty(isSettled) && "0".equals(isSettled)) {
                            certificateOptionsDo = new UserCertificateOptionsDo();
                            certificateOptionsDo.setCertificateId(10003);
                            certificateOptionsDo.setUserId(userId);
                            certificateOptionsDo.setCertificateApplyStatus(3);
                            certificateOptionsDo.setApplyTime(new Date());
                            certificateOptionsDo.setApprovalTime(new Date());
                            certificateOptionsDo.setCertificateStatus(0);
                            certificateOptionsDo.setCreator(userId);
                            certificateOptionsDo.setCreateTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setDeleted(0);
                            certificateOptionsDo.setDeletedTime((new Date()).getTime());
                            userCertificateOptionsDos.add(certificateOptionsDo);
                        }


                        //GSMA
                        String isGsma = item.getIsGsma();
                        if (!Strings.isNullOrEmpty(isGsma) && "1".equals(isGsma)) {
                            certificateOptionsDo = new UserCertificateOptionsDo();
                            certificateOptionsDo.setCertificateId(10007);
                            certificateOptionsDo.setUserId(userId);
                            certificateOptionsDo.setCertificateApplyStatus(3);
                            certificateOptionsDo.setApplyTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setApprovalTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setCertificateStatus(0);
                            certificateOptionsDo.setCreator(userId);
                            certificateOptionsDo.setCreateTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setDeleted(0);
                            certificateOptionsDo.setDeletedTime((new Date()).getTime());
                            userCertificateOptionsDos.add(certificateOptionsDo);
                        }


                        //5G
                        String is5G = item.getIs5G();
                        if (!Strings.isNullOrEmpty(is5G) && "1".equals(is5G)) {
                            certificateOptionsDo = new UserCertificateOptionsDo();
                            certificateOptionsDo.setCertificateId(10008);
                            certificateOptionsDo.setUserId(userId);
                            certificateOptionsDo.setCertificateApplyStatus(3);
                            certificateOptionsDo.setApplyTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setApprovalTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setCertificateStatus(0);
                            certificateOptionsDo.setCreator(userId);
                            certificateOptionsDo.setCreateTime(getTime(certificationInfo.getEnterpriseCreateDate()));
                            certificateOptionsDo.setDeleted(0);
                            certificateOptionsDo.setDeletedTime((new Date()).getTime());
                            userCertificateOptionsDos.add(certificateOptionsDo);
                        }
                        saveUsers.add(item.getPhoneNum() + "+++" + item.getUserName());

                    }
                    if (resp != null) {
                        resp.setSaveDBUsers(saveUsers);
                        resp.setUnSaveDBUsers(unSaveUsers);
                    }
//                    if(saveDB != null && saveDB){
                    log.info("-------------------------------------------------save-DB---------------------------------------------");
                    if (!CollectionUtils.isEmpty(userDos)) {
                        userDao.insertBatch(userDos);
//                            userPlatformPermissionsDao.insertBatch(userPlatformPermissionsDos);
                    }
                    /**
                     * 2023-5-16
                     * 新增用户数据同步信息
                     */
                    if (!CollectionUtils.isEmpty(userDataSynDos)) {
                        userDataSynDao.insertBatch(userDataSynDos);
                    }

                    if (!CollectionUtils.isEmpty(userPersonIdentificationDos)) {
                        userPersonIdentificationDao.insertBatch(userPersonIdentificationDos);
                    }
                    if (!CollectionUtils.isEmpty(userEnterpriseIdentificationDos)) {
                        userEnterpriseIdentificationDao.insertBatch(userEnterpriseIdentificationDos);
                    }
                    if (!CollectionUtils.isEmpty(userViolationDos)) {
                        userViolationDao.insertBatch(userViolationDos);
                    }
                    if (!CollectionUtils.isEmpty(userCertificateOptionsDos)) {
                        userCertificateOptionsDao.insertBatch(userCertificateOptionsDos);
//                        }
                    }


                    System.out.println("--------------同步结果-------user----------------:" + userDos.size());
                    System.out.println("--------------同步结果-------userPlatformPermissions----------------:" + userPlatformPermissionsDos.size());
                    System.out.println("--------------同步结果-------userPersonIdentification----------------:" + userPersonIdentificationDos.size());
                    System.out.println("--------------同步结果-------userEnterpriseIdentification----------------:" + userEnterpriseIdentificationDos.size());
                    System.out.println("--------------同步结果-------userViolation----------------:" + userViolationDos.size());
                    System.out.println("--------------同步结果-------userCertificateOptions----------------:" + userCertificateOptionsDos.size());
                }
            }
        }
    }


    private String getPictureId(String pictureUrl, String fileName) {
        return null;
    }

    private Date getTime(String date) {
        if (Strings.isNullOrEmpty(date)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    private Integer obtainAuthStatus(String communityStatus) {
        if ("3".equals(communityStatus)) {
            return 0;//未认证
        } else if ("0".equals(communityStatus)) {
            return 1;//认证审核中
        } else if ("2".equals(communityStatus)) {
            return 2;//认证不通过
        } else if ("1".equals(communityStatus)) {
            return 3;//认证通过
        } else {
            return 0;
        }
    }


    /**
     * 获取社区用户列表
     * @return 用户信息
     */
    private CommunityUserInfo obtainCommunityUser(UserDataSynReq req) {
        CommunityUserInfo communityUserInfo = new CommunityUserInfo();
        MultipartFile multipartFile = req.getFile();
        try {
            if (multipartFile != null) {
                InputStream bb = multipartFile.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(bb, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                communityUserInfo = JsonUtils.string2Obj(stringBuilder.toString(), CommunityUserInfo.class);
                reader.close();
                bb.close();
            } else {
                log.info("空的！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return communityUserInfo;
    }


}
