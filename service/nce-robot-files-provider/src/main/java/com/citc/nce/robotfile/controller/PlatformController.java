package com.citc.nce.robotfile.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountChatbotAccountQueryReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.csp.menu.MenuApi;
import com.citc.nce.auth.csp.menu.vo.ChatbotIdReq;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dto.*;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import com.citc.nce.robotfile.configure.RocketMQConfigure;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.robotfile.mapper.ExamineResultMapper;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.robotfile.service.impl.PlatformService;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.ExamResp;
import com.citc.nce.vo.StatisticsResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(RocketMQConfigure.class)
public class PlatformController implements PlatformApi {

    @Resource
    IExamineResultService examineResultService;

    @Resource
    ExamineResultMapper examineResultMapper;

    @Resource
    AccountManagementApi accountManagementApi;

    @Resource
    PlatformService platformService;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    private MenuApi menuApi;

    private final RocketMQConfigure rocketMQConfigure;

    @PostMapping("/{chatbotId}/delivery/mediaStatus")
    @Override
    public void mediaStatus(@RequestBody FileAccept accept, @PathVariable("chatbotId") String chatbotId) {
        log.info("The received request body is:{}   app id is {}", accept, chatbotId);
        try {
            if (StringUtils.equals(accept.getType(), "menu")) {
                ChatbotIdReq chatbotIdReq = new ChatbotIdReq();
                chatbotId = StringUtils.substringAfter(chatbotId, ":");
                chatbotId = StringUtils.substringBefore(chatbotId, "@");
                chatbotIdReq.setChatbotId(chatbotId);
                chatbotIdReq.setUseable(accept.getUseable());
                menuApi.updateStatus(chatbotIdReq);
                return;
            }
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            throw new BizException(821002007, "更新菜单失败");
        }
        String requestStr = JSONObject.toJSONString(accept);
        Message<String> message = MessageBuilder.withPayload(requestStr).build();
        //同步发送该消息，获取发送结果
        SendResult result = rocketMQTemplate.syncSend(rocketMQConfigure.getTopic(), message);
        log.info("发送到mq 结果为 ： {}", result);
    }

    @PostMapping("/delivery/mediaStatus")
    @Override
    public void mediaStatus(@RequestBody FileAccept accept) {
        log.info("供应商审核素材TID为:{}   appId is {}", accept.getFileId(), accept.getAppId());
        String requestStr = JSONObject.toJSONString(accept);
        Message<String> message = MessageBuilder.withPayload(requestStr).build();
        //同步发送该消息，获取发送结果
        SendResult result = rocketMQTemplate.syncSend(rocketMQConfigure.getTopic(), message);
        log.info("发送到mq 结果为 ： {}", result);
    }

    @PostMapping("/material/account/list")
    @Override
    public List<AccountResp> getAccountList() {
        String userId = SessionContextUtil.getUser().getUserId();
        List<AccountManagementResp> accounts = accountManagementApi.getAccountManagementlist(userId);
        List<AccountResp> accountResps = new ArrayList<>();
        accounts.forEach(accountManagement -> {
            AccountResp accountResp = new AccountResp();
            accountResp.setChatbotAccount(accountManagement.getChatbotAccount());
            accountResp.setAccountId(accountManagement.getChatbotAccountId());
            accountResp.setChatbotName(accountManagement.getAccountName());
            accountResp.setOperator(accountManagement.getAccountType());
            accountResp.setSupplierTag(accountManagement.getSupplierTag());
            accountResp.setChatbotStatus(accountManagement.getChatbotStatus());
            accountResps.add(accountResp);
        });
        return accountResps;
    }

    @PostMapping("/material/used")
    @Override
    public Boolean fileUsed(@RequestBody FileReq fileReq) {
        return platformService.fileUsed(fileReq);
    }

    @PostMapping("/material/usedByAll")
    public Boolean fileUsedAllPossible(@RequestBody FileReq fileReq) {
        return platformService.fileUsedAllPossible(fileReq);
    }

