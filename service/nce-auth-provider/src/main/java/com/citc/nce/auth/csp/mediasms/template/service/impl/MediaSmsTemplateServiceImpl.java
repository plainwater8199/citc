package com.citc.nce.auth.csp.mediasms.template.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.mediasms.template.dao.MediaSmsTemplateMapper;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateAuditDo;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateContentDo;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateDo;
import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.ContentMediaType;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import com.citc.nce.auth.csp.mediasms.template.service.MediaSmsTemplateAuditService;
import com.citc.nce.auth.csp.mediasms.template.service.MediaSmsTemplateContentService;
import com.citc.nce.auth.csp.mediasms.template.service.MediaSmsTemplateService;
import com.citc.nce.auth.csp.mediasms.template.vo.*;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateContentVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDataVo;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.account.service.CspVideoSmsAccountService;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
import com.citc.nce.auth.csp.videoSms.signature.service.CspVideoSmsSignatureService;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.FileInfo;
import com.citc.nce.filecenter.vo.FileInfoReq;
import com.citc.nce.robot.api.RichMediaNotifyApi;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.req.RobotGroupSendPlanIdReq;
import com.citc.nce.robot.vo.TemplateReq;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiancheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MediaSmsTemplateServiceImpl extends ServiceImpl<MediaSmsTemplateMapper, MediaSmsTemplateDo> implements MediaSmsTemplateService {
    @Resource
    private CommonKeyPairConfig keyPairConfig;
    private final static double CONTENT_SIZE_LIMIT = 1.8 * 1024 * 1024;
    private static final Map<ContentMediaType, List<String>> MEDIA_TYPES;

    @Value("${mediaDownLoad.baseUrI}")
    private String mediaDownLoadUrI;

    static {
        MEDIA_TYPES = new HashMap<>();
        MEDIA_TYPES.put(ContentMediaType.IMAGE, Arrays.asList("JPG", "JPEG", "BMP", "PNG", "GIF"));
        MEDIA_TYPES.put(ContentMediaType.SOUND, Arrays.asList("MP3", "WMV", "AAC"));
        MEDIA_TYPES.put(ContentMediaType.MOVIE, Collections.singletonList("MP4"));
    }

    private final MediaSmsTemplateContentService contentService;
    private final MediaSmsTemplateAuditService auditService;
    private final FileApi fileApi;
    private final RichMediaNotifyApi mediaNotifyApi;
    private final CspVideoSmsAccountService accountService;
    private final RobotGroupSendPlansApi sendPlansApi;
    private final CspVideoSmsSignatureService signatureService;

    private final CspVideoSmsAccountDao cspVideoSmsAccountDao;

    private CspVideoSmsAccountDo getCustomerVideoSmsAccountByAccountId(String accountId) {
        CspVideoSmsAccountDo smsAccountDo = cspVideoSmsAccountDao.selectOne(
                Wrappers.<CspVideoSmsAccountDo>lambdaQuery()
                        .eq(CspVideoSmsAccountDo::getAccountId, accountId)
                        .eq(CspVideoSmsAccountDo::getCustomerId, SessionContextUtil.getUser().getUserId())
        );
        if (Objects.nonNull(smsAccountDo)){
            smsAccountDo.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(),smsAccountDo.getAppSecret()));
        }
        return smsAccountDo;
    }


    /**
     * 新增模板
     *
     * @param templateAddVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addTemplate(MediaSmsTemplateAddVo templateAddVo) {
        String templateName = templateAddVo.getTemplateName();
        String accountId = templateAddVo.getAccountId();
        if (isRepeatTemplateName(accountId, templateName))
            throw new BizException(500, "模板名称重复");
        String userId = SessionContextUtil.getUser().getUserId();
        MediaSmsTemplateDo templateDo = new MediaSmsTemplateDo()
                .setTemplateName(templateName)
                .setAccountId(templateAddVo.getAccountId())
                .setTopic(templateAddVo.getTopic())
                .setCustomerId(userId)
                .setTemplateType(templateAddVo.getTemplateType())
                .setSignatureId(templateAddVo.getSignatureId());
        try {
            this.save(templateDo);
        } catch (DataIntegrityViolationException e) {
            log.error("新增模板失败", e);
            throw new BizException(500, "新增模板失败");
        }
        List<MediaSmsTemplateContentDo> contents = this.saveContent(templateAddVo.getContents(), templateDo.getId());
        //保存送审信息
        List<MediaSmsTemplateAuditDo> auditDos = Arrays.stream(OperatorPlatform.values())
                .map(operator -> new MediaSmsTemplateAuditDo()
                        .setMediaTemplateId(templateDo.getId())
                        .setOperator(operator)
                        .setStatus(AuditStatus.WAIT_POST)
                )
                .collect(Collectors.toList());
        this.auditService.saveBatch(auditDos);
        //异步送审
        if (templateAddVo.getSubmitForAudit()) {
            CspVideoSmsAccountDo accountDetail = this.getCustomerVideoSmsAccountByAccountId(accountId);
            if (accountDetail == null || accountDetail.getStatus() == null || !accountDetail.getStatus().equals(1))
                throw new BizException(500, "该视频短信账号已删除或禁用，请重新选择");
            String signature = this.getSignatureById(templateDo.getSignatureId());
            MediaSmsTemplateService self = SpringUtil.getBean(MediaSmsTemplateService.class);
            self.reportTemplate(templateDo.getId(), templateDo.getAccountId(), templateAddVo.getTopic(), signature, contents, templateAddVo.getTemplateType());
        }
    }

    @Override
    @Async
    public void reportTemplate(Long templateId, String accountId, String templateName, String sign, List<MediaSmsTemplateContentDo> contents, Integer templateType) {
        TemplateReq req = new TemplateReq();
        CspVideoSmsAccountDo accountDetail = this.getCustomerVideoSmsAccountByAccountId(accountId);
        if (accountDetail == null || accountDetail.getStatus() == null || !accountDetail.getStatus().equals(1))
            throw new BizException(500, "该视频短信账号已删除或禁用，请重新选择");
        req.setAppId(accountDetail.getAppId());
        req.setAppSecret(accountDetail.getAppSecret());
        req.setTitle(templateName.length() >= 15 ? templateName.substring(0, 15) : templateName);
        req.setTemplateSign(sign.length() >= 15 ? sign.substring(0, 15) : sign);
        req.setReportOperator(Arrays.stream(OperatorPlatform.values()).map(OperatorPlatform::getCode).collect(Collectors.toList()));
        List<TemplateReq.TemplatePage> templatePageList = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            TemplateReq.TemplatePage page = new TemplateReq.TemplatePage();
            page.setPageNumber(i + 1);
            List<TemplateReq.TemplateItem> items = new ArrayList<>();
            TemplateReq.TemplateItem item = new TemplateReq.TemplateItem();
            item.setSerialNumber(1);
            MediaSmsTemplateContentDo contentAddVo = contents.get(i);
            String content = contentAddVo.getContent();
            switch (contentAddVo.getMediaType()) {
                case TEXT: {
                    SmsTemplateContentVo smsTemplateContentVo = JSONObject.parseObject(content, SmsTemplateContentVo.class);
                    List<SmsTemplateDataVo> smsTemplateDataVos = smsTemplateContentVo.getNames();
                    String contentNew = smsTemplateContentVo.getValue();
                    if (templateType == 2) {
                        if (smsTemplateDataVos != null && !smsTemplateDataVos.isEmpty()) {
                            for (SmsTemplateDataVo smsTemplateDataVo : smsTemplateDataVos) {
                                contentNew = contentNew.replace("{{" + smsTemplateDataVo.getId() + "}}", "{#" + smsTemplateDataVo.getName() + "#}");
                            }
                        }
                    }
                    item.setExt("txt");
                    item.setContent(contentNew);
                    break;
                }
                case SOUND:
                case MOVIE:
                case IMAGE: {
                    FileInfo fileInfo = fileApi.getFileInfo(new FileInfoReq(content,null));
                    item.setExt(fileInfo.getFileFormat().toLowerCase());
                    item.setContent(String.format("%s/file/download/chatbot?uuid=%s&templateId=%s", mediaDownLoadUrI, content, templateId));
                }
            }
            items.add(item);
            page.setItems(items);
            templatePageList.add(page);
        }
        req.setPages(templatePageList);
        log.info("视频短信送审记录：{}",JSONObject.toJSONString(req));
        String platformId = mediaNotifyApi.templateReport(req);
        //回写平台模板ID
        this.setPlatformTemplateId(templateId, platformId);
        //修改送审状态
        auditService.lambdaUpdate()
                .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, templateId)
                .set(MediaSmsTemplateAuditDo::getStatus, AuditStatus.AUDITING)
                .update();
        //回写固定模板签名
        Long signatureId = this.lambdaQuery().select(MediaSmsTemplateDo::getSignatureId).eq(MediaSmsTemplateDo::getId, templateId).one().getSignatureId();
        String signature = this.getSignatureById(signatureId);
        this.lambdaUpdate()
                .eq(MediaSmsTemplateDo::getId, templateId)
                .set(MediaSmsTemplateDo::getSignature, signature)
                .update();
    }

    /**
     * 保存模板内容
     *
     * @param contents   内容数组
     * @param templateId 模板ID
     */
    private List<MediaSmsTemplateContentDo> saveContent(List<MediaSmsTemplateContentAddVo> contents, Long templateId) {
        //保存内容
        boolean hasText = false, hasMedia = false;
        for (MediaSmsTemplateContentAddVo content : contents) {
            if (content.getMediaType() == ContentMediaType.TEXT) {
                hasText = true;
            } else {
                hasMedia = true;
            }
            if (hasText && hasMedia)
                break;
        }
        if (!(hasText && hasMedia)) {
            throw new BizException(500, "模板内容至少包含文本和媒体各一项");
        }
        List<MediaSmsTemplateContentDo> contentDos = contents.stream()
                .map(vo -> {
                    FileInfo fileInfo = this.getFileInfo(vo);
                    return new MediaSmsTemplateContentDo()
                            .setMediaTemplateId(templateId)
                            .setName(vo.getName())
                            .setSize(fileInfo.getFileSize())
                            .setSortNum(contents.indexOf(vo))
                            .setMediaType(vo.getMediaType())
                            .setContent(vo.getContent())
                            .setFileType(fileInfo.getFileFormat());
                })
                .collect(Collectors.toList());
        long sum = contentDos.stream()
                .mapToLong(MediaSmsTemplateContentDo::getSize)
                .sum();
        if (sum > CONTENT_SIZE_LIMIT)
            throw new BizException(500, "模板内容最大允许" + CONTENT_SIZE_LIMIT + "字节,当前字节数:" + sum);
        this.contentService.saveBatch(contentDos);
        return contentDos;
    }

    private FileInfo getFileInfo(MediaSmsTemplateContentAddVo vo) {
        ContentMediaType mediaType = vo.getMediaType();
        String content = vo.getContent();
        switch (mediaType) {
            case TEXT: {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileSize((long) content.getBytes(StandardCharsets.UTF_8).length);
                fileInfo.setFileFormat("text");
                return fileInfo;
            }
            case IMAGE:
            case MOVIE:
            case SOUND: {
                FileInfoReq req = new FileInfoReq(content,null);
                FileInfo fileInfo = fileApi.getFileInfo(req);
                String type = fileInfo.getFileFormat();
                List<String> types = MEDIA_TYPES.get(mediaType);
                if (!types.contains(type.toUpperCase()))
                    throw new BizException(500, "允许的" + mediaType.getAlias() + "类型为" + Arrays.toString(types.toArray()) + ",请重新上传。");
                return fileInfo;
            }
            default:
                throw new BizException(500, "为止媒体类型");
        }
    }

    private String getSignatureById(Long signatureId) {
        List<CspVideoSmsSignatureResp> signatures = signatureService.getSignatureByIds(Collections.singletonList(signatureId));
        if (CollectionUtils.isNotEmpty(signatures))
            return signatures.get(0).getSignature();
        return null;
    }

    /**
     * 修改模板
     *
     * @param templateUpdateVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(MediaSmsTemplateUpdateVo templateUpdateVo) {
        Long id = templateUpdateVo.getId();

        List<MediaSmsTemplateAuditDo> list = auditService.lambdaQuery()
                .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, id)
                .list();
        if (CollectionUtils.isEmpty(list)) {
            throw new BizException("模板不存在");
        }
        String userId = SessionContextUtil.getLoginUser().getUserId();
        if (!list.stream().allMatch(s -> userId.equals(s.getCreator()))) {
            throw new BizException("只能操作自己的模板");
        }
        if (!list.stream().allMatch(s -> AuditStatus.WAIT_POST.equals(s.getStatus()))) {
            throw new BizException(500, "模板不是待送审，不能修改");
        }

        MediaSmsTemplateDo templateDo = new MediaSmsTemplateDo()
                .setId(id)
                .setTemplateName(templateUpdateVo.getTemplateName())
                .setTopic(templateUpdateVo.getTopic())
                .setAccountId(templateUpdateVo.getAccountId())
                .setTemplateType(templateUpdateVo.getTemplateType())
                .setSignatureId(templateUpdateVo.getSignatureId());
        this.updateById(templateDo);
        LambdaQueryWrapper<MediaSmsTemplateContentDo> removeContentQw = new LambdaQueryWrapper<MediaSmsTemplateContentDo>()
                .eq(MediaSmsTemplateContentDo::getMediaTemplateId, id);
        this.contentService.remove(removeContentQw);
        List<MediaSmsTemplateContentDo> contents = this.saveContent(templateUpdateVo.getContents(), id);
        //异步送审
        if (templateUpdateVo.getSubmitForAudit()) {
            String signature = this.getSignatureById(templateDo.getSignatureId());
//            MediaSmsTemplateService self = SpringUtil.getBean(MediaSmsTemplateService.class);
            this.reportTemplate(templateDo.getId(), templateDo.getAccountId(), templateUpdateVo.getTopic(), signature, contents, templateUpdateVo.getTemplateType());
        }
    }

    @Override
    public boolean isRepeatTemplateName(String accountId, String templateName) {
        return this.lambdaQuery()
                .eq(MediaSmsTemplateDo::getAccountId, accountId)
                .eq(MediaSmsTemplateDo::getTemplateName, templateName)
                .exists();
    }


    @Override
    public PageResult<MediaSmsTemplateSimpleVo> searchTemplate(String templateName, String accountId, OperatorPlatform operator, AuditStatus auditStatus, Integer templateType, Long pageNum, Long pageSize) {
        Page<MediaSmsTemplateSimpleVo> simpleVoPage = Page.of(pageNum, pageSize);
        simpleVoPage.setOrders(OrderItem.descs("create_time"));
        Page<MediaSmsTemplateSimpleVo> page = this.baseMapper.searchTemplate(SessionContextUtil.getUser().getUserId(), templateName, accountId, operator, auditStatus, templateType, simpleVoPage);
        for (MediaSmsTemplateSimpleVo vo : page.getRecords()) {
            List<MediaSmsTemplateSimpleVo.TemplateAuditVo> auditVos = auditService.lambdaQuery()
                    .select(MediaSmsTemplateAuditDo::getOperator, MediaSmsTemplateAuditDo::getStatus, MediaSmsTemplateAuditDo::getReason)
                    .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, vo.getId())
                    .list().stream()
                    .map(audit -> new MediaSmsTemplateSimpleVo.TemplateAuditVo(audit.getOperator(), audit.getStatus(), audit.getReason()))
                    .collect(Collectors.toList());
            vo.setAudits(auditVos);
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }


    /**
     * 慢查询-----------water
     * @param templateId 模板ID
     * @param inner
     * @return
     */
    @Override
    public MediaSmsTemplatePreviewVo getContents(Long templateId, Boolean inner) {
        MediaSmsTemplateDo templateDo = this.lambdaQuery()
                .eq(MediaSmsTemplateDo::getId, templateId)
                .select(MediaSmsTemplateDo::getId, MediaSmsTemplateDo::getSignatureId, MediaSmsTemplateDo::getSignature, MediaSmsTemplateDo::getPlatformTemplateId,
                        MediaSmsTemplateDo::getAccountId, MediaSmsTemplateDo::getTemplateName, MediaSmsTemplateDo::getTemplateType,
                        MediaSmsTemplateDo::getCustomerId)
                .one();
        if (templateDo == null)
            throw new BizException(500, "模板不存在");
        //如果不是内部调用 需要进行越权校验
        if (Boolean.FALSE.equals(inner)) {
            if (!SessionContextUtil.getUser().getUserId().equals(templateDo.getCustomerId())) {
                throw new BizException(500, "模板不是你的");
            }
        }
        MediaSmsTemplatePreviewVo previewVo = new MediaSmsTemplatePreviewVo();
        BeanUtils.copyProperties(templateDo, previewVo);
        //如果还没有送过审 则使用签名ID查询签名
//        if (!auditService.lambdaQuery()
//                .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, templateId)
//                .ne(MediaSmsTemplateAuditDo::getStatus, AuditStatus.WAIT_POST)
//                .exists()) {
//            previewVo.setSignature(signature);
//        }
        String signature = Strings.isNullOrEmpty(templateDo.getSignature()) ? this.getSignatureById(templateDo.getSignatureId()) : templateDo.getSignature();
        previewVo.setSignature(signature);
        previewVo.setSignatureDelete(StringUtils.isNotBlank(signature) ? 0 : 1);
        List<MediaSmsTemplateContentVo> contents = contentService.lambdaQuery()
                .eq(MediaSmsTemplateContentDo::getMediaTemplateId, templateId)
                .list().stream()
                .map(c -> {
                    MediaSmsTemplateContentVo vo = new MediaSmsTemplateContentVo();
                    BeanUtils.copyProperties(c, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        previewVo.setContents(contents);

        CspVideoSmsAccountQueryDetailReq detailReq = new CspVideoSmsAccountQueryDetailReq();
        detailReq.setAccountId(templateDo.getAccountId());
        CspVideoSmsAccountDetailResp accountDetail = accountService.queryDetail(detailReq);
        if (accountDetail == null || accountDetail.getStatus() == null || !accountDetail.getStatus().equals(1)) {
            previewVo.setAccountDelete(1);
        } else {
            previewVo.setAccountDelete(0);
        }
        previewVo.setTemplateId(templateId);
        return previewVo;
    }

    @Override
    public MediaSmsTemplateDetailVo getTemplateInfo(Long templateId) {
        MediaSmsTemplateDetailVo vo = new MediaSmsTemplateDetailVo();
        MediaSmsTemplateDo templateDo = this.getById(templateId);
        if (templateDo == null)
            throw new BizException(500, "模板不存在");
        List<MediaSmsTemplateContentVo> contents = getContents(templateId, false).getContents();
        BeanUtils.copyProperties(templateDo, vo);
        vo.setContents(contents);
        vo.setContentTotalSize(contents.stream().mapToLong(MediaSmsTemplateContentVo::getSize).sum());

        CspVideoSmsAccountQueryDetailReq detailReq = new CspVideoSmsAccountQueryDetailReq();
        detailReq.setAccountId(templateDo.getAccountId());
        CspVideoSmsAccountDetailResp accountDetail = accountService.queryDetail(detailReq);
        if (accountDetail == null || accountDetail.getStatus() == null || !accountDetail.getStatus().equals(1)) {
            vo.setAccountDelete(1);
        } else {
            vo.setAccountDelete(0);
        }

        String signature = this.getSignatureById(templateDo.getSignatureId());
        if (StringUtils.isNotBlank(signature)) {
            vo.setSignatureDelete(0);
        } else {
            vo.setSignatureDelete(1);
        }
        return vo;
    }

    @Override
    public MediaSmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(String platformTemplateId) {
        MediaSmsTemplateDo mediaSmsTemplateDo = this.lambdaQuery().eq(MediaSmsTemplateDo::getPlatformTemplateId, platformTemplateId).one();
        if (Objects.isNull(mediaSmsTemplateDo)) return null;

        MediaSmsTemplateDetailVo mediaSmsTemplateDetailVo = new MediaSmsTemplateDetailVo();
        BeanUtils.copyProperties(mediaSmsTemplateDo, mediaSmsTemplateDetailVo);
        //查询签名内容
        Long signatureId = mediaSmsTemplateDo.getSignatureId();
        if (Objects.nonNull(signatureId)) {
            List<CspVideoSmsSignatureResp> signature = signatureService.getSignatureByIds(Collections.singletonList(signatureId));
            if (CollectionUtils.isNotEmpty(signature)) {
                mediaSmsTemplateDetailVo.setSignatureContent(signature.get(0).getSignature());
            }
        }
        //查询审核状态（以前就是这个逻辑，只是代码简洁了）
        List<MediaSmsTemplateAuditDo> auditList = auditService.lambdaQuery().eq(MediaSmsTemplateAuditDo::getPlatformTemplateId, platformTemplateId).list();
        if (auditList.stream().anyMatch(s -> AuditStatus.PASS.equals(s.getStatus()))) {
            mediaSmsTemplateDetailVo.setAudit(AuditStatus.PASS.getValue());
        }
        return mediaSmsTemplateDetailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAuditStatus(MediaSmsTemplateAuditUpdateVo auditUpdateVo) {
        String platformTemplateId = auditUpdateVo.getPlatformTemplateId();
        if (!this.lambdaQuery().eq(MediaSmsTemplateDo::getPlatformTemplateId, platformTemplateId).exists())
            throw new BizException(500, "模板不存在");
        for (MediaSmsTemplateAuditUpdateVo.TemplateAudit audit : auditUpdateVo.getAudits()) {
            if (audit.getStatus() != null)
                auditService.lambdaUpdate()
                        .eq(MediaSmsTemplateAuditDo::getPlatformTemplateId, platformTemplateId)
                        .eq(MediaSmsTemplateAuditDo::getOperator, audit.getOperator())
                        .set(MediaSmsTemplateAuditDo::getStatus, audit.getStatus())
                        .set(audit.getReason() != null, MediaSmsTemplateAuditDo::getReason, audit.getReason())
                        .update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportTemplate(Long id) {
        MediaSmsTemplateDo template = this.getById(id);
        if (template == null) {
            throw new BizException(500, "模板不存在");
        }
        if (!SessionContextUtil.getLoginUser().getUserId().equals(template.getCustomerId())) {
            throw new BizException("只能送审自己的模板");
        }
        if (this.auditService.lambdaQuery()
                .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, id)
                .ne(MediaSmsTemplateAuditDo::getStatus, AuditStatus.WAIT_POST)
                .exists())
            throw new BizException(500, "请勿重复送审模板");
        List<MediaSmsTemplateContentDo> contents = this.contentService.lambdaQuery().eq(MediaSmsTemplateContentDo::getMediaTemplateId, id).list();
        String signature = this.getSignatureById(template.getSignatureId());
        this.reportTemplate(id, template.getAccountId(), template.getTopic(), signature, contents, template.getTemplateType());
    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void setPlatformTemplateId(Long id, String platformTemplateId) {
        this.lambdaUpdate()
                .eq(MediaSmsTemplateDo::getId, id)
                .set(MediaSmsTemplateDo::getPlatformTemplateId, platformTemplateId)
                .update();
        auditService.lambdaUpdate()
                .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, id)
                .set(MediaSmsTemplateAuditDo::getPlatformTemplateId, platformTemplateId)
                .update();
    }

    @Override
    @Transactional
    public void deleteTemplate(MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo) {
        for (Long templateId : mediaSmsTemplateCommonVo.getTemplateIds()) {
            deleteTemplate(templateId);
        }
    }

    private void deleteTemplate(Long templateId) {
        MediaSmsTemplateDo smsTemplateDo = getById(templateId);
        if (Objects.isNull(smsTemplateDo)) return;
        if (!SessionContextUtil.getUser().getUserId().equals(smsTemplateDo.getCustomerId())) {
            throw new BizException("不能删除别人的模板");
        }

        RobotGroupSendPlanIdReq req = new RobotGroupSendPlanIdReq();
        req.setId(templateId);
        req.setType(3);
        if (sendPlansApi.checkPlansUserTemplate(req)) {
            throw new BizException(500, "有计划正在使用此模板，无法删除");
        }
        this.removeById(templateId);
    }

    /**
     * @return 返回用户可用的模板列表(有任意一家运营商通过审核并没有任何一家还在审核中)以及其审核信息
     */
    @Override
    public List<MediaSmsTemplateSimpleVo> findEffectiveTemplate(List<String> accountIds, String
            templateName, Integer templateType) {
        return this.lambdaQuery()
                .eq(MediaSmsTemplateDo::getCustomerId, SessionContextUtil.getUser().getUserId())
                .eq(templateType != null, MediaSmsTemplateDo::getTemplateType, templateType)
                .in(CollectionUtils.isNotEmpty(accountIds), MediaSmsTemplateDo::getAccountId, accountIds)
                .like(StringUtils.isNotBlank(templateName), MediaSmsTemplateDo::getTemplateName, templateName)
                .select(MediaSmsTemplateDo::getId, MediaSmsTemplateDo::getTemplateName, MediaSmsTemplateDo::getCreateTime)
                .orderByDesc(MediaSmsTemplateDo::getCreateTime)
                .list().stream()
                .map(tmp -> {
                    MediaSmsTemplateSimpleVo simpleVo = new MediaSmsTemplateSimpleVo();
                    BeanUtils.copyProperties(tmp, simpleVo);
                    simpleVo.setAudits(
                            auditService.lambdaQuery()
                                    .eq(MediaSmsTemplateAuditDo::getMediaTemplateId, tmp.getId())
                                    .list().stream()
                                    .map(audit -> new MediaSmsTemplateSimpleVo.TemplateAuditVo(audit.getOperator(), audit.getStatus(), null))
                                    .collect(Collectors.toList())
                    );
                    return simpleVo;
                })
//                .filter(simpleVo -> simpleVo.getAudits().stream().allMatch(auditVo -> auditVo.getStatus() != AuditStatus.AUDITING))
                .filter(simpleVo -> simpleVo.getAudits().stream().anyMatch(auditVo -> auditVo.getStatus() == AuditStatus.PASS))
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaSmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId(String userId) {
        return this.baseMapper.getHaveTemplateAccountsByUserId(userId);
    }

    @Override
    public List<String> getTemplateAccountIds(List<Long> templateIds) {
        return this.lambdaQuery()
                .in(MediaSmsTemplateDo::getId, templateIds)
                .select(MediaSmsTemplateDo::getAccountId)
                .list()
                .stream().map(MediaSmsTemplateDo::getAccountId)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaSmsTemplateCheckVo> templateDeleteCheck(MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo) {
        List<MediaSmsTemplateCheckVo> mediaSmsTemplateCheckVoList = new ArrayList<>();
        for (Long templateId : mediaSmsTemplateCommonVo.getTemplateIds()) {
            RobotGroupSendPlanIdReq req = new RobotGroupSendPlanIdReq();
            req.setId(templateId);
            req.setType(3);
            MediaSmsTemplateCheckVo mediaSmsTemplateCheckVo = new MediaSmsTemplateCheckVo();
            mediaSmsTemplateCheckVo.setId(templateId);
            if (sendPlansApi.checkPlansUserTemplate(req)) {
                mediaSmsTemplateCheckVo.setResult(1);
            } else {
                mediaSmsTemplateCheckVo.setResult(0);
            }
            mediaSmsTemplateCheckVoList.add(mediaSmsTemplateCheckVo);
        }
        return mediaSmsTemplateCheckVoList;
    }
}
