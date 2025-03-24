package com.citc.nce.authcenter.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.common.CSPChannelEnum;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.meal.CspMealContractApi;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.readingLetter.template.ReadingLetterTemplateApi;
import com.citc.nce.auth.readingLetter.template.enums.SmsTypeEnum;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterAuditDeleteReq;
import com.citc.nce.authcenter.Utils.ExcelUtils;
import com.citc.nce.authcenter.admin.dao.AdminMenuDao;
import com.citc.nce.authcenter.admin.dao.AdminRoleDao;
import com.citc.nce.authcenter.admin.dao.AdminRoleMenuDao;
import com.citc.nce.authcenter.admin.dao.AdminUserDao;
import com.citc.nce.authcenter.admin.dao.AdminUserMapper;
import com.citc.nce.authcenter.admin.dao.AdminUserRoleDao;
import com.citc.nce.authcenter.admin.dto.OperatorDto;
import com.citc.nce.authcenter.admin.dto.UserAndRoleDto;
import com.citc.nce.authcenter.admin.entity.AdminMenuDo;
import com.citc.nce.authcenter.admin.entity.AdminRoleDo;
import com.citc.nce.authcenter.admin.entity.AdminRoleMenuDo;
import com.citc.nce.authcenter.admin.entity.AdminUserDo;
import com.citc.nce.authcenter.admin.entity.AdminUserRoleDo;
import com.citc.nce.authcenter.admin.service.AdminUserService;
import com.citc.nce.authcenter.auth.config.GatewayConfig;
import com.citc.nce.authcenter.auth.service.AdminAuthService;
import com.citc.nce.authcenter.auth.service.LoginRecordService;
import com.citc.nce.authcenter.auth.vo.AdminRoleItem;
import com.citc.nce.authcenter.auth.vo.AdminUserInfo;
import com.citc.nce.authcenter.auth.vo.AuthUrlInfo;
import com.citc.nce.authcenter.auth.vo.AuthUrlItem;
import com.citc.nce.authcenter.auth.vo.CspCustomerChatbotAccountVo;
import com.citc.nce.authcenter.auth.vo.ManageUserInfo;
import com.citc.nce.authcenter.auth.vo.MenuSortInfo;
import com.citc.nce.authcenter.auth.vo.PlatformApplicationReviewInfo;
import com.citc.nce.authcenter.auth.vo.RoleAndMenuItem;
import com.citc.nce.authcenter.auth.vo.SyncAuthInfoModel;
import com.citc.nce.authcenter.auth.vo.UserBaseInfo;
import com.citc.nce.authcenter.auth.vo.UserExcel;
import com.citc.nce.authcenter.auth.vo.UserExcelDataInfo;
import com.citc.nce.authcenter.auth.vo.UserIdAndNameInfo;
import com.citc.nce.authcenter.auth.vo.UserIdInfo;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.UserPageDBInfo;
import com.citc.nce.authcenter.auth.vo.UserPageInfo;
import com.citc.nce.authcenter.auth.vo.req.AdminLoginReq;
import com.citc.nce.authcenter.auth.vo.req.ChatbotSetWhiteListReq;
import com.citc.nce.authcenter.auth.vo.req.CheckLoadNameReq;
import com.citc.nce.authcenter.auth.vo.req.ClientUserDetailsReq;
import com.citc.nce.authcenter.auth.vo.req.CodeListReq;
import com.citc.nce.authcenter.auth.vo.req.CompleteSupplierChatbotConfigurationReq;
import com.citc.nce.authcenter.auth.vo.req.DeleteClientUserReq;
import com.citc.nce.authcenter.auth.vo.req.EditOperatorReq;
import com.citc.nce.authcenter.auth.vo.req.EditRoleReq;
import com.citc.nce.authcenter.auth.vo.req.EditSupplierChatbotConfigurationReq;
import com.citc.nce.authcenter.auth.vo.req.EnableOrDisableAdminUserReq;
import com.citc.nce.authcenter.auth.vo.req.EnableOrDisableReq;
import com.citc.nce.authcenter.auth.vo.req.EnableOrDisableRoleReq;
import com.citc.nce.authcenter.auth.vo.req.GetMemberByRoleIdReq;
import com.citc.nce.authcenter.auth.vo.req.GetMenuByRoleIdReq;
import com.citc.nce.authcenter.auth.vo.req.GetOperatorListReq;
import com.citc.nce.authcenter.auth.vo.req.GetReviewLogListReq;
import com.citc.nce.authcenter.auth.vo.req.GetRolePage;
import com.citc.nce.authcenter.auth.vo.req.GetUserInfoReq;
import com.citc.nce.authcenter.auth.vo.req.MenuSaveReq;
import com.citc.nce.authcenter.auth.vo.req.PlatformApplicationReviewReq;
import com.citc.nce.authcenter.auth.vo.req.QueryCommunityUserBaseInfoListReq;
import com.citc.nce.authcenter.auth.vo.req.QueryCommunityUserListReq;
import com.citc.nce.authcenter.auth.vo.req.QueryManageUserReq;
import com.citc.nce.authcenter.auth.vo.req.QuerySupplierChatbotReq;
import com.citc.nce.authcenter.auth.vo.req.QueryUserViolationReq;
import com.citc.nce.authcenter.auth.vo.req.RejectSupplierChatbotConfigurationReq;
import com.citc.nce.authcenter.auth.vo.req.RemoveRoleReq;
import com.citc.nce.authcenter.auth.vo.req.ReqDocZip;
import com.citc.nce.authcenter.auth.vo.req.ReviewPlatAppReq;
import com.citc.nce.authcenter.auth.vo.req.RoleConfigurationMemberReq;
import com.citc.nce.authcenter.auth.vo.req.RoleConfigurationMenuReq;
import com.citc.nce.authcenter.auth.vo.req.UpdateChatbotSupplierReq;
import com.citc.nce.authcenter.auth.vo.req.UpdateClientUserReq;
import com.citc.nce.authcenter.auth.vo.req.UpdateCspChannelReq;
import com.citc.nce.authcenter.auth.vo.req.UpdateSupplierContractReq;
import com.citc.nce.authcenter.auth.vo.req.UpdateUserViolationReq;
import com.citc.nce.authcenter.auth.vo.req.UserIdReq;
import com.citc.nce.authcenter.auth.vo.req.changeSupplierChatbotStatusReq;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserInfoResp;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserLoginResp;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserResp;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserSumResp;
import com.citc.nce.authcenter.auth.vo.resp.CertStatisticsResp;
import com.citc.nce.authcenter.auth.vo.resp.ChannelInfoResp;
import com.citc.nce.authcenter.auth.vo.resp.ChatbotProcessingSumResp;
import com.citc.nce.authcenter.auth.vo.resp.CheckLoadNameResp;
import com.citc.nce.authcenter.auth.vo.resp.ClientUserDetailsResp;
import com.citc.nce.authcenter.auth.vo.resp.ContractSupplierInfo;
import com.citc.nce.authcenter.auth.vo.resp.DeleteClientUserResp;
import com.citc.nce.authcenter.auth.vo.resp.EditOperatorResp;
import com.citc.nce.authcenter.auth.vo.resp.EditRoleResp;
import com.citc.nce.authcenter.auth.vo.resp.EnableOrDisableAdminUserResp;
import com.citc.nce.authcenter.auth.vo.resp.EnableOrDisableResp;
import com.citc.nce.authcenter.auth.vo.resp.EnableOrDisableRoleResp;
import com.citc.nce.authcenter.auth.vo.resp.GetEnterpriseUserListResp;
import com.citc.nce.authcenter.auth.vo.resp.GetMemberByRoleIdResp;
import com.citc.nce.authcenter.auth.vo.resp.GetMenuByRoleIdResp;
import com.citc.nce.authcenter.auth.vo.resp.GetReviewLogListResp;
import com.citc.nce.authcenter.auth.vo.resp.GetUserInfoResp;
import com.citc.nce.authcenter.auth.vo.resp.GetUserRoleListResp;
import com.citc.nce.authcenter.auth.vo.resp.MenuChildResp;
import com.citc.nce.authcenter.auth.vo.resp.MenuParentResp;
import com.citc.nce.authcenter.auth.vo.resp.MenuRecordResp;
import com.citc.nce.authcenter.auth.vo.resp.MenuResp;
import com.citc.nce.authcenter.auth.vo.resp.OperationLogResp;
import com.citc.nce.authcenter.auth.vo.resp.QueryAdminAuthListResp;
import com.citc.nce.authcenter.auth.vo.resp.QueryCommunityUserBaseInfoListResp;
import com.citc.nce.authcenter.auth.vo.resp.QueryUserViolationResp;
import com.citc.nce.authcenter.auth.vo.resp.RemoveRoleResp;
import com.citc.nce.authcenter.auth.vo.resp.ReviewPlatAppResp;
import com.citc.nce.authcenter.auth.vo.resp.RoleConfigurationMemberResp;
import com.citc.nce.authcenter.auth.vo.resp.RoleConfigurationMenuResp;
import com.citc.nce.authcenter.auth.vo.resp.SupplierChatbotConfigurationResp;
import com.citc.nce.authcenter.auth.vo.resp.SupplierChatbotExcelInfoResp;
import com.citc.nce.authcenter.auth.vo.resp.SupplierChatbotInfoResp;
import com.citc.nce.authcenter.auth.vo.resp.UpdateClientUserResp;
import com.citc.nce.authcenter.auth.vo.resp.UpdateUserViolationResp;
import com.citc.nce.authcenter.auth.vo.resp.UserInfoResp;
import com.citc.nce.authcenter.captch.service.CaptchaService;
import com.citc.nce.authcenter.configure.CspPlatformUrlConfigure;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerChatbotAccountMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomerChatbotAccount;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.directCustomer.dao.SupplierChatbotStatusOperationLogDao;
import com.citc.nce.authcenter.directCustomer.entity.SupplierChatbotStatusOperationLogDo;
import com.citc.nce.authcenter.directCustomer.enums.OperationTypeEnum;
import com.citc.nce.authcenter.identification.dao.ApprovalLogDao;
import com.citc.nce.authcenter.identification.dao.UserCertificateOptionsDao;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.authcenter.identification.dao.UserPlatformPermissionsDao;
import com.citc.nce.authcenter.identification.entity.ApprovalLogDo;
import com.citc.nce.authcenter.identification.entity.UserCertificateOptionsDo;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.authcenter.identification.entity.UserPlatformPermissionsDo;
import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.vo.ApprovalLogItem;
import com.citc.nce.authcenter.identification.vo.UserCertificateItem;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.authcenter.tenantdata.user.dao.ChatbotManageWhiteListDao;
import com.citc.nce.authcenter.tenantdata.user.dao.Cmcc1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.CmccNewDao;
import com.citc.nce.authcenter.tenantdata.user.dao.ContractManage1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.CspChannelDao;
import com.citc.nce.authcenter.tenantdata.user.dao.CspCustomerChatbotAccount1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.Menu1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.MenuChild1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.MenuParent1Dao;
import com.citc.nce.authcenter.tenantdata.user.entity.ChatbotManageWhiteListDo;
import com.citc.nce.authcenter.tenantdata.user.entity.CmccNewDo;
import com.citc.nce.authcenter.tenantdata.user.entity.ContractManageDo;
import com.citc.nce.authcenter.tenantdata.user.entity.CspChannelDo;
import com.citc.nce.authcenter.tenantdata.user.entity.CspCustomerChatbotAccountDo;
import com.citc.nce.authcenter.tenantdata.user.entity.MenuChildDo;
import com.citc.nce.authcenter.tenantdata.user.entity.MenuDo;
import com.citc.nce.authcenter.tenantdata.user.entity.MenuParentDo;
import com.citc.nce.authcenter.user.dao.UserDao;
import com.citc.nce.authcenter.user.dao.UserMapper;
import com.citc.nce.authcenter.user.dao.UserViolationDao;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.entity.UserViolationDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.authcenter.user.service.UserViolationService;
import com.citc.nce.authcenter.utils.AuthUtils;
import com.citc.nce.authcenter.utils.TokenUtils;
import com.citc.nce.common.constants.CarrierEnum;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.UserTokenUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.dto.FileExamineDeleteReq;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.filecenter.vo.FileInfo;
import com.citc.nce.filecenter.vo.FileInfoReq;
import com.citc.nce.misc.constant.BusinessTypeEnum;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.ProcessingContentEnum;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.RobotSceneNodeApi;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 */


@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(CspPlatformUrlConfigure.class)
public class AdminAuthServiceImpl implements AdminAuthService {
    @Resource
    private UserService userService;
    @Resource
    private IdentificationService identificationService;
    @Resource
    private CspMealContractApi mealContractApi;

    @Autowired
    private CspCustomerService cspCustomerService;
    @Resource
    private RobotGroupSendPlansApi robotGroupSendPlansApi;
    @Resource
    private final RobotSceneNodeApi robotSceneNodeApi;
    @Resource
    LoginRecordService loginRecordService;
    @Resource
    private MsSummaryApi msGoodsApi;

    @Autowired
    private CommonKeyPairConfig commonKeyPairConfig;

    @Resource
    private UserDao userDao;
    @Resource
    private AdminUserDao adminUserDao;
    @Resource
    private CspChannelDao cspChannelDao;
    @Resource
    private CspCustomerChatbotAccount1Dao cspCustomerChatbotAccountDao;
    @Resource
    private Cmcc1Dao cmccDao;
    @Resource
    private CmccNewDao cmccNewDao;
    @Resource
    private ChatbotManageWhiteListDao chatbotManageWhiteListDao;
    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ProcessingRecordApi processingRecordApi;
    @Resource
    private UserPlatformPermissionsDao userPlatformPermissionsDao;
    @Resource
    private ContractManage1Dao contractManageDao;
    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;
    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    @Resource
    private AdminRoleDao adminRoleDao;
    @Resource
    private AdminUserRoleDao adminUserRoleDao;
    @Resource
    private AdminMenuDao adminMenuDao;
    @Resource
    private AdminRoleMenuDao adminRoleMenuDao;
    @Resource
    private ApprovalLogDao approvalLogDao;

    @Resource
    private UserViolationService userViolationService;
    @Resource
    private UserViolationDao userViolationDao;
    @Resource
    private AdminUserService adminUserService;
    @Resource
    private CaptchaService verificationService;
    @Autowired
    private CspService cspService;
    @Resource
    FileApi fileApi;
    @Resource
    private GatewayConfig gatewayConfig;
    @Resource
    private SupplierChatbotStatusOperationLogDao supplierChatbotStatusOperationLogDao;
    @Resource
    private Menu1Dao menuDao;
    @Resource
    private MenuParent1Dao menuParentDao;
    @Resource
    private MenuChild1Dao menuChildDao;
    private static final Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    //在网关处保存的开发服务的IP端口
    @Value("${gateway.callback.fontdo}")
    private String callbackUrl;
    @Resource
    private ReadingLetterTemplateApi readingLetterTemplateApi;
    //下载背景图片的url(未替换)
//    @Value("${backgroundUrl")
//    private String backgroundUrl;
    @Resource
    private PlatformApi platformApi;

    @Resource
    MessageTemplateApi messageTemplateApi;

    @Autowired
    private CspCustomerChatbotAccountMapper accountManagementDao;


    @Override
    public ClientUserDetailsResp getClientUserDetails(ClientUserDetailsReq req) {
        ClientUserDetailsResp resp = new ClientUserDetailsResp();
        UserDo clientUserDo = userService.userInfoDetailByUserId(req.getUserId());
        if (clientUserDo != null) {
            BeanUtils.copyProperties(clientUserDo, resp);
            List<UserCertificateItem> certificateOptions = identificationService.getCertificateOptions(req.getUserId());
            StringBuilder sb = new StringBuilder();
            if (!CollectionUtils.isEmpty(certificateOptions)) {
                for (int i = 0; i < certificateOptions.size(); i++) {
                    UserCertificateItem certificateResp = certificateOptions.get(i);
                    String certificateName = certificateResp.getCertificateName();
                    sb.append(certificateName.contains("、"));
                }
            }
            String flags = sb.toString();
            if (!StringUtils.isEmpty(flags)) {
                flags = flags.substring(0, flags.length() - 1);
                resp.setFlags(flags);
            }
        }
        return resp;
    }

