package com.citc.nce.auth.csp.schedule;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageDao;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.unicomAndTelecom.service.CspPlatformService;
import com.citc.nce.misc.schedule.ScheduleApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 过期数据定时扫描
 */
@Component
@EnableScheduling
@Slf4j
public class ContractSchedule {


    @Resource
    ScheduleApi scheduleApi;
    @Resource
    ContractManageDao contractManageDao;

    @Resource
    CspPlatformService cspPlatformService;

    @Resource
    ChatbotManageDao chatbotManageDao;

    @Resource
    private AccountManageService accountManageService;


    @Scheduled(cron = "${unicomAndTelecom.contractScheduleCron}")
    @Transactional
    public void contractSchedule() {
        try {
            boolean canExec = scheduleApi.addRecord("contractScheduleCron", "H");
            if (canExec) {
                //查询当前数据库中过期时间小于当前时间的
                List<ContractManageDo> all = new ArrayList<>();
                LambdaQueryWrapper<ContractManageDo> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ContractManageDo::getOperatorCode, 1, 3);
                wrapper.eq(ContractManageDo::getContractIsRenewal, 0);
                wrapper.eq(ContractManageDo::getContractStatus, 66);
                wrapper.lt(ContractManageDo::getContractExpireDate, new Date());
                //过期时间小于当前时间的未续签
                List<ContractManageDo> list = contractManageDao.selectList(wrapper);

                LambdaQueryWrapper<ContractManageDo> wrapper2 = new LambdaQueryWrapper<>();
                wrapper2.in(ContractManageDo::getOperatorCode, 1, 3);
                wrapper2.eq(ContractManageDo::getContractIsRenewal, 1);
                wrapper2.eq(ContractManageDo::getContractStatus, 66);
                wrapper2.lt(ContractManageDo::getContractRenewalDate, new Date());
                List<ContractManageDo> other = contractManageDao.selectList(wrapper2);

                //续签且为变更待审核，将实际状态修改为已过期
                LambdaQueryWrapper<ContractManageDo> wrapper3 = new LambdaQueryWrapper<>();
                wrapper3.in(ContractManageDo::getOperatorCode, 1, 3);
                wrapper3.eq(ContractManageDo::getContractIsRenewal, 1);
                wrapper3.eq(ContractManageDo::getActualState, 66);
                wrapper3.notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
                wrapper3.lt(ContractManageDo::getContractRenewalDate, new Date());
                List<ContractManageDo> other2 = contractManageDao.selectList(wrapper3);

                //未续签且为变更待审核，将实际状态修改为已过期
                LambdaQueryWrapper<ContractManageDo> wrapper4 = new LambdaQueryWrapper<>();
                wrapper4.in(ContractManageDo::getOperatorCode, 1, 3);
                wrapper4.eq(ContractManageDo::getContractIsRenewal, 0);
                wrapper4.eq(ContractManageDo::getActualState, 66);
                wrapper4.notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());

                wrapper4.lt(ContractManageDo::getContractRenewalDate, new Date());
                List<ContractManageDo> other3 = contractManageDao.selectList(wrapper3);
                other2.forEach(contractManageDo -> {
                    contractManageDo.setActualState(68);
                });
                other3.forEach(contractManageDo -> {
                    contractManageDo.setActualState(68);
                });
                list.forEach(contractManageDo -> {
                    contractManageDo.setContractStatus(68);
                });
                other.forEach(contractManageDo -> {
                    contractManageDo.setContractStatus(68);
                });
                all.addAll(list);
                all.addAll(other);
                all.addAll(other2);
                all.addAll(other3);
                if (CollectionUtil.isNotEmpty(all)) {
                    contractManageDao.updateBatch(all);
                    //更新chatbot状态
                    for (ContractManageDo contractManageDo : all) {
                        String creator = contractManageDo.getCreator();
                        String customerId = contractManageDo.getCustomerId();
                        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne("customer_id", customerId);
                        if (ObjectUtil.isNotEmpty(chatbotManageDo)) {
                            AccountManageDo cspInfo = accountManageService.getCspAccount(chatbotManageDo.getOperatorCode(), creator);
                            cspPlatformService.isOnlineUpdate(chatbotManageDo.getAccessTagNo(), cspInfo.getCspPassword(), cspInfo.getCspAccount(), 2, chatbotManageDo.getOperatorCode());
                            chatbotManageDo.setChatbotStatus(71);
                            chatbotManageDo.setActualState(null);
                            chatbotManageDao.updateById(chatbotManageDo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }
}
