package com.citc.nce.module.service.impl;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.auth.contactgroup.ContactGroupApi;
import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.RocketMQSubscribeConfigure;
import com.citc.nce.module.constant.ModuleError;
import com.citc.nce.module.dao.ModuleButtonRelationDao;
import com.citc.nce.module.dao.SignModuleDao;
import com.citc.nce.module.dao.SubscribeModuleDao;
import com.citc.nce.module.dto.SendContentForSubscribeDto;
import com.citc.nce.module.entity.ModuleButtonRelationDo;
import com.citc.nce.module.entity.SignModuleDo;
import com.citc.nce.module.entity.SubscribeModuleDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.*;
import com.citc.nce.module.vo.req.ImportContactGroupReq;
import com.citc.nce.module.service.ModuleService;
import com.citc.nce.module.service.SignNamesService;
import com.citc.nce.module.service.SubscribeModuleService;
import com.citc.nce.module.service.SubscribeNamesService;
import com.citc.nce.module.vo.req.SignNamesReq;
import com.citc.nce.module.vo.req.SubscribeNamesReq;
import com.citc.nce.module.vo.resp.ImportContactGroupResp;
import com.citc.nce.robot.api.MessageApi;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.utils.DateUtils;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service("moduleService")
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    @Resource
    private ModuleButtonRelationDao moduleButtonRelationDao;

    @Resource
    private SignModuleDao signModuleDao;

    @Resource
    private SubscribeModuleDao subscribeModuleDao;

    @Resource
    private MessageApi messageApi;

    @Resource
    private SubscribeModuleService subscribeModuleService;

    @Resource
    private SignModuleService signModuleService;

    @Resource
    private SubscribeNamesService subscribeNamesService;

    @Resource
    private SignNamesService signNamesService;

    private final RocketMQSubscribeConfigure rocketMQConfigure;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private ContactGroupApi contactGroupApi;

    @Resource
    private ContactListApi contactListApi;


    @Resource
    private RechargeTariffApi rechargeTariffApi;
    @Resource
    private AccountManagementApi accountManagementApi;

    @Override
    public Boolean saveModuleButUuidRelation(Integer moduleType, String moduleId, String butUuid) {
        if (!Strings.isNullOrEmpty(butUuid) && !Strings.isNullOrEmpty(moduleId)) {
            //检查组件是否存在
            boolean isExist = checkModule(moduleType, moduleId);
            if (isExist) {
                //删除按钮组件映射信息
                LambdaQueryWrapper<ModuleButtonRelationDo> moduleButtonRelationDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                moduleButtonRelationDoLambdaQueryWrapper.eq(ModuleButtonRelationDo::getButUuid, butUuid);
                List<ModuleButtonRelationDo> moduleButtonRelationDos = moduleButtonRelationDao.selectList(moduleButtonRelationDoLambdaQueryWrapper);
                if (CollectionUtils.isNotEmpty(moduleButtonRelationDos)) {
                    List<Long> ids = new ArrayList<>();
                    for (ModuleButtonRelationDo item : moduleButtonRelationDos) {
                        ids.add(item.getId());
                    }
                    moduleButtonRelationDao.logicDeleteByIds(ids);
                }
                //新增按钮组件映射信息
                ModuleButtonRelationDo moduleButtonRelationDo = new ModuleButtonRelationDo();
                moduleButtonRelationDo.setModuleId(moduleId);
                moduleButtonRelationDo.setModuleType(moduleType);
                moduleButtonRelationDo.setButUuid(butUuid);
                moduleButtonRelationDo.setCreateTime(new Date());
                moduleButtonRelationDo.setCreator(SessionContextUtil.getUser().getUserId());
                int insert = moduleButtonRelationDao.insert(moduleButtonRelationDo);
                return insert == 1;
            }
        }
        return false;

    }

    private boolean checkModule(Integer moduleType, String moduleId) {
        List<Integer> subscribeTypeList = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode());
        List<Integer> signTypeList = Arrays.asList(ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
        if (subscribeTypeList.contains(moduleType)) {
            LambdaQueryWrapper<SubscribeModuleDo> subscribeQueryWrapper = new LambdaQueryWrapper<>();
            subscribeQueryWrapper.eq(SubscribeModuleDo::getSubscribeId, moduleId);
            subscribeQueryWrapper.eq(SubscribeModuleDo::getDeleted, 0);
            List<SubscribeModuleDo> subscribeModuleDos = subscribeModuleDao.selectList(subscribeQueryWrapper);
            return CollectionUtils.isNotEmpty(subscribeModuleDos);
        } else if (signTypeList.contains(moduleType)) {
            LambdaQueryWrapper<SignModuleDo> signQueryWrapper = new LambdaQueryWrapper<>();
            signQueryWrapper.eq(SignModuleDo::getSignModuleId, moduleId);
            signQueryWrapper.eq(SignModuleDo::getDeleted, 0);
            List<SignModuleDo> signModuleDoList = signModuleDao.selectList(signQueryWrapper);
            return CollectionUtils.isNotEmpty(signModuleDoList);
        }
        return false;
    }

    @Override
    public String getModuleIdByButUuid(String butUuid) {
        if (!Strings.isNullOrEmpty(butUuid)) {
            LambdaQueryWrapper<ModuleButtonRelationDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ModuleButtonRelationDo::getButUuid, butUuid);
            List<ModuleButtonRelationDo> moduleButtonRelationDos = moduleButtonRelationDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(moduleButtonRelationDos)) {
                ModuleButtonRelationDo moduleButtonRelationDo = moduleButtonRelationDos.get(0);
                return moduleButtonRelationDo.getModuleId();
            }
        }
        return null;
    }

    @Override
    public void sendMessage(Long messageTemplateId, String variableValue, String phone, String chatbotId, int moduleType) {
        System.out.println("########发送订阅消息：" + phone + ",内容模板信息：" + messageTemplateId + ",发送账号：" + chatbotId);
        if (messageTemplateId != null && !Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(chatbotId)) {
            // 订阅后发送短信
            TestSendMsgReq testSendMsgReq = new TestSendMsgReq();
            testSendMsgReq.setChatbotId(chatbotId);
            testSendMsgReq.setTemplateId(messageTemplateId);
            testSendMsgReq.setPhoneNum(phone);
            testSendMsgReq.setResourceType(moduleType);//组件消息51-订阅，52-打卡
            testSendMsgReq.setVariables(variableValue);

            AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(chatbotId);
            RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(chatbotAccount.getChatbotAccountId());
            if (rechargeTariff == null) {
                throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), chatbotAccount.getAccountName()));
            }
            Runnable runnable = () -> messageApi.testSendMessage(testSendMsgReq);
            ThreadTaskUtils.execute(runnable);
        }
    }

    /**
     * 订阅组件新增和变更是也要调用这个接口
     * mq只消费当天的订阅内容
     */
    @Override
    public void sendSubscribeToMQ() {
        log.info("--------------------------------------------------更新待发送的订阅组件内容-------------------------------------------------------");
        //获取今天需发送的订阅
        List<SubscribeModuleDo> needSendSubscribes = subscribeModuleService.getSendSubscribeForToday(new Date());
        if (CollectionUtils.isNotEmpty(needSendSubscribes)) {
            //获取需要发送的内容
            List<SendContentForSubscribeDto> sendContentForSubscribeDtoList = new ArrayList<>();
            //整理需要发送的内容
            obtainSendInfoForSubscribe(needSendSubscribes, sendContentForSubscribeDtoList);

            if (CollectionUtils.isNotEmpty(sendContentForSubscribeDtoList)) {
                //查询需要发送的订阅内容
                for (SendContentForSubscribeDto item : sendContentForSubscribeDtoList) {
                    log.info("SubscribeNamesServiceImpl 发送到mq 对象为 ：{}", item);
                    //将订阅的信息加载到mq中
                    String subscribeSendInfo = JSON.toJSONString(item);
                    Message<String> message = MessageBuilder.withPayload(subscribeSendInfo).build();
                    //同步发送该消息，获取发送结果
                    SendResult sendResult = rocketMQTemplate.syncSendDelayTimeMills(rocketMQConfigure.getTopic(), message, item.getDelayDate());
                    log.info("SubscribeNamesServiceImpl 发送到mq 结果为 ：{}", sendResult);
                }
            }
        }
    }


    /**
     * 对订阅信息进行整理
     *
     * @param needSendSubscribes 待发送的订阅组件
     */
    private void obtainSendInfoForSubscribe(List<SubscribeModuleDo> needSendSubscribes, List<SendContentForSubscribeDto> sendContentForSubscribeDtoList) {
        if (CollectionUtils.isNotEmpty(needSendSubscribes)) {
            String subscribeId;
            String sendTimeString;
            String dayString = DateUtils.obtainDateStr(new Date(), "yyyy-MM-dd");
            Date sendTime;
            for (SubscribeModuleDo item : needSendSubscribes) {
                sendTimeString = item.getSendTime();
                if (StringUtil.isNotEmpty(sendTimeString)) {
                    subscribeId = item.getSubscribeId();
                    sendTime = DateUtils.obtainDate(dayString + " " + sendTimeString, (sendTimeString.length() > 5) ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd HH:mm");
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if (LocalTime.now().isBefore(LocalTime.parse(Objects.requireNonNull(DateUtils.obtainDateStr(sendTime, "yyyy-MM-dd HH:mm:ss")), dateTimeFormatter))) {
                        long ms = DateUtil.between(new Date(), sendTime, DateUnit.MS);
                        //添加要发送的组件
                        SendContentForSubscribeDto sendContentForSubscribeDto = new SendContentForSubscribeDto();
                        sendContentForSubscribeDto.setSubscribeId(subscribeId);
                        sendContentForSubscribeDto.setDelayDate(ms);
                        sendContentForSubscribeDto.setSubscribeName(item.getName());
                        sendContentForSubscribeDto.setSendDate(item.getSendTime());
                        sendContentForSubscribeDtoList.add(sendContentForSubscribeDto);
                    }
                }
            }
        }
    }


    /**
     * 更新组件列表的名单信息
     * 按钮ID(butUuid--全局唯一) 通过这个ID去查询模板表（message_template）里面的shortcut_button字段中检索，如果存在，再获取这个模板关联的组件ID
     *
     * @param btnType 按钮类型
     * @param butUuid 按钮ID
     * @param sender  订阅者/打卡者 用户
     */
    @Override
    public void updateModuleHandle(int btnType, String butUuid, String sender, String chatbotId) {
        chatbotId = getRealChatbotId(chatbotId);
        //1、通过butUuid获取主键的ID
        String moduleId = getModuleIdByButUuid(butUuid);
        if (moduleId != null) {
            // 根据返回处理订阅打卡
            if (btnType == ButtonType.SUBSCRIBE_BTN.getCode() || btnType == ButtonType.CANCEL_SUBSCRIBE_BTN.getCode()) {
                subscribeNameHandle(btnType, moduleId, sender, chatbotId);
            }
            if (btnType == ButtonType.JOIN_SIGN_BTN.getCode() || btnType == ButtonType.SIGN_BTN.getCode()) {
                signNameHandle(btnType, moduleId, sender, chatbotId);
            }
        }
    }

    @Override
    public ImportContactGroupResp importContactGroup(ImportContactGroupReq req) {
        ImportContactGroupResp resp = new ImportContactGroupResp();
        Integer moduleType = req.getModuleType();
        String moduleId = req.getModuleId();
        resp.setSuccessNum(0);
        if(moduleType != null && StringUtil.isNotEmpty(moduleId)){
            Map<String, Set<String>> modulePhonesMap = getModulePhone(moduleType,moduleId);
            if(!modulePhonesMap.isEmpty()){
                String moduleName = modulePhonesMap.keySet().iterator().next();
                List<String> phoneList = new ArrayList<>(modulePhonesMap.get(moduleName));
                if(CollectionUtils.isNotEmpty(phoneList)){
                    resp.setSuccessNum(contactGroupApi.importContactForModule(phoneList,req.getGroupId(),moduleName+"-"+DateUtils.obtainDateStr(new Date(),"yyyyMMddHHmmss")));
                }
            }
        }
        return resp;
    }

    @Override
    public String getNowDay(Date date) {
        Date newDate = (date == null) ? new Date() : date;
        int dayOfWeekInt = DateUtils.getDayOfWeek(newDate);//获取今天星期几
        return getWeekDay(dayOfWeekInt);
    }
    /*
    获取当前日期是一周中的第几天，注意Java中周日是一周的第一天，值为1，周一为2，依此类推
    First,second,third,fourth,fifth,sixth,seventh
     */
    private String getWeekDay(int dayOfWeekInt) {
        switch (dayOfWeekInt){
            case 1:
                return "SEVENTH";
            case 2:
                return "FIRST";
            case 3:
                return "SECOND";
            case 4:
                return "THIRD";
            case 5:
                return "FOURTH";
            case 6:
                return "FIFTH";
            case 7:
                return "SIXTH";
            default :
                return null;
        }
    }

    private Map<String,Set<String>> getModulePhone(Integer moduleType, String moduleId) {
        Map<String,Set<String>> modulePhonesMap = new HashMap<>();
        if(moduleType == ModuleStatusEnums.MODULE_SUBSCRIBE.getCode()){//订阅
            SubscribeModuleDo subscribeModuleInfo = subscribeModuleService.getSubscribeModuleInfo(moduleId);
            if(subscribeModuleInfo != null){
                Set<String> phones = subscribeNamesService.getSubscribePhones(moduleId);
                modulePhonesMap.put(subscribeModuleInfo.getName(),phones);
            }else{
                throw new BizException(ModuleError.MODULE_NOT_EXIST);
            }
        }else if(moduleType == ModuleStatusEnums.MODULE_SIGN.getCode()){//打卡
            SignModuleDo signModuleInfo = signModuleService.getSignModuleById(moduleId);
            if(signModuleInfo != null){
                Set<String> phones = signNamesService.getSubscribePhones(moduleId);
                modulePhonesMap.put(signModuleInfo.getName(),phones);
            }else{
                throw new BizException(ModuleError.MODULE_NOT_EXIST);
            }
        }
        return modulePhonesMap;
    }

    private String getRealChatbotId(String chatbotId) {
        if (chatbotId.contains("sip:")) {
            chatbotId = chatbotId.replace("sip:", "");
            if (chatbotId.contains("@")) {
                String[] split1 = chatbotId.split("@");
                chatbotId = split1[0];
            }
        }
        return chatbotId;
    }

    private void subscribeNameHandle(int type, String moduleId, String phone, String chatbotId) {
        SubscribeNamesReq subscribeNamesReq = new SubscribeNamesReq();
        subscribeNamesReq.setSubscribeId(moduleId);
        subscribeNamesReq.setPhone(phone);
        subscribeNamesReq.setChatbotId(chatbotId);
        if (type == ButtonType.SUBSCRIBE_BTN.getCode()) {
            subscribeNamesReq.setAdvance("-1");//为推送
            subscribeNamesService.saveSubscribeNames(subscribeNamesReq);
        }
        if (type == ButtonType.CANCEL_SUBSCRIBE_BTN.getCode()) {
            subscribeNamesService.cancelSubscribeNames(subscribeNamesReq);
        }
    }

    private void signNameHandle(int type, String moduleId, String phone, String chatbotId) {
        SignNamesReq req = new SignNamesReq();
        req.setSignModuleId(moduleId);
        req.setPhone(phone);
        req.setChatbotId(chatbotId);
        if (type == ButtonType.JOIN_SIGN_BTN.getCode()) {
            signNamesService.saveSignNamesForButton(req);
        }
        if (type == ButtonType.SIGN_BTN.getCode()) {
            signNamesService.updateSignNames(req);
        }
    }
}