    @Override
    @Transactional
    public UpdateClientUserResp updateClientUserInfo(UpdateClientUserReq req) {
        UpdateClientUserResp resp = new UpdateClientUserResp();
        String userId = req.getUserId();
        String accountName = req.getAccountName();
        String email = req.getEmail();
        String phone = req.getPhone();
        if (StringUtils.isEmpty(req.getUserId()) || (StringUtils.isAllEmpty(req.getAccountName(), req.getEmail(), req.getPhone(), req.getUserImgUuid()))) {
            throw new BizException(AuthCenterError.PARAM_MISS.getCode(), AuthCenterError.PARAM_MISS.getMsg());
        }
        if (!StringUtils.isEmpty(phone) && !Validator.isMobile(phone)) {
            throw new BizException(AuthCenterError.PHONE_FORMAT_ERROR.getCode(), AuthCenterError.PHONE_FORMAT_ERROR.getMsg());
        }
        if (!StringUtils.isEmpty(email) && !Validator.isEmail(email)) {
            throw new BizException(AuthCenterError.EMAIL_FORMAT_ERROR.getCode(), AuthCenterError.EMAIL_FORMAT_ERROR.getMsg());
        }
        List<UserDo> userDos = null;
        UserDo user = userDao.selectOne("user_id", userId);

        if (StringUtils.isNotBlank(accountName)) {
            //设置时，对phone email  账号名 进行是否重复校验
            QueryWrapper<UserDo> nameCheckwrapper = new QueryWrapper<>();
            nameCheckwrapper.ne("user_id", userId).eq("name", accountName);
            userDos = userDao.selectList(nameCheckwrapper);
            if (!CollectionUtils.isEmpty(userDos) && !userDos.isEmpty()) {
                throw new BizException(AuthCenterError.USER_ACCOUNT_REPEAT);
            }
            user.setName(accountName);
        }

        //邮箱修改
        if (StringUtils.isNotBlank(email)) {
            QueryWrapper<UserDo> emailCheckwrapper = new QueryWrapper<>();
            emailCheckwrapper.ne("user_id", userId).eq("mail", email);
            userDos = userDao.selectList(emailCheckwrapper);
            if (!CollectionUtils.isEmpty(userDos) && !userDos.isEmpty()) {
                throw new BizException(AuthCenterError.USER_EMAIL_REPEAT);
            }
            user.setMail(email).setEmailActivated(NumCode.ONE.getCode());
        }

        //手机号修改
        if (StringUtils.isNotBlank(phone)) {
            QueryWrapper<UserDo> phoneCheckwrapper = new QueryWrapper<>();
            phoneCheckwrapper.ne("user_id", userId).eq("phone", phone);
            userDos = userDao.selectList(phoneCheckwrapper);
            if (!CollectionUtils.isEmpty(userDos) && !userDos.isEmpty()) {
                throw new BizException(AuthCenterError.USER_PHONE_REPEAT);
            }
            user.setPhone(phone);
        }

        if (StringUtils.isNotBlank(req.getUserImgUuid())) {
            user.setUserImgUuid(req.getUserImgUuid());
        }
        userDao.updateById(user);

        //添加处理记录
        identificationService.addProcessingRecord(userId, ProcessingContentEnum.BJYHXX.getName(), BusinessTypeEnum.YHGL_TY.getCode());

        resp.setResult(true);
        return resp;
    }

    @Override
    public DeleteClientUserResp deleteClientUserInfo(DeleteClientUserReq req) {
        LambdaUpdateWrapper<UserDo> updateWrapper = Wrappers.<UserDo>lambdaUpdate()
                .eq(UserDo::getUserId, req.getUserId())
                .set(UserDo::getDeleted, NumCode.ONE.getCode())
                .set(UserDo::getUpdater, SessionContextUtil.getUser().getUserId());
        userDao.update(null, updateWrapper);
        DeleteClientUserResp resp = new DeleteClientUserResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public CheckLoadNameResp checkLoadNameExist(CheckLoadNameReq req) {
        CheckLoadNameResp resp = new CheckLoadNameResp();
        LambdaQueryWrapperX<AdminUserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(AdminUserDo::getAccountName, req.getCheckValue()).or().eq(AdminUserDo::getPhone, req.getCheckValue());
        resp.setResult(null != adminUserDao.selectOne(queryWrapper));
        return resp;
    }

    @Override
    public AdminUserLoginResp adminUserWebLogin(AdminLoginReq req) {
        /**
         * 检验验证码 目前改为系统短信
         * 系统短信类型：CaptchaType.SMS.getCode()
         * 多因子类型： CaptchaType.DYZ_SMS.getCode()
         */
        req.getCaptchaCheckReq().setIsAdminAuth(true);
        verificationService.checkCaptcha(req.getCaptchaCheckReq());
        AdminUserDo adminUserDo = adminUserDao.selectOne(AdminUserDo::getPhone, req.getCaptchaCheckReq().getAccount());
        if (null == adminUserDo) {
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        if (NumCode.ONE.getCode() != adminUserDo.getUserStatus()) {
            throw new BizException(AuthCenterError.HAVE_NO_PERMISSION);
        }
        StpUtil.login(adminUserDo.getUserId());
        AdminUserLoginResp resp = new AdminUserLoginResp();
        BeanUtils.copyProperties(adminUserDo, resp);
        resp.setUserName(adminUserDo.getFullName());
        //查询用户角色code
        List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(adminUserDo.getUserId());
        Map<String, List<String>> url = userMenu(data);
        //菜单排序
        resp.setMenuSortInfos(getMenuSort(url));
        resp.setUrl(url);
        //根据userId查询用户角色信息
        List<String> roleList = adminUserMapper.selectUserRoleByUserId(adminUserDo.getUserId());
        resp.setRoleList(roleList);
        redisTemplate.opsForValue().set("USER_ID:" + adminUserDo.getUserId(), url);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(resp, baseUser);
        StpUtil.getTokenSession().set(UserTokenUtil.SESSION_USER_KEY, baseUser);

        resp.setToken(tokenInfo.getTokenValue());
        UserDo userDo = new UserDo();
        userDo.setUserId(adminUserDo.getUserId());
        userDo.setName(adminUserDo.getFullName());
        loginRecordService.insertLoginRecord(userDo, null, resp.getPlatformType());

        return resp;
    }

    private List<MenuSortInfo> getMenuSort(Map<String, List<String>> url) {
        List<MenuSortInfo> menuSortInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(url)) {
            MenuSortInfo menuSortInfo;
            int sort = 1;
            for (Map.Entry<String, List<String>> entry : url.entrySet()) {
                menuSortInfo = new MenuSortInfo();
                menuSortInfo.setCode(entry.getKey());
                menuSortInfo.setSort(sort++);
                menuSortInfoList.add(menuSortInfo);
            }
        }
        return menuSortInfoList;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public PageResult getManageUser(QueryManageUserReq req) {
        if (NumCode.ZERO.getCode() == req.getProtal()) {//查询统一用户管理
            Page<UserInfo> unifiedManageUserPage = userMapper.queryUnifiedManageUser(
                    req.getName(),
                    req.getPhone(),
                    req.getPersonAuthStatus(),
                    req.getEnterpriseAuthStatus(),
                    Page.of(req.getPageNo(), req.getPageSize())
            );
            //判断是否有处理记录
            List<String> userIds = new ArrayList<>();
            unifiedManageUserPage.getRecords().forEach(item -> {
                userIds.add(item.getUserId());
            });
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIds)) {
                BusinessIdsReq businessIdsReq = new BusinessIdsReq();
                businessIdsReq.setBusinessIds(userIds);
                businessIdsReq.setBusinessType(0);
                List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

                for (UserInfo userResp : unifiedManageUserPage.getRecords()) {
                    for (ProcessingRecordResp processingRecordResp : processingRecordRespList) {
                        if (userResp.getUserId().equals(processingRecordResp.getBusinessId())) {
                            userResp.setHasRecords(true);
                        }
                    }
                }
            }
            //用户标签同步
            Runnable runnable = () -> userService.userStatusSyn();
            ThreadTaskUtils.execute(runnable);
            return new PageResult<>(unifiedManageUserPage.getRecords(), unifiedManageUserPage.getTotal());
        } else if (NumCode.ONE.getCode() == req.getProtal()) {//查询核能商城用户管理
            UserPageDBInfo userPageListDBVO = new UserPageDBInfo();
            BeanUtils.copyProperties(req, userPageListDBVO);
            List<Integer> unruleNums = req.getUnruleNums();
            List<Integer> list = new ArrayList<>();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(unruleNums)) {
                if (unruleNums.contains(0)) { //0 ---0次
                    list.add(0);
                }
                if (unruleNums.contains(1)) {// 1--- 1~3次
                    list.addAll(Arrays.asList(1, 2, 3));
                }
                if (unruleNums.contains(2)) {// 2--- 4~10次
                    list.addAll(Arrays.asList(4, 5, 6, 7, 8, 9, 10));
                }
                if (unruleNums.contains(3)) {// 3--- 10次以上
                    userPageListDBVO.setTenFlag(1);
                }
            }
            userPageListDBVO.setPageNo(Math.max((req.getPageNo() - 1) * req.getPageSize(), 0));

            userPageListDBVO.setUnruleNums(null);
            //前端传全部资质的时候list里面为空得处理
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userPageListDBVO.getUserCertificate()) && userPageListDBVO.getUserCertificate().contains(null))
                userPageListDBVO.setUserCertificate(null);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list))
                userPageListDBVO.setUnruleNums(list);
            List<UserPageInfo> data = userMapper.getEnergyMallUserList(userPageListDBVO);

            //判断是否有处理记录
            List<String> userIds = new ArrayList<>();
            data.forEach(item -> {
                userIds.add(item.getUserId());
            });
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIds)) {
                BusinessIdsReq businessIdsReq = new BusinessIdsReq();
                businessIdsReq.setBusinessIds(userIds);
                businessIdsReq.setBusinessType(1);
                businessIdsReq.setBusinessTypeList(Arrays.asList(1, 9, 10));
                List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

                for (UserPageInfo userPageListResp : data) {
                    long count = processingRecordRespList.stream().filter(x -> x.getBusinessId().equals(userPageListResp.getUserId())).count();
                    if (count > 0) {
                        userPageListResp.setHasRecords(true);
                    }
                }
            }

            PageResult<UserPageInfo> pageResult = new PageResult<>();
            if (req.getIsNotExport()) {
                Long num = userMapper.getEnergyMallUserCount(userPageListDBVO);
                pageResult.setTotal(num);
            }
            pageResult.setList(data);
            return pageResult;
        } else if (NumCode.TWO.getCode() == req.getProtal() || NumCode.THREE.getCode() == req.getProtal()) {
            ManageUserInfo manageUserInfo = new ManageUserInfo();
            //查询chatbot或硬核桃用户管理
            req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            BeanUtils.copyProperties(req, manageUserInfo);
            List<UserInfo> data = userMapper.getChatBotAndHardWalnutsUserList(manageUserInfo);
            Long num = userMapper.getChatBotAndHardWalnutsUserCount(manageUserInfo);
            PageResult<UserInfo> pageResult = new PageResult<>();
            pageResult.setList(data);
            pageResult.setTotal(num);
            return pageResult;
        } else if (NumCode.FOUR.getCode() == req.getProtal()) {//硬核桃社区
            PageResult<UserInfo> pageResult = new PageResult<>();
            req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            ManageUserInfo manageUserInfo = new ManageUserInfo();
            BeanUtils.copyProperties(req, manageUserInfo);
            List<UserInfo> data = userMapper.getCommunityUserList(manageUserInfo);
            Long num = userMapper.getCommunityUserListCount(manageUserInfo);
            pageResult.setList(data);
            pageResult.setTotal(num);
            return pageResult;
        } else if (NumCode.FIVE.getCode() == req.getProtal()) {//CSP用户
            PageResult<UserInfo> pageResult = new PageResult<>();
            req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            ManageUserInfo manageUserInfo = new ManageUserInfo();
            BeanUtils.copyProperties(req, manageUserInfo);
            List<UserInfo> data = userMapper.getCSPUserList(manageUserInfo);
            Long num = userMapper.getCSPUserCount(manageUserInfo);
            fillMeal(data);
            pageResult.setList(data);
            pageResult.setTotal(num);
            return pageResult;
        } else if (NumCode.SEVEN.getCode() == req.getProtal()) {//核能商城
            UserPageDBInfo userPageListDBVO = new UserPageDBInfo();
            BeanUtils.copyProperties(req, userPageListDBVO);
            List<Integer> unruleNums = req.getUnruleNums();
            List<Integer> list = new ArrayList<>();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(unruleNums)) {
                if (unruleNums.contains(0)) { //0 ---0次
                    list.add(0);
                }
                if (unruleNums.contains(1)) {// 1--- 1~3次
                    list.addAll(Arrays.asList(1, 2, 3));
                }
                if (unruleNums.contains(2)) {// 2--- 4~10次
                    list.addAll(Arrays.asList(4, 5, 6, 7, 8, 9, 10));
                }
                if (unruleNums.contains(3)) {// 3--- 10次以上
                    userPageListDBVO.setTenFlag(1);
                }
            }
            userPageListDBVO.setPageNo(Math.max((req.getPageNo() - 1) * req.getPageSize(), 0));

            userPageListDBVO.setUnruleNums(null);
            //前端传全部资质的时候list里面为空得处理
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userPageListDBVO.getUserCertificate()) && userPageListDBVO.getUserCertificate().contains(null))
                userPageListDBVO.setUserCertificate(null);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list))
                userPageListDBVO.setUnruleNums(list);
            List<UserPageInfo> data = userMapper.getMallUserList(userPageListDBVO);

            //判断是否有处理记录
            List<String> userIds = new ArrayList<>();
            data.forEach(item -> {
                userIds.add(item.getUserId());
            });
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIds)) {
                BusinessIdsReq businessIdsReq = new BusinessIdsReq();
                businessIdsReq.setBusinessIds(userIds);
                businessIdsReq.setBusinessType(1);
                businessIdsReq.setBusinessTypeList(Arrays.asList(1, 9, 10));
                List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

                for (UserPageInfo userPageListResp : data) {
                    long count = processingRecordRespList.stream().filter(x -> x.getBusinessId().equals(userPageListResp.getUserId())).count();
                    if (count > 0) {
                        userPageListResp.setHasRecords(true);
                    }
                }
            }

            PageResult<UserPageInfo> pageResult = new PageResult<>();
            if (req.getIsNotExport()) {
                Long num = userMapper.getMallUserListCount(userPageListDBVO);
                pageResult.setTotal(num);
            }
            pageResult.setList(data);
            return pageResult;
        } else {
            throw new BizException(AuthCenterError.PARAMETER_BAD);
        }
    }


    private void fillMeal(List<UserInfo> data) {
        for (UserInfo user : data) {
            //1、客户数量
            String cspId = user.getCspId();
            Long customerNum = cspCustomerService.countActiveCustomerByCspId(cspId);
            user.setCspCustomerNum(customerNum);
            //2、合同套餐数量
            Long mealNumMun = mealContractApi.countCurrentMealNumMunByCspId(cspId);
            user.setCspMealCustomerNum(mealNumMun);
            Integer status = customerNum > mealNumMun ? 1 : 0;
            //3、当前状态和数据库状态不一样，修改数据库状态
            if (!status.equals(user.getCspMealStatus())) {
                user.setCspMealStatus(status);
                userMapper.changeCspMealStatusByUserId(user.getUserId(), status);
            }
        }
    }


    @Override
    public PageResult<UserInfo> getPlatformApplicationReview(PlatformApplicationReviewReq req) {
        req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
        PlatformApplicationReviewInfo platformApplicationReviewInfo = new PlatformApplicationReviewInfo();
        BeanUtils.copyProperties(req, platformApplicationReviewInfo);
        List<UserInfo> data = userMapper.getgetPlatformApplicationReviewList(platformApplicationReviewInfo);
        Long num = userMapper.getPlatformApplicationReviewCount(platformApplicationReviewInfo);
        PageResult<UserInfo> pageResult = new PageResult<>();
        pageResult.setList(data);
        pageResult.setTotal(num);
        return pageResult;
    }

    @Override
    public EnableOrDisableResp enableOrDisableUser(EnableOrDisableReq req) {
        UserPlatformPermissionsDo permission = new UserPlatformPermissionsDo();
        BeanUtils.copyProperties(req, permission);
        LambdaUpdateWrapper<UserPlatformPermissionsDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserPlatformPermissionsDo::getUserId, req.getUserId())
                .eq(UserPlatformPermissionsDo::getProtal, req.getProtal());
        if (NumCode.ONE.getCode() != userPlatformPermissionsDao.update(permission, updateWrapper)) {
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        }

        //添加处理记录
        String processingContent = ProcessingContentEnum.JYYH.getName();
        if (req.getUserStatus().equals(NumCode.ONE.getCode())) {
            processingContent = ProcessingContentEnum.QYYH.getName();
        }
        identificationService.addProcessingRecord(req.getUserId(), processingContent, BusinessTypeEnum.YHGL_HN.getCode());
        EnableOrDisableResp resp = new EnableOrDisableResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public GetUserInfoResp getUserInfo(GetUserInfoReq req) {
        GetUserInfoResp resp = new GetUserInfoResp();
        UserIdInfo userIdInfo = new UserIdInfo();
        userIdInfo.setUserId(req.getUserId());
        List<UserIdAndNameInfo> userIdAndNameInfoList = userMapper.selectUserInfoByUserId(userIdInfo);
        resp.setUserIdAndNameInfos(userIdAndNameInfoList);
        return resp;
    }

    @Override
    public GetEnterpriseUserListResp getEnterpriseUserList() {
        GetEnterpriseUserListResp resp = new GetEnterpriseUserListResp();
        QueryWrapper<UserDo> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("enterprise_auth_status", 3);
        queryWrapper1.eq("deleted", 0);
        List<UserDo> userDoList = userDao.selectList(queryWrapper1);

        QueryWrapper<UserPlatformPermissionsDo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("protal", 1);
        queryWrapper2.eq("deleted", 0);
        List<UserPlatformPermissionsDo> userPlatformPermissionsDoList = userPlatformPermissionsDao.selectList(queryWrapper2);

        QueryWrapper<UserCertificateOptionsDo> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("certificate_apply_status", 3);
        queryWrapper3.eq("certificate_status", 0);
        queryWrapper3.eq("deleted", 0);
        List<UserCertificateOptionsDo> userCertificateDoList = userCertificateOptionsDao.selectList(queryWrapper3);

        QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.eq("enterprise_auth_status", 3);
        queryWrapper4.eq("deleted", 0);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDoList = userEnterpriseIdentificationDao.selectList(queryWrapper4);

        List<UserInfo> userRespList = new ArrayList<>();
        userDoList.forEach(item -> {
            UserInfo userResp = new UserInfo();
            BeanUtil.copyProperties(item, userResp);
            if (StringUtils.isNotEmpty(item.getSpTel())) {
                userResp.setIsSetSp(true);
            } else {
                userResp.setIsSetSp(false);
            }
            userResp.setIsDisable(false);
            userResp.setIsProvider(false);

            for (UserPlatformPermissionsDo userPlatformPermissionsDo : userPlatformPermissionsDoList) {
                if (item.getUserId().equals(userPlatformPermissionsDo.getUserId())) {
                    if (userPlatformPermissionsDo.getUserStatus().equals(NumCode.TWO.getCode())) {
                        userResp.setIsDisable(true);
                        break;
                    }
                }
            }

            for (UserCertificateOptionsDo userCertificateOptionsDo : userCertificateDoList) {
                if (item.getUserId().equals(userCertificateOptionsDo.getUserId())) {
                    if (userCertificateOptionsDo.getCertificateId().equals(QualificationType.ABILITY_SUPPLIER.getCode())) {
                        userResp.setIsProvider(true);
                        break;
                    }
                }
            }

            for (UserEnterpriseIdentificationDo userEnterpriseIdentificationDo : userEnterpriseIdentificationDoList) {
                if (item.getUserId().equals(userEnterpriseIdentificationDo.getUserId())) {
                    userResp.setEnterpriseName(userEnterpriseIdentificationDo.getEnterpriseName());
                    break;
                }
            }
            userRespList.add(userResp);
        });
        resp.setUserInfos(userRespList);
        return resp;
    }

    @Override
    public GetUserRoleListResp getUserRoleList() {
        GetUserRoleListResp resp = new GetUserRoleListResp();
        LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.orderByDesc(AdminRoleDo::getCreateTime);
        List<AdminRoleItem> result = new ArrayList<>();
        List<AdminRoleDo> adminRoleList = adminRoleDao.selectList(queryWrapper);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(adminRoleList)) {
            result = JSONObject.parseArray(JSON.toJSONString(adminRoleList), AdminRoleItem.class);
        }
        resp.setAdminRoleItems(result);
        return resp;
    }

    @Override
    public PageResult<UserAndRoleDto> getOperatorList(GetOperatorListReq req) {
        OperatorDto operatorDto = new OperatorDto();
        BeanUtils.copyProperties(req, operatorDto);
        List<UserAndRoleDto> data = adminUserMapper.selectOperatorList(operatorDto);
        int toIndex = req.getPageNo() * req.getPageSize();
        int from = (req.getPageNo() - 1) * req.getPageSize();
        List<UserAndRoleDto> resultList = data.subList(Math.min(data.size(), from), Math.min(data.size(), toIndex));
        PageResult<UserAndRoleDto> result = new PageResult<>();
        result.setTotal((long) data.size());
        result.setList(resultList);
        return result;
    }

    @Override
    public EnableOrDisableAdminUserResp enableOrDisableAdminUser(EnableOrDisableAdminUserReq req) {
        LambdaUpdateWrapper<AdminUserDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AdminUserDo::getUserId, req.getCode());
        AdminUserDo adminUserDo = new AdminUserDo().setUserStatus(req.getStatus());
        if (NumCode.ONE.getCode() != adminUserDao.update(adminUserDo, wrapper))
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(req.getCode());
        Map<String, List<String>> url = userMenu(data);
        redisTemplate.opsForValue().set("USER_ID:" + req.getCode(), url);
        EnableOrDisableAdminUserResp resp = new EnableOrDisableAdminUserResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    @Transactional()
    public EditOperatorResp editOperator(EditOperatorReq req) {
        String newAccount = req.getAccountName();
        String newPhone = req.getPhone();
        Matcher m = pattern.matcher(req.getAccountName().trim());
        if (m.find()) {
            throw new BizException(AuthCenterError.CAN_NOT_HAVE_CHINESE);
        }
        AdminUserDo userAccountName = adminUserDao.selectOne(AdminUserDo::getAccountName, req.getAccountName());
        AdminUserDo userPhone = adminUserDao.selectOne(AdminUserDo::getPhone, req.getPhone());
        //查询user表是否已注册
        UserDo normalUser = userService.findByPhone(req.getPhone());
        String userId;
        //有该运营人员 编辑
        if (StringUtils.isNotBlank(req.getUserId())) {
            if (null != userAccountName && !req.getUserId().equalsIgnoreCase(userAccountName.getUserId())) {
                throw new BizException(AuthCenterError.ACCOUNTNAME_EXISTS);
            }
            if (null != userPhone && !req.getUserId().equalsIgnoreCase(userPhone.getUserId())) {
                throw new BizException(AuthCenterError.PHONE_EXISTS);
            }
            userId = req.getUserId();
            AdminUserDo adminUserDo = adminUserDao.selectOne(AdminUserDo::getUserId, userId);
            String oldAccount = adminUserDo.getAccountName();
            String oldPhone = adminUserDo.getPhone();
            LambdaUpdateWrapper<AdminUserDo> wrapper = new LambdaUpdateWrapper<>();
            AdminUserDo user = new AdminUserDo()
                    .setAccountName(req.getAccountName())
                    .setFullName(req.getFullName())
                    .setPhone(req.getPhone());
            wrapper.eq(AdminUserDo::getUserId, userId);
            if (NumCode.ONE.getCode() != adminUserDao.update(user, wrapper)) {
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
            }

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(req.getRoleIdList())) {
                List<AdminUserRoleDo> adminUserRoleList = adminUserRoleDao.selectList(AdminUserRoleDo::getUserId, req.getUserId());
                List<String> oldRoleIdList = adminUserRoleList.stream().map(AdminUserRoleDo::getRoleId).collect(Collectors.toList());
                List<String> newRoleIdList = req.getRoleIdList();
                List<String> removeRoleIdList = oldRoleIdList.stream().filter(i -> !newRoleIdList.contains(i)).collect(Collectors.toList());
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(removeRoleIdList)) {
                    if (adminUserMapper.deleteRoleList(removeRoleIdList, req.getUserId()) < NumCode.ONE.getCode()) {
                        throw new BizException(AuthCenterError.Execute_SQL);
                    }
                }
                List<String> saveRoleIdList = newRoleIdList.stream().filter(i -> !oldRoleIdList.contains(i)).collect(Collectors.toList());
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(saveRoleIdList)) {
                    List<AdminUserRoleDo> list = new ArrayList<>();
                    saveRoleIdList.forEach(i -> list.add(new AdminUserRoleDo().setRoleId(i).setUserId(userId).setDeleted(0)));
                    adminUserRoleDao.insertBatch(list);
                }
            } else {
                adminUserMapper.deleteRoleByUserId(req.getUserId());
            }
            //没有该运营人员 新增
        } else {
            userId = AuthUtils.randomID(10);
            if (null != userAccountName) {
                throw new BizException(AuthCenterError.ACCOUNTNAME_EXISTS);
            }
            if (null != userPhone) {
                throw new BizException(AuthCenterError.PHONE_EXISTS);
            }
            AdminUserDo adminUserDo = new AdminUserDo();
            BeanUtils.copyProperties(req, adminUserDo);
            adminUserDo.setUserStatus(NumCode.ONE.getCode()).setUserId(userId);
            if (NumCode.ONE.getCode() != adminUserDao.insert(adminUserDo)) {
                throw new BizException(AuthCenterError.Execute_SQL_SAVE);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(req.getRoleIdList())) {
                List<AdminUserRoleDo> list = new ArrayList<>();
                req.getRoleIdList().forEach(i -> list.add(new AdminUserRoleDo().setRoleId(i).setUserId(userId)));
                list.forEach(i -> i.setDeleted(0));
                adminUserRoleDao.insertBatch(list);
            }
        }
        //查询该用户所有资源刷新redis里面数据
        List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(userId);
        Map<String, List<String>> url = userMenu(data);
        redisTemplate.opsForValue().set("USER_ID:" + userId, url);
        EditOperatorResp resp = new EditOperatorResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public PageResult getRolePage(GetRolePage req) {
        PageParam page = new PageParam();
        page.setPageNo(req.getPageNo());
        page.setPageSize(req.getPageSize());
        LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.likeIfPresent(AdminRoleDo::getRoleName, req.getKeyWord())
                .eqIfPresent(AdminRoleDo::getRoleStatus, req.getStatus())
                .inIfPresent(AdminRoleDo::getRoleId, req.getRoleIdList())
                .orderByDesc(AdminRoleDo::getCreateTime);
        return adminRoleDao.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional()
    public EnableOrDisableRoleResp enableOrDisableRole(EnableOrDisableRoleReq req) {
        LambdaUpdateWrapper<AdminRoleDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AdminRoleDo::getRoleId, req.getCode());
        AdminRoleDo adminRoleDo = new AdminRoleDo().setRoleStatus(req.getStatus());
        adminRoleDao.update(adminRoleDo, wrapper);
        List<AdminUserRoleDo> roleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getCode());
        //刷新redis里面用户权限
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleList)) {
            roleList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i.getUserId());
                Map<String, List<String>> url = userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i.getUserId(), url);
            });
        }
        EnableOrDisableRoleResp resp = new EnableOrDisableRoleResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public EditRoleResp editRole(EditRoleReq req) {
        AdminRoleDo role = adminRoleDao.selectOne(AdminRoleDo::getRoleName, req.getRoleName());
        AdminRoleDo adminRoleDo = new AdminRoleDo();
        BeanUtils.copyProperties(req, adminRoleDo);
        if (StringUtils.isNotBlank(req.getRoleId())) {
            if (null != role && !req.getRoleId().equalsIgnoreCase(role.getRoleId()))
                throw new BizException(AuthCenterError.ROLENAME_EXISTS);
            LambdaUpdateWrapper<AdminRoleDo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AdminRoleDo::getRoleId, req.getRoleId());
            if (NumCode.ONE.getCode() != adminRoleDao.update(adminRoleDo, wrapper))
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        } else {
            if (null != role) throw new BizException(AuthCenterError.ROLENAME_EXISTS);
            LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(AdminRoleDo::getRoleName, req.getRoleName());
            AdminRoleDo roleDo = adminRoleDao.selectOne(queryWrapper);
            if (null != roleDo) throw new BizException(AuthCenterError.ROLE_REPETITION);
            adminRoleDo.setRoleId(java.util.UUID.randomUUID().toString().replaceAll("-", ""))
                    .setRoleStatus(NumCode.ONE.getCode());
            adminRoleDao.insert(adminRoleDo);
        }
        EditRoleResp resp = new EditRoleResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    @Transactional()
    public RemoveRoleResp removeRole(RemoveRoleReq req) {
        //查询角色受影响的所有userId
        List<AdminUserRoleDo> roleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getCode());
