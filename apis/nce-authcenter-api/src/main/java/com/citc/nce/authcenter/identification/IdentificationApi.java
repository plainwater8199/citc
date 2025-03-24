package com.citc.nce.authcenter.identification;


import com.citc.nce.authcenter.identification.vo.CertificateOptions;
import com.citc.nce.authcenter.identification.vo.req.*;
import com.citc.nce.authcenter.identification.vo.resp.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "用户--认证模块")
@FeignClient(value = "authcenter-service", contextId = "identification", url = "${authCenter:}")
public interface IdentificationApi {
    @ApiOperation("用户端-查询登录人个人认证信息")
    @GetMapping("/user/identification/getPersonIdentification")
    GetPersonIdentificationResp getPersonIdentification();

    @ApiOperation("用户端-查询个人认证信息")
    @GetMapping("/user/identification/getPersonIdentificationByUserId/{userId}")
    GetPersonIdentificationResp getPersonIdentificationByUserId(@PathVariable("userId") String userId);

    @ApiOperation("用户端--个人实名认证申请")
    @PostMapping("/user/identification/personIdentificationApply")
    PersonIdentificationApplyResp personIdentificationApply(@RequestBody @Valid PersonIdentificationApplyReq personIdentificationReq);

    @ApiOperation("用户端--查询登陆用户企业认证信息")
    @PostMapping("/user/identification/getEnterpriseIdentificationInfo")
    GetEnterpriseIdentificationResp getEnterpriseIdentificationInfo();

    @ApiOperation("用户端--查询用户企业认证信息")
    @GetMapping("/user/identification/getEnterpriseIdentificationInfoByUserId/{userId}")
    GetEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(@PathVariable("userId") String userId);

    @ApiOperation("用户端--企业认证实名申请")
    @PostMapping("/user/identification/enterpriseIdentificationApply")
    EnterpriseIdentificationApplyResp enterpriseIdentificationApply(@RequestBody @Valid EnterpriseIdentificationApplyReq req);

    @ApiOperation("用户端--对企业账户名做唯一性校验")
    @GetMapping("/user/identification/checkEnterpriseAccountNameUnique")
    CheckEnterpriseAccountNameUniqueResp checkEnterpriseAccountNameUnique(@RequestParam("enterpriseAccountName") String enterpriseAccountName);

    @ApiOperation("用户端--获取用户资质列表")
    @PostMapping("/user/certificate/queryCertificateOptionList")
    QueryCertificateOptionListResp queryCertificateOptionList(@RequestBody @Valid QueryCertificateOptionListReq req);

    @ApiOperation("用户端--获取用户资质（标签）列表 包括未拥有的资质")
    @PostMapping("/user/certificate/getCertificateOptionsAll")
    QueryAllCertificateOptionsResp queryAllCertificateOptions(@RequestBody @Valid QueryAllCertificateOptionsReq req);

    @ApiOperation("用户端--校验能力提供商、解决方案商权限状态")
    @PostMapping("/user/certificate/checkPermissionStatus")
    CheckPermissionStatusResp checkPermissionStatus(@RequestBody CheckPermissionStatusReq req);

    @ApiOperation("管理平台--获取用户资质（标签）下拉列表")
    @GetMapping("/admin/user/certificate/getCertificateList")
    GetCertificateListResp getCertificateList();

    @ApiOperation("管理平台--开启关闭用户标签")
    @PostMapping("/admin/user/onOrOffUserCertificate")
    OnOrOffUserCertificateResp onOrOffUserCertificate(@RequestBody @Valid OnOrOffUserCertificateReq req);

    @ApiOperation("管理平台--通过用户资质信息表id查询日志操作信息")
    @PostMapping("/admin/user/getUserTagLogByCertificateOptionsId")
    GetUserTagLogByCertificateOptionsIdResp getUserTagLogByCertificateOptionsId(@RequestBody @Valid GetUserTagLogByCertificateOptionsIdReq req);

    @ApiOperation("管理平台--查看用户标签")
    @PostMapping("/admin/user/getClientUserCertificate")
    GetClientUserCertificateResp getClientUserCertificate(@RequestBody @Valid GetClientUserCertificateReq req);

    @ApiOperation("管理平台--查看审核备注列表")
    @PostMapping("/admin/user/viewRemarkHistory")
    ViewRemarkHistoryResp viewRemarkHistory(@RequestBody @Valid ViewRemarkHistoryReq req);

    @ApiOperation("管理平台--审核用户认证")
    @PostMapping("/admin/user/auditIdentification")
    AuditIdentificationResp auditIdentification(@RequestBody @Valid AuditIdentificationReq req);

    @ApiOperation("管理端--获取客户端用户认证信息")
    @PostMapping("/admin/user/getClientUserIdentifications")
    GetClientUserIdentificationResp getClientUserIdentifications(@RequestBody @Valid GetClientUserIdentificationReq req);

    @ApiOperation("管理端--用户标签统计")
    @PostMapping("/admin/user/getDashboardUserStatistics")
    DashboardUserStatisticsResp getDashboardUserStatistics();
    @ApiOperation("管理平台--新增用户标签")
    @PostMapping("/admin/user/addUserCertificate")
    AddUserCertificateResp addUserCertificate(@RequestBody @Valid AddUserCertificateReq req);
    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateUserCertificate")
    void updateUserCertificate(@RequestBody @Valid UpdateUserCertificateReq req);

    @ApiOperation("管理平台--通过id查询用户资质")
    @PostMapping("/admin/user/queryCertificateOptionsById")
    CertificateOptions queryCertificateOptionsById(@RequestBody @Valid QueryCertificateOptionsByIdReq req);

    @ApiOperation("管理平台--插入用户资质")
    @PostMapping("/admin/user/insertCertificateOptions")
    Integer insertCertificateOptions(@RequestBody @Valid CertificateOptions req);

    @ApiOperation("管理平台--更新用户资质")
    @PostMapping("/admin/user/updateCertificateOptions")
    Integer updateCertificateOptions(@RequestBody @Valid CertificateOptions req);

    @ApiOperation("管理平台--插入用户zhi")
    @PostMapping("/admin/user/insertIdentificationAuditRecord")
    Integer insertIdentificationAuditRecord(@RequestBody @Valid InsertIdentificationAuditRecordReq req);
    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateUserAuthStatus")
    void updateUserAuthStatus(@RequestParam("userId") String userId);

    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateCertificateOptionsByBusinessId")
    Integer updateCertificateOptionsByBusinessId(@RequestBody @Valid  CertificateOptions req);

    @ApiOperation("处理资质申请")
    @PostMapping("/worksheet/disposeQualificationsApply")
    void disposeQualificationsApply(@RequestBody @Valid  DisposeQualificationApplyReq processingStateReq);

    @ApiOperation("查询资质申请")
    @PostMapping("/worksheet/getQualificationsApply")
    PageResult getQualificationsApply(@RequestBody @Valid  GetQualificationsApplyReq getQualificationsApplyReq);

    @ApiOperation("查询资质申请详情页")
    @PostMapping("/worksheet/getQualificationsApplyInfoById")
    GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(@RequestBody @Valid  GetInfoByIdReq getInfoByIdReq);

    @PostMapping("/user/identification/getEnterpriseInfoByUserIds")
    Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(@RequestBody GetEnterpriseInfoByUserIdsReq req);

    @ApiOperation("根据userId查询用户的标签列表")
    @PostMapping("/worksheet/getCertificateListByUserId")
    List<String> getCertificateListByUserId(@RequestParam("userId") String userId);

}
