package com.citc.nce.robotfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.dto.PageReq;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.robotfile.mapper.ExamineResultMapper;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.ExamineResultResp;
import com.citc.nce.vo.ExamineResultVo;
import com.citc.nce.vo.FileExamineStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: ExamineResultServiceImpl
 */
@Service
@Slf4j
public class ExamineResultServiceImpl extends ServiceImpl<ExamineResultMapper, ExamineResultDo> implements IExamineResultService {

    @Resource
    private ExamineResultMapper examineResultMapper;


    @Override
    public List<AccountResp> getAccountList(PageReq pageReq, String fileUrl, List<AccountManagementResp> list) {
        QueryWrapper<ExamineResultDo> examineWrapper = new QueryWrapper<>();
        List<AccountResp> accounts = new ArrayList<>();
        examineWrapper.eq("file_uuid", fileUrl);
        List<ExamineResultDo> examineResults = examineResultMapper.selectList(examineWrapper);
        Date now = new Date();
        Map<String, AccountManagementResp> chatbotMap = list.stream()
                .collect(Collectors.toMap(AccountManagementResp::getChatbotAccountId, Function.identity()));
        for (ExamineResultDo examineResult : examineResults) {
            AccountResp accountResp = new AccountResp();
            String accountId = examineResult.getChatbotAccountId();
            if (chatbotMap.containsKey(accountId)) {
                if (examineResult.getValidity() != null && examineResult.getValidity().before(now)) {
                    accountResp.setStatus(3);
                } else {
                    accountResp.setSupplierTag(examineResult.getSupplierTag());
                    accountResp.setStatus(examineResult.getFileStatus());
                    accountResp.setOperator(chatbotMap.get(accountId).getAccountType());
                    accountResp.setChatbotAccount(examineResult.getChatbotId());
                    accounts.add(accountResp);
                }
            } else {
                list.stream()
                        .filter(chatbot -> Objects.equals(chatbot.getAccountType(), examineResult.getOperator()))
                        .findAny()
                        .ifPresent(chatbot -> {
                            //如果存在就将状态设置为4
                            examineResult.setChatbotId(chatbot.getChatbotAccount());
                            examineResult.setChatbotAccountId(chatbot.getChatbotAccountId());
                            examineResult.setValidity(null);
                            examineResult.setFileStatus(4);
                            saveOrUpdate(examineResult);
                        });
            }
        }
        return accounts;
    }

    @Override
    public ExamineResultVo queryExamineResult(ExamineResultResp examineResultResp) {
        ExamineResultDo examineResultDo = this.lambdaQuery()
                .eq(ExamineResultDo::getFileUuid, examineResultResp.getFileUuid())
                .eq(ExamineResultDo::getChatbotId, examineResultResp.getChatbotId())
                .orderByDesc(BaseDo::getCreateTime)
                .one();
        if (examineResultDo != null) {
            ExamineResultVo examineResultVo = new ExamineResultVo();
            BeanUtils.copyProperties(examineResultDo, examineResultVo);
            return examineResultVo;
        }
        return null;
    }

    @Override
    public void deleteAuditRecord(String chatbotAccount) {
        LambdaQueryWrapper<ExamineResultDo> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ExamineResultDo::getChatbotId, chatbotAccount);
        int delete = examineResultMapper.delete(updateWrapper);
        log.info("删除原来的chatbot的素材审核记录,chatbotAccount:{},数量:{}", chatbotAccount, delete);
    }

    /**
     * 切换chatbot时更新素材到新机器人
     *
     * @param oldChatbotId 老机器人id
     * @return 更新素材数量
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long updateMaterialFromChatbotUpdate(String oldChatbotId) {
        LambdaUpdateWrapper<ExamineResultDo> updateWrapper = new LambdaUpdateWrapper<ExamineResultDo>()
                .eq(ExamineResultDo::getChatbotId, oldChatbotId)
                .set(ExamineResultDo::getFileStatus, 4)
                .set(ExamineResultDo::getValidity, null);
        return (long) baseMapper.update(new ExamineResultDo(), updateWrapper);
    }


    @Override
    public List<FileExamineStatusDto> queryExamineResultBatch(List<String> uuids) {
        Map<String, List<ExamineResultDo>> examineResultMap = lambdaQuery()
                .in(ExamineResultDo::getFileUuid, uuids)
                .list().stream()
                .collect(Collectors.groupingBy(ExamineResultDo::getFileUuid));
        return uuids.stream()
                .map(uuid -> {
                    FileExamineStatusDto examineStatusDto = new FileExamineStatusDto();
                    examineStatusDto.setUuid(uuid);
                    List<ExamineResultVo> examineResultVos = examineResultMap.get(uuid).stream()
                            .map(record -> {
                                ExamineResultVo examineResultVo = new ExamineResultVo();
                                examineResultVo.setFileStatus(record.getFileStatus());
                                examineResultVo.setValidity(record.getValidity());
                                examineResultVo.setOperator(record.getOperator());
                                examineResultVo.setChatbotAccount(record.getChatbotId());
                                return examineResultVo;
                            })
                            .collect(Collectors.toList());
                    examineStatusDto.setExamineResults(examineResultVos);
                    return examineStatusDto;
                })
                .collect(Collectors.toList());
    }
}