//        CodeDto codeDto = new CodeDto();
//        req.setCode(req.getCode());
//        adminUserMapper.deleteRole(codeDto);
        deleteAdminRole(req.getCode());//删除角色
        //刷新redis里面用户权限
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleList)) {
            //删除角色关联的管理员
            LambdaQueryWrapperX<AdminUserRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(AdminUserRoleDo::getRoleId, req.getCode());
            adminUserRoleDao.delete(queryWrapper);
            roleList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i.getUserId());
                Map<String, List<String>> url = userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i.getUserId(), url);
            });
        }
        RemoveRoleResp resp = new RemoveRoleResp();
        resp.setResult(true);
        return resp;
    }

    private void deleteAdminRole(String code) {
        if (!Strings.isNullOrEmpty(code)) {
            LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(AdminRoleDo::getRoleId, code);
            List<AdminRoleDo> adminRoleDos = adminRoleDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(adminRoleDos)) {
                adminRoleDao.delete(queryWrapper);
            }
        }
    }

    @Override
    @Transactional()
    public GetMemberByRoleIdResp getMemberByRoleId(GetMemberByRoleIdReq req) {
        List<AdminUserInfo> adminUserInfos = null;
        if (StringUtils.isBlank(req.getCode())) {
            List<AdminUserDo> adminUserDos = adminUserDao.selectList();
            adminUserInfos = JSONObject.parseArray(JSON.toJSONString(adminUserDos), AdminUserInfo.class);
        }
        List<AdminUserRoleDo> list = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getCode());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            List<String> collect = list.stream().map(i -> i.getUserId()).collect(Collectors.toList());
            LambdaQueryWrapperX<AdminUserDo> userWrapper = new LambdaQueryWrapperX<>();
            userWrapper.inIfPresent(AdminUserDo::getUserId, collect);
            List<AdminUserDo> adminUserDos = adminUserDao.selectList(userWrapper);
            adminUserInfos = JSONObject.parseArray(JSON.toJSONString(adminUserDos), AdminUserInfo.class);
        }
        /**
         * #8367
         * 新增某个角色，进入时不全选
         */
