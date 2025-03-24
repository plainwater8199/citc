package com.citc.nce.auth.csp.contract.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.agent.dao.AgentDao;
import com.citc.nce.auth.csp.agent.entity.AgentDo;
import com.citc.nce.auth.csp.common.CSPChannelEnum;
import com.citc.nce.auth.csp.common.CSPStatusOptionsEnum;
import com.citc.nce.auth.csp.contract.dao.ContractManageChangeDao;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageChangeDo;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.contract.service.ContractManageService;
import com.citc.nce.auth.csp.contract.vo.*;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.mobile.req.AgentServiceCode;
import com.citc.nce.auth.mobile.req.SignedCustomer;
import com.citc.nce.auth.mobile.resp.SyncResult;
import com.citc.nce.auth.mobile.service.ChatBotService;
import com.citc.nce.auth.unicomAndTelecom.req.*;
import com.citc.nce.auth.unicomAndTelecom.service.CspPlatformService;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.req.GetUserInfoReq;
import com.citc.nce.authcenter.auth.vo.resp.ChannelInfoResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.service.impl
 * @Author: litao
 * @CreateTime: 2023-02-13  10:22
 * @Version: 1.0
 */
@Service
@Slf4j
public class ContractManageServiceImpl extends ServiceImpl<ContractManageDao, ContractManageDo> implements ContractManageService {
    @Resource
    private ContractManageDao contractManageDao;
    @Resource
    private ChatBotService chatBotService;

    @Resource
    private ContractManageChangeDao contractManageChangeDao;

    @Resource
    private CspPlatformService cspPlatformService;

    @Resource
    private AccountManageService accountManageService;

    @Resource
    private AccountManagementDao accountManagementDao;

    @Resource
    private AgentDao agentDao;

    @Resource
    private CspService cspService;

    @Autowired
    private CspCustomerApi cspCustomerApi;

    @Autowired
    private AdminAuthApi adminAuthApi;

    @Autowired
    private UserEnterpriseIdentificationDao ueiDao;
    @Autowired
    private FileApi fileApi;

    @Autowired
    private CommonKeyPairConfig commonKeyPairConfig;

    public static final String SUCCESS_CODE = "00000";
    public static final String ISP_ERROR = "运营商返回错误信息:";

    @Override
    public PageResult<ContractResp> queryList(ContractReq req) {
        req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);
        req.setCspId(cspId);
        Long total = contractManageDao.queryListCount(req);
        PageResult<ContractResp> pageResult = new PageResult<>();
        if (total > 0) {
            List<ContractResp> list = contractManageDao.queryList(req);
            pageResult.setList(list);
        } else {
            pageResult.setList(Collections.emptyList());
        }
        pageResult.setTotal(total);
        return pageResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int setServiceCode(ContractServiceCodeReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        ContractManageDo contractManageDo = new ContractManageDo();
        BeanUtil.copyProperties(req, contractManageDo);
        ContractManageDo manageDo = contractManageDao.selectById(req.getId());
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!manageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        contractManageDo.setContractStatus(20);
        //绑定运营平台服务代码
        allotServiceCode(req, baseUser, "1");
        if (30 == manageDo.getContractStatus()) {
            //正常状态不改状态
            contractManageDo.setContractStatus(30);
        }
        return contractManageDao.updateById(contractManageDo);
    }