    @Override
    public FileAccept getFileTid(@RequestBody FileTidReq fileTidReq) {
        QueryWrapper<ExamineResultDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operator", fileTidReq.getOperator());
        queryWrapper.eq(Objects.nonNull(fileTidReq.getSupplierTag()), "supplier_tag", fileTidReq.getSupplierTag());
        queryWrapper.eq("file_uuid", fileTidReq.getFileUrlId());
        ExamineResultDo examineResultDo = examineResultMapper.selectOne(queryWrapper);
        FileAccept fileAccept = new FileAccept();
        if (ObjectUtil.isNotEmpty(examineResultDo)) {
            fileAccept.setFileId(examineResultDo.getFileId());
            fileAccept.setUseable(examineResultDo.getFileStatus());
            fileAccept.setThumbnailTid(examineResultDo.getThumbnailTid());
            if (ObjectUtil.isNotEmpty(examineResultDo.getValidity()) && examineResultDo.getValidity().compareTo(new Date()) > 0
                    && examineResultDo.getFileStatus() != 4) {
                //素材没过期
                fileAccept.setType("NotExpire");
            } else {
                //素材过期
                fileAccept.setType("Expire");
            }
        }
        return fileAccept;
    }

    @Override
    public void examine(FileTidReq fileTidReq) {
        UpdateWrapper<ExamineResultDo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("operator", fileTidReq.getOperator());
        updateWrapper.eq("file_uuid", fileTidReq.getFileUrlId());
        updateWrapper.set("file_status", 1);
        examineResultService.update(updateWrapper);
        //查找出uuid对应的所有账户，如果不在
    }

    @Override
    public void deleteAuditRecord(FileExamineDeleteReq fileExamineDeleteReq) {
        examineResultService.deleteAuditRecord(fileExamineDeleteReq.getChatbotAccount());
    }

    @Transactional
    @Override
    public void saveExam(UpFileReq upFileReq) {
        QueryWrapper<ExamineResultDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_uuid", upFileReq.getFileUrlId());
        queryWrapper.eq("operator", upFileReq.getOperator());
        ExamineResultDo resultDo = examineResultMapper.selectOne(queryWrapper);
        if (Objects.isNull(resultDo)) {
            resultDo = new ExamineResultDo();
            resultDo.setFileUuid(upFileReq.getFileUrlId())
                    .setChatbotAccountId(upFileReq.getChatbotAccountId())
                    .setFileId(upFileReq.getFileId())
                    .setAppId(upFileReq.getAppId())
                    .setOperator(upFileReq.getOperator())
                    .setChatbotName(upFileReq.getChatbotName())
                    .setChatbotId(upFileReq.getChatBotId())
                    .setSupplierTag(upFileReq.getSupplierTag())
                    .setFileStatus(upFileReq.getFileStatus())
                    .setCreator(upFileReq.getCreator());
            if (StringUtils.isNotEmpty(upFileReq.getThumbnailTid())) {
                resultDo.setThumbnailTid(upFileReq.getThumbnailTid());
            }
            log.info("saveExam creat record {}", upFileReq);
        } else {
            resultDo.setFileStatus(upFileReq.getFileStatus());
            resultDo.setValidity(null);
            resultDo.setFileId(upFileReq.getFileId());
            if (StringUtils.isNotEmpty(upFileReq.getThumbnailTid())) {
                resultDo.setThumbnailTid(upFileReq.getThumbnailTid());
            }
            log.info("saveExam user old record {}", upFileReq);
        }
        examineResultService.saveOrUpdate(resultDo);
    }

