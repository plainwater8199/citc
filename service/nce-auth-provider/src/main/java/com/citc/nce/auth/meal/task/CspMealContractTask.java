package com.citc.nce.auth.meal.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.citc.nce.auth.meal.enums.CspMealContractStatus;
import com.citc.nce.auth.meal.service.ICspMealContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * bydud
 * 2024/1/23
 **/
@Component
@Slf4j
public class CspMealContractTask {
    @Resource
    private ICspMealContractService contractService;

    /**
     * 每天晚上00：03 分
     */
    @Scheduled(cron = "0 3 0 * * ?")
    public void contractStatus() {
        //今天开始时间算
        DateTime today = DateUtil.beginOfDay(new Date());
        Map<String, List<CspMealContract>> contractMap = contractService.list().stream()
                .collect(Collectors.groupingBy(CspMealContract::getCspId));
        log.info("定时任务处理合同状态 合同数量：{}", contractMap.size());
        //分csp处理
        for (Map.Entry<String, List<CspMealContract>> entry : contractMap.entrySet()) {
            List<CspMealContract> contractList = entry.getValue();
            if (CollectionUtil.isEmpty(contractList)) continue;

            //1、失效合同
            for (CspMealContract contract : contractList) {
                if (today.getTime() > contract.getExpireTime().getTime()) {
                    //修改合同状态
                    contractService.updateStatus(contract.getContractId(), CspMealContractStatus.INEFFECTIVE);
                    log.info("定时任务处理合同状态 失效合同id：{}", contract.getContractId());
                }
            }

            //2、生效合同 (只要待生效的合同，按照开始时间排序（正序）)
            for (CspMealContract c : contractList.stream().filter(s -> CspMealContractStatus.PENDING.equals(s.getStatus()))
                    .sorted(Comparator.comparingLong(s -> s.getExpireTime().getTime()))
                    .collect(Collectors.toList())) {
                if (today.getTime() >= c.getEffectiveTime().getTime() && today.getTime() < c.getExpireTime().getTime()) {
                    contractService.effectContract(c.getContractId());
                    log.info("定时任务处理合同状态 生效合同id：{}", c.getContractId());
                    break;
                }
            }
            //3 刷新合同和用户数 状态
            contractService.refreshCspMealStatus(entry.getKey());
        }
    }


}