    private void allotServiceCode(ContractServiceCodeReq req, BaseUser baseUser, String type) {
        AgentServiceCode agentServiceCode = new AgentServiceCode();
        ContractManageDo manageDo = contractManageDao.selectOne("id", req.getId());
        if (StringUtils.isEmpty(manageDo.getCustomerNum())) {
            throw new BizException(820103020, "客户代码为空，请联系管理员");
        }
        agentServiceCode.setServiceCode(req.getContractServiceCode())
                .setExtCode(req.getContractServiceExtraCode())
                .setCustomerNum(manageDo.getCustomerNum())
                .setType(type)
                .setCreator(baseUser.getUserId());
        SyncResult syncResult = chatBotService.syncAllotServiceCode(agentServiceCode);
        if (!StringUtils.equals(syncResult.getResultCode(), SUCCESS_CODE)) {
            throw new BizException(820103007, syncResult.getResultDesc());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int cleanServiceCode(ContractServiceCodeReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        ContractManageDo manageDo = contractManageDao.selectById(req.getId());
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!manageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        Integer result = contractManageDao.cleanServiceCode(req);
        allotServiceCode(req, baseUser, "2");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOrUpdate(ContractSaveReq req) {
        return -1;
    }


    public static Date getEndOfDay(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        calendarEnd.set(Calendar.MILLISECOND, 0); // 这一句比较关键
        return calendarEnd.getTime();
    }

    private static Date atStartOfDayJava8(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        ZonedDateTime startOfDay = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(startOfDay.toInstant());
    }

    //移动合同开始和结束时间验证时间验证
    private static void verifyCMCCContractTime(ContractCmccAdd req) {
        Date todayEnd = getEndOfDay(new Date());
        //将合同日期延长至日期的最后时间
        if (ObjectUtils.isNotEmpty(req.getContractEffectiveDate())) {
            req.setContractEffectiveDate(atStartOfDayJava8(req.getContractEffectiveDate()));
        }
        if (ObjectUtils.isNotEmpty(req.getContractExpireDate())) {
            req.setContractExpireDate(getEndOfDay(req.getContractExpireDate()));
        }
        if (ObjectUtils.isNotEmpty(req.getContractRenewalDate())) {
            req.setContractRenewalDate(getEndOfDay(req.getContractRenewalDate()));
        }

        boolean startContractIsEmpty = ObjectUtils.isEmpty(req.getContractEffectiveDate());
        boolean endContractIsEmpty = ObjectUtils.isEmpty(req.getContractExpireDate());
        // 只填一个
        if ((startContractIsEmpty && !endContractIsEmpty) || (!startContractIsEmpty && endContractIsEmpty)) {
            throw new BizException(820103024, "合同起止时间可不填，如果填写需要同时填写");
        }
        // 都填写
        if (!startContractIsEmpty) {
            //开始和结束不能是同一天
            if (DateUtils.isSameDay(req.getContractExpireDate(), req.getContractEffectiveDate())) {
                throw new BizException(820103025, "合同起止时间不能是同一天，失效时间需要大于（不能等于）生效时间");
            }
            //开始不能是当天
            if (req.getContractExpireDate().before(todayEnd)) {
                throw new BizException(820103026, "合同到期时间不能早于当天（≥当天）");
            }
        } else {
            log.warn("长期合同");
        }

        //处理续签时间是否正确
        if (Integer.valueOf(1).equals(req.getContractIsRenewal())) {
            if (ObjectUtils.isNotEmpty(req.getContractRenewalDate())) {
                if (!todayEnd.before(req.getContractRenewalDate())) {
                    throw new BizException(820103027, "续签时间需要≥当天");
                }
                if (ObjectUtils.isNotEmpty(req.getContractExpireDate())) {
                    if (!req.getContractRenewalDate().after(req.getContractExpireDate())) {
                        throw new BizException(820103028, "续签时间需要大于合同到期时间");
                    }
                }
            }
        }
    }

    /**
     * 构建联通电信合同信息
     *
     * @param req 信息
     * @return
     */
    private CspCustomerReq buildParam(Integer isp, ContractUTAdd req, String cspEcNo) {
        CspCustomerReq cspCustomerReq = new CspCustomerReq();


        RcsRegisterInfo rcsRegisterInfo = new RcsRegisterInfo();
        if (StringUtils.isNotEmpty(cspEcNo)) {
            rcsRegisterInfo.setCspEcNo(cspEcNo);
        }
        //基本信息
        rcsRegisterInfo.setEcName(req.getEnterpriseName())
                .setBusinessType(Integer.parseInt(req.getIndustryCodePrefix()))
                .setEcGrade(req.getCustomerGrade());

        RcsInfo rcsInfo = new RcsInfo();
        rcsInfo.setIntroduce(req.getIntroduce())
                .setServiceIcon(req.getServiceIcon())
                .setWorkPhone(req.getWorkPhone())
                .setBusinessLicense(getUnicomAndTelecomFile(isp, req.getBusinessLicense()))
                .setBusinessAddress(req.getAddress())
                .setProvince(req.getProvince())
                .setCity(req.getCity())
                .setArea(req.getRegion())
                .setOperatorName(req.getContactName())
                .setOperatorCard(req.getContractLegalPersonCardNumber())
                .setOperatorPhone(String.valueOf(req.getContactPhoneNumber()))
                .setEmailAddress(req.getContactMail())
                .setOperatorIdentityPic(Arrays.asList(getUnicomAndTelecomFile(isp, req.getContactsIdCardFront()), getUnicomAndTelecomFile(isp, req.getContactsIdCardReverse())));

        RcsContractInformation rcsContractInformation = new RcsContractInformation();
        rcsContractInformation.setName(req.getContractName())
                .setContractNo(IdUtil.fastUUID())
                .setEffectiveDate(DateUtil.format(req.getContractEffectiveDate(), "yyyyMMddHHmmss"))
                .setExpiryDate(DateUtil.format(req.getContractExpireDate(), "yyyyMMddHHmmss"))
                .setRenewalDate(DateUtil.format(req.getContractRenewalDate(), "yyyyMMddHHmmss"))
                .setAccessory(req.getContractScanningUrl());
        rcsContractInformation.setStatus(Integer.valueOf(0).equals(req.getContractIsRenewal()) ? 2 : 1);
        RcsLegalP rcsLegalP = new RcsLegalP();
        rcsLegalP.setLegalName(req.getContractLegalPerson())
                .setLegalIdentification(req.getContractLegalPersonCardNumber())
                .setIdentificationPic(Arrays.asList(req.getCorporateIdCardFront(), req.getCorporateIdCardReverse()));
        cspCustomerReq.setRcsContractInformation(rcsContractInformation);
        cspCustomerReq.setRcsInfo(rcsInfo);
        cspCustomerReq.setRcsLegalP(rcsLegalP);
        cspCustomerReq.setRcsRegisterInfo(rcsRegisterInfo);
        return cspCustomerReq;
    }

    private String getUnicomAndTelecomFile(Integer isp, String file) {
        if (file.startsWith("http")) return file;
        ResponseEntity<byte[]> fileEntity = fileApi.download(new DownloadReq().setFileUUID(file));
        List<String> list = fileEntity.getHeaders().get("Content-Disposition");
        Assert.isTrue(list != null && !list.isEmpty(), "file name can not be empty");
        FileItem fileItem = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , list.get(0).replace("attachment; filename=", "")
        );
        try (InputStream input = new ByteArrayInputStream(Objects.requireNonNull(fileEntity.getBody()));
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            log.error("文件转换错误", e);
        }
        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        return cspPlatformService.uploadContractFile(new UploadReq().setFile(multipartFile).setUploadType(0).setOperatorCode(isp).setSceneId("codeincodeservice")).getOperatorUrl();
    }


    @Override
    public ContractDetailResp getDetailById(ContractDetailReq req) {
        ContractDetailResp resp = new ContractDetailResp();
        if (req.getChangeDetails() == 1) {
            ContractManageChangeDo contractManageChangeDo = contractManageChangeDao.selectOne("contract_id", req.getId());
            decode(contractManageChangeDo);
            BeanUtil.copyProperties(contractManageChangeDo, resp);
        } else {
            ContractManageDo contractManageDo = contractManageDao.selectById(req.getId());
            decode(contractManageDo);
            BeanUtil.copyProperties(contractManageDo, resp);
        }
        return resp;
    }


    @Override
    public int updateContractStatusByContractId(Long contractId, int status) {
        UpdateWrapper<ContractManageDo> update = new UpdateWrapper<>();
        update.eq("id", contractId).set("contract_status", status);
        contractManageDao.update(null, update);
        return 0;
    }

    /**
     * 【移动】
     * 1.合同起止时间可不填，如果填写需要同时填写（不能只填写其中一个时间）。
     * 如果未填写合同起止时间（且没有续签时间），则合同没有有效期（永不过期）；
     * 2.合同起止时间不能是同一天，失效时间需要大于（不能等于）生效时间；
     * 3.合同到期时间不能早于当天（≥当天）
     * 4.合同选择是续签，续签时间为合同的到期时间。续签时间需要大于合同到期时间（如果设置了合同到期时间），且续签时间需要≥当天；
     * 5.如果合同过期，不影响已有的Chatbot的状态和可用性；
     * 6.如果合同过期或不在有效期内，不能新增Chatbot。新增或者编辑页面可以选到过期合同对应的归属客户，提交保存的时候提示：“该客户的合同已过期，不能新增Chatbot”。
     */
    @Override
    @Transactional
    public void addContractCmcc(ContractCmccAdd req) {
        verifyCustomer(req.getCustomerId());
        //验证合同时间是否填写正确
        verifyCMCCContractTime(req);
        //合同查重
        duplicates(2, req.getCustomerId(), req.getContractName());
        ContractManageDo contractManageDo = new ContractManageDo();
        //向运营商添加非直签客户
        BeanUtil.copyProperties(req, contractManageDo);
        //存合同表
        contractManageDo.setCustomerNum("");
        contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_10_INIT.getCode());
        contractManageDo.setOperatorCode(2);
        encode(contractManageDo);
        contractManageDao.insert(contractManageDo);

        SyncResult syncResult = chatBotService.clientNew(buildCmccContractParam(req));
        if (!SUCCESS_CODE.equals(syncResult.getResultCode()) || Objects.isNull(syncResult.getData())) {
            throw new BizException(820103005, ISP_ERROR + syncResult.getResultDesc());
        }
        contractManageDao.update(
                null,
                Wrappers.<ContractManageDo>lambdaUpdate()
                        .eq(BaseDo::getId, contractManageDo.getId())
                        .set(ContractManageDo::getCustomerNum, syncResult.getData().getCustomerNum())
        );
    }

    @Override
    @Transactional
    public void upgradeCmcc(ContractCmccUpgrade req) {
        verifyCustomer(req.getCustomerId());
        //验证合同时间是否填写正确
        verifyCMCCContractTime(req);
        ContractManageDo upgradeDo = new ContractManageDo();
        BeanUtil.copyProperties(req, upgradeDo);
        //合同查重
        ContractManageDo oldContract = contractManageDao.selectById(req.getId());
        if (Objects.isNull(oldContract)) {
            throw new BizException(500, "合同不存在");
        }

        //判断合同是否允许编辑
        Integer oldContractStatus = oldContract.getContractStatus();
        if (!CSPContractStatusEnum.STATUS_30_ONLINE.getCode().equals(oldContractStatus)
                && !CSPContractStatusEnum.STATUS_12_UPDATE_AND_AUDIT_NOT_PASS.getCode().equals(oldContractStatus)
                && !CSPContractStatusEnum.STATUS_11_CRATE_AND_AUDIT_NOT_PASS.getCode().equals(oldContractStatus)) {
            throw new BizException(500, "当前合同状态不允许此操作");
        }

        //查询合同变更记录是否存在同名
        if (!oldContract.getContractName().equals(req.getContractName())) {
            if (contractManageDao.selectCount(new LambdaQueryWrapper<ContractManageDo>()
                    .eq(ContractManageDo::getCreator, SessionContextUtil.getUser().getUserId())
                    .eq(ContractManageDo::getContractName, req.getContractName())
                    .ne(ContractManageDo::getId, req.getId())) > 0) {
                throw new BizException(820103023, "合同名称重复，请修改信息");
            }
        }
        //向运营商变更非直签客户
        SignedCustomer signedCustomer = buildCmccContractParam(req);
        signedCustomer.setCustomerNum(oldContract.getCustomerNum());
        SyncResult syncResult;
        if (CSPContractStatusEnum.STATUS_11_CRATE_AND_AUDIT_NOT_PASS.getCode().equals(oldContract.getContractStatus())) {
            //变更前是 新增审核不通过
            upgradeDo.setContractStatus(CSPContractStatusEnum.STATUS_10_INIT.getCode());
            ContractServiceCodeReq codeReq = new ContractServiceCodeReq();
            codeReq.setId(oldContract.getId());
            contractManageDao.cleanServiceCode(codeReq);
            syncResult = chatBotService.clientNew(signedCustomer);
            //写入最新合同编号(客户回执id)
            upgradeDo.setCustomerNum(syncResult.getData().getCustomerNum());
        } else {
            upgradeDo.setContractStatus(CSPContractStatusEnum.STATUS_23_WAIT_UPDATE_AUDITING.getCode());
            syncResult = chatBotService.clientChange(signedCustomer);
        }
        if (!SUCCESS_CODE.equals(syncResult.getResultCode())) {
            throw new BizException(820103006, ISP_ERROR + syncResult.getResultDesc());
        }
        encode(upgradeDo);
        contractManageDao.updateById(upgradeDo);
    }

    @Override
    public ContractCmccInfo getCmccContract(Long id) {
        ContractManageDo contractManageDo = contractManageDao.selectById(id);
        decode(contractManageDo);
        //验证是不是当前csp得客户的合同
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!contractManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        ContractCmccInfo info = new ContractCmccInfo();
        BeanUtils.copyProperties(contractManageDo, info);
        info.setEnterpriseAccountName(this.queryUserEnterpriseAccountNameByUserId(info.getCustomerId()));
        return info;
    }

    /**
     * 【电信、联通】
     * 1.合同起止时间必填，不能是同一天，失效日期需要大于生效时间；
     * 2.合同到期时间≥当天；
     * 3.合同选择是续签，续签时间为合同的到期时间。续签时间需要大于合同到期时间，且续签时间需要≥当天；
     */
    @Override
    @Transactional
    public void saveUniTel(ContractUTAdd req, Integer isp) {
        verifyCustomer(req.getCustomerId());
        verifyTelecomUnicomTime(req);
        //合同查重
        duplicates(isp, req.getCustomerId(), req.getContractName());
        //创建非直签客户新增
        ContractManageDo contractManageDo = new ContractManageDo();
        BeanUtil.copyProperties(req, contractManageDo);
        contractManageDo.setCustomerNum("");
        contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_60_NEW_WAIT_AUDIT.getCode());
        //存储合同数据
        contractManageDo.setOperatorCode(isp);
        encode(contractManageDo);
        contractManageDao.insert(contractManageDo);
        AccountManageDo cspInfo = accountManageService.getCspAccount(isp, SessionContextUtil.verifyCspLogin());
        CspCustomerReq cspCustomerReq = buildParam(isp, req, null);
        String customerNum = cspPlatformService.addCspCustomer(cspCustomerReq, cspInfo.getCspPassword()
                , cspInfo.getCspAccount(), isp);
        contractManageDao.update(
                null,
                Wrappers.<ContractManageDo>lambdaUpdate()
                        .eq(BaseDo::getId, contractManageDo.getId())
                        .set(ContractManageDo::getCustomerNum, customerNum)
        );

    }