//        else {
//            LambdaQueryWrapperX<AdminUserDo> userWrapper = new LambdaQueryWrapperX<>();
//            List<AdminUserDo> adminUserDos = adminUserDao.selectList(userWrapper);
//            adminUserInfos = JSONObject.parseArray(JSON.toJSONString(adminUserDos), AdminUserInfo.class);
//        }
        GetMemberByRoleIdResp resp = new GetMemberByRoleIdResp();
        resp.setAdminUserInfos(adminUserInfos);
        return resp;
    }

    @Override
    @Transactional()
    public RoleConfigurationMemberResp roleConfigurationMember(RoleConfigurationMemberReq req) {
        if ("admin".equalsIgnoreCase(req.getRoleId())) req.getUserIdList().add("admin");
        //根据角色查询所有成员
        List<AdminUserRoleDo> roleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getRoleId());
        //所有受影响的userid
        List<String> userList = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleList)) {
            List<String> oldUserIdList = roleList.stream().map(AdminUserRoleDo::getUserId).collect(Collectors.toList());
            List<String> newUserIdList = req.getUserIdList();
            userList = Stream.of(oldUserIdList, newUserIdList).flatMap(Collection::stream).distinct().collect(Collectors.toList());
            //求差集(删除)
            List<String> removeList = oldUserIdList.stream().filter(i -> !newUserIdList.contains(i)).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(removeList)) {
                if (adminUserMapper.deleteRoleByUserIdList(req.getRoleId(), removeList) < NumCode.ONE.getCode())
                    throw new BizException(AuthCenterError.Execute_SQL_DELETE);
            }
            List<String> saveList = newUserIdList.stream().filter(i -> !oldUserIdList.contains(i)).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(saveList)) {
                List<AdminUserRoleDo> list = new ArrayList<>();
                saveList.forEach(i -> list.add(new AdminUserRoleDo().setRoleId(req.getRoleId()).setUserId(i).setDeleted(0)));
                adminUserRoleDao.insertBatch(list);
//                if (!saveBatch(list)) throw new BizException(AuthCenterError.Execute_SQL_SAVE);
            }
        } else {
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(req.getUserIdList())) {
                userList = req.getUserIdList();
                List<AdminUserRoleDo> list = new ArrayList<>();
                req.getUserIdList().forEach(i -> list.add(new AdminUserRoleDo().setRoleId(req.getRoleId()).setUserId(i).setDeleted(0)));
                adminUserRoleDao.insertBatch(list);
//                if (!saveBatch(list)) throw new BizException(AuthCenterError.Execute_SQL_SAVE);
            }
        }
        //刷新redis用户权限
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userList)) {
            userList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i);
                Map<String, List<String>> url = userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i, url);
            });
        }
        RoleConfigurationMemberResp resp = new RoleConfigurationMemberResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public GetMenuByRoleIdResp getMenuByRoleId(GetMenuByRoleIdReq req) {
        GetMenuByRoleIdResp resp = new GetMenuByRoleIdResp();
        LambdaQueryWrapperX<AdminMenuDo> wrapper = new LambdaQueryWrapperX<>();
        List<AdminMenuDo> menuDoList = adminMenuDao.selectList(wrapper);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(menuDoList)) {
            List<RoleAndMenuItem> result = JSONObject.parseArray(JSON.toJSONString(menuDoList), RoleAndMenuItem.class);
            LambdaQueryWrapperX<AdminRoleMenuDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(AdminRoleMenuDo::getRoleId, req.getCode());
            List<AdminRoleMenuDo> roleMenuList = adminRoleMenuDao.selectList(queryWrapper);
            List<String> menuList = roleMenuList.stream().map(AdminRoleMenuDo::getMenuCode).collect(Collectors.toList());
            List<RoleAndMenuItem> collect = result.stream().filter(i -> Objects.equals(0, i.getLevel())).map(item -> {
                RoleAndMenuItem roleAndMenuItem = new RoleAndMenuItem();
                BeanUtils.copyProperties(item, roleAndMenuItem);
                if (menuList.contains(item.getMenuCode())) roleAndMenuItem.setChoose(true);
                roleAndMenuItem.setChildren(this.children(item, result, menuList));
                return roleAndMenuItem;
            }).collect(Collectors.toList());
            resp.setRoleAndMenuItems(collect);
        }
        return resp;
    }

    @Override
    @Transactional()
    public RoleConfigurationMenuResp roleConfigurationMenu(RoleConfigurationMenuReq req) {
        //查询受影响的用户 用于刷新redis数据
        List<AdminUserRoleDo> adminUserRoleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getRoleId());
        //根据角色查询所有菜单资源
        List<AdminRoleMenuDo> roleList = adminRoleMenuDao.selectList(AdminRoleMenuDo::getRoleId, req.getRoleId());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleList)) {
            List<String> oldMenuCodeList = roleList.stream().map(AdminRoleMenuDo::getMenuCode).collect(Collectors.toList());
            List<String> newMenuCodeList = req.getMenuCodeList();
            //求差集(删除)
            List<String> removeList = oldMenuCodeList.stream().filter(i -> !newMenuCodeList.contains(i)).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(removeList)) {
                if (adminUserMapper.deleteRoleMenu(removeList, req.getRoleId()) == NumCode.ZERO.getCode())
                    throw new BizException(AuthCenterError.Execute_SQL_DELETE);
            }
            List<String> saveList = newMenuCodeList.stream().filter(i -> !oldMenuCodeList.contains(i)).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(saveList)) {
                List<AdminRoleMenuDo> list = new ArrayList<>();
                saveList.forEach(i -> list.add(new AdminRoleMenuDo().setRoleId(req.getRoleId()).setMenuCode(i).setDeletedTime(0L).setDeleted(NumCode.ZERO.getCode())));
                if (list.size() != adminRoleMenuDao.insertBatch(list))
                    throw new BizException(AuthCenterError.Execute_SQL_SAVE);
            }
        } else {
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(req.getMenuCodeList())) {
                List<AdminRoleMenuDo> list = new ArrayList<>();
                req.getMenuCodeList().forEach(i -> list.add(
                        new AdminRoleMenuDo()
                                .setRoleId(req.getRoleId())
                                .setMenuCode(i)
                                .setDeleted(NumCode.ZERO.getCode())
                                .setDeletedTime(0L)
                ));
                if (list.size() != adminRoleMenuDao.insertBatch(list))
                    throw new BizException(AuthCenterError.Execute_SQL_SAVE);
            }
        }
        //刷新redis存的用户权限
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(adminUserRoleList)) {
            adminUserRoleList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i.getUserId());
                Map<String, List<String>> url = userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i.getUserId(), url);
            });
        }
        RoleConfigurationMenuResp resp = new RoleConfigurationMenuResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public List<UserExcel> exportUserExcel(QueryManageUserReq req) {
        List<UserExcel> userExcelList = new ArrayList<>();
        PageResult userPageList = getManageUser(req);
        List<UserExcelDataInfo> data = JSONObject.parseArray(JSON.toJSONString(userPageList.getList()), UserExcelDataInfo.class);
        if (com.alibaba.nacos.common.utils.CollectionUtils.isNotEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                UserExcelDataInfo resp = data.get(i);
                UserExcel userExcel = new UserExcel();
                BeanUtils.copyProperties(resp, userExcel);
                userExcel.setIndex(i + 1)
                        .setUserStatus(1 == resp.getUserStatus() ? "可用" : 0 == resp.getUserStatus() ? "禁用" : null)
                        .setUserCertificateApplyStatus(0 == resp.getUserCertificateApplyStatus() ? "未认证" :
                                1 == resp.getUserCertificateApplyStatus() ? "待审核" :
                                        2 == resp.getUserCertificateApplyStatus() ? "审核不通过" :
                                                3 == resp.getUserCertificateApplyStatus() ? "审核通过" : null);
                userExcelList.add(userExcel);
            }
        }
        return userExcelList;
    }

    /**
     * 更新用户违规记录表，--核能更新！！！！！
     *
     * @param req 请求信息
     * @return 信息
     */
    @Override
    public UpdateUserViolationResp updateUserViolation(UpdateUserViolationReq req) {
        UpdateUserViolationResp resp = new UpdateUserViolationResp();
        //查询用户的违规信息
        List<UserViolationDo> userViolationDos = userViolationService.selectListByInfo(req.getUserId(), req.getPlate(), req.getViolationType());
        if (!CollectionUtils.isEmpty(userViolationDos)) {//已有记录
            UserViolationDo userViolationDo = userViolationDos.get(0);
            Integer num = userViolationDo.getViolationNum();
            if (req.getIsCancel() != null && req.getIsCancel()) {
                if (num > 0) {
                    userViolationDo.setViolationNum(num - 1);
                } else {
                    throw new BizException(AuthCenterError.USER_NOT_HAVE_VIOLATION);
                }
            } else {
                userViolationDo.setViolationNum(num + 1);
            }
            userViolationDo.setUpdater(SessionContextUtil.getUser().getUserId());
            userViolationDo.setUpdateTime(new Date());
            userViolationDao.updateById(userViolationDo);
        } else {//无记录
            if (req.getIsCancel() != null && req.getIsCancel()) {
                throw new BizException(AuthCenterError.USER_NOT_HAVE_VIOLATION);
            } else {
                UserViolationDo userViolationDo = new UserViolationDo();
                userViolationDo.setUserId(req.getUserId());
                userViolationDo.setViolationType(req.getViolationType() == null ? 0 : req.getViolationType());
                userViolationDo.setPlate(req.getPlate());
                userViolationDo.setViolationNum(1);
                userViolationDo.setDeleted(0);
                userViolationDo.setDeletedTime((new Date()).getTime());
                userViolationDo.setCreator(SessionContextUtil.getUser().getUserId());
                userViolationDo.setCreateTime(new Date());
                userViolationDao.insert(userViolationDo);
            }
        }
        resp.setResult(true);
        return resp;
    }

    @Override
    public QueryUserViolationResp queryUserViolation(QueryUserViolationReq req) {
        QueryUserViolationResp resp = new QueryUserViolationResp();
        //查询用户的违规信息
        List<UserViolationDo> userViolationDos = userViolationService.selectListByInfo(req.getUserId(), req.getPlate(), req.getViolationType());
        if (!CollectionUtils.isEmpty(userViolationDos)) {
            resp.setViolationTotal(userViolationDos.stream().mapToInt(UserViolationDo::getViolationNum).sum());
        } else {
            resp.setViolationTotal(0);
        }
        return resp;
    }

    @Override
    @Transactional
    public QueryAdminAuthListResp queryAdminAuthList() {
        QueryAdminAuthListResp resp = new QueryAdminAuthListResp();
        //根据管理员ID获取管理员角色
        List<String> roleIdList = adminUserService.findAdminRoleList(SessionContextUtil.getUser().getUserId());
        if (!CollectionUtils.isEmpty(roleIdList)) {
            //根据角色获取角色权限
            List<AdminMenuDo> adminMenuDos = adminUserService.findAuthUrlListByRoleIds(roleIdList);
            if (!CollectionUtils.isEmpty(adminMenuDos)) {
                Map<String, List<AuthUrlItem>> authUrlItemMap = new HashMap<>();
                Map<String, AdminMenuDo> adminMenuDoMap = new HashMap<>();
                adminMenuDos.forEach(i -> adminMenuDoMap.put(i.getMenuCode(), i));
                String key;
                List<AuthUrlItem> authUrlItems;
                AuthUrlItem authUrlItem;
                for (AdminMenuDo item : adminMenuDos) {
                    key = getRootKey(adminMenuDoMap, item);
                    if (!key.equals(item.getMenuCode())) {
                        authUrlItem = new AuthUrlItem();
                        authUrlItem.setIcon(item.getIcon());
                        authUrlItem.setName(item.getMenuName());
                        authUrlItem.setUrl(item.getMenuUrl());
                        if (authUrlItemMap.containsKey(key)) {
                            authUrlItems = authUrlItemMap.get(key);
                            authUrlItems.add(authUrlItem);
                        } else {
                            authUrlItems = new ArrayList<>();
                            authUrlItems.add(authUrlItem);
                            authUrlItemMap.put(key, authUrlItems);
                        }
                    }
                }
                AuthUrlInfo authUrl = new AuthUrlInfo();
                authUrl.setAuthUrlItemMap(authUrlItemMap);
                resp.setAuthUrl(authUrl);
            }
        }
        return resp;
    }

    @Override
    public PageResult<UserInfo> queryCommunityUserList(QueryCommunityUserListReq req) {
        PageResult<UserInfo> pageResult = new PageResult<>();
        req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
        ManageUserInfo manageUserInfo = new ManageUserInfo();
        BeanUtils.copyProperties(req, manageUserInfo);
        List<UserInfo> data = userMapper.getCommunityUserList(manageUserInfo);
        Long num = userMapper.getCommunityUserListCount(manageUserInfo);
        pageResult.setList(data);
        pageResult.setTotal(num);
        return pageResult;
    }

    @Override
    public List<UserExcel> exportCommunityUserExcel(QueryCommunityUserListReq req) {
        List<UserExcel> userExcelList = new ArrayList<>();
        PageResult<UserInfo> userPageList = queryCommunityUserList(req);
        List<UserExcelDataInfo> data = JSONObject.parseArray(JSON.toJSONString(userPageList.getList()), UserExcelDataInfo.class);
        if (com.alibaba.nacos.common.utils.CollectionUtils.isNotEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                UserExcelDataInfo resp = data.get(i);
                UserExcel userExcel = new UserExcel();
                BeanUtils.copyProperties(resp, userExcel);
                userExcel.setIndex(i + 1)
                        .setUserStatus(1 == resp.getUserStatus() ? "可用" : 0 == resp.getUserStatus() ? "禁用" : null);
                userExcelList.add(userExcel);
            }
        }
        return userExcelList;
    }

    @Override
    public QueryCommunityUserBaseInfoListResp queryCommunityUserBaseInfoList(QueryCommunityUserBaseInfoListReq req) {
        QueryCommunityUserBaseInfoListResp resp = new QueryCommunityUserBaseInfoListResp();
        List<String> userIds = req.getUserIds();
        if (!CollectionUtils.isEmpty(userIds)) {
            List<UserDo> userDos = userService.findUserByIds(userIds);
            if (!CollectionUtils.isEmpty(userDos)) {
                List<UserBaseInfo> userBaseInfos = new ArrayList<>();
                UserBaseInfo userBaseInfo;
                for (UserDo item : userDos) {
                    userBaseInfo = new UserBaseInfo();
                    userBaseInfo.setUserId(item.getUserId());
                    userBaseInfo.setName(item.getName());
                    userBaseInfo.setUserImgUuid(item.getUserImgUuid());
                    userBaseInfos.add(userBaseInfo);
                }
                resp.setUserBaseInfos(userBaseInfos);
            }
        }
        return resp;
    }


    @Override
    public QueryCommunityUserBaseInfoListResp queryCommunityAdminBaseInfoList(QueryCommunityUserBaseInfoListReq req) {
        QueryCommunityUserBaseInfoListResp resp = new QueryCommunityUserBaseInfoListResp();
        List<String> userIds = req.getUserIds();
        if (!CollectionUtils.isEmpty(userIds)) {
            LambdaQueryWrapper<AdminUserDo> queryWrapper = Wrappers.<AdminUserDo>lambdaQuery()
                    .in(AdminUserDo::getUserId, userIds);
            List<AdminUserDo> adminUserDos = adminUserDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(adminUserDos)) {
                List<UserBaseInfo> userBaseInfos = new ArrayList<>();
                UserBaseInfo userBaseInfo;
                for (AdminUserDo item : adminUserDos) {
                    userBaseInfo = new UserBaseInfo();
                    userBaseInfo.setUserId(item.getUserId());
                    userBaseInfo.setName(item.getAccountName());
                    userBaseInfo.setUserImgUuid("");
                    userBaseInfos.add(userBaseInfo);
                }
                resp.setUserBaseInfos(userBaseInfos);
            }
        }
        return resp;
    }

    @Override
    public AdminUserInfo queryCurrentCommunityAdminBaseInfo() {
        //获取当前登陆用户id
        String userId = SessionContextUtil.getUser().getUserId();
        AdminUserDo adminUserDo = adminUserDao.selectOne(AdminUserDo::getUserId, userId);
        if (Objects.isNull(adminUserDo)) {
            return null;
        }
        AdminUserInfo resp = BeanUtil.copyProperties(adminUserDo, AdminUserInfo.class);
        return resp;
    }

    @Override
    public AdminUserInfoResp getAdminUserInfoByUserId(String adminUserId) {
        AdminUserInfoResp resp = new AdminUserInfoResp();
        if (!Strings.isNullOrEmpty(adminUserId)) {
            QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("deleted", NumCode.ZERO.getCode());
            queryWrapper.eq("user_id", adminUserId);
            AdminUserDo adminUserDo = adminUserDao.selectOne(queryWrapper);
            if (adminUserDo != null) {
                BeanUtils.copyProperties(adminUserDo, resp);
            }
        }
        return resp;
    }

    @Override
    public AdminUserResp getAdminUserByUserId(UserIdReq req) {
        QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode());
        queryWrapper.eq("user_id", req.getUserId());
        AdminUserDo adminUserDo = adminUserDao.selectOne(queryWrapper);
        AdminUserResp resp = new AdminUserResp();
        if (adminUserDo != null) {
            BeanUtils.copyProperties(adminUserDo, resp);
        }
        return resp;
    }

    @Override
    public List<AdminUserResp> getAdminUserByUserId(Collection<String> userIds) {
        QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode());
        queryWrapper.in(!CollectionUtils.isEmpty(userIds), "user_id", userIds);
        List<AdminUserDo> adminUserDo = adminUserDao.selectList(queryWrapper);
        return adminUserDo.stream().filter(Objects::nonNull).map(s -> {
            AdminUserResp resp = new AdminUserResp();
            BeanUtils.copyProperties(s, resp);
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public ChannelInfoResp getCspUserChannelByUserId(String userId) {
        QueryWrapper<CspChannelDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CspChannelDo cspChannelDo = cspChannelDao.selectOne(queryWrapper);
        ChannelInfoResp resp = new ChannelInfoResp();
        if (cspChannelDo != null) {
            BeanUtils.copyProperties(cspChannelDo, resp);
        }
        return resp;
    }

    @Override
    public UpdateClientUserResp updateCspUserChannelByUserId(UpdateCspChannelReq req) {
        UpdateClientUserResp resp = new UpdateClientUserResp();
        LambdaUpdateWrapper<CspChannelDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CspChannelDo::getUserId, req.getUserId());
        CspChannelDo cspChannelDo = new CspChannelDo();
        BeanUtils.copyProperties(req, cspChannelDo);
        CspChannelDo exsitsChannel = cspChannelDao.selectOne(wrapper);
        cspChannelDao.update(cspChannelDo, wrapper);
        resp.setResult(true);
        String updateChannel = "";
        if ((int) exsitsChannel.getMobileChannel() != req.getMobileChannel()) {
            updateChannel = CarrierEnum.CMCC.getName();
        }
        if ((int) exsitsChannel.getUnicomChannel() != req.getUnicomChannel()) {
            updateChannel = CarrierEnum.Unicom.getName() + (StrUtil.isEmpty(updateChannel) ? "" : ("," + updateChannel));
        }
        if ((int) exsitsChannel.getTelecomChannel() != req.getTelecomChannel()) {
            updateChannel = CarrierEnum.Telecom.getName() + (StrUtil.isEmpty(updateChannel) ? "" : ("," + updateChannel));
        }
        if (StrUtil.isNotEmpty(updateChannel)) {
            addProcessingRecord(req.getUserId(), ProcessingContentEnum.BGTD.getName(), BusinessTypeEnum.YHGL_HN.getCode(), updateChannel);
        }
        return resp;
    }

    private void addProcessingRecord(String businessId, String processingContent, Integer businessType, String remark) {
        BaseUser baseUser = SessionContextUtil.getUser();
        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(businessId);
        processingRecordReq.setBusinessType(businessType);
        processingRecordReq.setProcessingContent(processingContent);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordReq.setRemark(remark);
        processingRecordApi.addRecord(processingRecordReq);
    }

    @Override
    public PageResult<CspCustomerChatbotAccountVo> getSupplierChatbot(QuerySupplierChatbotReq req) {
        //只有管理员才能查看
        checkUserPermission();

        //移动和电信对Chatbot状态定义是不一样的
        Integer chatbotStatuas = req.getChatbotStatus();
        //getChannelType 通道(0 all ,1蜂动电信 2蜂动移动)

        LambdaQueryWrapper<CspCustomerChatbotAccountDo> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(req.getChannelType() == 1, CspCustomerChatbotAccountDo::getAccountTypeCode, CSPOperatorCodeEnum.CT.getCode())
                .eq(req.getChannelType() == 2, CspCustomerChatbotAccountDo::getAccountTypeCode, CSPOperatorCodeEnum.CMCC.getCode())
                .eq(req.getChannelType() == 3, CspCustomerChatbotAccountDo::getAccountTypeCode, CSPOperatorCodeEnum.CUNC.getCode())
                .like(StringUtils.isNotBlank(req.getCustomerName()), CspCustomerChatbotAccountDo::getCustomerName, req.getCustomerName())
                .orderByDesc(CspCustomerChatbotAccountDo::getCreateTime)
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode());
        //如果getIdOrName不为空
        if (StringUtils.isNotBlank(req.getIdOrName())) {
            objectLambdaQueryWrapper
                    .and(wapper -> wapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotAccount, req.getIdOrName())
                            .or()
                            .like(CspCustomerChatbotAccountDo::getAccountName, req.getIdOrName()));
        }

        //  chatbotStatuas 状态  -1全部 ;   待处理：0;   上线：1;       已注销：2 ;   下线：3;   已驳回：4
        //  channelType 通道类型 (0 all ,1蜂动电信 2蜂动移动)
        switch (chatbotStatuas) {
            //全部状态
            case -1:
                //全部通道
                if (req.getChannelType() == 0) {
                    objectLambdaQueryWrapper
                            .ne(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_10_NO_COMMIT.getCode())
                            .ne(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_72_NO_COMMIT.getCode());
                }
                break;
            case 0:
//                待处理：0
                if (req.getChannelType() == 1 || req.getChannelType() == 3) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode());
                } else if (req.getChannelType() == 2) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode());
                } else {
                    //全部通道
                    objectLambdaQueryWrapper
                            .and(wapper -> wapper
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode())
                                    .or()
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode())
                            );
                }
                break;
            case 1:
