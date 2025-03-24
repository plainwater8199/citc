package com.citc.nce.auth.identification;

import com.citc.nce.auth.adminUser.vo.resp.UserEnterpriseIdentificationResp;
import com.citc.nce.auth.identification.service.IdentificationAuditRecordService;
import com.citc.nce.auth.identification.service.UserEnterpriseIdentificationService;
import com.citc.nce.auth.identification.service.UserPersonIdentificationService;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.GetEnterpriseInfoByUserIdsReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.auth.identification.vo.resp.*;
import com.citc.nce.common.util.SessionContextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/14 16:47
 * @Version: 1.0
 * @Description: 用户认证
 */
@RestController
public class IdentificationController implements IdentificationApi {

    @Resource
    private UserPersonIdentificationService userPersonIdentificationService;
    @Resource
    private UserEnterpriseIdentificationService userEnterpriseIdentificationService;
    @Resource
    private IdentificationAuditRecordService identificationAuditRecordService;

    @Override
    @PostMapping("/user/identification/personIdentificationApply")
    @Transactional
    public void personIdentificationApply(@Valid @RequestBody PersonIdentificationReq personIdentificationReq) {
        userPersonIdentificationService.personIdentificationApply(personIdentificationReq);
    }

    @Override
    @PostMapping("/user/identification/enterpriseIdentificationApply")
    public void enterpriseIdentificationApply(@Valid @RequestBody EnterpriseIdentificationReq enterpriseIdentificationReq) {
        userEnterpriseIdentificationService.enterpriseIdentificationApply(enterpriseIdentificationReq);
    }

    @Override
    @PostMapping("/user/identification/getEnterpriseIdentificationInfo")
    public WebEnterpriseIdentificationResp getEnterpriseIdentificationInfo() {
        return userEnterpriseIdentificationService.getIdentificationInfo(SessionContextUtil.getUser().getUserId());
    }

    @Override
    @PostMapping("/user/identification/getIdentificationForBoss")
    public BossIdentificationResp getIdentificationForBoss(String userId) {
        BossIdentificationResp resp = new BossIdentificationResp();

        WebPersonIdentificationResp personIdentificationResp = userPersonIdentificationService.getPersonIdentification(userId);
        WebEnterpriseIdentificationResp identificationInfoResp = userEnterpriseIdentificationService.getIdentificationInfo(userId);
        BossIdentificationResp.PersonIdentification personIdentification = new BossIdentificationResp.PersonIdentification();
        BeanUtils.copyProperties(personIdentificationResp, personIdentification);
        if (Objects.nonNull(personIdentificationResp)) {
            List<IdentificationAuditResp> identificationAudits = identificationAuditRecordService.getIdentificationAudits(personIdentificationResp.getId());
            personIdentification.setAuditRemarkList(identificationAudits);
        }
        resp.setPersonIdentification(personIdentification);
        BossIdentificationResp.EnterpriseIdentification enterpriseIdentification = new BossIdentificationResp.EnterpriseIdentification();
        BeanUtils.copyProperties(identificationInfoResp, enterpriseIdentification);
        if (Objects.nonNull(identificationInfoResp)) {
            List<IdentificationAuditResp> identificationAudits = identificationAuditRecordService.getIdentificationAudits(identificationInfoResp.getId());
            enterpriseIdentification.setAuditRemarkList(identificationAudits);
        }
        resp.setEnterpriseIdentification(enterpriseIdentification);
        return resp;
    }

    @Override
    @GetMapping("/user/identification/getPersonIdentification")
    public WebPersonIdentificationResp getPersonIdentification() {
        return userPersonIdentificationService.getPersonIdentification(SessionContextUtil.getUser().getUserId());
    }

    @Override
    @GetMapping("/user/identification/getPersonIdentificationByUserId/{userId}")
    public WebPersonIdentificationResp getPersonIdentificationByUserId(@PathVariable("userId") String userId) {
        return userPersonIdentificationService.getPersonIdentification(userId);
    }

    @Override
    @GetMapping("/user/identification/getEnterpriseIdentificationInfoByUserId/{userId}")
    public WebEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(@PathVariable("userId") String userId) {
        return userEnterpriseIdentificationService.getIdentificationInfo(userId);
    }


    /**
     * 企业账号名是否唯一校验
     *
     * @param enterpriseAccountName
     * @return true 唯一  false 不唯一
     */
    @Override
    @GetMapping("/user/identification/checkEnterpriseAccountNameUnique")
    public Boolean checkEnterpriseAccountNameUnique(@RequestParam("enterpriseAccountName") String enterpriseAccountName) {
        return userEnterpriseIdentificationService.checkEnterpriseAccountNameUnique(enterpriseAccountName);
    }

    /**
     * 平台使用权限申请
     * wll
     */
    @Override
    @PostMapping("/user/identification/applyPlatformPermission")
    public void applyPlatformPermission(@RequestBody IdentificationPlatformPermissionReq req) {
        userEnterpriseIdentificationService.applyPlatformPermission(req);
    }

    @Override
    public Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(GetEnterpriseInfoByUserIdsReq req) {
        return userEnterpriseIdentificationService.getEnterpriseInfoByUserIds(req);
    }

    @Override
    public List<UserEnterpriseIdentificationResp> getEnterpriseInfoByIds(List<String> ids) {
        return userEnterpriseIdentificationService.getEnterpriseInfoByIds(ids);
    }

    @Override
    public Map<String, String> getEnterpriseIdentificationInfoByUserIds(List<String> customerIds) {
        return userEnterpriseIdentificationService.getEnterpriseIdentificationInfoByUserIds(customerIds);
    }

//    @Override
//    public List<UserProvinceResp> queryUserProvince() {
//        return userEnterpriseIdentificationService.queryUserProvince();
//    }

}