    @Override
    public void upgradeUniTel(ContractUTUpgrade req, Integer isp) {
        verifyCustomer(req.getCustomerId());
        verifyTelecomUnicomTime(req);

        //查询合同
        ContractManageDo oldContract = contractManageDao.selectById(req.getId());
        if (Objects.isNull(oldContract)) {
            throw new BizException(500, "合同不存在");
        }

        //判断合同是否允许编辑
        Integer oldContractStatus = oldContract.getContractStatus();
        if (!CSPContractStatusEnum.STATUS_66_NORMAL.getCode().equals(oldContractStatus)
                && !CSPContractStatusEnum.STATUS_68_EXPIRED.getCode().equals(oldContractStatus)
                && !CSPContractStatusEnum.STATUS_63_NEW_AUDIT_NOT_PASS.getCode().equals(oldContractStatus)) {
            throw new BizException(500, "当前合同状态不允许此操作");
        }

        //查询合同变更记录是否存在同名
        if (!oldContract.getContractName().equals(req.getContractName())) {
            if (contractManageDao.selectCount(new LambdaQueryWrapper<ContractManageDo>()
                    .eq(ContractManageDo::getCreator, SessionContextUtil.getUser().getUserId())
                    .eq(ContractManageDo::getContractName, req.getContractName())
                    .ne(ContractManageDo::getId, req.getId())) > 0) {
                throw new BizException(820103023, "合同名称重复，请修改信息");
            }
        }

        //联通电信编辑
        ContractManageDo update = new ContractManageDo();
        update.setId(req.getId());
        update.setContractStatus(CSPContractStatusEnum.STATUS_61_EDIT_WAIT_AUDIT.getCode());
        //保存合同真实历史状态
        update.setActualState(oldContractStatus);
        //通过合同id查询合同历史记录
        ContractManageChangeDo manageHistory = new ContractManageChangeDo();
        BeanUtil.copyProperties(req, manageHistory);
        manageHistory.setContractStatus(CSPContractStatusEnum.STATUS_61_EDIT_WAIT_AUDIT.getCode());
        manageHistory.setContractId(req.getId());
        encode(manageHistory);
        LambdaQueryWrapper<ContractManageChangeDo> queryByContractId = new LambdaQueryWrapper<ContractManageChangeDo>()
                .eq(ContractManageChangeDo::getContractId, req.getId());
        if (contractManageChangeDao.exists(queryByContractId)) {
            contractManageChangeDao.update(manageHistory, queryByContractId);
        } else {
            contractManageChangeDao.insert(manageHistory);
        }
        //如果是新增审核不通过，认为一次都没有通过，电信联通无此合同需要新增
        AccountManageDo cspInfo = accountManageService.getCspAccount(isp, SessionContextUtil.getUser().getUserId());
        if (CSPContractStatusEnum.STATUS_63_NEW_AUDIT_NOT_PASS.getCode().equals(oldContractStatus)) {
            //新增审核不通过
            update.setContractStatus(CSPContractStatusEnum.STATUS_60_NEW_WAIT_AUDIT.getCode());
            // 电信和联通，如果是新增审核不通过会直接删除数据，编辑的时候需要走新增的处理，所以这里不传原来的 cspEcNo
            CspCustomerReq cspCustomerReq = buildParam(isp, req, null);
            String customerNum = cspPlatformService.addCspCustomer(cspCustomerReq, cspInfo.getCspPassword(), cspInfo.getCspAccount(), isp);
            update.setCustomerNum(customerNum);
        } else {
            CspCustomerReq cspCustomerReq = buildParam(isp, req, oldContract.getCustomerNum());
            cspPlatformService.editCspCustomer(cspCustomerReq, cspInfo.getCspPassword(), cspInfo.getCspAccount(), isp);
        }
        contractManageDao.updateById(update);
    }


