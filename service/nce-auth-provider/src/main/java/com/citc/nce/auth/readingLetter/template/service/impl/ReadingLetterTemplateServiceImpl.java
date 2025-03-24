package com.citc.nce.auth.readingLetter.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.csp.readingLetter.service.CspReadingLetterAccountService;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterDetailResp;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountListVo;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountSearchReq;
import com.citc.nce.auth.csp.smsTemplate.service.SmsTemplateService;
import com.citc.nce.auth.readingLetter.shortUrl.service.ReadingLetterShortUrlService;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateDao;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateAuditDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateAuditService;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateService;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterAuditAccountReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterDeleteReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateAddReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateAuditReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateDetailVo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameRepeatReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateOfFifthReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSearchReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSimpleVo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateUpdateReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.api.RobotGroupSendPlanDescApi;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.req.RobotGroupSendPlanDescContainShortUrlReq;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.STATUS_24_SHELVE_AND_IS_AUDITING;
import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.STATUS_25_SHELVE_AND_AUDIT_NOT_PASS;
import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.STATUS_30_ONLINE;
import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.STATUS_50_DEBUG;
import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.STATUS_68_ONLINE;
import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.STATUS_70_TEST;

/**
 * @author zjy
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ReadingLetterTemplateServiceImpl extends ServiceImpl<ReadingLetterTemplateDao, ReadingLetterTemplateDo> implements ReadingLetterTemplateService {

    @Resource
    private ReadingLetterTemplateAuditService auditService;

    @Resource
    private ReadingLetterTemplateDao templateDao;

    @Resource
    private ReadingLetterShortUrlService readingLetterShortUrlService;

    @Resource
    private CspReadingLetterAccountService cspReadingLetterAccountService;

    @Resource
    AccountManagementService accountManagementService;
    @Resource
    RobotGroupSendPlansApi robotGroupSendPlansApi;

    @Resource
    RobotGroupSendPlanDescApi robotGroupSendPlanDescApi;
    @Resource
    SmsTemplateService smsTemplateService;

    /**
     * 新增模板
     *
     * @param readingLetterTemplateAddReq
     */
    @Override
    public Long addTemplate(ReadingLetterTemplateAddReq readingLetterTemplateAddReq) {
        String userId = SessionContextUtil.getUser().getUserId();

        //查询该用户是否已经存在同名模板
        ReadingLetterTemplateDo existTemplate = getOne(new LambdaQueryWrapper<ReadingLetterTemplateDo>()
                .eq(ReadingLetterTemplateDo::getTemplateName, readingLetterTemplateAddReq.getTemplateName())
                .eq(ReadingLetterTemplateDo::getCustomerId, userId));
        //如果存在,则抛出异常
        if (Objects.nonNull(existTemplate)) {
            throw new BizException("模板名不能重复,请更改模板名");
        }

        ReadingLetterTemplateDo readingLetterTemplateDo = new ReadingLetterTemplateDo();
        BeanUtil.copyProperties(readingLetterTemplateAddReq, readingLetterTemplateDo);
        readingLetterTemplateDo.setCreator(userId);
        readingLetterTemplateDo.setCustomerId(userId);
        templateDao.insert(readingLetterTemplateDo);

        //如果需要送审
        if (readingLetterTemplateAddReq.getNeedAudit()) {
            // (阅信+账号)
            List<CspReadingLetterDetailResp> readingLetterAccounts = cspReadingLetterAccountService.queryAllAccountsOfCustomer(1);
            //查找蜂动账号(工作状态)
            List<AccountManagementResp> accountManagementlist = accountManagementService.getAccountManagementlist(userId);
            List<AccountManagementResp> fontdo = accountManagementlist.stream()
                    .filter(account -> CSPChatbotSupplierTagEnum.FONTDO.getValue().equals(account.getSupplierTag()))
                    .filter(this::isAvailableChanel)
                    .collect(Collectors.toList());
            auditService.audit(readingLetterTemplateDo, readingLetterAccounts, fontdo);
        }
        return readingLetterTemplateDo.getId();
    }

    /**
     * 修改模板
     *
     * @param readingLetterTemplateUpdateReq
     */
    @Override
    public void updateTemplate(ReadingLetterTemplateUpdateReq readingLetterTemplateUpdateReq) {

        //提交过的模板不能再更改
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> auditQueryWrapper = new LambdaQueryWrapper<>();
        auditQueryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateUpdateReq.getId())
                .eq(ReadingLetterTemplateAuditDo::getDeleted, 0);
        if (CollectionUtil.isNotEmpty(auditService.list(auditQueryWrapper))) {
            throw new BizException("提交过的模板不能再更改");
        }

