package com.citc.nce.authcenter.identification.service;


import com.citc.nce.authcenter.identification.entity.*;
import com.citc.nce.authcenter.identification.vo.CertificateItem;
import com.citc.nce.authcenter.identification.vo.CertificateOptions;
import com.citc.nce.authcenter.identification.vo.UserCertificateItem;
import com.citc.nce.authcenter.identification.vo.req.*;
import com.citc.nce.authcenter.identification.vo.resp.*;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Map;

public interface IdentificationService {
    /**
     * 初始化用户的权限信息
     * @param userId 用户id
     */
    void initializeUserIdentification(String userId);

    /**
     * 查询用户的企业认证信息
     * @param userId 用户ID
     * @return 用户企业认证信息
     */
    UserEnterpriseIdentificationDo findUserEnterpriseIdent(String userId);

    /**
     * 查询用户的平台权限信息
     * @param platformType 平台信息
     * @param userId 用户ID
     * @return 用户平台信息
     */
    UserPlatformPermissionsDo findByPlatAndUserId(Integer platformType, String userId);

    /**
     * 查询用户的审批信息
     * @param approvalLogId 审批ID
     * @return 审批结果
     */
    List<ApprovalLogDo> findApprovalById(String approvalLogId);

    /**
     * 用户端-查询登录人个人认证信息
     * @return 结果
     */
    GetPersonIdentificationResp getPersonIdentification();

    /**
     * 管理平台-查询个人认证信息
     * @param userId 用户id
     * @return 响应信息
     */
    GetPersonIdentificationResp getPersonIdentificationByUserId(String userId);

    /**
     * 个人实名认证申请
     * @param personIdentificationReq 请求信息
     * @return 响应信息
     */
    PersonIdentificationApplyResp personIdentificationApply(PersonIdentificationApplyReq personIdentificationReq);

    /**
     * 更新用户的认证状态
     * @param userId 用户id
     */
    void updateUserAuthStatus(String userId);

    /**
     * 用户端--查询登陆用户企业认证信息
     * @return 用户信息
     */
    GetEnterpriseIdentificationResp getEnterpriseIdentificationInfo();

    /**
     * 管理平台--查询用户企业认证信息
     * @param userId 用户ID
     * @return 响应信息
     */
    GetEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(String userId);

    /**
     * 企业认证实名申请
     * @param req 请求信息
     * @return 响应结果
     */
    EnterpriseIdentificationApplyResp enterpriseIdentificationApply(EnterpriseIdentificationApplyReq req);

    /**
     * 对企业账户名做唯一性校验
     * @param enterpriseAccountName 企业名称
     * @return 检验结果
     */
    CheckEnterpriseAccountNameUniqueResp checkEnterpriseAccountNameUnique(String enterpriseAccountName);


    /**
     * 获取用户资质列表
     * @param req 请求信息
     * @return 响应结果
     */
    QueryCertificateOptionListResp queryCertificateOptionList(QueryCertificateOptionListReq req);

    /**
     * 获取用户资质（标签）列表 包括未拥有的资质
     * @param req 请求信息
     * @return 响应信息
     */
    QueryAllCertificateOptionsResp queryAllCertificateOptions(QueryAllCertificateOptionsReq req);

    /**
     * 校验能力提供商、解决方案商权限状态
     * @param req 请求信息
     * @return 响应信息
     */
    CheckPermissionStatusResp checkPermissionStatus(CheckPermissionStatusReq req);

    /**
     * 获取用户资质（标签）下拉列表
     * @return 返回信息
     */
    GetCertificateListResp getCertificateList();

    /**
     * 管理平台--开启关闭用户标签
     * @param req 请求信息
     * @return 响应信息
     */
    OnOrOffUserCertificateResp onOrOffUserCertificate(OnOrOffUserCertificateReq req);

    /**
     * 管理平台--通过用户资质信息表id查询日志操作信息
     * @param req 请求信息
     * @return 响应信息
     */
    GetUserTagLogByCertificateOptionsIdResp getUserTagLogByCertificateOptionsId(GetUserTagLogByCertificateOptionsIdReq req);

    /**
     * 管理平台--查看用户标签
     * @param req 请求信息
     * @return 响应结果
     */
    GetClientUserCertificateResp getClientUserCertificate(GetClientUserCertificateReq req);

    /**
     * 管理平台--查看审核备注列表
     * @param req 请求信息
     * @return 响应结果
     */
    ViewRemarkHistoryResp viewRemarkHistory(ViewRemarkHistoryReq req);

    /**
     * 管理平台--审核用户认证
     * @param req 请求信息
     * @return 响应信息
     */
    AuditIdentificationResp auditIdentification(AuditIdentificationReq req);

    /**
     * 管理平台--获取客户端用户认证信息
     * @param req 请求信息
     * @return 响应信息
     */
    GetClientUserIdentificationResp getClientUserIdentifications(GetClientUserIdentificationReq req);

    /**
     * 管理平台--用户标签统计
     * @return 响应信息
     */
    DashboardUserStatisticsResp getDashboardUserStatistics();


    /**
     * 根据用户ID查询用户的认证信息
     * @param userId 用户Id
     * @return 放回信息
     */
    List<UserCertificateItem> getCertificateOptions(String userId);

    /**
     * 添加处理记录
     * @param userId 用户ID
     * @param name 姓名
     * @param code 编码
     */
    void addProcessingRecord(String userId, String name, Integer code);

    /**
     * 获取用户认证信息
     * @param userId 用户ID
     * @return 响应信息
     */
    WebEnterpriseIdentificationResp getIdentificationInfo(String userId);

    /**
     * 获取用户认证信息
     * @param userId 用户ID
     * @return 响应信息
     */
    List<CertificateOptionsDo> queryUserCertificateByUserId(String userId);

    /**
     * 获取用户可选择的资历列表
     * @return 列表信息
     */
    List<UserCertificateDo> getUserCertificate();

    /**
     * 根据标签列表获取用户id；
     * @param tags 标签列表
     * @return 用户信息
     */
    List<String> findUsersByCertificateOptions(List<Integer> tags);

    /**
     * 根据标签id列表查询标签名称映射
     * @param tags 标签id列表
     * @return 标签映射信息
     */
    Map<String, String> findCertificateNameMap(List<Integer> tags);

    /**
     * 获取用户标签
     * @param plats 平台信息，如果没有就查询所有有效的
     * @return 标签信息
     */
    List<CertificateItem> getCertificateList(List<Integer> plats);

    /**
     * 根据userId查询用户的标签列表
     * @param userId 用户ID
     * @return 标签列表
     */
    List<String> getUserCertificateByUserId(String userId);
    /**
     * 管理平台--新增用户标签
     * @param req 请求信息
     */
    AddUserCertificateResp addUserCertificate(AddUserCertificateReq req);
    /**
     * 管理平台--更新用户标签状态
     * @param req 请求信息
     */
    void updateUserCertificate(UpdateUserCertificateReq req);

    CertificateOptions queryCertificateOptionsById(QueryCertificateOptionsByIdReq req);

    Integer insertCertificateOptions(CertificateOptions req);

    Integer updateCertificateOptions(CertificateOptions req);

    Integer insertIdentificationAuditRecord(InsertIdentificationAuditRecordReq req);

    Integer updateCertificateOptionsByBusinessId(CertificateOptions req);

    void disposeQualificationsApply(DisposeQualificationApplyReq req);

    PageResult getQualificationsApply(GetQualificationsApplyReq req);

    GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(GetInfoByIdReq req);

    Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(GetEnterpriseInfoByUserIdsReq req);
}