    @Override
    public ContractUTInfo getUnTelContract(Long id) {
        ContractManageDo contractManageDo = contractManageDao.selectById(id);
        decode(contractManageDo);
        //验证是不是当前csp得客户的合同
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!contractManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        ContractUTInfo info = new ContractUTInfo();
        BeanUtils.copyProperties(contractManageDo, info);
        info.setEnterpriseAccountName(this.queryUserEnterpriseAccountNameByUserId(info.getCustomerId()));
        return info;
    }

    @Override
    public ContractCmccInfo getCmccContractChange(Long id) {
        LambdaQueryWrapper<ContractManageChangeDo> queryByContractId = new LambdaQueryWrapper<ContractManageChangeDo>()
                .eq(ContractManageChangeDo::getContractId, id);
        ContractManageChangeDo changeDo = contractManageChangeDao.selectOne(queryByContractId);
        if (Objects.isNull(changeDo)) return null;
        //验证是不是当前csp得客户的合同
        decode(changeDo);
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!changeDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        ContractCmccInfo info = new ContractCmccInfo();
        BeanUtils.copyProperties(changeDo, info);
        info.setEnterpriseAccountName(this.queryUserEnterpriseAccountNameByUserId(info.getCustomerId()));
        return info;
    }

    @Override
    public ContractUTInfo getUnTelContractChange(Long id) {
        LambdaQueryWrapper<ContractManageChangeDo> queryByContractId = new LambdaQueryWrapper<ContractManageChangeDo>()
                .eq(ContractManageChangeDo::getContractId, id);
        ContractManageChangeDo changeDo = contractManageChangeDao.selectOne(queryByContractId);
        if (Objects.isNull(changeDo)) return null;
        //验证是不是当前csp得客户的合同
        decode(changeDo);
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!changeDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        ContractUTInfo info = new ContractUTInfo();
        BeanUtils.copyProperties(changeDo, info);
        info.setEnterpriseAccountName(this.queryUserEnterpriseAccountNameByUserId(info.getCustomerId()));
        return info;
    }

