package com.citc.nce.developer.schedule;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.developer.entity.DeveloperCustomer5gApplicationDo;
import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.fileApi.ExamineResultApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.ExamineReq;
import com.citc.nce.vo.ExamineResultResp;
import com.citc.nce.vo.ExamineResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时送审素材快过期的应用模板素材
 *
 *  * @author ping chen
 */

@Component
@EnableScheduling
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class Developer5gApplicationApprovalSchedule {

    private final DeveloperCustomer5gApplicationService developerCustomer5gApplicationService;

    private final RedissonClient redissonClient;

    private final MessageTemplateApi messageTemplateApi;

    private final ExamineResultApi examineResultApi;

    private final AccountManagementApi accountManagementApi;

    private final FileApi fileApi;


    @Scheduled(cron = "${developer.application.approvalCron}")
    public void schedule(){
        RLock lock = redissonClient.getLock("developer5gApplicationApprovalSchedule");
        try {
            lock.lock();
            log.info("定时送审素材快过期的应用模板素材开始");
            List<DeveloperCustomer5gApplicationDo> developerCustomer5gApplicationDoList = developerCustomer5gApplicationService.lambdaQuery().eq(DeveloperCustomer5gApplicationDo::getApplicationState,0).list();
            if(!developerCustomer5gApplicationDoList.isEmpty()){
                for(DeveloperCustomer5gApplicationDo developerCustomer5gApplicationDo:developerCustomer5gApplicationDoList){
                    MessageTemplateResp messageTemplateResp = messageTemplateApi.getMessageTemplateById(developerCustomer5gApplicationDo.getTemplateId());
                    String fileUuid = null;
                    List<ExamineResultVo> examineResultVoList = new ArrayList<>();
                    String moduleInformation = messageTemplateResp.getModuleInformation();
                    JSONObject jsonObject = JsonUtils.string2Obj(moduleInformation, JSONObject.class);
                    switch (messageTemplateResp.getMessageType()){
                        case 2:
                            //图片
                            fileUuid = jsonObject.getString("pictureUrlId");
                            break;
                        case 3:
                            //视屏
                            fileUuid = jsonObject.getString("videoUrlId");
                            break;
                        case 4:
                            //音频
                            fileUuid = jsonObject.getString("audioUrlId");
                            break;
                        default:
                            break;
                    }
                    if(fileUuid != null){
                        String[] chatbotAccountIds = developerCustomer5gApplicationDo.getChatbotAccountId().split(",");
                        for(String chatbotAccountId:chatbotAccountIds){
                            ExamineResultResp examineResultResp = new ExamineResultResp();
                            AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByChatbotAccountId(chatbotAccountId);
                            examineResultResp.setChatbotId(accountManagementResp.getChatbotAccount());
                            examineResultResp.setFileUuid(fileUuid);
                            ExamineResultVo examineResultVo = examineResultApi.queryExamineResult(examineResultResp);
                            examineResultVoList.add(examineResultVo);

                        }
                    }
                    //判断素材是否过期
                    if(examineResultVoList.size() !=0 ) {
                        for (ExamineResultVo examineResultVo : examineResultVoList) {
                            if (examineResultVo.getValidity() != null && this.compare(examineResultVo.getValidity())) {
                                //素材过期送审
                                ExamineReq examineReq = new ExamineReq();
                                List<String> fileUUIDs = new ArrayList<>();
                                fileUUIDs.add(fileUuid);
                                List<String> operators = new ArrayList<>();
                                operators.add(examineResultVo.getOperator());
                                examineReq.setOperators(operators);
                                examineReq.setFileUUIDs(fileUUIDs);
                                fileApi.examine(examineReq);
                                developerCustomer5gApplicationDo.setApplicationTemplateState(2);
                            }
                        }
                        developerCustomer5gApplicationService.saveOrUpdate(developerCustomer5gApplicationDo);
                    }
                }
                log.info("定时送审素材快过期的应用模板素材结束");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 判断是否即将过期
     * @param date
     * @return
     */
    private Boolean compare(Date date){
        Date now = new Date();
        long timestamp = now.getTime();
        long timeAgo = timestamp - 25 * 60 * 60 * 1000;
        Date dateAgo = new Date(timeAgo);
        return date.compareTo(dateAgo) > 0;
    }
}