    @Override
    public Boolean verification(VerificationReq verificationReq) {
        //List<String> accounts = Arrays.asList(verificationReq.getAccounts().split(","));
        LambdaQueryWrapper<ExamineResultDo> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.in(ExamineResultDo::getChatbotId,accounts);
        if (StrUtil.isEmpty(verificationReq.getOperators())) {
            throw new BizException("运营商为空");
        }
        List<String> operators = Arrays.asList(verificationReq.getOperators().split(","));
        queryWrapper.in(ExamineResultDo::getOperator, operators);
        List<String> fileIds = verificationReq.getFileIds().stream().filter(opt -> StrUtil.isNotEmpty(opt) && !"null".equals(opt.toLowerCase()) && !"undefined".equals(opt.toLowerCase())).distinct().collect(Collectors.toList());
        if (ObjectUtil.isEmpty(fileIds))
            return true;
        queryWrapper.in(ExamineResultDo::getFileUuid, fileIds);
        if (ObjectUtil.isNotEmpty(verificationReq.getDays())) {
            //兼容蜂动素材无有效期限制
            queryWrapper.and(wrapper -> wrapper.isNull(ExamineResultDo::getValidity).or().ge(ExamineResultDo::getValidity, DateUtil.offsetDay(new Date(), verificationReq.getDays())));
        }
        queryWrapper.eq(ExamineResultDo::getFileStatus, 2);
        //判断是不是每个file在所需要的Chatbot上素材都是审核完毕的
        return examineResultMapper.selectCount(queryWrapper) == (long) operators.size() * fileIds.size();
    }