//                上线：1;
                if (req.getChannelType() == 1 || req.getChannelType() == 3) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode());
                } else if (req.getChannelType() == 2) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode());
                } else {
                    //全部通道
                    objectLambdaQueryWrapper
                            .and(wapper -> wapper
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode())
                                    .or()
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode())
                            );
                }
                break;
            case 2:
                //已注销
                if (req.getChannelType() == 1 || req.getChannelType() == 3) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode());
                } else if (req.getChannelType() == 2) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode());
                } else {
                    //全部通道
                    objectLambdaQueryWrapper
                            .and(wapper -> wapper
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode())
                                    .or()
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode())
                            );
                }
                break;
            case 3:
//                下线：3;
                if (req.getChannelType() == 1 || req.getChannelType() == 3) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode());
                } else if (req.getChannelType() == 2) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode());
                } else {
                    //全部通道
                    objectLambdaQueryWrapper
                            .and(wapper -> wapper
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode())
                                    .or()
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode())
                            );
                }
                break;
            case 4:
//                已驳回：4
                if (req.getChannelType() == 1 || req.getChannelType() == 3) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_74_REJECT.getCode());
                } else if (req.getChannelType() == 2) {
                    objectLambdaQueryWrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_28_REJECT.getCode());
                } else {
                    //全部通道
                    objectLambdaQueryWrapper
                            .and(wapper -> wapper
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_74_REJECT.getCode())
                                    .or()
                                    .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_28_REJECT.getCode())
                            );
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + chatbotStatuas);
        }

        Page<CspCustomerChatbotAccountDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        Page<CspCustomerChatbotAccountDo> cspCustomerChatbotAccountDoPage = cspCustomerChatbotAccountDao.selectPage(page, objectLambdaQueryWrapper);
        List<CspCustomerChatbotAccountDo> records = cspCustomerChatbotAccountDoPage.getRecords();
        List<UserDo> cspList = userService.findUserByIds(records.stream().map(CspCustomerChatbotAccountDo::getCspId).collect(Collectors.toList()));
        HashMap<String, String> cspSet = new HashMap<>();
        for (UserDo userDo : cspList) {
            cspSet.put(userDo.getUserId(), userDo.getName());
        }

        List<CspCustomerChatbotAccountVo> cspCustomerChatbotAccountVoList = BeanUtil.copyToList(records, CspCustomerChatbotAccountVo.class);
        for (CspCustomerChatbotAccountVo cspCustomerChatbotAccountVo : cspCustomerChatbotAccountVoList) {
            String cspId = cspCustomerChatbotAccountVo.getCspId();
            cspCustomerChatbotAccountVo.setCspName(cspSet.get(cspId));
        }
        PageResult<CspCustomerChatbotAccountVo> pageResult = new PageResult<>();
        pageResult.setTotal(cspCustomerChatbotAccountDoPage.getTotal());
        pageResult.setList(cspCustomerChatbotAccountVoList);
        return pageResult;
    }

    @Override
    public ContractSupplierInfo getSupplerContractByChatbotId(String chatbotAccountId) {
//只有管理员才能查看
        checkUserPermission();
        //根据chatbotAccountId寻找Do
        CspCustomerChatbotAccountDo cspCustomerChatbotAccountDo = findCspCustomerChatbotAccountDo(chatbotAccountId);
        //如果为空,直接返回
        if (Objects.isNull(cspCustomerChatbotAccountDo)) {
            return null;
        }
        //根据Chatbot的CustomerId去找到对应的(蜂动)合同
        String customerId = cspCustomerChatbotAccountDo.getCustomerId();
        LambdaQueryWrapper<ContractManageDo> contractManageDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contractManageDoLambdaQueryWrapper
                .eq(ContractManageDo::getCustomerId, customerId)
                .eq(ContractManageDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                //非注销及暂停合同
                .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode())
                //"归属运营商编码：0：缺省(硬核桃)，1：联通，2：移动，3：电信"
                .eq(ContractManageDo::getOperatorCode, cspCustomerChatbotAccountDo.getAccountTypeCode())
                .eq(ContractManageDo::getDeleted, NumCode.ZERO.getCode());

        ContractManageDo contractManageDo = contractManageDao.selectOne(contractManageDoLambdaQueryWrapper);
        decode(contractManageDo);
        ContractSupplierInfo info = new ContractSupplierInfo();
        BeanUtils.copyProperties(contractManageDo, info);

        //设置客户企业账户名称
        LambdaQueryWrapper<UserEnterpriseIdentificationDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEnterpriseIdentificationDo::getUserId, customerId);
        queryWrapper.eq(UserEnterpriseIdentificationDo::getDeleted, 0);
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(queryWrapper);
        info.setEnterpriseAccountName(userEnterpriseIdentificationDo.getEnterpriseName());
        return info;
    }

    private void decode(ContractManageDo entity) {
        if (StrUtil.isNotBlank(entity.getWorkPhone()) && entity.getWorkPhone().length() > 50) {
            entity.setWorkPhone(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), entity.getWorkPhone()));
        }
        if (StrUtil.isNotBlank(entity.getContactPhoneNumber()) && entity.getContactPhoneNumber().length() > 50) {
            entity.setContactPhoneNumber(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), entity.getContactPhoneNumber()));
        }
        if (StrUtil.isNotBlank(entity.getContractLegalPersonCardNumber()) && entity.getContractLegalPersonCardNumber().length() > 50) {
            entity.setContractLegalPersonCardNumber(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), entity.getContractLegalPersonCardNumber()));
        }
    }

    @Override
    public SupplierChatbotInfoResp getChatbotById(String chatbotAccountId) {
        //只有管理员才能查看
        checkUserPermission();
        SupplierChatbotInfoResp response = new SupplierChatbotInfoResp();

        CspCustomerChatbotAccountDo cspCustomerChatbotAccountDo = findCspCustomerChatbotAccountDo(chatbotAccountId);
        //如果为空,直接返回
        BeanUtils.copyProperties(cspCustomerChatbotAccountDo, response);

        //客户id
        String customerId = cspCustomerChatbotAccountDo.getCustomerId();
        //找到客户名
        String customerName = cspCustomerService.getCustomerDetail(customerId).getName();
        response.setCustomerName(customerName);
        response.setCustomerId(customerId);

        //补充详情
        LambdaQueryWrapper<CmccNewDo> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        detailLambdaQueryWrapper.eq(CmccNewDo::getChatbotAccountId, chatbotAccountId);
        CmccNewDo detail = cmccNewDao.selectOne(detailLambdaQueryWrapper);
        BeanUtils.copyProperties(detail, response);


        LambdaQueryWrapper<ChatbotManageWhiteListDo> whiteListDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        whiteListDoLambdaQueryWrapper.eq(ChatbotManageWhiteListDo::getChatbotAccountId, chatbotAccountId);
        ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(whiteListDoLambdaQueryWrapper);
        response.setWhiteList(whiteListDo.getWhiteList());
        return response;
    }

    @Override
    public ResponseEntity<byte[]> download(String chatbotAccountId) throws IOException {
        log.info("准备下载, chatbotAccountId:{}", chatbotAccountId);
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attr.getResponse();

        //只有管理员才能查看
        checkUserPermission();
        //生成chatbot信息
        CspCustomerChatbotAccountDo cspCustomerChatbotAccountDo = findCspCustomerChatbotAccountDo(chatbotAccountId);

        //补充chatbot详情
        LambdaQueryWrapper<CmccNewDo> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        detailLambdaQueryWrapper.eq(CmccNewDo::getChatbotAccountId, cspCustomerChatbotAccountDo.getChatbotAccountId());
        CmccNewDo chatbotAccountDetail = cmccNewDao.selectOne(detailLambdaQueryWrapper);

        //生成合同信息
        ContractSupplierInfo contractSupplierInfo = getSupplerContractByChatbotId(chatbotAccountId);
        //生成白名单信息
        ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(ChatbotManageWhiteListDo::getChatbotAccountId, chatbotAccountId);

        List<ReqDocZip> reqDocZips = new ArrayList<>();
        //1.生成第一个excel文件
        try {
            reqDocZips.add(buildExcelOfChatbot(cspCustomerChatbotAccountDo, chatbotAccountDetail, contractSupplierInfo, whiteListDo));
            log.info("生成{}报备信息excel", chatbotAccountId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        生成背景图片
//        String backgroundImgUuid = chatbotAccountDetail.getBackgroundImgUrl();
//        if (StringUtils.isNotBlank(backgroundImgUuid)) {
//            reqDocZips.add(getInputStreamOfFile(backgroundImgUuid, "chatbot背景图片"));
//            log.info("chatbot背景图片完成");
//        }

        //生成授权书
        String authorizationLetterSrc = contractSupplierInfo.getAuthorizationLetterSrc();
        if (StringUtils.isNotBlank(authorizationLetterSrc)) {
            String[] uuids = authorizationLetterSrc.split(";");
            for (int i = 0; i < uuids.length; i++) {
                reqDocZips.add(getInputStreamOfFile(uuids[i], "5G消息合作授权书" + (i + 1)));
                log.info("5G消息合作授权书{}完成", i + 1);
            }
        }
        //chatbot签名
        String chatbotSmsSign = chatbotAccountDetail.getChatbotSmsSign();
        //生成chatbot头像
        String logoUrl = chatbotAccountDetail.getChatbotLogoUrl();
        if (StringUtils.isNotBlank(logoUrl)) {
            reqDocZips.add(getInputStreamOfFile(logoUrl, "头像-" + chatbotSmsSign));
            log.info("chatbot头像完成");
        }
        //生成证明材料
        String evidenceSrc = contractSupplierInfo.getEvidenceSrc();
        if (StringUtils.isNotBlank(evidenceSrc)) {
            reqDocZips.add(getInputStreamOfFile(evidenceSrc, "证明材料-" + chatbotSmsSign));
            log.info("证明材料完成");
        }

        //生成营业执照
        String businessLicenseSrc = contractSupplierInfo.getBusinessLicenseSrc();
        if (StringUtils.isNotBlank(businessLicenseSrc)) {
            String[] uuids = businessLicenseSrc.split(";");
            for (int i = 0; i < uuids.length; i++) {
                reqDocZips.add(getInputStreamOfFile(uuids[i], contractSupplierInfo.getEnterpriseAccountName() + (i + 1)));
                log.info("生成营业执照{}", i + 1);
            }
        }

        //生成身份证正面
        String contactsIdCardFrontSrc = contractSupplierInfo.getContactsIdCardFrontSrc();
        if (StringUtils.isNotBlank(contactsIdCardFrontSrc)) {
            reqDocZips.add(getInputStreamOfFile(contactsIdCardFrontSrc, "经办人身份证1"));
            log.info("生成身份证正面");
        }

        //生成身份证反面
        String contactsIdCardReverseSrc = contractSupplierInfo.getContactsIdCardReverseSrc();
        if (StringUtils.isNotBlank(contactsIdCardReverseSrc)) {
            reqDocZips.add(getInputStreamOfFile(contactsIdCardReverseSrc, "经办人身份证2"));
            log.info("生成身份证反面");
        }

        //5G消息业务承诺函
        String businessCommitmentLetterSrc = contractSupplierInfo.getBusinessCommitmentLetterSrc();
        if (StringUtils.isNotBlank(businessCommitmentLetterSrc)) {
            String[] uuids = businessCommitmentLetterSrc.split(";");
            for (int i = 0; i < uuids.length; i++) {
                reqDocZips.add(getInputStreamOfFile(uuids[i], "5G消息业务承诺函" + (i + 1)));
                log.info("生成5G消息业务承诺函{}", i + 1);
            }
        }

        //安全保障责任书
        String securityResponsibilityLetterSrc = contractSupplierInfo.getSecurityResponsibilityLetterSrc();
        if (StringUtils.isNotBlank(securityResponsibilityLetterSrc)) {
            String[] uuids = securityResponsibilityLetterSrc.split(";");
            for (int i = 0; i < uuids.length; i++) {
                reqDocZips.add(getInputStreamOfFile(uuids[i], "安全保障责任书" + (i + 1)));
                log.info("生成安全保障责任书{}", i + 1);
            }
        }
        //合同文件
        String contractFileSrc = contractSupplierInfo.getContractFileSrc();
        if (StringUtils.isNotBlank(contractFileSrc)) {
            String[] uuids = contractFileSrc.split(";");
            for (int i = 0; i < uuids.length; i++) {
                reqDocZips.add(getInputStreamOfFile(uuids[i], "合同文件" + (i + 1)));
                log.info("合同文件{}", i + 1);
            }
        }
        //整体打包.装载到response
        if (Objects.isNull(response)) {
            throw new RuntimeException("下载出现错误");
        }
        log.info("生成资料完成");

        String[] fileNames = reqDocZips.stream().map(ReqDocZip::getName).toArray(String[]::new);
        InputStream[] inputStreams = reqDocZips.stream().map(ReqDocZip::getInputStream).toArray(InputStream[]::new);
        //       response.setContentType("application/zip");
        //        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("导出归档文档", StandardCharsets.UTF_8.toString()) + ".zip");
        ServletOutputStream outputStream = response.getOutputStream();
        ZipUtil.zip(response.getOutputStream(), fileNames, inputStreams);
        int bufferSize = response.getBufferSize();
        byte[] bytes = new byte[bufferSize];
        outputStream.write(bytes);
        //    No converter for [class com.citc.nce.common.core.pojo.RestResult] with preset Content-Type 'application/zip;charset=utf-8'

        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> entity = null;
        String fileNameURL = URLEncoder.encode("导出归档文档.zip", "UTF-8");
//        headers.add("Content-Type", "application/x-zip-compressed");
        headers.add("Content-Disposition", "attachment; filename=" + fileNameURL);

        headers.add("Content-Type", "application/zip");
        headers.setContentLength(Math.toIntExact(bytes.length));
        headers.add("Content-Range", "bytes 0-1/" + bytes.length);
        headers.add("Accept-Ranges", "bytes");
        log.info("所有资料上传到流");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

    }

    //获取文件的数据
    private ReqDocZip getInputStreamOfFile(String uuid, String newFileName) {
        String[] split = uuid.split("/");
        FileInfoReq fileInfoReq = new FileInfoReq();
        fileInfoReq.setUuid(split[0]);
        FileInfo fileInfo = fileApi.getFileInfo(fileInfoReq);

        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(split[0]);
        ResponseEntity<byte[]> responseEntity = fileApi.download(downloadReq);
//        将byte[]转换为inputStream
        InputStream inputStream = new ByteArrayInputStream(responseEntity.getBody());

        ReqDocZip reqDocZip = new ReqDocZip();
        reqDocZip.setInputStream(inputStream);
        reqDocZip.setName(newFileName + "." + fileInfo.getFileFormat());
        return reqDocZip;
    }

    //将多个文件及其流进行打包压缩
    private void zipList(List<ReqDocZip> reqDocZips, HttpServletResponse response) throws IOException {
        String[] fileNames = reqDocZips.stream().map(ReqDocZip::getName).toArray(String[]::new);
        ByteArrayInputStream[] inputStreams = reqDocZips.stream().map(ReqDocZip::getInputStream).toArray(ByteArrayInputStream[]::new);
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("导出归档文档", StandardCharsets.UTF_8.toString()) + ".zip");
        ZipUtil.zip(response.getOutputStream(), fileNames, inputStreams);

    }


    //根据chatbotId查询到其信息后,制作Excel
    private ReqDocZip buildExcelOfChatbot(CspCustomerChatbotAccountDo cspCustomerChatbotAccountDo, CmccNewDo detail, ContractSupplierInfo contractSupplierInfo, ChatbotManageWhiteListDo whiteListDo) throws IOException {

        SupplierChatbotExcelInfoResp supplierChatbotExcelInfoResp = new SupplierChatbotExcelInfoResp();
        //如果为空,直接返回
        BeanUtils.copyProperties(cspCustomerChatbotAccountDo, supplierChatbotExcelInfoResp);
        //补充chatbot详情
        BeanUtils.copyProperties(detail, supplierChatbotExcelInfoResp);
        //查找一级二级行业具体值
        Integer chatbotFirstIndustryType = detail.getChatbotFirstIndustryType();
        Integer chatbotSecondIndustryType = detail.getChatbotSecondIndustryType();

        ClassPathResource resource = new ClassPathResource("industry.json");
        InputStream inputStream = resource.getInputStream();
        String industryJson = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        //把json转换为map
        JSONObject jsonObject = JSONObject.parseObject(industryJson);
        Object industryList = jsonObject.get("industryList");
        JSONArray jsonArray = JSONArray.parseArray(industryList.toString());
        for (Object o : jsonArray) {
            JSONObject firstIndustry = (JSONObject) o;
            Integer firstIndustryCode = (Integer) (firstIndustry.get("value"));
            if (chatbotFirstIndustryType.equals(firstIndustryCode)) {
                String firstIndustryString = (String) (firstIndustry.get("label"));
                JSONArray chatbotSecondIndustryArray = JSONArray.parseArray(firstIndustry.get("children").toString());
                for (Object chatbotSecondIndustryObject : chatbotSecondIndustryArray) {
                    JSONObject secondIndustry = (JSONObject) chatbotSecondIndustryObject;
                    Integer secondIndustryCode = (Integer) (secondIndustry.get("value"));
                    if (chatbotSecondIndustryType.equals(secondIndustryCode)) {
                        String SecondIndustryString = (String) (secondIndustry.get("label"));
                        supplierChatbotExcelInfoResp.setChatbotSecondIndustryType(SecondIndustryString);
                    }
                }
            }
        }

        //合同信息
        BeanUtils.copyProperties(contractSupplierInfo, supplierChatbotExcelInfoResp);
        //白名单
        if (Objects.nonNull(whiteListDo) && CharSequenceUtil.isNotBlank(whiteListDo.getWhiteList())) {
            supplierChatbotExcelInfoResp.setWhiteList(whiteListDo.getWhiteList());
        }
        //背景图片UUID需要转化为可访问的文件地址
//        String backgroundUuid = supplierChatbotExcelInfoResp.getBackgroundImgUrl();
//        if (StringUtils.isNotBlank(backgroundUuid)) {
//            String format = String.format(backgroundUrl, cspCustomerChatbotAccountDo.getChatbotAccount(), backgroundUuid);
//            supplierChatbotExcelInfoResp.setBackgroundImgUrl(format);
//        }
        byte[] data = ExcelUtils.exportExcel(SupplierChatbotExcelInfoResp.class, CollectionUtil.newArrayList(supplierChatbotExcelInfoResp), "报备信息");
        return new ReqDocZip("报备信息.xlsx", new ByteArrayInputStream(data));
    }

    /**
     * 供应商返回的信息填入,完成supplier chatbot配置
     *
     * @param req 需要完成的配置信息对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Boolean> completeConfiguration(CompleteSupplierChatbotConfigurationReq req) {

        checkUserPermission();
        ensureChatbotInfoUnique(null, req.getChatbotAccount());

        LambdaQueryWrapper<CspCustomerChatbotAccountDo> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
        ;
        //查询结果
        CspCustomerChatbotAccountDo chatbot = cspCustomerChatbotAccountDao.selectOne(objectLambdaQueryWrapper);
        //确认该用户在此渠道下没有chatbot是上线状态
        checkCustomerSupplierChatbotNum(chatbot.getCustomerId(), chatbot.getAccountTypeCode());

        //要更改的实体类
        CspCustomerChatbotAccountDo updateCspCustomerChatbotAccountDo = new CspCustomerChatbotAccountDo();
        updateCspCustomerChatbotAccountDo.setAppId(req.getAppId());
        updateCspCustomerChatbotAccountDo.setAppKey(req.getAppKey());
        updateCspCustomerChatbotAccountDo.setAgentId(req.getAgentId());
        updateCspCustomerChatbotAccountDo.setEcId(req.getEcId());
        updateCspCustomerChatbotAccountDo.setChatbotAccount(req.getChatbotAccount());

//        ，2：移动，3：电信
        if (chatbot.getAccountTypeCode().equals(CSPOperatorCodeEnum.CMCC.getCode()))  {
            updateCspCustomerChatbotAccountDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode());
        } else if (chatbot.getAccountTypeCode().equals(CSPOperatorCodeEnum.CT.getCode()) || chatbot.getAccountTypeCode().equals(CSPOperatorCodeEnum.CUNC.getCode())) {
            updateCspCustomerChatbotAccountDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode());
        }

        //完成配置
        int update = cspCustomerChatbotAccountDao.update(updateCspCustomerChatbotAccountDo, new LambdaUpdateWrapper<CspCustomerChatbotAccountDo>()
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
                //                STATUS_73_PROCESSING(73, "处理中"),
                //                STATUS_13_PROCESSING(13, "处理中"),
                .and(wrapper -> wrapper
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode())
                )
        );
        //设置状态为已完成
        ContractManageDo contractManageDo = new ContractManageDo();
        contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_30_ONLINE.getCode());

        //修改合同状态为已完成
        int updateNum = contractManageDao.update(contractManageDo, new LambdaQueryWrapper<ContractManageDo>()
                .eq(ContractManageDo::getCustomerId, chatbot.getCustomerId())
                .eq(ContractManageDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode())
                .eq(ContractManageDo::getOperatorCode, chatbot.getAccountTypeCode())
                .eq(ContractManageDo::getDeleted, 0)
        );
        if (updateNum < 1) {
            log.error("合同更改状态失败,CustomerId:{},AccountTypeCode:{},Channel:{}", chatbot.getCustomerId(), chatbot.getAccountTypeCode(), CSPChannelEnum.FONTDO.getValue());
        }
        //todo 记录操作日志
        operationLog(req.toString(), req.getChatbotAccountId(), OperationTypeEnum.COMPLETE_CONFIGURATION);
        //向网关同步chatbot信息
        syncInfoToGateway(updateCspCustomerChatbotAccountDo, chatbot);

        //如果此顾客之前存在直连chatbot, 那么需要将旧chatbot的模板审核记录删除
//        LambdaQueryWrapper<CspCustomerChatbotAccountDo> oldChatbotWrapper = new LambdaQueryWrapper<>();
//        objectLambdaQueryWrapper
//                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.OWNER.getValue())
//                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.DIRECT.getValue())
//                .eq(CspCustomerChatbotAccountDo::getCustomerId, chatbot.getCustomerId())
//                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
//        ;
//        //查询结果
//        List<CspCustomerChatbotAccountDo> directChatbots = cspCustomerChatbotAccountDao.selectList(oldChatbotWrapper);
//        for (CspCustomerChatbotAccountDo directChatbot : directChatbots) {
//            //删除原来的chatbot的审核记录
//            messageTemplateApi.cancelAudit(directChatbot.getChatbotAccount());
//        }

        return update == 1 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "更新失败", Boolean.FALSE);

    }

    private void ensureChatbotInfoUnique(Long excludeId, String chatbotAccount) {
        if (StringUtils.isEmpty(chatbotAccount))
            return;
        if (accountManagementDao.selectCount(Wrappers.<CspCustomerChatbotAccount>lambdaQuery()
                .ne(Objects.nonNull(excludeId), CspCustomerChatbotAccount::getId, excludeId)
                .eq(CspCustomerChatbotAccount::getChatbotAccount, chatbotAccount))
                > 0) {
            throw new BizException(500, "机器人账号或账号名称重复");
        }
    }


    /*
     * @describe
     * @Param 检查当前是不是管理用户
     * @return void
     **/
    private void checkUserPermission() {
        //只有管理员才能查看
        String userId = SessionContextUtil.getUser().getUserId();
        AdminUserDo adminUserDo = adminUserDao.selectOne(AdminUserDo::getUserId, userId);
        if (Objects.isNull(adminUserDo)) {
            throw new BizException(403, "当前用户没有操作权限");
        }
    }

    //向网管同步chatbot信息
    //completeConfig主要包含需要完善的信息, oldChatbotMessage主要包含原来的信息
    private void syncInfoToGateway(CspCustomerChatbotAccountDo completeConfig, CspCustomerChatbotAccountDo oldChatbotMessage) {
        SyncAuthInfoModel syncAuthInfoModel = new SyncAuthInfoModel();
        try {
            //使用set方法将cspCustomerChatbotAccountDo的值赋值给syncAuthInfoModel
            syncAuthInfoModel.setAppId(completeConfig.getAppId());
            syncAuthInfoModel.setOpenId(completeConfig.getAgentId());
            syncAuthInfoModel.setOpenSecret(completeConfig.getAppKey());
            syncAuthInfoModel.setEcId(completeConfig.getEcId());
            syncAuthInfoModel.setAgentName(oldChatbotMessage.getAccountName());
            syncAuthInfoModel.setChatbotId(completeConfig.getChatbotAccount());
            syncAuthInfoModel.setSupplierTag(oldChatbotMessage.getSupplierTag());
            syncAuthInfoModel.setToken(oldChatbotMessage.getToken());
            syncAuthInfoModel.setCallbackUrl(callbackUrl);
            //调用网关接口

            String supplierResultString = getSupplierResultString(syncAuthInfoModel, completeConfig, gatewayConfig.getSupplierSyncUrl());
            ResponseVo restResult = JSONObject.parseObject(supplierResultString, ResponseVo.class);
            log.info("向网关同步chatbot信息返回结果: {}", JSONObject.toJSONString(restResult));
            if (!"200".equals(restResult.getCode())) {
                //同步失败
                //需要将cspCustomerChatbotAccountDo对象的同步状态改为失败
                CspCustomerChatbotAccountDo updateCspCustomerChatbotAccountDo = new CspCustomerChatbotAccountDo();
                updateCspCustomerChatbotAccountDo.setId(oldChatbotMessage.getId());
                updateCspCustomerChatbotAccountDo.setSyncGatewayState(-1);
                cspCustomerChatbotAccountDao.updateById(updateCspCustomerChatbotAccountDo);
                throw new RuntimeException("向网关同步chatbot信息失败");
            }
        } catch (Exception e) {
            log.error("向网关同步chatbot信息失败: {},\nreason:{}", JSONObject.toJSONString(syncAuthInfoModel), e);
            throw new BizException(81001235, "向网关同步chatbot信息失败");
        }
    }

    //通知网关发送供应商消息
    public String getSupplierResultString(Object obj, CspCustomerChatbotAccountDo account, String url) throws URISyntaxException, IOException, NoSuchAlgorithmException {
        try (CloseableHttpClient client = createSSLClientDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            String jsonString = JSONObject.toJSONString(obj);
            log.info("请求体数据为： {}", jsonString);
            //在网关这个是openId

            String timestamp = System.currentTimeMillis() + "";
            String input = gatewayConfig.getAccessKey() + gatewayConfig.getAccessSecret() + timestamp;
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String sign = java.util.Base64.getEncoder().encodeToString(md5.digest(input.getBytes(StandardCharsets.UTF_8)));
            httpPost.addHeader("sign", sign);
            httpPost.addHeader("accessKey", gatewayConfig.getAccessKey());
            httpPost.addHeader("timestamp", timestamp);

            HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String resultString = EntityUtils.toString(responseEntity);
            log.info("发送至网关的结果为  " + resultString);
            return resultString;
        } finally {
            createSSLClientDefault().close();
        }
//        log.info("请求体数据为： {}",JSON.toJSONString(obj));
//        String agentId = account.getAgentId();
//        String appId = account.getAppId();
//        String once = "123456";
//        String input = appId + agentId + account.getAppKey() +once;
//        String sign = DigestUtil.sha256Hex(input);
//        String result = HttpRequest.post(url)
//                .header("Content-Type", "application/json")
//                .header("openId",agentId)
//                .header("appId", appId)
//                .header("nonce", once)
//                .header("sign", sign)
//                .body(JSON.toJSONString(obj))
//                .execute().body();
//        log.info("发送至网关的结果为  " + result);
//        return result;
    }

    /**
     * 获取https客户端
     *
     * @return https客户端
     */
    private CloseableHttpClient createSSLClientDefault() {
        try {
            X509TrustManager x509mgr = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509mgr}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(81001234, "客户端创建失败");
        }
    }


    @Override
    @Transactional
    public RestResult<Boolean> rejectConfiguration(RejectSupplierChatbotConfigurationReq req) {

        checkUserPermission();
        //查找chatbot
        CspCustomerChatbotAccountDo chatbot = findCspCustomerChatbotAccountDo(req.getChatbotAccountId());

        CspCustomerChatbotAccountDo updateCspCustomerChatbotAccountDo = new CspCustomerChatbotAccountDo();

        if (chatbot.getAccountTypeCode().equals(CSPOperatorCodeEnum.CMCC.getCode())) {
            updateCspCustomerChatbotAccountDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_28_REJECT.getCode());
        } else if (chatbot.getAccountTypeCode().equals(CSPOperatorCodeEnum.CT.getCode()) || chatbot.getAccountTypeCode().equals(CSPOperatorCodeEnum.CUNC.getCode())) {
            updateCspCustomerChatbotAccountDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_74_REJECT.getCode());
        }

        //完成配置
        int update = cspCustomerChatbotAccountDao.update(updateCspCustomerChatbotAccountDo, new LambdaUpdateWrapper<CspCustomerChatbotAccountDo>()
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
                //                STATUS_73_PROCESSING(73, "处理中"),
                //                STATUS_13_PROCESSING(13, "处理中"),
                .and(wrapper -> wrapper
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode())
                )
        );
        if (update > 0) {
            //更新cmcc表中的失败原因
            CmccNewDo cmccNewDo = new CmccNewDo();
            cmccNewDo.setFailureReason(req.getRemark());
            cmccNewDao.update(cmccNewDo, new LambdaUpdateWrapper<CmccNewDo>()
                    .eq(CmccNewDo::getChatbotAccountId, req.getChatbotAccountId())
            );
        }

        //todo 记录操作日志
        operationLog(req.getRemark(), req.getChatbotAccountId(), OperationTypeEnum.REJECT);

        return update == 1 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "更新失败", Boolean.FALSE);

    }

    //根据chatbotAccountId查找数据库信息
    private CspCustomerChatbotAccountDo findCspCustomerChatbotAccountDo(String chatbotAccountId) {
        //先查一次,主要是查询chatbot是哪个运营商
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, chatbotAccountId)
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
        ;
        //查询结果
        return cspCustomerChatbotAccountDao.selectOne(objectLambdaQueryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Boolean> changeSupplierChatbotStatus(changeSupplierChatbotStatusReq req) {

        checkUserPermission();
        //原chatbot信息
        CspCustomerChatbotAccountDo chatbot = findCspCustomerChatbotAccountDo(req.getChatbotAccountId());
        String supplierTag = chatbot.getSupplierTag();
        if (!Objects.equals(supplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())) {
            throw new BizException(500, "该chatbot不能被以此方法修改");
        }
        //2移动  ,3电信
        Integer accountTypeCode = chatbot.getAccountTypeCode();
        //基础的where
        LambdaUpdateWrapper<CspCustomerChatbotAccountDo> whereWrapper = new LambdaUpdateWrapper<CspCustomerChatbotAccountDo>()
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode());
        int finalStatus;
        CspCustomerChatbotAccountDo updateDo = new CspCustomerChatbotAccountDo();

        //设定finalStatus 和whereWrapper中的chatbotStatus
        OperationTypeEnum operationTypeEnum = OperationTypeEnum.getByCode(req.getNewStatus());
        switch (operationTypeEnum) {
            case LOG_OFF:
                if (accountTypeCode.equals(CSPOperatorCodeEnum.CMCC.getCode())) {
                    finalStatus = CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode();
                    whereWrapper.and(wrapper -> wrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode())
                            .or()
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode())
                    );

                } else if (accountTypeCode.equals(CSPOperatorCodeEnum.CT.getCode()) || accountTypeCode.equals(CSPOperatorCodeEnum.CUNC.getCode())) {
                    finalStatus = CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode();
                    whereWrapper.and(wrapper -> wrapper
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode())
                            .or()
                            .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode())
                    );
                } else {
                    throw new BizException(500, "该chatbot不能被以此方法修改");
                }
                break;
            case LOG_ON:
                //原状态必须是offLine
                if (accountTypeCode.equals(CSPOperatorCodeEnum.CMCC.getCode())) {
                    finalStatus = CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode();
                    whereWrapper.eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode());
                } else if (accountTypeCode.equals(CSPOperatorCodeEnum.CT.getCode()) || accountTypeCode.equals(CSPOperatorCodeEnum.CUNC.getCode())) {
                    finalStatus = CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode();
                    whereWrapper.eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode());
                } else {
                    throw new BizException(500, "该chatbot不能被以此方法修改");
                }
                //确认该用户在此渠道下没有chatbot是上线状态
                checkCustomerSupplierChatbotNum(chatbot.getCustomerId(), accountTypeCode);

                break;
            case OFFLINE:
                if (accountTypeCode.equals(CSPOperatorCodeEnum.CMCC.getCode())) {
                    finalStatus = CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode();
                    whereWrapper.eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode());
                } else if (accountTypeCode.equals(CSPOperatorCodeEnum.CT.getCode()) || accountTypeCode.equals(CSPOperatorCodeEnum.CUNC.getCode())) {
                    finalStatus = CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode();
                    whereWrapper.eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode());
                } else {
                    throw new BizException(500, "该chatbot不能被以此方法修改");
                }
                break;
            default:
                throw new BizException(500, "只能进行上线、下线和注销");
        }
        //更新
        updateDo.setChatbotStatus(finalStatus);
        int update = cspCustomerChatbotAccountDao.update(updateDo, whereWrapper);
        operationLog(req.getRemark(), req.getChatbotAccountId(), operationTypeEnum);
        if (operationTypeEnum.getCode() == OperationTypeEnum.LOG_OFF.getCode()) {
            //删除chatbot对应的模板的审核记录等
            messageTemplateApi.cancelAudit(chatbot.getChatbotAccount());
            //删除素材审核记录
            FileExamineDeleteReq fileExamineDeleteReq = new FileExamineDeleteReq();
            fileExamineDeleteReq.setChatbotAccount(chatbot.getChatbotAccount());
            platformApi.deleteAuditRecord(fileExamineDeleteReq);

            //群发计划取消关联账号绑定
            robotGroupSendPlansApi.removeChatbotAccount(chatbot.getChatbotAccount(), chatbot.getAccountType(), chatbot.getSupplierTag());
            //机器人场景取消关联账号
            robotSceneNodeApi.removeChatbotAccount(chatbot.getChatbotAccount());
            //删除阅信+模板的审核记录
            ReadingLetterAuditDeleteReq readingLetterAuditDeleteReq = new ReadingLetterAuditDeleteReq();
            readingLetterAuditDeleteReq.setAccountId(chatbot.getChatbotAccountId());
            readingLetterAuditDeleteReq.setSmsType(SmsTypeEnum.FIFTH_READING_LETTER.getCode());
            readingLetterAuditDeleteReq.setAppId(chatbot.getAppId());
            readingLetterAuditDeleteReq.setAppKey(chatbot.getAppKey());
            readingLetterAuditDeleteReq.setAgentId(chatbot.getAgentId());

            readingLetterTemplateApi.deleteAuditAndProvedByAccount(readingLetterAuditDeleteReq);
        }

        return update == 1 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "更该状态失败", Boolean.FALSE);
    }

    //确认该用户在此渠道下没有chatbot是上线/调试/测试状态
    private void checkCustomerSupplierChatbotNum(String customerId, Integer accountTypeCode) {
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(CspCustomerChatbotAccountDo::getCustomerId, customerId)
                .eq(CspCustomerChatbotAccountDo::getAccountTypeCode, accountTypeCode)
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())

                .and(wrapper -> wrapper
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_70_TEST.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_50_DEBUG.getCode())
                );
        if (cspCustomerChatbotAccountDao.selectCount(queryWrapper) > 0) {
            throw new BizException(400, "该用户在此渠道下已有chatbot是上线状态");
        }
    }

    //chatbotAccountId是uuid识别码
    private void operationLog(String remark, String chatbotAccountId, OperationTypeEnum operationTypeEnum) {
        SupplierChatbotStatusOperationLogDo logDo = new SupplierChatbotStatusOperationLogDo();
        logDo.setRemark(remark);
        logDo.setChatbotAccountId(chatbotAccountId);
        logDo.setUserName(SessionContextUtil.getUser().getUserName());
        logDo.setOperationTypeCode(operationTypeEnum.getCode());
        logDo.setOperationType(operationTypeEnum.getOperationType());
        supplierChatbotStatusOperationLogDao.insert(logDo);
    }


    @Override
    public SupplierChatbotConfigurationResp findConfiguration(String chatbotAccountId) {
        CspCustomerChatbotAccountDo chatbotAccountDo = cspCustomerChatbotAccountDao.selectOne(CspCustomerChatbotAccountDo::getChatbotAccountId, chatbotAccountId);
        SupplierChatbotConfigurationResp supplierChatbotConfigurationResp = new SupplierChatbotConfigurationResp();
        BeanUtils.copyProperties(chatbotAccountDo, supplierChatbotConfigurationResp);
        return supplierChatbotConfigurationResp;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Boolean> editChatbotConfiguration(EditSupplierChatbotConfigurationReq req) {
        //查询原来的chatbot信息
        CspCustomerChatbotAccountDo chatbotAccountDo = cspCustomerChatbotAccountDao.selectOne(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId());

        //判断请求参数中的值和数据库中的值是不是一样的,如果都是一样的,那么就不需要修改
        if (req.getEcId().equals(chatbotAccountDo.getEcId()) &&
                req.getAgentId().equals(chatbotAccountDo.getAgentId()) &&
                req.getAppKey().equals(chatbotAccountDo.getAppKey()) &&
                req.getAppId().equals(chatbotAccountDo.getAppId()) &&
                req.getChatbotAccount().equals(chatbotAccountDo.getChatbotAccount())) {
            log.info("chatbot配置信息没有变化,不需要修改, req:{}", req);
            return RestResult.success(Boolean.TRUE);
        }

        checkUserPermission();
        ensureChatbotInfoUnique(chatbotAccountDo.getId(), req.getChatbotAccount());

        CspCustomerChatbotAccountDo updateDo = new CspCustomerChatbotAccountDo();
        updateDo.setEcId(req.getEcId());
        updateDo.setAgentId(req.getAgentId());
        updateDo.setAppKey(req.getAppKey());
        updateDo.setAppId(req.getAppId());
        updateDo.setChatbotAccount(req.getChatbotAccount());

        int update = cspCustomerChatbotAccountDao.update(updateDo, new LambdaUpdateWrapper<CspCustomerChatbotAccountDo>()
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
                //           where status = ONLINE or OFFLINE
                .and(wrapper -> wrapper
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode())
                        .or()
                        .eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode())
                )
        );
        //如果没有更新成功一条数据
        if (update == 0) {
            return RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "更该状态失败", Boolean.FALSE);
        }
        //如果只有ChatbotAccount不一样,那么替换原有的模板审核记录
        if ((req.getEcId().equals(chatbotAccountDo.getEcId()) &&
                req.getAgentId().equals(chatbotAccountDo.getAgentId()) &&
                req.getAppKey().equals(chatbotAccountDo.getAppKey()) &&
                req.getAppId().equals(chatbotAccountDo.getAppId())) &&
                !req.getChatbotAccount().equals(chatbotAccountDo.getChatbotAccount())) {
            messageTemplateApi.replaceChatbotAccount(req.getChatbotAccount(), chatbotAccountDo.getChatbotAccount());
        } else {
            //向网关同步chatbot的AppAuth信息
            syncInfoToGateway(updateDo, chatbotAccountDo);
            //这里的修改都视为修改成新的chatbot了,  因为如果是修改错误的参数的话,原chatbot应该是错误的信息, 那么原来的也应该都没通过审核
            //删除原来的chatbot的审核记录
            messageTemplateApi.cancelAudit(chatbotAccountDo.getChatbotAccount());
        }
        //如果修改了ChatbotAccount
        if (!req.getChatbotAccount().equals(chatbotAccountDo.getChatbotAccount()) || !req.getAppId().equals(chatbotAccountDo.getAppId())) {
            //删除素材审核记录
            FileExamineDeleteReq fileExamineDeleteReq = new FileExamineDeleteReq();
            fileExamineDeleteReq.setChatbotAccount(chatbotAccountDo.getChatbotAccount());
            platformApi.deleteAuditRecord(fileExamineDeleteReq);
        }

        //操作日志为:修改Chatbot配置
        operationLog(req.toString(), req.getChatbotAccountId(), OperationTypeEnum.UPDATE_CONFIGURATION);
        return update == 1 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "更该状态失败", Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Boolean> updateSupplierChatbot(UpdateChatbotSupplierReq req) {
        checkUserPermission();
        CspCustomerChatbotAccountDo updateDo = new CspCustomerChatbotAccountDo();
        BeanUtils.copyProperties(req, updateDo);
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> queryWrapper = new LambdaQueryWrapper<CspCustomerChatbotAccountDo>()
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChatbotAccountId, req.getChatbotAccountId())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
                //不为已驳回状态
                .and(wrapper -> wrapper
                        .ne(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_28_REJECT.getCode())
                        .ne(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_74_REJECT.getCode())
                );

        //完成配置
        CspCustomerChatbotAccountDo target = cspCustomerChatbotAccountDao.selectOne(queryWrapper);
        if (Objects.isNull(target)) {
            return RestResult.error(500, "没有符合的chatbot");
        }
        CmccNewDo cmccNewDo = new CmccNewDo();
        BeanUtils.copyProperties(req, cmccNewDo);
        int update = cmccNewDao.update(cmccNewDo, new LambdaUpdateWrapper<CmccNewDo>().eq(CmccNewDo::getChatbotAccountId, req.getChatbotAccountId()));

        //需要同时修改主表中的AccountName
        if (!target.getAccountName().equals(req.getChatbotName())) {
            target.setAccountName(req.getChatbotName());
            cspCustomerChatbotAccountDao.updateById(target);
        }


        //操作日志为:编辑信息
        operationLog(req.toString(), req.getChatbotAccountId(), OperationTypeEnum.EDIT);
        return update == 1 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "修改失败", Boolean.FALSE);
    }

    //更新chatbot白名单
    @Override
    public void setWhiteList(ChatbotSetWhiteListReq req) {
        //先查询机器人账号是否存在
        CspCustomerChatbotAccountDo chatbotManageDo = cspCustomerChatbotAccountDao.selectOne("chatbot_account_id", req.getChatbotAccountId());
        if (Objects.isNull(chatbotManageDo)) {
            throw new BizException("机器人账号不存在");
        }

        if (StringUtils.isEmpty(req.getWhiteList())) {
            throw new BizException("白名单手机号不能为空");
        }
        ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(
                Wrappers.<ChatbotManageWhiteListDo>lambdaQuery()
                        .eq(ChatbotManageWhiteListDo::getChatbotAccountId, req.getChatbotAccountId())
        );
//        if (CSPOperatorCodeEnum.CMCC.getCode().equals(req.getOperatorCode()) && whiteListDo.getStatus() == 0) {
//            throw new BizException("白名单审核中，不允许再次提交");
//        }
        chatbotManageWhiteListDao.deleteById(whiteListDo);
        ChatbotManageWhiteListDo insert = new ChatbotManageWhiteListDo();
        insert.setWhiteList(req.getWhiteList());
        insert.setStatus(0);
        insert.setChatbotAccountId(req.getChatbotAccountId());
        insert.setChatbotId(req.getChatbotId());
        chatbotManageWhiteListDao.insert(insert);
    }

    @Override
    public RestResult<Boolean> updateSupplierContract(UpdateSupplierContractReq req) {

        String customerId = req.getCustomerId();
        //先查一次,主要是查询chatbot是哪个运营商
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper
                .eq(CspCustomerChatbotAccountDo::getSupplierTag, CSPChatbotSupplierTagEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(CspCustomerChatbotAccountDo::getCustomerId, customerId)
                .eq(CspCustomerChatbotAccountDo::getAccountTypeCode, req.getOperatorCode())
                .eq(CspCustomerChatbotAccountDo::getDeleted, NumCode.ZERO.getCode())
        ;
        //查询结果
        CspCustomerChatbotAccountDo chatbotAccountDo = cspCustomerChatbotAccountDao.selectOne(objectLambdaQueryWrapper);
        //如果是驳回状态,不能修改, 报错!!
        if (CSPChatbotStatusEnum.STATUS_74_REJECT == CSPChatbotStatusEnum.byCode(chatbotAccountDo.getChatbotStatus()) || CSPChatbotStatusEnum.STATUS_28_REJECT == CSPChatbotStatusEnum.byCode(chatbotAccountDo.getChatbotStatus())) {
            throw new BizException(500, "驳回状态不能修改合同信息");
        }
        //根据Chatbot的CustomerId去找到对应的(蜂动)合同
        ContractManageDo contractManageDo = new ContractManageDo();
        BeanUtils.copyProperties(req, contractManageDo);

        int updateById = contractManageDao.updateById(contractManageDo);
        //操作日志为:编辑信息
        operationLog(req.toString(), chatbotAccountDo.getChatbotAccountId(), OperationTypeEnum.EDIT);
        return updateById == 1 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "修改失败", Boolean.FALSE);
    }


    @Override
    public MenuResp queryMenu(String chatbotAccountId) {

        MenuResp menuResp = new MenuResp();
        List<MenuRecordResp> menuRecordRespList = new ArrayList<>();
        //1.查询主表
        List<MenuDo> menuDoList = menuDao.selectList(MenuDo::getChatbotAccountId, chatbotAccountId);
        List<MenuDo> versionList = menuDoList.stream().sorted(Comparator.comparing(MenuDo::getVersion, Comparator.reverseOrder())).collect(Collectors.toList());
        List<Integer> status = menuDoList.stream().map(MenuDo::getMenuStatus).collect(Collectors.toList());
        //如果数据库有审核通过的才展示还原按钮
        menuResp.setIsShowReduction(CollectionUtil.isNotEmpty(status) && status.contains(1));
        if (CollectionUtil.isNotEmpty(versionList)) {
            menuResp.setVersion(versionList.get(0).getVersion());
        }

        if (CollectionUtil.isEmpty(menuDoList)) {
            menuResp.setMenuStatus(-1);
            return menuResp;
        }
        //2.查询父菜单
        List<Long> menuIdList = new ArrayList<>();
        menuDoList.forEach(item -> {
            menuIdList.add(item.getId());
        });
        QueryWrapper<MenuParentDo> parentDoQueryWrapper = new QueryWrapper<>();
        parentDoQueryWrapper.in("menu_id", menuIdList);
        List<MenuParentDo> menuParentDoList = menuParentDao.selectList(parentDoQueryWrapper);
        //3.查询子菜单
        List<Long> parentIdList = new ArrayList<>();
        menuParentDoList.forEach(item -> {
            parentIdList.add(item.getId());
        });
        QueryWrapper<MenuChildDo> menuChildDoQueryWrapper = new QueryWrapper<>();
        menuChildDoQueryWrapper.in("parent_id", parentIdList);
        List<MenuChildDo> menuChildDoList = menuChildDao.selectList(menuChildDoQueryWrapper);

        menuDoList.forEach(item -> {
            MenuRecordResp resp = new MenuRecordResp();
            BeanUtil.copyProperties(item, resp);
            resp.setSubmitUser(item.getSubmitUser());
            menuRecordRespList.add(resp);
        });

        for (MenuRecordResp resp : menuRecordRespList) {
            List<MenuParentResp> parentRespList = new ArrayList<>();
            for (MenuParentDo menuParentDo : menuParentDoList) {
                if (resp.getId().equals(menuParentDo.getMenuId())) {
                    MenuParentResp parentResp = new MenuParentResp();
                    BeanUtil.copyProperties(menuParentDo, parentResp);

                    List<MenuChildResp> menuChildRespList = new ArrayList<>();
                    for (MenuChildDo childDo : menuChildDoList) {
                        if (menuParentDo.getId().equals(childDo.getParentId())) {
                            MenuChildResp childResp = new MenuChildResp();
                            BeanUtil.copyProperties(childDo, childResp);
                            menuChildRespList.add(childResp);
                        }
                    }
                    parentResp.setMenuChildRespList(menuChildRespList);
                    parentRespList.add(parentResp);
                }
            }
            resp.setMenuParentRespList(parentRespList);
        }

        List<MenuRecordResp> sortList = menuRecordRespList.stream().sorted(Comparator.comparing(MenuRecordResp::getVersion, Comparator.reverseOrder())).collect(Collectors.toList());
        menuResp.setMenuParentRespList(sortList.get(0).getMenuParentRespList());
        //展示可用的菜单
//        if (ObjectUtil.isNotEmpty(req.getUseable()) && 1 == req.getUseable()) {
//            List<MenuRecordResp> versions = sortList.stream().filter(m -> m.getMenuStatus() == 1).sorted(Comparator.comparing(MenuRecordResp::getVersion, Comparator.reverseOrder())).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(versions)) {
//                menuResp.setMenuParentRespList(versions.get(0).getMenuParentRespList());
//            } else {
//                menuResp.setMenuParentRespList(new ArrayList<>());
//            }
//        }
        List<MenuRecordResp> sortTimeList = menuRecordRespList.stream().sorted(Comparator.comparing(MenuRecordResp::getSubmitTime, Comparator.reverseOrder())).collect(Collectors.toList());
        menuResp.setMenuRecordRespList(sortTimeList);
        menuResp.setMenuStatus(sortList.get(0).getMenuStatus());
        return menuResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Boolean> submitMenu(MenuSaveReq req) {
        checkUserPermission();
        BaseUser baseUser = SessionContextUtil.getUser();
        String chatbotAccountId = req.getChatbotAccountId();

        LambdaQueryWrapper<MenuDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDo::getChatbotAccountId, chatbotAccountId);
        queryWrapper.orderByDesc(MenuDo::getVersion);
        List<MenuDo> menuDoList = menuDao.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(menuDoList) && ObjectUtil.isNotEmpty(req.getVersion()) &&
                !req.getVersion().equals(menuDoList.get(0).getVersion())) {
            throw new BizException(820103019, "当前版本非最新版，请刷新后操作");
        }
        Integer version = 1;
        if (CollectionUtil.isNotEmpty(menuDoList)) {
            version = menuDoList.get(0).getVersion() + 1;
        }

        CspCustomerChatbotAccount accountManagementDo = accountManagementDao.selectOne(Wrappers.<CspCustomerChatbotAccount>lambdaQuery()
                .eq(CspCustomerChatbotAccount::getChatbotAccountId, chatbotAccountId)
                .eq(CspCustomerChatbotAccount::getDeleted, 0));
        if (accountManagementDo == null) {
            throw new BizException(500, "查询不到机器人信息");
        }
        log.info("chatbot信息:ChatbotAccount:{}, ChatbotAccountId:{}", accountManagementDo.getChatbotAccount(), accountManagementDo.getChatbotAccountId());

        //构建菜单信息
        //1.新增主表（记录表）数据
        MenuDo menuDo = new MenuDo();
        menuDo.setMenuStatus(NumCode.ONE.getCode());
        menuDo.setSubmitTime(new Date());
        menuDo.setVersion(version);
        menuDo.setChatbotId(accountManagementDo.getChatbotAccount());
        menuDo.setChatbotAccountId(accountManagementDo.getChatbotAccountId());
        menuDo.setSubmitUser(baseUser.getUserName());
        //移动流水号
        menuDao.insert(menuDo);

        //2.新增父菜单
        List<MenuChildDo> menuChildDoList = new ArrayList<>();
        req.getMenuParentReqList().forEach(item -> {
            MenuParentDo menuParentDo = new MenuParentDo();
            menuParentDo.setMenuId(menuDo.getId());
            menuParentDo.setMenuCode(item.getMenuCode());
            menuParentDo.setMenuType(item.getMenuType());
            menuParentDo.setMenuContent(item.getMenuContent());
            menuParentDo.setSort(item.getSort());
            menuParentDao.insert(menuParentDo);

            //3.新增子菜单
            item.getMenuChildReqList().forEach(childReq -> {
                if (Constants.SUPPLIER_TAG_FONTDO.equals(accountManagementDo.getSupplierTag()) && (String.valueOf(ButtonType.CAMERA.getCode()).equals(childReq.getButtonType()) || String.valueOf(ButtonType.CONTACT.getCode()).equals(childReq.getButtonType()))) {
                    throw new BizException("当前账号不支持拍摄或调起联系人操作");
                }
                MenuChildDo menuChildDo = new MenuChildDo();
                menuChildDo.setParentId(menuParentDo.getId());
                menuChildDo.setMenuCode(childReq.getMenuCode());
                menuChildDo.setButtonId(childReq.getButtonId());
                menuChildDo.setButtonType(childReq.getButtonType());
                menuChildDo.setButtonDesc(childReq.getButtonDesc());
                menuChildDo.setButtonContent(childReq.getButtonContent());
                menuChildDo.setSort(childReq.getSort());
                menuChildDo.setOptionList(JSON.toJSONString(childReq.getOption()));
                menuChildDoList.add(menuChildDo);
            });
        });
        return menuChildDao.insertBatch(menuChildDoList) > 0 ? RestResult.success(Boolean.TRUE) : RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(), "修改失败", Boolean.FALSE);
    }


    @Override
    public List<OperationLogResp> getOperationLog(String chatbotAccountId) {
        ArrayList<OperationLogResp> objects = new ArrayList<>();
        LambdaQueryWrapper<SupplierChatbotStatusOperationLogDo> supplierChatbotStatusOperationLogDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        supplierChatbotStatusOperationLogDoLambdaQueryWrapper.eq(SupplierChatbotStatusOperationLogDo::getChatbotAccountId, chatbotAccountId);
        List<SupplierChatbotStatusOperationLogDo> supplierChatbotStatusOperationLogDos = supplierChatbotStatusOperationLogDao.selectList(supplierChatbotStatusOperationLogDoLambdaQueryWrapper);
        for (SupplierChatbotStatusOperationLogDo supplierChatbotStatusOperationLogDo : supplierChatbotStatusOperationLogDos) {
            OperationLogResp operationLogResp = new OperationLogResp();
            BeanUtils.copyProperties(supplierChatbotStatusOperationLogDo, operationLogResp);
            objects.add(operationLogResp);
        }
        return objects;
    }


    @Override
    public Map<String, UserInfoResp> getUserInfoByUserId(CodeListReq req) {
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.inIfPresent(UserDo::getUserId, req.getCodeList());
        List<UserDo> list = userDao.selectList(queryWrapper);
        Map<String, UserInfoResp> resultMap = new HashMap<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            list.forEach(l -> {
                UserInfoResp resp = new UserInfoResp();
                BeanUtils.copyProperties(l, resp);
                resultMap.put(l.getUserId(), resp);
            });
        }
        return resultMap;
    }

    @Override
    public AdminUserSumResp getPlatformApplicationReviewSum() {
        AdminUserSumResp resp = new AdminUserSumResp();
        PlatformApplicationReviewInfo platformApplicationReviewInfo = new PlatformApplicationReviewInfo();
        platformApplicationReviewInfo.setApprovalStatus(1);
        Long num = userMapper.getPlatformApplicationReviewCount(platformApplicationReviewInfo);
        resp.setUserSum(num);
        return resp;
    }

    @Override
    public ChatbotProcessingSumResp getSupplierChatbotProcessingSum() {
        ChatbotProcessingSumResp resp = new ChatbotProcessingSumResp();
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode())
                .or().eq(CspCustomerChatbotAccountDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode());
        long num = cspCustomerChatbotAccountDao.selectCount(queryWrapper);
        resp.setChatbotSum(num);
        return resp;
    }

    private String getRootKey(Map<String, AdminMenuDo> adminMenuDoMap, AdminMenuDo adminMenuDo) {
        if (adminMenuDo.getLevel() == 0) {
            return adminMenuDo.getMenuCode();
        } else {
            return getRootKey(adminMenuDoMap, adminMenuDoMap.get(adminMenuDo.getMenuParentCode()));
        }
    }


    /**
     * 查询所有子节点
     *
     * @param resp     资源对象
     * @param respList 资源集合
     * @return
     */
    private List<RoleAndMenuItem> children(RoleAndMenuItem resp, List<RoleAndMenuItem> respList, List<String> menuList) {
        List<RoleAndMenuItem> collect = respList.stream().filter(i -> {
            return i.getMenuParentCode().equals(resp.getMenuCode());
        }).map(j -> {
            // 子节点
            RoleAndMenuItem result = new RoleAndMenuItem();
            BeanUtils.copyProperties(j, result);
            if (menuList.contains(j.getMenuCode())) result.setChoose(true);
            result.setChildren(children(j, respList, menuList));
            return result;
        }).collect(Collectors.toList());
        return collect;
    }

    public Map<String, List<String>> userMenu(List<AdminMenuDo> data) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(data)) return null;
        Map<String, List<String>> result = new LinkedHashMap<>();//test
        List<String> systemList = data.stream().filter(menu -> NumCode.ZERO.getCode() == menu.getLevel()).sorted(Comparator.comparing(AdminMenuDo::getSort)).map(i -> i.getMenuCode()).collect(Collectors.toList());
        systemList.forEach(i -> {
            List<String> list = new ArrayList<>();
            result.put(i, getUrl(data, i, list));
        });
        return result;
    }

    public List<String> getUrl(List<AdminMenuDo> ms, String code, List<String> urlList) {
        ms.forEach(i -> {
            if (code.equalsIgnoreCase(i.getMenuParentCode())) {
                urlList.add(i.getMenuUrl());
                getUrl(ms, i.getMenuCode(), urlList);
            }
        });
        return urlList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewPlatAppResp reviewPlatformApplication(ReviewPlatAppReq req) {
        UserCertificateOptionsDo userCertificateOptionsDo = new UserCertificateOptionsDo();
        UserPlatformPermissionsDo permission = new UserPlatformPermissionsDo();
        permission.setApprovalStatus(req.getApprovalStatus());
        String userId = req.getUserId();
        if (NumCode.THREE.getCode() == req.getApprovalStatus()) {
            LambdaQueryWrapperX<UserCertificateOptionsDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(UserCertificateOptionsDo::getCertificateId, QualificationType.CSP_USER.getCode())
                    .eq(UserCertificateOptionsDo::getCertificateApplyStatus, NumCode.THREE.getCode())
                    .eq(UserCertificateOptionsDo::getUserId, userId);
            List<UserCertificateOptionsDo> userCertificateOptionsDos = userCertificateOptionsDao.selectList(queryWrapperX);
            if (CollectionUtils.isEmpty(userCertificateOptionsDos)) {
                userCertificateOptionsDo.setCertificateId(QualificationType.CSP_USER.getCode());
                userCertificateOptionsDo.setUserId(userId);
                userCertificateOptionsDo.setCertificateApplyStatus(NumCode.THREE.getCode());
                userCertificateOptionsDo.setApplyTime(new Date());
                userCertificateOptionsDo.setApprovalTime(new Date());
                userCertificateOptionsDo.setCertificateStatus(NumCode.ZERO.getCode());
                userCertificateOptionsDao.insert(userCertificateOptionsDo);
            }
            permission.setUserStatus(NumCode.ONE.getCode());
            //向csp表插入数据
            cspService.createCsp(userId);
            // 向csp_channel表插入数据 默认直连
            cspService.createCspChannel(userId);
        } else {
            permission.setUserStatus(NumCode.ZERO.getCode());
        }


        LambdaUpdateWrapper<UserPlatformPermissionsDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserPlatformPermissionsDo::getUserId, userId)
                .eq(UserPlatformPermissionsDo::getProtal, req.getProtal());
        if (NumCode.ONE.getCode() != userPlatformPermissionsDao.update(permission, updateWrapper))
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        ApprovalLogDo log = new ApprovalLogDo()
                .setAdminUserId(SessionContextUtil.getUser().getUserId())
                .setAdminUserName(SessionContextUtil.getUser().getUserName())
                .setApprovalLogId(req.getApprovalLogId())
                .setHandleTime(new Date())
                .setRemark(req.getRemark());
        if (NumCode.ONE.getCode() != approvalLogDao.insert(log))
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        ReviewPlatAppResp resp = new ReviewPlatAppResp();
        resp.setResult(true);
        return resp;
    }

    private Map<String, String> createPublicKey() {
        try {
            // 加密算法
            String algorithm = "RSA";
            //  创建密钥对生成器对象
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            // 生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // 生成私钥
            PrivateKey privateKey = keyPair.getPrivate();
            // 生成公钥
            PublicKey publicKey = keyPair.getPublic();
            // 获取私钥字节数组
            byte[] privateKeyEncoded = privateKey.getEncoded();
            // 获取公钥字节数组
            byte[] publicKeyEncoded = publicKey.getEncoded();
            String privateKeyString = Base64.encode(privateKeyEncoded);
            String publicKeyString = Base64.encode(publicKeyEncoded);
            // 对公私钥进行base64编码
            Map<String, String> map = new HashMap<>();
            map.put("privateKey", privateKeyString);
            map.put("publicKey", publicKeyString);
            return map;
        } catch (Exception e) {
            log.error("生成密钥对失败,", e);
        }
        return null;
    }


    @Override
    public GetReviewLogListResp getReviewLogList(GetReviewLogListReq req) {
        GetReviewLogListResp resp = new GetReviewLogListResp();
        LambdaQueryWrapperX<ApprovalLogDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(ApprovalLogDo::getApprovalLogId, req.getUuid())
                .orderByDesc(ApprovalLogDo::getHandleTime);
        List<ApprovalLogDo> data = approvalLogDao.selectList(queryWrapper);
        List<ApprovalLogItem> list = new ArrayList<>();
        data.forEach(i -> {
            ApprovalLogItem Item = new ApprovalLogItem();
            BeanUtils.copyProperties(i, Item);
            list.add(Item);
        });
        resp.setApprovalLogItems(list);
        return resp;
    }

    @Override
    public void changeTempStorePermission(ChangePrem changePrem) {
        userMapper.changeTempStorePermission(changePrem);
        //添加操作处理记录
        String opName = changePrem.getPermission().equals(0) ? "禁用模版发布" : "启用模版发布";
        BaseUser baseUser = SessionContextUtil.getUser();
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(changePrem.getUserId());
        processingRecordReq.setBusinessType(BusinessTypeEnum.YHGL_HN.getCode());
        processingRecordReq.setProcessingContent(opName);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordReq.setRemark(changePrem.getRemark());
        processingRecordApi.addRecord(processingRecordReq);
        TokenUtils.updateTempStorePermContextUser(changePrem.getUserId(), !changePrem.getPermission().equals(0));
    }

    @Override
    public boolean haseTempStorePerm(String userId) {
        Integer perm = userMapper.getTempStorePerm(userId);
        return Integer.valueOf(1).equals(perm);
    }

    @Override
    public void tempStorePermissionActiveOff(String userId) {
        msGoodsApi.closeTempStorePermission(userId);
    }

    @Override
    public CertStatisticsResp statisticsForCertificate(@RequestParam("status") Integer status) {
        List<Integer> statusList = Arrays.asList(0, 1, 2, 3);
        if (ObjUtil.isNull(status) || !statusList.contains(status)) {
            throw new BizException("错误的参数(status)值");
        }
        CertStatisticsResp certStatisticsResp = new CertStatisticsResp();

        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getDeleted, NumCode.ZERO.getCode());
        queryWrapper.eq(UserDo::getPersonAuthStatus, status);
        certStatisticsResp.setPersonal(userDao.selectCount(queryWrapper));
        queryWrapper.clear();
        queryWrapper.eq(UserDo::getDeleted, NumCode.ZERO.getCode());
        queryWrapper.eq(UserDo::getEnterpriseAuthStatus, status);
        certStatisticsResp.setEnterprise(userDao.selectCount(queryWrapper));
        return certStatisticsResp;
    }
}
