package com.citc.nce.auth.unicomAndTelecom.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageDao;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.unicomAndTelecom.UnicomAndTelecomApi;
import com.citc.nce.auth.unicomAndTelecom.req.AccessInformation;
import com.citc.nce.auth.unicomAndTelecom.req.CodeResult;
import com.citc.nce.auth.unicomAndTelecom.req.OperatorCodeReq;
import com.citc.nce.auth.unicomAndTelecom.req.UploadReq;
import com.citc.nce.auth.unicomAndTelecom.resp.ChatbotUploadResp;
import com.citc.nce.auth.unicomAndTelecom.service.CspPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UnicomAndTelecomController implements UnicomAndTelecomApi {

    @Resource
    private CspPlatformService cspPlatformService;
    @Resource
    private ContractManageDao contractManageDao;

    @Resource
    private ChatbotManageDao chatbotManageDao;

    @Resource
    private AccountManageService accountManageService;


    @Override
    public ChatbotUploadResp uploadContractFile(UploadReq uploadReq) {
        return cspPlatformService.uploadContractFile(uploadReq);
    }

    @Override
    public ChatbotUploadResp uploadChatBotFile(UploadReq uploadReq) {
        return cspPlatformService.uploadChatBotFile(uploadReq);
    }

    @Override
    public CodeResult getUnicomAndTelecomServiceCode(@RequestBody OperatorCodeReq operatorCode) {
        return cspPlatformService.getUnicomAndTelecomServiceCode(operatorCode.getOperatorCode());
    }

    @Override
    public ResponseEntity<byte[]> getFile(OperatorCodeReq operatorCodeReq) {
        return cspPlatformService.getFile(operatorCodeReq);
    }

    @Override
    public Map<Integer, String> getUnicomAndTelecomIndustry() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "其他行业");
        map.put(1, "农、林、牧、渔业");
        map.put(2, "采矿业");
        map.put(3, "制造业");
        map.put(4, "电力、燃气及水的生产和供应业");
        map.put(5, "建筑业");
        map.put(6, "交通运输、仓储和邮政业");
        map.put(7, "信息传输、软件和信息技术服务业");
        map.put(8, "批发和零售业");
        map.put(9, "住宿和餐饮业");
        map.put(10, "金融业");
        map.put(11, "房地产业");
        map.put(12, "租赁和商务服务业");
        map.put(13, "科学研究和技术服务业");
        map.put(14, "水利、环境和公共设施管理业");
        map.put(15, "居民服务、修理和其他服务业");
        map.put(16, "教育");
        map.put(17, "卫生、社会保障和社会福利业");
        map.put(18, "文化、体育和娱乐业");
        map.put(19, "公共管理和社会组织");
        map.put(20, "国际组织");
        map.put(21, "国防");
        return map;
    }

    @Override
    public void withdrawContract(@RequestBody OperatorCodeReq req) {
        cspPlatformService.withdrawContract(req);
    }

    @Override
    public void revokeCancellation(@RequestBody OperatorCodeReq req) {
        cspPlatformService.revokeCancellation(req);
    }

    @Override
    public void revokeChangeContract(@RequestBody OperatorCodeReq req) {
        cspPlatformService.revokeChangeContract(req);
    }

    @Override
    public void withdrawChatBot(OperatorCodeReq req) {
        cspPlatformService.withdrawChatBot(req);
    }

    @Override
    public void revokeCancellationChatBot(OperatorCodeReq req) {
        cspPlatformService.revokeCancellationChatBot(req);
    }

    @Override
    public void revokeChangeChatBot(OperatorCodeReq req) {
        cspPlatformService.revokeChangeChatBot(req);
    }

    @Override
    public void applyOnlineChatBot(OperatorCodeReq req) {
        cspPlatformService.applyOnlineChatBot(req);
    }

    @Override
    public void revokeOnlineChatBot(OperatorCodeReq req) {
        cspPlatformService.revokeOnlineChatBot(req);
    }

    @Override
    public AccessInformation accessInformation(OperatorCodeReq req) {
        return cspPlatformService.accessInformation(req);
    }

    @Override
    public void testChatBot(OperatorCodeReq req) {
        cspPlatformService.testChatBot(req);
    }

    @Override
    @Transactional
    public void testContractSchedule(OperatorCodeReq req) {
        contractSchedule(req.getNowDate());
    }

    @Transactional
    public void contractSchedule(String date) {
        //查询当前数据库中过期时间小于当前时间的
        List<ContractManageDo> all = new ArrayList<>();
        LambdaQueryWrapper<ContractManageDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ContractManageDo::getOperatorCode, 1, 3);
        wrapper.eq(ContractManageDo::getContractIsRenewal, 0);
        wrapper.eq(ContractManageDo::getContractStatus, 66);
        DateTime parseDate = DateUtil.parseDate(date);
        wrapper.lt(ContractManageDo::getContractExpireDate, parseDate);
        //过期时间小于当前时间的未续签
        List<ContractManageDo> list = contractManageDao.selectList(wrapper);

        LambdaQueryWrapper<ContractManageDo> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.in(ContractManageDo::getOperatorCode, 1, 3);
        wrapper2.eq(ContractManageDo::getContractIsRenewal, 1);
        wrapper2.eq(ContractManageDo::getContractStatus, 66);
        wrapper2.lt(ContractManageDo::getContractRenewalDate, parseDate);
        List<ContractManageDo> other = contractManageDao.selectList(wrapper2);

        //续签且为变更待审核，将实际状态修改为已过期
        LambdaQueryWrapper<ContractManageDo> wrapper3 = new LambdaQueryWrapper<>();
        wrapper3.in(ContractManageDo::getOperatorCode, 1, 3);
        wrapper3.eq(ContractManageDo::getContractIsRenewal, 1);
        wrapper3.eq(ContractManageDo::getActualState, 66);
        wrapper3.notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
        wrapper3.lt(ContractManageDo::getContractRenewalDate, parseDate);
        List<ContractManageDo> other2 = contractManageDao.selectList(wrapper3);

        //未续签且为变更待审核，将实际状态修改为已过期
        LambdaQueryWrapper<ContractManageDo> wrapper4 = new LambdaQueryWrapper<>();
        wrapper4.in(ContractManageDo::getOperatorCode, 1, 3);
        wrapper4.eq(ContractManageDo::getContractIsRenewal, 0);
        wrapper4.eq(ContractManageDo::getActualState, 66);
        wrapper4.notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
        wrapper4.lt(ContractManageDo::getContractRenewalDate, parseDate);
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

    @Override
    public void deleteChatbot(
            @RequestParam("accessTagNo") String accessTagNo,
            @RequestParam("accessKey") String accessKey,
            @RequestParam("cspId") String cspId,
            @RequestParam("operatorCode") Integer operatorCode
    ) {
        cspPlatformService.deleteChatBot(accessTagNo, accessKey, cspId, operatorCode);
    }
}