    /**
     * 保存模板时检查是否所有素材有共有的运营商服务商交集
     *
     * @param verificationReq
     * @return
     */
    @Override
    public List<TemplateOwnershipReflect> verificationShare(VerificationReq verificationReq) {
        log.info("verificationShare,{}", JSONObject.toJSONString(verificationReq));
        QueryWrapper<ExamineResultDo> queryWrapper = new QueryWrapper<>();

        queryWrapper.select("count(1) as fileCount", "operator", "supplier_tag", "creator");
        LambdaQueryWrapper<ExamineResultDo> lambdaQueryWrapper = queryWrapper.lambda();
        lambdaQueryWrapper.eq(ExamineResultDo::getDeleted, 0);
        //不通过chatbotAccount判断素材是否过期，系统逻辑是 同运营商下所有素材过期状态可以公用
        int operatorSize = 0;
        if (ObjectUtil.isNotEmpty(verificationReq.getAccounts())) {
            String[] accountArr = verificationReq.getAccounts().split(",");
            operatorSize = accountArr.length;
            AccountChatbotAccountQueryReq accountChatbotAccountQueryReq = new AccountChatbotAccountQueryReq();
            accountChatbotAccountQueryReq.setChatbotAccountList(Arrays.asList(accountArr));
            accountChatbotAccountQueryReq.setCreator(verificationReq.getCreator());
            List<AccountManagementResp> accountManagementResps = accountManagementApi.getListByChatbotAccounts(accountChatbotAccountQueryReq);
            if (ObjectUtil.isEmpty(accountManagementResps) || accountManagementResps.size() != accountArr.length) {
                throw new BizException("chatbot状态异常");
            }
            accountManagementResps = accountManagementResps.stream().filter(chatbot -> CSPChatbotStatusEnum.getUseableStatus().contains(chatbot.getChatbotStatus())).collect(Collectors.toList());
            if (ObjectUtil.isEmpty(accountManagementResps) || accountManagementResps.size() != accountArr.length) {
                throw new BizException("chatbot状态异常");
            }
            lambdaQueryWrapper.in(ExamineResultDo::getOperator, accountManagementResps.stream().map(AccountManagementResp::getAccountTypeCode).distinct().collect(Collectors.toList()));
            lambdaQueryWrapper.in(ExamineResultDo::getSupplierTag, accountManagementResps.stream().map(AccountManagementResp::getSupplierTag).distinct().collect(Collectors.toList()));
        }
        if (ObjectUtil.isNotEmpty(verificationReq.getOperators())) {
            String[] operatorArr = verificationReq.getOperators().split(",");
            operatorSize = operatorArr.length;
            lambdaQueryWrapper.in(ExamineResultDo::getOperator, Arrays.asList(operatorArr));
        }
        List<String> fileIds = verificationReq.getFileIds().stream().distinct().collect(Collectors.toList());
        lambdaQueryWrapper.in(ExamineResultDo::getFileUuid, fileIds);
//不检查到期时间
        //        if (ObjectUtil.isNotEmpty(verificationReq.getDays())){
//            lambdaQueryWrapper.ge(ExamineResultDo::getValidity, DateUtil.offsetDay(new Date(), verificationReq.getDays()));
//        }
        lambdaQueryWrapper.eq(ExamineResultDo::getFileStatus, 2);

        lambdaQueryWrapper.groupBy(true, ExamineResultDo::getCreator, ExamineResultDo::getOperator, ExamineResultDo::getSupplierTag);
        //判断是不是每个file在所需要的Chatbot上素材都是审核完毕的
        List<ExamineResultDo> examineShareCarrierResultDos = examineResultMapper.selectList(queryWrapper);
        log.info("全部满足条件素材数量：{}", examineShareCarrierResultDos.size());
        List<TemplateOwnershipReflect> shareMaterialOwnershipReflects = new ArrayList<>();
        //按运营商分组的数量不等于传入的运营商+chatbot个数表示不是所有通道都审核通过
        if (examineShareCarrierResultDos.size() != operatorSize && operatorSize != 0) {
            return null;
        }
        for (ExamineResultDo item : examineShareCarrierResultDos) {//按运营商和服务商分组的文件数等于检查的文件数，说明所有文件都已经在这个服务商的运营商这里送审通过了
            //没传运营商或chatbot账号，直接审核通过的素材全部都要，否则，每个运营商下审核通过的素材数量等于校验的文件数量
            if (item.getFileCount() == fileIds.size()) {
                TemplateOwnershipReflect templateOwnershipReflect = new TemplateOwnershipReflect();
                templateOwnershipReflect.setOperator(item.getOperator());
                templateOwnershipReflect.setChatbotName(item.getChatbotName());
                templateOwnershipReflect.setCreator(item.getCreator());
                templateOwnershipReflect.setSupplierTag(item.getSupplierTag());
                //防止是旧的chatbot账号送审的素材，所以重新获取下chatbot账号
                AccountManagementTypeReq accountManagementTypeReq = new AccountManagementTypeReq();
                accountManagementTypeReq.setAccountType(item.getOperator());
                accountManagementTypeReq.setCreator(item.getCreator());
                AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByAccountType(accountManagementTypeReq);
                templateOwnershipReflect.setChatbotAccount(accountManagementResp.getChatbotAccount());
                templateOwnershipReflect.setChatbotName(accountManagementResp.getAccountName());
                shareMaterialOwnershipReflects.add(templateOwnershipReflect);
            }
            //有运营商通道素材没有完全过审则送审失败
            else {
                //机器人流程要所有素材通过所有账号审核
                if (verificationReq.getTemplateSource() == Constants.TEMPLATE_SOURCE_ROBOT) {
                    return null;
                }
                //群发计划要所有素材通过某一个账号审核
                else {
                    continue;
                }
            }
        }
        return shareMaterialOwnershipReflects;
    }

    /**
     * 统计素材数量接口
     *
     * @param fileTidReq 运营商
     * @return 数量集合
     */
    @Override
    public List<StatisticsResp> statistics(FileTidReq fileTidReq) {
        return platformService.statistics(fileTidReq);
    }

    @Override
    public List<ExamResp> findExamByUuid(@RequestBody VerificationReq verificationReq) {
        LambdaQueryWrapper<ExamineResultDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExamineResultDo::getFileUuid, verificationReq.getFileIds());
        List<ExamineResultDo> examineResultDos = examineResultMapper.selectList(queryWrapper);
        List<ExamResp> examResps = BeanUtil.copyToList(examineResultDos, ExamResp.class);
        if (CollectionUtils.isEmpty(examResps)) {
            return new ArrayList<>();
        }
        return examResps;
    }
}