    /**
     * 供应商新建合同
     */
    @Override
    @Transactional
    public void saveSupplierContract(ContractSupplierAdd req) {
        // 验证客户信息
        verifyCustomer(req.getCustomerId());
        // 合同查重
        verifySupplierSaveInfo(req);
        ContractManageDo contractManageDo = new ContractManageDo();
        // 赋值
        BeanUtil.copyProperties(req, contractManageDo);
        // 存合同表
        if (Objects.equals(req.getOperatorCode(), CSPOperatorCodeEnum.CMCC.getCode())) {
            contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_25_NO_COMMIT.getCode());
        }
        if (Objects.equals(req.getOperatorCode(), CSPOperatorCodeEnum.CT.getCode())
                || Objects.equals(req.getOperatorCode(), CSPOperatorCodeEnum.CUNC.getCode())
        ) {
            contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_69_NO_COMMIT.getCode());
        }
        contractManageDo.setChannel(CSPChannelEnum.FONTDO.getValue());
        contractManageDo.setOperatorCode(req.getOperatorCode());
        encode(contractManageDo);
        contractManageDao.insert(contractManageDo);
    }


    /**
     * 供应商更新合同
     */
    @Override
    @Transactional
    public void updateSupplierContract(ContractSupplierUpdate req) {
        // 验证客户信息
        verifyCustomer(req.getCustomerId());
        // 合同查重
        verifySupplierUpdateInfo(req);
        ContractManageDo contractManageDo = new ContractManageDo();
        // 赋值
        BeanUtil.copyProperties(req, contractManageDo);
        // 更新
        encode(contractManageDo);
        contractManageDao.updateById(contractManageDo);
    }

    /**
     * 获取供应商合同信息
     */
    @Override
    public ContractSupplierInfo getSupplierContractInfo(Long id) {
        ContractManageDo contractManageDo = contractManageDao.selectById(id);
        ContractSupplierInfo info = new ContractSupplierInfo();
        decode(contractManageDo);
        BeanUtils.copyProperties(contractManageDo, info);
        return info;
    }

    /**
     * 删除供应商合同信息
     */
    @Override
    public void deleteSupplierContract(Long id) {
        ContractManageDo contractManageDo = contractManageDao.selectById(id);
        if (contractManageDo == null) {
            throw new BizException(820103033, "合同处于不可用状态");
        }
        Integer[] canDeleteStatus = {
                CSPContractStatusEnum.STATUS_25_NO_COMMIT.getCode(),
                CSPContractStatusEnum.STATUS_69_NO_COMMIT.getCode(),
                CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(),
                CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode()
        };
        if (!Arrays.asList(canDeleteStatus).contains(contractManageDo.getContractStatus())) {
            throw new BizException(820103033, "合同无法删除");
        }
        LambdaUpdateWrapper<ContractManageDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ContractManageDo::getId, id);
        updateWrapper.set(ContractManageDo::getDeleted, 1);
        updateWrapper.set(ContractManageDo::getDeletedTime, DateUtil.date());
        contractManageDao.delete(updateWrapper);
    }

    /**
     * 注销供应商合同信息
     */
    @Override
    public void logOffSupplierContract(Long id) {
        ContractManageDo contractManageDo = contractManageDao.selectById(id);
        // TODO 验证chatbot信息
        QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", contractManageDo.getCustomerId());
        queryWrapper.eq("account_type_code", contractManageDo.getOperatorCode());
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
        if (accountManagementDo != null) {
            Integer[] canLogOffChatbotStatus = {
                    CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode(),
                    CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode(),
                    CSPChatbotStatusEnum.STATUS_28_REJECT.getCode(),
                    CSPChatbotStatusEnum.STATUS_74_REJECT.getCode()
            };
            if (!Arrays.asList(canLogOffChatbotStatus).contains(accountManagementDo.getChatbotStatus())) {
                throw new BizException(820103034, "存在可用Chatbot, 不可注销合同");
            }
        }

        // 电联设置状态
        if (contractManageDo.getOperatorCode() == 1 || contractManageDo.getOperatorCode() == 3) {
            contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode());
        } else {
            contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode());
        }
        contractManageDao.updateById(contractManageDo);
    }

    /**
     * 查询合同状态配置
     */
    @Override
    public ContractStatusResp getContractStatusOptions() {
        String userId = SessionContextUtil.getUser().getUserId();
        ContractStatusResp resp = new ContractStatusResp();
        resp.setUnicomContractOptions(CSPStatusOptionsEnum.DIRECT.getValue());
        // 查询当前用户的通道
        GetUserInfoReq req = new GetUserInfoReq();
        req.setUserId(userId);
        ChannelInfoResp channelResp = adminAuthApi.getCspUserChannel(req);
        // 查询当前CSP用户签的合同类型（暂时只有电信和移动有代理合同
        // 电信
        QueryWrapper<ContractManageDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("customer_id", userId);
        queryWrapper.eq("operator_code", CSPOperatorCodeEnum.CT.getCode());
        queryWrapper.ne("channel", channelResp.getTelecomChannel()); // 查询当前用户不是当前通道的合同
        queryWrapper.notIn("contract_status", CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
        long telecomCount = contractManageDao.selectCount(queryWrapper);
        if (telecomCount == 0) {
            resp.setTelecomContractOptions(channelResp.getTelecomChannel());
        } else {
            resp.setTelecomContractOptions(CSPStatusOptionsEnum.UNION.getValue());
        }
        // 移动
        queryWrapper.eq("operator_code", CSPOperatorCodeEnum.CMCC.getCode());
        queryWrapper.ne("channel", channelResp.getMobileChannel()); // 查询当前用户不是当前通道的合同
        queryWrapper.notIn("contract_status", CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
        long mobileCount = contractManageDao.selectCount(queryWrapper);
        if (mobileCount == 0) {
            resp.setMobileContractOptions(channelResp.getMobileChannel());
        } else {
            resp.setMobileContractOptions(CSPStatusOptionsEnum.UNION.getValue());
        }
        // 联通
        queryWrapper.eq("operator_code", CSPOperatorCodeEnum.CUNC.getCode());
        queryWrapper.ne("channel", channelResp.getUnicomChannel()); // 查询当前用户不是当前通道的合同
        queryWrapper.notIn("contract_status", CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
        long unionCount = contractManageDao.selectCount(queryWrapper);
        if (unionCount == 0) {
            resp.setMobileContractOptions(channelResp.getUnicomChannel());
        } else {
            resp.setMobileContractOptions(CSPStatusOptionsEnum.UNION.getValue());
        }
        return resp;
    }

    /**
     * 查询新增合同权限
     */
    @Override
    public Boolean getNewContractPermission(ContractNewPermissionReq req) {
        verifyCustomer(req.getCustomerId());
        QueryWrapper<ContractManageDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", req.getCustomerId());
        queryWrapper.eq("operator_code", req.getOperatorCode());
        queryWrapper.notIn("contract_status", CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
        ContractManageDo contractManageDo = contractManageDao.selectOne(queryWrapper);
        // 没有合同，可以新建
        if (contractManageDo == null) {
            return true;
        }
        Integer[] noPassNewPermissionStatus = {
                CSPContractStatusEnum.STATUS_69_NO_COMMIT.getCode(),
                CSPContractStatusEnum.STATUS_25_NO_COMMIT.getCode(),
                CSPContractStatusEnum.STATUS_70_PROCESSING.getCode(),
                CSPContractStatusEnum.STATUS_26_PROCESSING.getCode(),
                CSPContractStatusEnum.STATUS_30_ONLINE.getCode(),
                CSPContractStatusEnum.STATUS_66_NORMAL.getCode()
        };

        // 合同处于未提交，处理中，正常的不能再次创建合同
        return !Arrays.asList(noPassNewPermissionStatus).contains(contractManageDo.getContractStatus());
    }

    @Override
    public Boolean checkForCanCreate(ContractNewPermissionReq req) {
        verifyCustomer(req.getCustomerId());
        QueryWrapper<ContractManageDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", req.getCustomerId());
        queryWrapper.eq("operator_code", req.getOperatorCode());
        List<ContractManageDo> contractManageDo = contractManageDao.selectList(queryWrapper);
        // 没有合同，可以新建
        if (CollectionUtils.isEmpty(contractManageDo)) {
            return true;
        }
        Integer[] noPassNewPermissionStatus = {
                CSPContractStatusEnum.STATUS_40_PAUSE.getCode(),
                CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(),
                CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode()
        };

        // 如果存在合同的状态不是注销或者暂停状态时，不能再次创建合同
        List<ContractManageDo> collect = contractManageDo.stream().filter(contract -> !Arrays.asList(noPassNewPermissionStatus).contains(contract.getContractStatus())).collect(Collectors.toList());

        return CollectionUtils.isEmpty(collect);
    }

    /**
     * 移动非直签客户参数构建
     *
     * @param req 请求参数
     * @return
     */
    private SignedCustomer buildCmccContractParam(ContractCmccAdd req) {
        //新增非直签客户
        //根据代理商id查询代理商名称和编码
        String agentName = "";
        String agentCode = "";
        if (req.getAgentInfoId() != 0L) {
            AgentDo agentDo = agentDao.selectById(req.getAgentInfoId());
            if (StringUtils.isNotEmpty(agentDo.getAgentName())) {
                agentName = agentDo.getAgentName();
            }
            if (StringUtils.isNotEmpty(agentDo.getAgentCode())) {
                agentCode = agentDo.getAgentCode();
            }
        }
        //根据归属客户id查询客户名称
        SignedCustomer signedCustomer = new SignedCustomer();
        if (StringUtils.isNotEmpty(req.getContractScanningUrl())) {
            signedCustomer.setContractUrl("isoftstone/" + req.getContractScanningUrl() + ".pdf");
        }

        signedCustomer.setApplyTime(DateUtil.formatDate(new Date()))
                .setCustomerUrl("isoftstone/" + req.getContractFileUrl() + ".pdf")
                .setBelongRegionCode(getBelongRegionCode(req))
                .setCustomerName(req.getEnterpriseName())
                .setContractName(req.getContractName())
                .setContactPersonEmail(req.getContactMail())
                .setContactPersonPhone(req.getContactPhoneNumber())
                .setBelongAgentName(agentName)
                .setBelongAgentCode(agentCode)
                .setIndustryTypeCode(req.getIndustryCodeSuffix())
                .setUnifySocialCreditCodes(req.getContractOrginCode())
                .setEnterpriseOwnerName(req.getContractLegalPerson())
                .setCertificateType("0" + req.getContractLegalPersonCardType())
                .setCertificateCode(req.getContractLegalPersonCardNumber());

        signedCustomer.setContractValidDate(DateUtil.formatDate(req.getContractEffectiveDate()));
        signedCustomer.setContractInvalidDate(DateUtil.formatDate(req.getContractExpireDate()));

        return signedCustomer;
    }

    /**
     * 生成省市编码
     */
    @NotNull
    private static String getBelongRegionCode(ContractCmccAdd req) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(req.getRegion())) {
            sb.append(req.getRegion());
        }
        if (StringUtils.isNotBlank(req.getProvince())) {
            if (sb.length() > 0) sb.append(",");
            sb.append(req.getProvince());
        }
        if (StringUtils.isNotBlank(req.getCity())) {
            if (sb.length() > 0) sb.append(",");
            sb.append(req.getCity());
        }
        return sb.toString();
    }

    /**
     * 检查合同名称是否重复
     *
     * @param customerId   归属client用户的id
     * @param contractName 合同名称
     * @param isp          运营商类型 ：1：联通，2：移动，3：电信
     */
    private void duplicates(Integer isp, String customerId, String contractName) {
        //检查合同是否重复
        if (contractManageDao.selectCount(
                Wrappers.<ContractManageDo>lambdaQuery()
                        .eq(ContractManageDo::getCustomerId, customerId)
                        .eq(ContractManageDo::getOperatorCode, isp)
                        .ge(ContractManageDo::getContractExpireDate, new Date())//为什么要判断合同失效日期？？？？？
                        .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode())//为什么要判断合同失效日期？？？？？
                        .eq(ContractManageDo::getDeleted, false)) > 0) {
            throw new BizException(820103023, "同一运营商下同一客户只允许有一个合同");
        }
        //移动联通电信合同名称不能重复
        if (contractManageDao.selectCount(new LambdaQueryWrapper<ContractManageDo>()
                .eq(ContractManageDo::getCreator, SessionContextUtil.getUser().getUserId())
                .eq(ContractManageDo::getContractName, contractName)) > 1) {
            throw new BizException(820103023, "合同名称重复，请修改信息");
        }
    }

    /**
     * 检查供应商新建合同信息
     *
     * @param info 新建合同信息
     */
    private void verifySupplierSaveInfo(ContractSupplierAdd info) {
        // 检查相同客户只能有一个正常情况的合同
        LambdaQueryWrapper<ContractManageDo> queryOneWrapper = new LambdaQueryWrapper<>();
        queryOneWrapper.eq(ContractManageDo::getCustomerId, info.getCustomerId())
                .eq(ContractManageDo::getOperatorCode, info.getOperatorCode())
                .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode())
                .eq(ContractManageDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(ContractManageDo::getDeleted, false);
        if (contractManageDao.selectCount(queryOneWrapper) > 0) {
            throw new BizException(820103023, "该客户已具备正在使用中的本网合同");
        }

        // 检查合同名称不能重复
        if (contractManageDao.selectCount(new LambdaQueryWrapper<ContractManageDo>()
                .eq(ContractManageDo::getCreator, SessionContextUtil.getUser().getUserId())
                .eq(ContractManageDo::getContractName, info.getContractName())) > 0) {
            throw new BizException(820103023, "合同名称重复，请修改信息");
        }
    }

    /**
     * 检查供应商更新合同信息
     *
     * @param info 更新合同信息
     */
    private void verifySupplierUpdateInfo(ContractSupplierUpdate info) {
        // 检查相同客户只能有一个正常情况的合同
        LambdaQueryWrapper<ContractManageDo> queryOneWrapper = new LambdaQueryWrapper<>();
        queryOneWrapper.eq(ContractManageDo::getCustomerId, info.getCustomerId())
                .eq(ContractManageDo::getOperatorCode, info.getOperatorCode())
                .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode())
                .eq(ContractManageDo::getChannel, CSPChannelEnum.FONTDO.getValue())
                .eq(ContractManageDo::getDeleted, false)
                .ne(ContractManageDo::getId, info.getId());
        if (contractManageDao.selectCount(queryOneWrapper) > 0) {
            throw new BizException(820103023, "该客户已具备正在使用中的本网合同");
        }

        // 检查合同名称不能重复
        if (contractManageDao.selectCount(new LambdaQueryWrapper<ContractManageDo>()
                .eq(ContractManageDo::getCreator, SessionContextUtil.getUser().getUserId())
                .eq(ContractManageDo::getContractName, info.getContractName())
                .ne(ContractManageDo::getId, info.getId())) > 0) {
            throw new BizException(820103023, "合同名称重复，请修改信息");
        }
    }

    //电信联通合同开始和结束时间验证时间验证
    private static void verifyTelecomUnicomTime(ContractUTAdd req) {
        Date todayEnd = getEndOfDay(new Date());
        if (ObjectUtils.isNotEmpty(req.getContractEffectiveDate())) {
            req.setContractEffectiveDate(atStartOfDayJava8(req.getContractEffectiveDate()));
        }
        if (ObjectUtils.isNotEmpty(req.getContractExpireDate())) {
            req.setContractExpireDate(getEndOfDay(req.getContractExpireDate()));
        }
        if (ObjectUtils.isNotEmpty(req.getContractRenewalDate())) {
            req.setContractRenewalDate(getEndOfDay(req.getContractRenewalDate()));
        }
        // 同时不填
        if (ObjectUtils.isEmpty(req.getContractEffectiveDate()) && ObjectUtils.isEmpty(req.getContractExpireDate())) {
            throw new BizException(820103029, "合同起止时间必填");
        }
        if (DateUtils.isSameDay(req.getContractExpireDate(), req.getContractEffectiveDate())) {
            throw new BizException(820103025, "合同起止时间不能是同一天，失效时间需要大于（不能等于）生效时间");
        }
        if (!todayEnd.before(req.getContractExpireDate())) {
            throw new BizException(820103026, "合同到期时间不能早于当天（≥当天）");
        }
        //处理续签时间是否正确
        if (Integer.valueOf(1).equals(req.getContractIsRenewal())) {
            if (ObjectUtils.isNotEmpty(req.getContractRenewalDate())) {
                if (!todayEnd.before(req.getContractRenewalDate())) {
                    throw new BizException(820103027, "续签时间需要≥当天");
                }
                if (ObjectUtils.isNotEmpty(req.getContractExpireDate())) {
                    if (!req.getContractRenewalDate().after(req.getContractExpireDate())) {
                        throw new BizException(820103028, "续签时间需要大于合同到期时间");
                    }
                }
            }
        }
    }

    private void verifyCustomer(String customerId) {
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!customerId.substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        List<UserInfoVo> userInfoVos = cspCustomerApi.getByCustomerIds(Collections.singletonList(customerId));
        if (CollectionUtils.isEmpty(userInfoVos))
            throw new BizException("客户不存在");
        if (!Boolean.TRUE.equals(userInfoVos.get(0).getCustomerActive()))
            throw new BizException("客户未启用");
    }

    private String queryUserEnterpriseAccountNameByUserId(String userId) {
        UserEnterpriseIdentificationDo identificationDo = ueiDao.selectOne(UserEnterpriseIdentificationDo::getUserId, userId);
        if (identificationDo == null)
            return null;
        return identificationDo.getEnterpriseAccountName();
    }


    private void encode(ContractManageDo entity) {
        if (StrUtil.isNotBlank(entity.getWorkPhone()) && entity.getWorkPhone().length() < 50) {
            entity.setWorkPhone(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), entity.getWorkPhone()));
        }
        if (StrUtil.isNotBlank(entity.getContactPhoneNumber()) && entity.getContactPhoneNumber().length() < 50) {
            entity.setContactPhoneNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), entity.getContactPhoneNumber()));
        }
        if (StrUtil.isNotBlank(entity.getContractLegalPersonCardNumber()) && entity.getContractLegalPersonCardNumber().length() < 50) {
            entity.setContractLegalPersonCardNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), entity.getContractLegalPersonCardNumber()));
        }
    }

    private void encode(ContractManageChangeDo entity) {
        if (StrUtil.isNotBlank(entity.getWorkPhone()) && entity.getWorkPhone().length() < 50) {
            entity.setWorkPhone(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), entity.getWorkPhone()));
        }
        if (StrUtil.isNotBlank(entity.getContactPhoneNumber()) && entity.getContactPhoneNumber().length() < 50) {
            entity.setContactPhoneNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), entity.getContactPhoneNumber()));
        }
        if (StrUtil.isNotBlank(entity.getContractLegalPersonCardNumber()) && entity.getContractLegalPersonCardNumber().length() < 50) {
            entity.setContractLegalPersonCardNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), entity.getContractLegalPersonCardNumber()));
        }
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

    private void decode(ContractManageChangeDo entity) {
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

}
