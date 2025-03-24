package com.citc.nce.auth.identification;

import com.citc.nce.auth.adminUser.vo.resp.UserEnterpriseIdentificationResp;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.GetEnterpriseInfoByUserIdsReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.auth.identification.vo.resp.BossIdentificationResp;
import com.citc.nce.auth.identification.vo.resp.UserProvinceResp;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.auth.identification.vo.resp.WebPersonIdentificationResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/14 18:57
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "auth-service", contextId = "identificationApi", url = "${auth:}")
public interface IdentificationApi {

    /**
     * 个人实名认证申请
     *
     * @param personIdentificationReq
     */
    @PostMapping("/user/identification/personIdentificationApply")
    void personIdentificationApply(@RequestBody PersonIdentificationReq personIdentificationReq);


    /**
     * 企业认证实名申请
     *
     * @param enterpriseIdentificationReq
     */
    @PostMapping("/user/identification/enterpriseIdentificationApply")
    void enterpriseIdentificationApply(@RequestBody @Valid EnterpriseIdentificationReq enterpriseIdentificationReq);


    /**
     * 查看企业认证信息
     *
     * @return
     */
    @PostMapping("/user/identification/getEnterpriseIdentificationInfo")
    WebEnterpriseIdentificationResp getEnterpriseIdentificationInfo();


    /**
     * 根据用户uuid查询查看企业认证信息，目前就管理平台调用
     *
     * @param userId
     * @return
     */
    @PostMapping("/user/identification/getIdentificationInfoByUserId")
    BossIdentificationResp getIdentificationForBoss(String userId);

    /**
     * 查询个人认证信息
     *
     * @return
     */
    @GetMapping("/user/identification/getPersonIdentification")
    WebPersonIdentificationResp getPersonIdentification();

    /**
     * 企业账号名是否唯一校验
     *
     * @param enterpriseAccountName
     * @return true 唯一  false 不唯一
     */
    @GetMapping("/user/identification/checkEnterpriseAccountNameUnique")
    Boolean checkEnterpriseAccountNameUnique(@RequestParam("enterpriseAccountName") String enterpriseAccountName);

    @GetMapping("/user/identification/getPersonIdentificationByUserId/{userId}")
    WebPersonIdentificationResp getPersonIdentificationByUserId(@PathVariable("userId") String userId);


    @GetMapping("/user/identification/getEnterpriseIdentificationInfoByUserId/{userId}")
    WebEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(@PathVariable("userId") String userId);

    /**
     * 提交平台使用权限
     */
    @PostMapping("/user/identification/applyPlatformPermission")
    void applyPlatformPermission(@RequestBody IdentificationPlatformPermissionReq identificationPlatformPermissionReq);

    @PostMapping("/user/identification/getEnterpriseInfoByUserIds")
    Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(@RequestBody GetEnterpriseInfoByUserIdsReq req);

    @PostMapping("/user/identification/getEnterpriseInfoByIds")
    List<UserEnterpriseIdentificationResp> getEnterpriseInfoByIds(@RequestParam("ids") List<String> ids);

    @PostMapping("/user/identification/getEnterpriseIdentificationInfoByUserIds")
    Map<String, String> getEnterpriseIdentificationInfoByUserIds(@RequestParam("customerIds") List<String> customerIds);

//    @PostMapping("/user/identification/queryUserProvince")
//    List<UserProvinceResp> queryUserProvince();
}