//            模板名修改
        ReadingLetterTemplateDo existTemplate = getById(readingLetterTemplateUpdateReq.getId());
        String templateName = existTemplate.getTemplateName();
        if (!templateName.equals(readingLetterTemplateUpdateReq.getTemplateName())) {
            if (Objects.nonNull(getOne(new LambdaQueryWrapper<ReadingLetterTemplateDo>()
                    .eq(ReadingLetterTemplateDo::getTemplateName, readingLetterTemplateUpdateReq.getTemplateName())
                    .eq(ReadingLetterTemplateDo::getCustomerId, existTemplate.getCustomerId())))) {
                throw new BizException("模板名不能重复,请更改模板名");
            }
        }

        BeanUtil.copyProperties(readingLetterTemplateUpdateReq, existTemplate);
        templateDao.updateById(existTemplate);

        //更新不送审，模板变为未送审状态
        if (!readingLetterTemplateUpdateReq.getNeedAudit()) {
            auditService.deleteAuditAndProved(readingLetterTemplateUpdateReq.getId());
            return;
        }
        //判断是否需要重新送审
        // (阅信+账号)
        List<CspReadingLetterDetailResp> readingLetterAccounts = cspReadingLetterAccountService.queryAllAccountsOfCustomer(1);
        //查找蜂动账号(工作状态)
        String userId = SessionContextUtil.getUser().getUserId();
        List<AccountManagementResp> fontdoAccount = accountManagementService.getAccountManagementlist(userId).stream()
                .filter(account -> CSPChatbotSupplierTagEnum.FONTDO.getValue().equals(account.getSupplierTag()))
                .filter(this::isAvailableChanel)
                .collect(Collectors.toList());
        //按需重新送审
        auditService.reAudit(existTemplate, readingLetterAccounts, fontdoAccount);
    }

    /**
     * 搜索模板列表
     *
     * @param req
     */
    @Override
    public PageResult<ReadingLetterTemplateSimpleVo> list(ReadingLetterTemplateSearchReq req) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();

        Page<ReadingLetterTemplateSimpleVo> page = new Page<>(req.getPageNo(), req.getPageSize());

        page.setOrders(OrderItem.descs("create_time"));
        log.info("开始查询:{}", req);
        getBaseMapper().selectReadingLetterTemplates(customerId,
                req.getTemplateName(),
                req.getOperatorCode(),
                req.getSmsType(),
                req.getStatus(),
                page);
        log.info("查询结果size:{}", page.getRecords().size());
        List<ReadingLetterTemplateSimpleVo> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageResult<>(records, page.getTotal());
        }

        //查到所有该customer的阅信+和5G阅信账号
        CustomerReadingLetterAccountSearchReq searchVo = new CustomerReadingLetterAccountSearchReq();
        List<CustomerReadingLetterAccountListVo> customerReadingLetterAccountListVos = cspReadingLetterAccountService.queryCustomerReadingLetterAccountList(searchVo);
        log.info("阅信+ 和 5G阅信账号:{}", customerReadingLetterAccountListVos);
        //补充各审核记录

        List<Long> templateIds = records.stream().map(ReadingLetterTemplateSimpleVo::getId).collect(Collectors.toList());
        //将审核的详细信息查询并设置进主体对象
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ReadingLetterTemplateAuditDo::getTemplateId, templateIds)
                .eq(ReadingLetterTemplateAuditDo::getDeleted, 0);
        List<ReadingLetterTemplateAuditDo> auditDoList = auditService.list(queryWrapper);
        log.info("查询审核记录 auditDoList.size:{}", auditDoList.size());
        for (ReadingLetterTemplateSimpleVo record : records) {
            List<ReadingLetterTemplateDetailVo> templateDetailList = auditDoList.stream()
                    .filter(audit -> audit.getTemplateId().equals(record.getId()))
                    .map(audit -> BeanUtil.copyProperties(audit, ReadingLetterTemplateDetailVo.class))
                    .collect(Collectors.toList());
            record.setTemplateDetailList(templateDetailList);
            record.setCanEdit(CollectionUtil.isEmpty(templateDetailList));
        }
        log.info("设置完成已经存在的审核记录");

        //给不存在审核记录的template设置一下审核记录(未送审)
        for (CustomerReadingLetterAccountListVo customerReadingLetterAccountListVo : customerReadingLetterAccountListVos) {
            for (ReadingLetterTemplateSimpleVo record : page.getRecords()) {
                boolean needCreateAudit = true;
                //如果在该模板的所有的审核账号中存在该账号的审核记录, 则不需要创建
                List<ReadingLetterTemplateDetailVo> templateDetailList = record.getTemplateDetailList();
                for (ReadingLetterTemplateDetailVo templateAuditVo : templateDetailList) {
                    if (templateAuditVo.getAuditAccount().equals(customerReadingLetterAccountListVo.getAccountId())) {
                        needCreateAudit = false;
                        break;
                    }
                }
                if (needCreateAudit) {
                    ReadingLetterTemplateDetailVo templateAuditAdd = new ReadingLetterTemplateDetailVo();
                    templateAuditAdd.setAuditAccount(customerReadingLetterAccountListVo.getAccountId());
                    //未送审
                    templateAuditAdd.setStatus(-1);
                    templateAuditAdd.setSmsType(customerReadingLetterAccountListVo.getSmsType());
                    templateAuditAdd.setOperatorCode(customerReadingLetterAccountListVo.getOperator());
                    templateDetailList.add(templateAuditAdd);
                }
            }
        }
        log.info("给不存在审核记录的模板设置审核记录(未审核)");
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<ReadingLetterTemplateSimpleVo> getApprovedTemplateOfFifth(ReadingLetterTemplateOfFifthReq req) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        String templateName = req.getTemplateName();

        //账号或者群发计划id 不能同时为空/null
        Long groupSendId = req.getGroupSendId();
        //其实是chatbotAccountIds
        String chatbotAccounts = req.getChatbotAccounts();
        if (Objects.isNull(groupSendId) && StrUtil.isBlank(chatbotAccounts)) {
            throw new BizException("参数错误,群发计划和chatbotAccounts账号不能同时缺失");
        }
        if (Objects.nonNull(groupSendId) && StrUtil.isNotBlank(chatbotAccounts)) {
            throw new BizException("参数错误,群发计划和chatbotAccounts账号不能同时存在");
        }

        //汇总所有需要查找的chatbotAccount
        List<String> accounts = CollectionUtil.newArrayList();

        if (Objects.nonNull(groupSendId)) {
            RobotGroupSendPlansReq robotGroupSendPlansReq = new RobotGroupSendPlansReq();
            robotGroupSendPlansReq.setId(groupSendId);
            //chatbotAccount ,还需要找到对应的chatbotAccountId
            String chatbotAccountStr = robotGroupSendPlansApi.selectPlanChatbotAccount(robotGroupSendPlansReq);
            List<String> chatbotAccountIds = accountManagementService.getListByChatbotAccountList(StrUtil.split(chatbotAccountStr, ","))
                    .stream()
                    //只判断其中蜂动账号就行
                    .filter(account -> CSPChatbotSupplierTagEnum.FONTDO.getValue().equalsIgnoreCase(account.getSupplierTag()))
                    .map(AccountManagementResp::getChatbotAccountId)
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(chatbotAccountIds)) {
                throw new BizException("参数错误,群发计划没有对应的chatbot账号");
            }
            //把字符串分隔后变成集合
            accounts.addAll(chatbotAccountIds);
        }
        if (StrUtil.isNotBlank(chatbotAccounts)) {
            //只需要其中蜂动账号就行
            List<String> chatbotAccountIds = accountManagementService.getListByChatbotAccountIdList(StrUtil.split(chatbotAccounts, ","))
                    .stream()
                    //只判断其中蜂动账号就行
                    .filter(account -> CSPChatbotSupplierTagEnum.FONTDO.getValue().equalsIgnoreCase(account.getSupplierTag()))
                    .map(AccountManagementResp::getChatbotAccountId)
                    .collect(Collectors.toList());

            accounts.addAll(chatbotAccountIds);
        }
        //未选择蜂动账号时, 直接返回空集合
        if (CollectionUtil.isEmpty(accounts)) {
            return ListUtil.empty();
        }
        //获取所有的审核通过的模板
        List<ReadingLetterTemplateSimpleVo> approvedTemplateOfFifth = getBaseMapper().getApprovedTemplateOfFifth(customerId, templateName, accounts);
        //判断非空
        if (CollectionUtil.isEmpty(approvedTemplateOfFifth)) {
            return ListUtil.empty();
        }
        //过滤,例如当前群发计划有两个账号支持5G阅信，则需要该模板在两个账号通道下都审核通过，否则不显示
        return filterUnAuditResult(accounts, approvedTemplateOfFifth);
    }
    public ReadingLetterTemplateSimpleVo getOneApprovedTemplate(Long id,Integer smsType){
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        ReadingLetterTemplateSimpleVo approvedTemplateOfFifth = getBaseMapper().getOneApprovedTemplate(customerId, id,smsType);
        return approvedTemplateOfFifth;

    }
    // 仅显示审核通过情况与当前群发计划中支持5G阅信账号一致的消息模板, 例如当前群发计划有两个账号支持5G阅信，则需要该模板在两个账号通道下都审核通过，否则不显示
    private List<ReadingLetterTemplateSimpleVo> filterUnAuditResult(List<String> chatbotAccountList, List<ReadingLetterTemplateSimpleVo> approvedTemplateOfFifth) {
        Collections.sort(chatbotAccountList);
        List<ReadingLetterTemplateSimpleVo> filterList = new ArrayList<>();
        //删除不是所有chatbot都通过审核的模板
        for (int i = 0; i < approvedTemplateOfFifth.size(); i++) {
            ReadingLetterTemplateSimpleVo readingLetterTemplateSimpleVo = approvedTemplateOfFifth.get(i);
            List<String> auditAccounts = readingLetterTemplateSimpleVo.getTemplateDetailList().stream().map(ReadingLetterTemplateDetailVo::getAuditAccount).collect(Collectors.toList());
            Collections.sort(auditAccounts);
            if (chatbotAccountList.equals(auditAccounts)) {
                filterList.add(readingLetterTemplateSimpleVo);
            }
        }
        return filterList;
    }

    @Override
    public ReadingLetterTemplateVo getTemplateInfo(Long templateId) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        LambdaQueryWrapper<ReadingLetterTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterTemplateDo::getId, templateId);
        queryWrapper.eq(ReadingLetterTemplateDo::getCustomerId, customerId);
        ReadingLetterTemplateDo readingLetterTemplateDo = templateDao.selectOne(queryWrapper);

        ReadingLetterTemplateVo readingLetterTemplateVo = new ReadingLetterTemplateVo();
        BeanUtil.copyProperties(readingLetterTemplateDo, readingLetterTemplateVo);

        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> auditQueryWrapper = new LambdaQueryWrapper<>();
        auditQueryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, templateId)
                .eq(ReadingLetterTemplateAuditDo::getDeleted, 0);
        readingLetterTemplateVo.setCanEdit(CollectionUtil.isEmpty(auditService.list(auditQueryWrapper)));
        return readingLetterTemplateVo;
    }

    @Override
    public ReadingLetterTemplateVo getTemplateInfoWithoutLogin(Long templateId) {
        LambdaQueryWrapper<ReadingLetterTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterTemplateDo::getId, templateId);
        ReadingLetterTemplateDo readingLetterTemplateDo = templateDao.selectOne(queryWrapper);

        ReadingLetterTemplateVo readingLetterTemplateVo = new ReadingLetterTemplateVo();
        BeanUtil.copyProperties(readingLetterTemplateDo, readingLetterTemplateVo);

        return readingLetterTemplateVo;
    }

    @Override
    public void deleteTemplate(ReadingLetterDeleteReq readingLetterDeleteReq) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        LambdaQueryWrapper<ReadingLetterTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterTemplateDo::getId, readingLetterDeleteReq.getId());
        queryWrapper.eq(ReadingLetterTemplateDo::getCustomerId, customerId);
        templateDao.delete(queryWrapper);
        log.info("删除模板成功");

        auditService.deleteAuditAndProved(readingLetterDeleteReq.getId());
    }

    @Override
    public boolean deleteWarn(ReadingLetterDeleteReq req) {
        Long id = req.getId();
        //根据阅信模板id找到对应的阅信+短链url
        List<ReadingLetterShortUrlVo> shortUrlByTemplateId = readingLetterShortUrlService.findShortUrlByTemplateId(id);
        if (CollectionUtil.isNotEmpty(shortUrlByTemplateId)) {
            List<String> shortUrls = shortUrlByTemplateId.stream().map(ReadingLetterShortUrlVo::getShortUrl).collect(Collectors.toList());
            //短链被哪些短信模板所使用
            List<Long> templateIds = smsTemplateService.existShortUrl(shortUrls);
            List<String> templateIdsString = templateIds.stream().map(Object::toString).collect(Collectors.toList());
            RobotGroupSendPlanDescContainShortUrlReq createShortUrlReq = new RobotGroupSendPlanDescContainShortUrlReq(templateIdsString);
            //判断未执行或执行中的群发计划是否使用了这些短信模板
            if (robotGroupSendPlanDescApi.containList(createShortUrlReq)) {
                return true;
            }
        }
        //查询未执行或执行中的群发计划是否使用了5G阅信
        if (robotGroupSendPlanDescApi.containOne(id.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public void auditTemplate(ReadingLetterTemplateAuditReq readingLetterTemplateAuditReq) {
        //找到该阅信模板
        ReadingLetterTemplateDo readingLetterTemplateDo = templateDao.selectById(readingLetterTemplateAuditReq.getId());

        //正确的阅信账号
        List<String> readingLetterPlusAccountIds = readingLetterTemplateAuditReq.getReadingLetterAccounts().stream()
                .filter(account -> account.getSmsType() == 2)
                .map(ReadingLetterAuditAccountReq::getAuditAccountId)
                .collect(Collectors.toList());

        // (customer 所属 阅信+账号)
        List<CspReadingLetterDetailResp> readingLetterPlusAccounts = cspReadingLetterAccountService.queryAllAccountsOfCustomer(1).stream()
                .filter(account -> readingLetterPlusAccountIds.contains(account.getAccountId()))
                .collect(Collectors.toList());
//----------------------------
        // (customer 所属 5G阅信账号ID)
        List<String> fifthReadingLetterAccountIds = readingLetterTemplateAuditReq.getReadingLetterAccounts().stream()
                .filter(account -> account.getSmsType() == 1)
                .map(ReadingLetterAuditAccountReq::getAuditAccountId)
                .collect(Collectors.toList());

        String userId = SessionContextUtil.getUser().getUserId();
        List<AccountManagementResp> fifthReadingLetterAccounts = accountManagementService.getAccountManagementlist(userId).stream()
                .filter(account -> CSPChatbotSupplierTagEnum.FONTDO.getValue().equals(account.getSupplierTag()))
                .filter(account -> fifthReadingLetterAccountIds.contains(account.getChatbotAccountId()))
                .filter(this::isAvailableChanel)
                .collect(Collectors.toList());

        auditService.audit(readingLetterTemplateDo, readingLetterPlusAccounts, fifthReadingLetterAccounts);
    }

    /**
     * 返回机器人通道是否可用，可用返回true.
     * 1. 本地添加机器人通道始终可用
     * 2. 线上流程添加的机器人根据运营商类型按如下规则判断
     * - 移动：当状态为上架审核中、上架审核不通过、在线、调试时，表示运营商通道可用
     * - 电信、联通：上线、测试，表示可用
     *
     * @param chatbot
     * @return
     */
    private boolean isAvailableChanel(AccountManagementResp chatbot) {
        Assert.notNull(chatbot, "需要验证通道可用性的机器人不能为空");

        CSPOperatorCodeEnum operatorCodeEnum = CSPOperatorCodeEnum.byName(chatbot.getAccountType());
        CSPChatbotStatusEnum cspChatbotStatusEnum = CSPChatbotStatusEnum.byCode((chatbot.getChatbotStatus())
        );
        if (operatorCodeEnum == null || cspChatbotStatusEnum == null)
            return false;
        switch (operatorCodeEnum) {
            case CMCC:
                return cspChatbotStatusEnum == STATUS_24_SHELVE_AND_IS_AUDITING
                        || cspChatbotStatusEnum == STATUS_25_SHELVE_AND_AUDIT_NOT_PASS
                        || cspChatbotStatusEnum == STATUS_30_ONLINE
                        || cspChatbotStatusEnum == STATUS_50_DEBUG;
            case CUNC:
            case CT:
            case DEFAULT:
                return cspChatbotStatusEnum == STATUS_68_ONLINE
                        || cspChatbotStatusEnum == STATUS_70_TEST;
            default:
                return false;
        }
    }

    @Override
    public boolean checkForRepeat(ReadingLetterTemplateNameRepeatReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        //查询该用户是否已经存在同名模板
        log.info("查询是否存在同名模板:{}", req);
        ReadingLetterTemplateDo existTemplate = getOne(new LambdaQueryWrapper<ReadingLetterTemplateDo>()
                .eq(ReadingLetterTemplateDo::getTemplateName, req.getTemplateName())
                .ne(Objects.nonNull(req.getTemplateId()), ReadingLetterTemplateDo::getId, req.getTemplateId())
                .eq(ReadingLetterTemplateDo::getCustomerId, userId));
        //如果存在,则抛出异常
        return Objects.isNull(existTemplate);
    }
}
