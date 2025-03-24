package com.citc.nce.authcenter.identification;

import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.vo.CertificateOptions;
import com.citc.nce.authcenter.identification.vo.req.*;
import com.citc.nce.authcenter.identification.vo.resp.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class IdentificationController implements IdentificationApi {
    @Resource
    private IdentificationService identificationService;


    /**
     * 查询登录人个人认证信息
     */
    @ApiOperation("用户端-查询登录人个人认证信息")
    @GetMapping("/user/identification/getPersonIdentification")
    public GetPersonIdentificationResp getPersonIdentification() {
        return identificationService.getPersonIdentification();
    }

    /**
     * 查询个人认证信息
     */
    @ApiOperation("用户端-查询个人认证信息")
    @GetMapping("/user/identification/getPersonIdentificationByUserId/{userId}")
    public GetPersonIdentificationResp getPersonIdentificationByUserId(@PathVariable("userId") String userId) {
        return identificationService.getPersonIdentificationByUserId(userId);
    }

    /**
     * 个人实名认证申请
     */
    @ApiOperation("用户端--个人实名认证申请")
    @PostMapping("/user/identification/personIdentificationApply")
    public PersonIdentificationApplyResp personIdentificationApply(@RequestBody PersonIdentificationApplyReq personIdentificationReq) {
        return identificationService.personIdentificationApply(personIdentificationReq);
    }


    /**
     * 查询登陆用户企业认证信息
     */
    @ApiOperation("用户端--查询登陆用户企业认证信息")
    @PostMapping("/user/identification/getEnterpriseIdentificationInfo")
    public GetEnterpriseIdentificationResp getEnterpriseIdentificationInfo() {
        return identificationService.getEnterpriseIdentificationInfo();
    }

    /**
     * 查询用户企业认证信息
     */
    @ApiOperation("用户端--查询用户企业认证信息")
    @GetMapping("/user/identification/getEnterpriseIdentificationInfoByUserId/{userId}")
    public GetEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(@PathVariable("userId") String userId) {
        return identificationService.getEnterpriseIdentificationInfoByUserId(userId);
    }

    /**
     * 企业认证实名申请
     */
    @ApiOperation("用户端--企业认证实名申请")
    @PostMapping("/user/identification/enterpriseIdentificationApply")
    public EnterpriseIdentificationApplyResp enterpriseIdentificationApply(@RequestBody EnterpriseIdentificationApplyReq req) {
        return identificationService.enterpriseIdentificationApply(req);
    }

    /**
     * 对企业账户名做唯一性校验
     */
    @ApiOperation("用户端--对企业账户名做唯一性校验")
    @GetMapping("/user/identification/checkEnterpriseAccountNameUnique")
    public CheckEnterpriseAccountNameUniqueResp checkEnterpriseAccountNameUnique(@RequestParam("enterpriseAccountName") String enterpriseAccountName) {
        return identificationService.checkEnterpriseAccountNameUnique(enterpriseAccountName);
    }


    /**
     * 获取用户资质（标签）列表
     */
    @ApiOperation("用户端--获取用户资质列表")
    @PostMapping("/user/certificate/queryCertificateOptionList")
    public QueryCertificateOptionListResp queryCertificateOptionList(@RequestBody QueryCertificateOptionListReq req) {
        return identificationService.queryCertificateOptionList(req);
    }

    /**
     * 获取用户资质（标签）列表 包括未拥有的资质 也返回
     *
     */
    @ApiOperation("用户端--获取用户资质（标签）列表 包括未拥有的资质")
    @PostMapping("/user/certificate/getCertificateOptionsAll")
    public QueryAllCertificateOptionsResp queryAllCertificateOptions(@RequestBody QueryAllCertificateOptionsReq req) {
        return identificationService.queryAllCertificateOptions(req);
    }


    @ApiOperation("用户端--校验能力提供商、解决方案商权限状态")
    @PostMapping("/user/certificate/checkPermissionStatus")
    public CheckPermissionStatusResp checkPermissionStatus(@RequestBody CheckPermissionStatusReq req){
        return identificationService.checkPermissionStatus(req);
    }


    /**
     * 获取用户资质（标签）下拉列表
     */
    @ApiOperation("管理平台--获取用户资质（标签）下拉列表")
    @GetMapping("/admin/user/certificate/getCertificateList")
    public GetCertificateListResp getCertificateList() {
        return identificationService.getCertificateList();
    }


    /**
     * 管理平台--开启关闭用户标签
     */
    @ApiOperation("管理平台--开启关闭用户标签")
    @PostMapping("/admin/user/onOrOffUserCertificate")
    public OnOrOffUserCertificateResp onOrOffUserCertificate(@RequestBody OnOrOffUserCertificateReq req) {
        return identificationService.onOrOffUserCertificate(req);
    }

    /**
     * 通过用户资质信息表id查询日志操作信息
     */
    @ApiOperation("管理平台--通过用户资质信息表id查询日志操作信息")
    @PostMapping("/admin/user/getUserTagLogByCertificateOptionsId")
    public GetUserTagLogByCertificateOptionsIdResp getUserTagLogByCertificateOptionsId(@RequestBody GetUserTagLogByCertificateOptionsIdReq req) {
        return identificationService.getUserTagLogByCertificateOptionsId(req);
    }


    /**
     * 管理端 查看用户标签
     */
    @ApiOperation("管理平台--查看用户标签")
    @PostMapping("/admin/user/getClientUserCertificate")
    public GetClientUserCertificateResp getClientUserCertificate(@RequestBody GetClientUserCertificateReq req) {
        return identificationService.getClientUserCertificate(req);
    }

    /**
     * 管理端 查看审核备注历史
     */
    @ApiOperation("管理平台--查看审核备注列表")
    @PostMapping("/admin/user/viewRemarkHistory")
    public ViewRemarkHistoryResp viewRemarkHistory(@RequestBody ViewRemarkHistoryReq req) {
        return identificationService.viewRemarkHistory(req);
    }

    /**
     * 管理端--审核用户认证
     */
    @ApiOperation("管理平台--审核用户认证")
    @PostMapping("/admin/user/auditIdentification")
    public AuditIdentificationResp auditIdentification(@RequestBody AuditIdentificationReq req) {
        return identificationService.auditIdentification(req);
    }

    /**
     * 管理端--获取客户端用户认证信息
     */
    @ApiOperation("管理平台--获取客户端用户认证信息")
    @PostMapping("/admin/user/getClientUserIdentifications")
    public GetClientUserIdentificationResp getClientUserIdentifications(@RequestBody GetClientUserIdentificationReq req) {
        return identificationService.getClientUserIdentifications(req);
    }

    /**
     * 管理端dashboard用户统计
     */
    @ApiOperation("管理平台--用户标签统计")
    @PostMapping("/admin/user/getDashboardUserStatistics")
    public DashboardUserStatisticsResp getDashboardUserStatistics() {
        return identificationService.getDashboardUserStatistics();
    }

    @Override
    @ApiOperation("管理平台--新增用户标签")
    @PostMapping("/admin/user/addUserCertificate")
    public AddUserCertificateResp addUserCertificate(AddUserCertificateReq req) {
        return identificationService.addUserCertificate(req);
    }

    @Override
    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateUserCertificate")
    public void updateUserCertificate(UpdateUserCertificateReq req) {
        identificationService.updateUserCertificate(req);
    }

    @Override
    @ApiOperation("管理平台--通过id查询用户资质")
    @PostMapping("/admin/user/queryCertificateOptionsById")
    public CertificateOptions queryCertificateOptionsById(QueryCertificateOptionsByIdReq req) {
        return identificationService.queryCertificateOptionsById(req);
    }

    @Override
    @ApiOperation("管理平台--插入用户资质")
    @PostMapping("/admin/user/insertCertificateOptions")
    public Integer insertCertificateOptions(CertificateOptions req) {
        return identificationService.insertCertificateOptions(req);
    }

    @Override
    @ApiOperation("管理平台--更新用户资质")
    @PostMapping("/admin/user/updateCertificateOptions")
    public Integer updateCertificateOptions(CertificateOptions req) {
        return identificationService.updateCertificateOptions(req);
    }

    @Override
    @ApiOperation("管理平台--插入用户zhi")
    @PostMapping("/admin/user/insertIdentificationAuditRecord")
    public Integer insertIdentificationAuditRecord(InsertIdentificationAuditRecordReq req) {
        return identificationService.insertIdentificationAuditRecord(req);
    }

    @Override
    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateUserAuthStatus")
    public void updateUserAuthStatus(String userId) {
        identificationService.updateUserAuthStatus(userId);
    }

    @Override
    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateCertificateOptionsByBusinessId")
    public Integer updateCertificateOptionsByBusinessId(CertificateOptions req) {
        return identificationService.updateCertificateOptionsByBusinessId(req);
    }

    @Override
    @ApiOperation("处理资质申请")
    @PostMapping("/worksheet/disposeQualificationsApply")
    public void disposeQualificationsApply(DisposeQualificationApplyReq req) {
        identificationService.disposeQualificationsApply(req);
    }

    @Override
    @ApiOperation("查询资质申请")
    @PostMapping("/worksheet/getQualificationsApply")
    public PageResult getQualificationsApply(GetQualificationsApplyReq req) {
        return identificationService.getQualificationsApply(req);
    }

    @Override
    @ApiOperation("查询资质申请详情页")
    @PostMapping("/worksheet/getQualificationsApplyInfoById")
    public GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(GetInfoByIdReq req) {
        return identificationService.getQualificationsApplyInfoById(req);
    }

    @Override
    @ApiOperation("查询资质申请详情页")
    @PostMapping("/user/identification/getEnterpriseInfoByUserIds")
    public Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(GetEnterpriseInfoByUserIdsReq req) {
        return identificationService.getEnterpriseInfoByUserIds(req);
    }

    @Override
    @PostMapping("/worksheet/getCertificateListByUserId")
    public List<String> getCertificateListByUserId(@RequestParam("userId") String userId) {
        return identificationService.getUserCertificateByUserId(userId);
    }
}
