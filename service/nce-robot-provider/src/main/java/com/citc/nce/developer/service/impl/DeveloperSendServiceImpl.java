package com.citc.nce.developer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.contactbacklist.ContactBackListApi;
import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateDetailVo;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.enums.SmsAuditStatus;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateVariable;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.configure.DeveloperReceiveConfigure;
import com.citc.nce.configure.RocketMQSendTopicConfigure;
import com.citc.nce.developer.dao.DeveloperCustomer5gApplicationMapper;
import com.citc.nce.developer.dao.DeveloperSendMapper;
import com.citc.nce.developer.entity.DeveloperCustomer5gApplicationDo;
import com.citc.nce.developer.entity.DeveloperCustomer5gDo;
import com.citc.nce.developer.entity.DeveloperSendDo;
import com.citc.nce.developer.entity.SmsDeveloperCustomerDo;
import com.citc.nce.developer.entity.VideoDeveloperCustomerDo;
import com.citc.nce.developer.enums.DeveloperCallStatus;
import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.developer.service.DeveloperCustomer5gService;
import com.citc.nce.developer.service.DeveloperSendService;
import com.citc.nce.developer.service.SmsDeveloperCustomerService;
import com.citc.nce.developer.service.VideoDeveloperCustomerService;
import com.citc.nce.developer.vo.DeveloperCustomer5gSendVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendDataVo;
import com.citc.nce.developer.vo.DeveloperSend5gSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendDataVo;
import com.citc.nce.developer.vo.DeveloperSendInfoVo;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.developer.vo.DeveloperSendSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendVideoSaveDataVo;
import com.citc.nce.developer.vo.enums.DeveloperSendStatus;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.api.MassSegmentApi;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.utils.AesForDeveloperUtil;
import com.citc.nce.utils.UUIDUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ping chen
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeveloperSendServiceImpl extends ServiceImpl<DeveloperSendMapper, DeveloperSendDo> implements DeveloperSendService {
    private static final String phoneRegex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    private final DeveloperReceiveConfigure developerReceiveConfigure;
    private final CommonKeyPairConfig commonKeyPairConfig;

    private final RocketMQSendTopicConfigure rocketMQSmsSendTopicConfigure;

    private final RocketMQTemplate rocketMQTemplate;

    private final SmsTemplateApi smsTemplateApi;

    private final SmsDeveloperCustomerService smsDeveloperCustomerService;

    private final VideoDeveloperCustomerService videoDeveloperCustomerService;

    private final CspSmsAccountApi cspSmsAccountApi;

    private final CspVideoSmsAccountApi cspVideoSmsAccountApi;

    private final MediaSmsTemplateApi mediaSmsTemplateApi;

    private final MessageTemplateApi messageTemplateApi;

    private final AccountManagementApi accountManagementApi;

    private final DeveloperCustomer5gApplicationService developerCustomer5gApplicationService;

    private final DeveloperCustomer5gApplicationMapper developerCustomer5gApplicationMapper;

    private final DeveloperCustomer5gService developerCustomer5gService;

    private final MassSegmentApi massSegmentApi;

    private final CspApi cspApi;

    private final CspCustomerApi cspCustomerApi;

    private final ContactBackListApi contactBackListApi;

    private final PrepaymentApi prepaymentApi;

    private final RechargeTariffApi rechargeTariffApi;

    @Resource
    ObjectMapper objectMapper;

    /**
     * 开发者 sms vide——sms
     *
     * @param userRequest
     * @return
     */
    @Override
    public List<DeveloperSendPhoneVo> send(DeveloperCustomerSendDataVo userRequest, String token) {
        String cusId = AesForDeveloperUtil.getInfo(token);
        List<DeveloperSendPhoneVo> developerSendPhoneVoList = new ArrayList<>();
        DeveloperSendDataVo developerSendDataVo = new DeveloperSendDataVo();
        String customSmsId = UUIDUtil.getuuid();
        String batchNumber = UUIDUtil.getuuid();
        //1、鉴权
        ErrorCode errorCode = this.auth(userRequest, cusId, developerSendDataVo);

        CustomerDetailResp detailByCustomer = cspCustomerApi.getDetailByCustomerIdForDontCheck(developerSendDataVo.getCustomerId());
        //3:短信 2:视屏短信
        Integer type = userRequest.getType();
        //后付费对象,必须设置资费
        if (detailByCustomer.getPayType() == CustomerPayType.POSTPAY) {
            RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(developerSendDataVo.getAccountId());
            if (rechargeTariff == null) {
                throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), developerSendDataVo.getAccountId()));
            }
        }
        //预付费对象,如果在套餐不足的情况下,必须设置资费  BUG#4168(预付费-视频短信和短信。如果没有套餐，且无自费时，发送开发者服务要提示没有资费)
        else if (detailByCustomer.getPayType() == CustomerPayType.PREPAY) {
            if (type == MsgTypeEnum.SHORT_MSG.getCode()) {
                if (prepaymentApi.getRemainingCountByMessageType(developerSendDataVo.getAccountId(), MsgTypeEnum.SHORT_MSG, null) <= 0) {
                    RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(developerSendDataVo.getAccountId());
                    if (rechargeTariff == null) {
                        throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), developerSendDataVo.getAccountId()));
                    }
                    if (cspCustomerApi.getBalance(developerSendDataVo.getCustomerId()) <= 0L) {
                        throw new BizException(AuthError.DEVELOPER_ACCOUNT_OUT_OF_BALANCE_ERROR);
                    }
                }
            } else if (type == MsgTypeEnum.MEDIA_MSG.getCode()) {
                if (prepaymentApi.getRemainingCountByMessageType(developerSendDataVo.getAccountId(), MsgTypeEnum.MEDIA_MSG, null) <= 0) {
                    RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(developerSendDataVo.getAccountId());
                    if (rechargeTariff == null) {
                        throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), developerSendDataVo.getAccountId()));
                    }
                    if (cspCustomerApi.getBalance(developerSendDataVo.getCustomerId()) <= 0L) {
                        throw new BizException(AuthError.DEVELOPER_ACCOUNT_OUT_OF_BALANCE_ERROR);
                    }
                }
            }
        }

        DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
        developerSendPhoneVo.setDeveloperSenId(customSmsId);
        developerSendPhoneVo.setPhone(userRequest.getPhone());
        if (errorCode == null) {
            DeveloperSendDo developerSendDo = this.saveData(developerSendDataVo, userRequest, 0, customSmsId, batchNumber, DeveloperCallStatus.CALL_SUCCESS.getDesc());
            developerSendPhoneVo.setState(DeveloperCallStatus.CALL_SUCCESS.getCode());
            developerSendPhoneVo.setDesc(DeveloperCallStatus.CALL_SUCCESS.getDesc());
            developerSendPhoneVoList.add(developerSendPhoneVo);
            userRequest.setDeveloperSendPhoneVoList(developerSendPhoneVoList);


            String requestStr = JSONObject.toJSONString(userRequest);
            Message<String> message = MessageBuilder.withPayload(requestStr).build();
            rocketMQTemplate.asyncSend(rocketMQSmsSendTopicConfigure.getTopic(), message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    developerSendDo.setSendMqResult(0);
                    saveOrUpdate(developerSendDo);
                    log.info("发送短信消息到mq成功 结果为 ： {}", sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    developerSendDo.setSendMqResult(1);
                    developerSendDo.setSendPlatformResult(1);
                    developerSendDo.setCallResultMsg("消息消费失败！");
                    saveOrUpdate(developerSendDo);
                    log.info("发送短信消息成功到mq失败 ： {}", e.getMessage(), e);
                    throw new BizException(500, "发送失败");
                }
            });
        } else {
            log.info("errorCode {}", errorCode);
            this.saveData(developerSendDataVo, userRequest, 1, customSmsId, batchNumber, errorCode.getMsg());
            developerSendPhoneVo.setState(errorCode.getCode());
            developerSendPhoneVo.setDesc(errorCode.getMsg());
            developerSendPhoneVoList.add(developerSendPhoneVo);
        }
        return developerSendPhoneVoList;
    }


    /**
     * 发送5g消息
     *
     * @param userRequest
     * @param token
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<DeveloperSendPhoneVo> send5g(DeveloperCustomer5gSendVo userRequest, String token) {
        String cusId = AesForDeveloperUtil.getInfo(token);
        //确认5g消息app和配置信息（确认调用者身份和app）
        Confirm5gInfo confirm5GInfo = confirm5GUser(userRequest, cusId);
        DeveloperCustomer5gApplicationDo applicationDo = confirm5GInfo.getApplicationDo();
        DeveloperCustomer5gDo developerCustomer5gDo = confirm5GInfo.getDeveloperCustomer5gDo();
        String customSmsId = UUIDUtil.getuuid();
        String batchNumber = UUIDUtil.getuuid();

        //返回用户的对象
        List<DeveloperSendPhoneVo> developerSendPhoneVoList = new ArrayList<>();
        DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
        developerSendPhoneVo.setDeveloperSenId(customSmsId);
        developerSendPhoneVo.setPhone(userRequest.getPhone());
        developerSendPhoneVoList.add(developerSendPhoneVo);

        //模板信息
        DeveloperSendDataVo developerSendDataVo = new DeveloperSendDataVo();
        developerSendDataVo.setCustomerId(applicationDo.getCustomerId());
        //数据库记录对象
        DeveloperSendDo developerSend = null;
        try {
            //以前是先查询模板后鉴权
            MessageTemplateResp messageTemplateResp = messageTemplateApi.getMessageTemplateById(applicationDo.getTemplateId());
            if (Objects.isNull(messageTemplateResp)) {
                throw new BizException(AuthError.SMS_DEVELOPER_TEMPLATE_ERROR);
            }
            developerSendDataVo.setTemplateName(messageTemplateResp.getTemplateName());
            developerSendDataVo.setShortcutButton(messageTemplateResp.getShortcutButton());
            developerSendDataVo.setMessageType(messageTemplateResp.getMessageType());
            developerSendDataVo.setModuleInformation(messageTemplateResp.getModuleInformation());

            //sign鉴权
            if (!userRequest.getSign().equals(DigestUtils.md5Hex(applicationDo.getAppKey() + applicationDo.getAppSecret() + userRequest.getTime()))) {
                throw new BizException(AuthError.SMS_DEVELOPER_AUTH_ERROR);
            }

            //应用状态
            if (applicationDo.getApplicationState() != 0) {
                throw new BizException(AuthError.DEVELOPER_APPLICATION_STATES_ERROR);
            }

            //手机号正则 测试发送
            if (!Pattern.matches(phoneRegex, userRequest.getPhone())) {
                throw new BizException(AuthError.PHONE_ERROR);
            }

            //确认csp状态
            if (!cspApi.getCspStatus(applicationDo.getCspId())) {
                throw new BizException(AuthError.DEVELOPER_CSP_ACCOUNT_STATUS_ERROR);
            }

            //确认app对应的用户状态
            CustomerDetailResp detailByCustomer = cspCustomerApi.getDetailByCustomerIdForDontCheck(applicationDo.getCustomerId());
            if (Boolean.FALSE.equals(detailByCustomer.getCustomerActive())) {
                throw new BizException(AuthError.DEVELOPER_CUSTOMER_ACCOUNT_STATUS_ERROR);
            }
            //确认有5g消息权限
            if (!detailByCustomer.getPermissions().contains("5")) {//1、群发，2机器人，3，视频短信，4，短信，5：5G消息
                throw new BizException(AuthError.DEVELOPER_CUSTOMER_ACCOUNT_AUTH_STATUS_ERROR);
            }

            //黑名单中的手机号不能进行测试发送
            if (contactBackListApi.getContactInBlack(applicationDo.getCustomerId(), userRequest.getPhone())) {
                throw new BizException(AuthError.BLACKLIST_ERROR);
            }

            //使用前刷新模板状态
            Integer appTempStatus = developerCustomer5gApplicationService.refreshTemplateStatus(applicationDo);
            if (!appTempStatus.equals(applicationDo.getApplicationTemplateState())) {
                applicationDo.setApplicationTemplateState(appTempStatus);
                developerCustomer5gApplicationService.lambdaUpdate()
                        .set(DeveloperCustomer5gApplicationDo::getApplicationTemplateState, appTempStatus)
                        .eq(BaseDo::getId, applicationDo.getId())
                        .update();
            }
            //应用模板状态检查
            if (applicationDo.getApplicationTemplateState() != 4) {
                throw new BizException(AuthError.DEVELOPER_APPLICATION_TEMPLATE_STATES_ERROR);
            }
            //匹配机器人账号
            AccountManagementResp account = matchingChatbotAccount(applicationDo.getChatbotAccountId().split(","), userRequest.getPhone());

            RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(account.getChatbotAccountId());
            if (rechargeTariff == null) {
                throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), account.getAccountName()));
            }

            MsgSubTypeEnum subType = MsgSubTypeEnum.convertTemplateType2MsgSubType(messageTemplateResp.getMessageType());
            if (detailByCustomer.getPayType() == CustomerPayType.PREPAY &&
                    (prepaymentApi.getRemainingCountByMessageType(account.getChatbotAccount(), MsgTypeEnum.M5G_MSG, subType) <= 0
                            && cspCustomerApi.getBalance(account.getCustomerId()) <= 0L)) {
                throw new BizException(AuthError.DEVELOPER_ACCOUNT_OUT_OF_BALANCE_ERROR);
            }

            DeveloperCustomerSendDataVo sendDataMq = new DeveloperCustomerSendDataVo();
            BeanUtils.copyProperties(userRequest, sendDataMq);
            sendDataMq.setPlatformTemplateId(applicationDo.getTemplateId().toString());
            sendDataMq.setType(MsgTypeEnum.M5G_MSG.getCode());
            sendDataMq.setTime(userRequest.getTime());
            sendDataMq.setSign(userRequest.getSign());
            sendDataMq.setAccountId(account.getChatbotAccount());
            sendDataMq.setApplicationUniqueId(applicationDo.getUniqueId());
            sendDataMq.setCspId(applicationDo.getCspId());

            developerSendPhoneVo.setState(DeveloperCallStatus.CALL_SUCCESS.getCode());
            developerSendPhoneVo.setDesc(DeveloperCallStatus.CALL_SUCCESS.getDesc());
            developerSendPhoneVo.setCallBackUrl(developerCustomer5gDo.getCallbackUrl());
            sendDataMq.setDeveloperSendPhoneVoList(developerSendPhoneVoList);

            //保存发送记录
            developerSendDataVo.setAccountId(account.getChatbotAccountId());
            developerSend = this.saveData(developerSendDataVo, sendDataMq, 0, customSmsId, batchNumber, DeveloperCallStatus.CALL_SUCCESS.getDesc());//用于执行进度判断
            Message<String> message = MessageBuilder.withPayload(JSONObject.toJSONString(sendDataMq)).build();
            rocketMQTemplate.send(rocketMQSmsSendTopicConfigure.getTopic(), message);
        } catch (BizException bizException) {
            if (Objects.isNull(developerSend)) {
                developerSend = new DeveloperSendDo();
            }
            developerSend.setAppId(applicationDo.getAppId());
            developerSend.setApplicationUniqueId(applicationDo.getUniqueId());
            developerSend.setCspId(applicationDo.getCspId());
            developerSend.setAccountType(1);//1  5g消息
            developerSend.setPlatformTemplateSign(developerSendDataVo.getTemplateSign());
            developerSend.setPlatformTemplateId(applicationDo.getTemplateId().toString());
            developerSend.setPlatformTemplateName(developerSendDataVo.getTemplateName());
            developerSend.setPlatformTemplateMessageType(developerSendDataVo.getMessageType());
            developerSend.setPlatformTemplateShortcutButton(developerSendDataVo.getShortcutButton());
            developerSend.setPlatformTemplateModuleInformation(developerSendDataVo.getModuleInformation());
            developerSend.setCreator(developerSendDataVo.getCustomerId());
            developerSend.setUpdater(developerSendDataVo.getCustomerId());
            developerSend.setCustomerId(developerSendDataVo.getCustomerId());
            developerSend.setAccountId(developerSendDataVo.getAccountId());
            developerSend.setCallTime(LocalDateTime.now());
            developerSend.setPhone(userRequest.getPhone());
            developerSend.setCallResult(1);
            developerSend.setCustomSmsId(customSmsId);
            developerSend.setBatchNumber(batchNumber);
            developerSend.setCallResultMsg(bizException.getMsg());
            saveOrUpdate(developerSend);
            //返回失败原因
            developerSendPhoneVo.setState(bizException.getCode());
            developerSendPhoneVo.setDesc(bizException.getMsg());
        } catch (Exception e) {
            log.error("5g开发者应用未知错误", e);
            throw e;
        }
        return developerSendPhoneVoList;
    }

    private AccountManagementResp matchingChatbotAccount(String[] accountIds, String phone) {
        //查询号段 与机器人是否匹配
        String phoneOperator = massSegmentApi.queryOperator(phone.substring(0, 3));

        for (String accountId : accountIds) {
            AccountManagementResp account = accountManagementApi.getAccountManagementByChatbotAccountId(accountId);
            //是否查询到机器人
            if (Objects.isNull(account) || StringUtils.isBlank(account.getChatbotAccountId())) {
                log.warn("机器人账号不存在 ChatbotAccountId： {}", accountId);
                continue;
            }

            //匹配应核桃
            if ("硬核桃".equals(account.getAccountType())) {
                //下线的机器人不能进行测试发送
                return account;
            }

            //机器人账号和运营商匹配成功
            if (account.getAccountType().equals(phoneOperator)) {
                //机器人匹配上，检查机器人状态
                return account;
            }

        }

        throw new BizException(AuthError.DEVELOPER_OPERATOR_NOT_MATCH);
    }


    @Data
    @AllArgsConstructor
    private static class Confirm5gInfo {
        private DeveloperCustomer5gApplicationDo applicationDo;
        DeveloperCustomer5gDo developerCustomer5gDo;
    }

    private Confirm5gInfo confirm5GUser(DeveloperCustomer5gSendVo userRequest, String cusId) {
        //查询开发者配置
        DeveloperCustomer5gDo devConfig = developerCustomer5gService.lambdaQuery()
                .eq(DeveloperCustomer5gDo::getCustomerId, cusId)
                .one();
        if (Objects.isNull(devConfig)) {
            throw new BizException(AuthError.SMS_DEVELOPER_CALL_BASE_ERROR);
        }

        //查询开发者应用
        DeveloperCustomer5gApplicationDo applicationDo = developerCustomer5gApplicationService.lambdaQuery()
                .eq(DeveloperCustomer5gApplicationDo::getAppId, userRequest.getAppId()).one();
        if (applicationDo == null) {
            throw new BizException(AuthError.SMS_DEVELOPER_APPID_ERROR);
        }

        //开发者配置信息和应用是同一个人
        if (!devConfig.getUniqueId().equals(applicationDo.getDeveloperCustomerUniqueId())) {
            throw new BizException(AuthError.SMS_DEVELOPER_APPID_ERROR);
        }
        applicationDo.setAppSecret(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), applicationDo.getAppSecret()));
        return new Confirm5gInfo(applicationDo, devConfig);
    }

    private DeveloperSendDo saveData(DeveloperSendDataVo developerSendDataVo,
                                     DeveloperCustomerSendDataVo developerCustomerSendDataVo,
                                     int callResult,
                                     String customSmsId,
                                     String batchNumber,
                                     String callResultMsg) {
        DeveloperSendDo developerSendDo = new DeveloperSendDo();
        developerSendDo.setAccountId(developerSendDataVo.getAccountId());
        developerSendDo.setAppId(developerCustomerSendDataVo.getAppId());
        developerSendDo.setPhone(developerCustomerSendDataVo.getPhone());
        developerSendDo.setCustomSmsId(customSmsId);
        developerSendDo.setPlatformTemplateId(developerCustomerSendDataVo.getPlatformTemplateId());
        developerSendDo.setCallResult(callResult);
        developerSendDo.setCallTime(LocalDateTime.now());
        developerSendDo.setCallResultMsg(callResultMsg);
        developerSendDo.setCustomerId(developerSendDataVo.getCustomerId());
        developerSendDo.setCspId(developerCustomerSendDataVo.getCspId());
        developerSendDo.setBatchNumber(batchNumber);
        developerSendDo.setAccountType(developerCustomerSendDataVo.getType());
        developerSendDo.setCreator(developerSendDataVo.getCustomerId());
        developerSendDo.setUpdater(developerSendDataVo.getCustomerId());
        developerSendDo.setApplicationUniqueId(developerCustomerSendDataVo.getApplicationUniqueId());
        developerSendDo.setCspId(developerCustomerSendDataVo.getCspId());
        developerSendDo.setPlatformTemplateSign(developerSendDataVo.getTemplateSign());
        developerSendDo.setPlatformTemplateName(developerSendDataVo.getTemplateName());
        developerSendDo.setPlatformTemplateMessageType(developerSendDataVo.getMessageType());
        developerSendDo.setPlatformTemplateShortcutButton(developerSendDataVo.getShortcutButton());
        developerSendDo.setPlatformTemplateModuleInformation(developerSendDataVo.getModuleInformation());
        this.save(developerSendDo);
        return developerSendDo;
    }

    /**
     * 鉴权
     *
     * @param userRequest
     * @param cusId
     * @return
     */
    @SneakyThrows(JsonProcessingException.class)
    public ErrorCode auth(DeveloperCustomerSendDataVo userRequest, String cusId, DeveloperSendDataVo developerSendDataVo) {
        DeveloperSendInfoVo developerSendInfoVo = new DeveloperSendInfoVo();
        //获取开发者应用信息--短信应用
        if (userRequest.getType() == MsgTypeEnum.SHORT_MSG.getCode()) {
            //开发者身份查询
            SmsDeveloperCustomerDo smsDevCustomer = smsDeveloperCustomerService.lambdaQuery()
                    .eq(SmsDeveloperCustomerDo::getAppId, userRequest.getAppId())
                    .eq(SmsDeveloperCustomerDo::getCustomerId, cusId)
                    .one();
            if (Objects.isNull(smsDevCustomer)) {
                throw new BizException(AuthError.SMS_DEVELOPER_CALL_BASE_ERROR);
            }
            developerSendDataVo.setCustomerId(smsDevCustomer.getCustomerId());
            smsDevCustomer.setAppSecret(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), smsDevCustomer.getAppSecret()));

            BeanUtils.copyProperties(smsDevCustomer, developerSendInfoVo);
            userRequest.setCspId(smsDevCustomer.getCspId());
            userRequest.setApplicationUniqueId(smsDevCustomer.getAppId());
            //获取短信模板信息
            SmsTemplateDetailVo template = smsTemplateApi.getTemplateInfoByPlatformTemplateId(userRequest.getPlatformTemplateId());
            if (Objects.isNull(template)) {
                return AuthError.SMS_DEVELOPER_TEMPLATE_ERROR;
            }
            BeanUtils.copyProperties(template, developerSendDataVo);
            developerSendDataVo.setTemplateName(template.getTemplateName());
            developerSendDataVo.setTemplateSign(template.getSignatureContent());
            developerSendDataVo.setModuleInformation(template.getContent());
            developerSendDataVo.setShortcutButton(template.getSignatureContent());
            userRequest.setAccountId(template.getAccountId());
            userRequest.setTemplateId(template.getId());
        }
        //获取开发者应用信息--视频短信短信应用
        if (userRequest.getType() == MsgTypeEnum.MEDIA_MSG.getCode()) {
            VideoDeveloperCustomerDo videoDeveloperCustomerDo = videoDeveloperCustomerService.lambdaQuery()
                    .eq(VideoDeveloperCustomerDo::getAppId, userRequest.getAppId())
                    .eq(VideoDeveloperCustomerDo::getCustomerId, cusId).one();

            if (Objects.isNull(videoDeveloperCustomerDo)) {
                throw new BizException(AuthError.SMS_DEVELOPER_CALL_BASE_ERROR);
            }
            developerSendDataVo.setCustomerId(videoDeveloperCustomerDo.getCustomerId());
            videoDeveloperCustomerDo.setAppSecret(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), videoDeveloperCustomerDo.getAppSecret()));

            BeanUtils.copyProperties(videoDeveloperCustomerDo, developerSendInfoVo);
            userRequest.setCspId(videoDeveloperCustomerDo.getCspId());
            userRequest.setApplicationUniqueId(videoDeveloperCustomerDo.getAppId());
            //获取视频短信模板信息
            MediaSmsTemplateDetailVo template = mediaSmsTemplateApi.getTemplateInfoByPlatformTemplateId(userRequest.getPlatformTemplateId());
            if (Objects.isNull(template)) {
                return AuthError.SMS_DEVELOPER_TEMPLATE_ERROR;
            }

            BeanUtils.copyProperties(template, developerSendDataVo);
            developerSendDataVo.setTemplateName(template.getTemplateName());
            developerSendDataVo.setTemplateSign(template.getSignatureContent());
            template.setContents(mediaSmsTemplateApi.getContents(template.getId(), true).getContents());
            developerSendDataVo.setModuleInformation(objectMapper.writeValueAsString(template));
            userRequest.setAccountId(template.getAccountId());
            userRequest.setTemplateId(template.getId());

        }


        //服务调用鉴权校验
        String sign = DigestUtils.md5Hex(developerSendInfoVo.getAppKey() + developerSendInfoVo.getAppSecret() + userRequest.getTime());
        if (!userRequest.getSign().equals(sign)) {
            return AuthError.SMS_DEVELOPER_AUTH_ERROR;
        }

        //服务调用状态校验--是否已禁用
        if (developerSendInfoVo.getState() == 1) {
            return AuthError.SMS_DEVELOPER_TEMPLATE_STATE_ERROR;
        }
        //服务归属异常
        if (StringUtils.isEmpty(developerSendInfoVo.getCustomerId())) {
            return AuthError.SMS_DEVELOPER_APPID_ERROR;
        }

        //手机号正则 测试发送
        if (!Pattern.matches(phoneRegex, userRequest.getPhone())) {
            return AuthError.PHONE_ERROR;
        }

        //查询csp状态
        if (!cspApi.getCspStatus(developerSendInfoVo.getCspId())) {
            return AuthError.DEVELOPER_CSP_ACCOUNT_STATUS_ERROR;
        }
        //查询用户状态
        CustomerDetailResp detailByCustomer = cspCustomerApi.getDetailByCustomerIdForDontCheck(developerSendInfoVo.getCustomerId());
        Boolean customerActive = detailByCustomer.getCustomerActive();
        if (Boolean.FALSE.equals(customerActive)) {
            return AuthError.DEVELOPER_CUSTOMER_ACCOUNT_STATUS_ERROR;
        }
        String permissions = detailByCustomer.getPermissions();
        if (userRequest.getType() == MsgTypeEnum.SHORT_MSG.getCode()) {
            if (!permissions.contains("4")) {//1、群发，2机器人，3，视频短信，4，短信，5：5G消息
                return AuthError.DEVELOPER_CUSTOMER_ACCOUNT_AUTH_STATUS_ERROR;
            }
        } else {
            if (!permissions.contains("3")) {//1、群发，2机器人，3，视频短信，4，短信，5：5G消息
                return AuthError.DEVELOPER_CUSTOMER_ACCOUNT_AUTH_STATUS_ERROR;
            }
        }


        //消息模板异常
        if (StringUtils.isEmpty(developerSendDataVo.getAccountId())) {
            return AuthError.SMS_DEVELOPER_TEMPLATE_ERROR;
        }

        //消息模板状态异常
        if (developerSendDataVo.getDeletedTime() != null) {
            return AuthError.SMS_DEVELOPER_TEMPLATE_DELETE_ERROR;
        }
        //消息模板审核状态异常
        if (!Objects.equals(developerSendDataVo.getAudit(), SmsAuditStatus.PASS.getValue())) {
            return AuthError.SMS_DEVELOPER_TEMPLATE_AUTH_ERROR;
        }
        //手机号异常
        if (StringUtils.isEmpty(userRequest.getPhone()) || userRequest.getPhone().length() != 11) {
            return AuthError.DEVELOPER_PHONE_ERROR;
        }

        //短信账号信息校验
        if (userRequest.getType() == MsgTypeEnum.SHORT_MSG.getCode()) {
            CspSmsAccountDetailResp smsAccountDetailResp = cspSmsAccountApi.queryDetailInner(developerSendDataVo.getAccountId());
            if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
                return AuthError.SMS_DEVELOPER_TEMPLATE_ACCOUNT_ERROR;
            }
            // 验证后置，不需要在auth里面验证了
//            if (detailByCustomer.getPayType() == CustomerPayType.PREPAY &&
//                    prepaymentApi.getRemainingCountByMessageType(developerSendDataVo.getAccountId(), MsgTypeEnum.SHORT_MSG, null) <= 0) {
//                return AuthError.DEVELOPER_ACCOUNT_OUT_OF_BALANCE_ERROR;
//            }
        } else if (userRequest.getType() == MsgTypeEnum.MEDIA_MSG.getCode()) {//视频短信账号检验
            CspVideoSmsAccountDetailResp cspVideoSmsAccountDetailResp = cspVideoSmsAccountApi.queryDetailInner(developerSendDataVo.getAccountId());
            if (cspVideoSmsAccountDetailResp == null || cspVideoSmsAccountDetailResp.getStatus() == null || !cspVideoSmsAccountDetailResp.getStatus().equals(1)) {
                return AuthError.SMS_DEVELOPER_TEMPLATE_ACCOUNT_ERROR;
            }
            userRequest.setVideoAppId(cspVideoSmsAccountDetailResp.getAppId());
            userRequest.setVideoAppSecret(cspVideoSmsAccountDetailResp.getAppSecret());
            // 验证后置，不需要在auth里面验证了
//            if (detailByCustomer.getPayType() == CustomerPayType.PREPAY &&
//                    prepaymentApi.getRemainingCountByMessageType(developerSendDataVo.getAccountId(), MsgTypeEnum.MEDIA_MSG, null) <= 0) {
//                return AuthError.DEVELOPER_ACCOUNT_OUT_OF_BALANCE_ERROR;
//            }
        }
        return null;
    }


    /**
     * 修改短信发送信息到平台的状态
     *
     * @param developerSendPhoneVoList
     * @param smsTemplateSendVariableList
     */
    @Override
    public void updateSmsDeveloperSendPlatformResult(List<DeveloperSendPhoneVo> developerSendPhoneVoList, List<SmsTemplateVariable> smsTemplateSendVariableList) {
        List<String> smsDeveloperSendIdList = developerSendPhoneVoList.stream()
                .map(DeveloperSendPhoneVo::getDeveloperSenId)
                .collect(Collectors.toList());
        Map<String, DeveloperSendPhoneVo> stringDeveloperSendPhoneVoMap = new HashMap<>();
        developerSendPhoneVoList.forEach(i -> stringDeveloperSendPhoneVoMap.put(i.getDeveloperSenId(), i));
        List<DeveloperSendDo> developerSendDoList = this.lambdaQuery().in(DeveloperSendDo::getCustomSmsId, smsDeveloperSendIdList).list();
        log.info("smsTemplateSendVariableList:{}", smsTemplateSendVariableList);
        log.info("developerSendDoList:{}", developerSendDoList);
        if (CollectionUtils.isNotEmpty(smsTemplateSendVariableList)) {
            Map<String, SmsTemplateVariable> smsTemplateSendVariableMap = smsTemplateSendVariableList.stream().
                    collect(Collectors.toMap(SmsTemplateVariable::getCustomSmsId, Function.identity()));
            developerSendDoList.forEach(developerSendDo -> {
                SmsTemplateVariable sendResult = smsTemplateSendVariableMap.get(developerSendDo.getCustomSmsId());
                if (Objects.nonNull(sendResult)) {
                    developerSendDo.setSmsId(sendResult.getSmsId());
                    String content = sendResult.getContent();
                    if (StringUtils.isNotBlank(content)) {
                        JSONObject jsonObject = JSON.parseObject(content);
                        jsonObject.put("replaceFlag", "true");
                        developerSendDo.setPlatformTemplateModuleInformation(jsonObject.toJSONString());
                    }
                } else {
                    developerSendDo.setSendPlatformResult(1);
                    developerSendDo.setCallResultMsg("发送失败！");
                    DeveloperSendPhoneVo developerSendPhoneVo = stringDeveloperSendPhoneVoMap.get(developerSendDo.getCustomSmsId());
                    developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
                    developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
                    this.sendMessage(developerSendPhoneVo, developerSendDo);
                }
            });
        } else {
            developerSendDoList.forEach(smsDeveloperSendDo -> smsDeveloperSendDo.setSendMqResult(1));
        }
        this.saveOrUpdateBatch(developerSendDoList);
    }


    /**
     * 修改视屏短信发送信息到平台的状态
     *
     * @param developerSendPhoneVo
     * @param richMediaResultArray
     */
    @Override
    public void updateVideoDeveloperSendPlatformResult(DeveloperSendPhoneVo developerSendPhoneVo, RichMediaResultArray richMediaResultArray) {
        DeveloperSendDo developerSendDo = this.lambdaQuery().in(DeveloperSendDo::getCustomSmsId, developerSendPhoneVo.getDeveloperSenId()).one();
        if (developerSendDo != null) {
            if (richMediaResultArray != null) {
                if (ObjectUtil.isNotEmpty(richMediaResultArray.getSuccess()) && Boolean.TRUE.equals(richMediaResultArray.getSuccess())) {
                    String messageId = richMediaResultArray.getResult().get(0).getVmsId();
                    developerSendDo.setSmsId(messageId);
                    String moduleInformation = developerSendDo.getPlatformTemplateModuleInformation();
                    if (StringUtils.isNotBlank(moduleInformation) && StringUtils.isNotBlank(richMediaResultArray.getContents())) {
                        log.info("开始替换");
                        try {
                            ObjectNode jsonObject = objectMapper.readValue(moduleInformation, ObjectNode.class);
                            ObjectNode templateInfo = objectMapper.readValue(richMediaResultArray.getContents(), ObjectNode.class);
                            jsonObject.set("contents", templateInfo.get("contents"));
                            jsonObject.put("replaceFlag", "true");
                            developerSendDo.setPlatformTemplateModuleInformation(objectMapper.writeValueAsString(jsonObject));
                            log.info("替换后的保存信息：{}", developerSendDo);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    developerSendDo.setSendPlatformResult(1);
                    developerSendDo.setCallResultMsg(richMediaResultArray.getMessage());
                    //向用户发送结果，发送失败，
                    developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
                    developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
                    this.sendMessage(developerSendPhoneVo, developerSendDo);
                }
            } else {
                developerSendDo.setSendPlatformResult(1);
                developerSendDo.setCallResultMsg("发送失败！");
                //向用户发送结果，发送失败，
                developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
                developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
                this.sendMessage(developerSendPhoneVo, developerSendDo);
            }
            log.info("开始保存");
            this.saveOrUpdate(developerSendDo);
        }
    }

    @Override
    public void update5gDeveloperSendPlatformResult(MessageData messageData, DeveloperSendPhoneVo developerSendPhoneVo) {
        DeveloperSendDo developerSendDo = this.lambdaQuery().in(DeveloperSendDo::getCustomSmsId, developerSendPhoneVo.getDeveloperSenId()).one();
        if (Objects.isNull(developerSendDo)) {
            log.warn("发送记录丢失developerSendPhoneVo {}  messageData {}", developerSendPhoneVo, messageData);
            return;
        }
        if (messageData != null) {
            if (messageData.getCode() == 0) {
                String messageId = messageData.getData().getMessageId();
//                developerSendDo.setSendPlatformResult(0);
                developerSendDo.setSmsId(messageId);
//                developerSendDo.setCallbackPlatformResult(2);
                String moduleInformation = messageData.getTemplateReplaceModuleInformation();
                if (StringUtils.isNotBlank(moduleInformation)) {
                    JSONObject jsonObject = JSON.parseObject(moduleInformation);
                    jsonObject.put("replaceFlag", "true");
                    developerSendDo.setPlatformTemplateModuleInformation(jsonObject.toJSONString());
                }
            } else {
                developerSendDo.setSendPlatformResult(1);
                developerSendDo.setCallResultMsg(messageData.getMessage());
                developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
                developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
                this.sendMessage(developerSendPhoneVo, developerSendDo);
            }
        } else {
            developerSendDo.setSendPlatformResult(1);
            developerSendDo.setCallResultMsg("发送失败！");
            developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
            developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
            this.sendMessage(developerSendPhoneVo, developerSendDo);
        }
        this.updateById(developerSendDo);
    }

    @Override
    public void saveData(DeveloperSendSaveDataVo developerSendSaveDataVo) {
        DeveloperSendDo developerSendDo = this.lambdaQuery().eq(DeveloperSendDo::getSmsId, developerSendSaveDataVo.getMessageId())
                .eq(DeveloperSendDo::getPhone, developerSendSaveDataVo.getPhoneNum()).one();
        if (developerSendDo == null) {
            log.warn("收到异常开发者短信回调 smsId:{}", developerSendSaveDataVo.getMessageId());
            return;
        }
        DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
        developerSendPhoneVo.setDeveloperSenId(developerSendDo.getCustomSmsId());
        developerSendPhoneVo.setPhone(developerSendDo.getPhone());
        SmsDeveloperCustomerDo smsDeveloperCustomerDo = smsDeveloperCustomerService.lambdaQuery().eq(SmsDeveloperCustomerDo::getAppId, developerSendDo.getAppId()).one();
        developerSendPhoneVo.setCallBackUrl(smsDeveloperCustomerDo.getCallbackUrl());

        if (developerSendSaveDataVo.getStatus().equals("DELIVRD")) {
            developerSendDo.setCallbackPlatformResult(0);
            developerSendDo.setSendPlatformResult(0);
            developerSendDo.setCallResultMsg("发送成功！");
            developerSendPhoneVo.setState(DeveloperSendStatus.DELIVRD.getValue());
            developerSendPhoneVo.setDesc(DeveloperSendStatus.DELIVRD.getAlias());
        } else {
            developerSendDo.setSendPlatformResult(1);
            developerSendDo.setCallResultMsg("发送失败！");
            developerSendDo.setCallbackPlatformResult(1);
            developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
            developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
        }
        this.saveOrUpdate(developerSendDo);
        if (developerSendDo.getCallbackPlatformResult() != null && developerSendDo.getCallbackPlatformResult() != 2) {
            //回调客户
            this.sendMessage(developerSendPhoneVo, developerSendDo);
        }
    }

    @Override
    public void saveMediaData(DeveloperSendVideoSaveDataVo developerSendVideoSaveDataVo) {
        DeveloperSendDo developerSendDo = this.lambdaQuery()
                .eq(DeveloperSendDo::getSmsId, developerSendVideoSaveDataVo.getMessageId())
                .eq(DeveloperSendDo::getPhone, developerSendVideoSaveDataVo.getPhoneNum())
                .one();
        if (Objects.isNull(developerSendDo)) {
            log.warn("saveMediaData error developerSendDo is null messageId {}", developerSendVideoSaveDataVo.getMessageId());
            return;
        }
        DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
        developerSendPhoneVo.setDeveloperSenId(developerSendDo.getCustomSmsId());
        developerSendPhoneVo.setPhone(developerSendDo.getPhone());
        switch (developerSendVideoSaveDataVo.getStatus()) {
            case 4:
                developerSendDo.setSendPlatformResult(0);
                developerSendDo.setCallResultMsg("发送成功！");
                developerSendDo.setCallbackPlatformResult(0);
                developerSendPhoneVo.setState(DeveloperSendStatus.DELIVRD.getValue());
                developerSendPhoneVo.setDesc(DeveloperSendStatus.DELIVRD.getAlias());
                break;
            case 5:
            case 6:
                developerSendDo.setSendPlatformResult(1);
                developerSendDo.setCallResultMsg("发送失败！");
                developerSendDo.setCallbackPlatformResult(1);
                developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
                developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
                break;
            default:
                log.error("status not support messageId {} , status {}", developerSendVideoSaveDataVo.getMessageId(), developerSendVideoSaveDataVo.getStatus());
                throw new BizException("status not support messageId");
        }

        this.updateById(developerSendDo);

        //回调客户
        if (Objects.nonNull(developerSendDo.getCallbackPlatformResult()) && developerSendDo.getCallbackPlatformResult() != 2) {
            VideoDeveloperCustomerDo videoDeveloperCustomerDo = videoDeveloperCustomerService.lambdaQuery()
                    .eq(VideoDeveloperCustomerDo::getAppId, developerSendDo.getAppId()).one();
            developerSendPhoneVo.setCallBackUrl(videoDeveloperCustomerDo.getCallbackUrl());

            SpringUtil.getBean(this.getClass()).sendMessageAsync(developerSendPhoneVo, developerSendDo);
        }
    }

    @Override
    public void save5gData(DeveloperSend5gSaveDataVo developerSend5gSaveDataVo) {
        DeveloperSendDo developerSendDo = this.lambdaQuery().eq(DeveloperSendDo::getSmsId, developerSend5gSaveDataVo.getMessageId())
                .eq(StringUtils.isNotBlank(developerSend5gSaveDataVo.getPhoneNum()), DeveloperSendDo::getPhone, developerSend5gSaveDataVo.getPhoneNum()).one();
        if (developerSendDo != null) {
            DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
            developerSendPhoneVo.setDeveloperSenId(developerSendDo.getCustomSmsId());
            developerSendPhoneVo.setPhone(developerSendDo.getPhone());
            DeveloperCustomer5gApplicationDo developerCustomer5gApplicationDo = developerCustomer5gApplicationService.lambdaQuery().eq(DeveloperCustomer5gApplicationDo::getAppId, developerSendDo.getAppId()).one();
            DeveloperCustomer5gDo developerCustomer5gDo = developerCustomer5gService.lambdaQuery().eq(DeveloperCustomer5gDo::getUniqueId, developerCustomer5gApplicationDo.getDeveloperCustomerUniqueId()).one();
            developerSendPhoneVo.setCallBackUrl(developerCustomer5gDo.getCallbackUrl());
            switch (developerSend5gSaveDataVo.getStatus()) {
                case 602:
                    developerSendDo.setCallbackPlatformResult(0);
                    developerSendDo.setSendPlatformResult(0);
                    developerSendDo.setCallResultMsg("发送成功！");
                    developerSendPhoneVo.setState(DeveloperSendStatus.DELIVRD.getValue());
                    developerSendPhoneVo.setDesc(DeveloperSendStatus.DELIVRD.getAlias());
                    break;
                case 603:
                    developerSendDo.setSendPlatformResult(1);
                    developerSendDo.setCallResultMsg("发送失败！");
                    developerSendDo.setCallbackPlatformResult(1);
                    developerSendPhoneVo.setState(DeveloperSendStatus.FAIL.getValue());
                    developerSendPhoneVo.setDesc(DeveloperSendStatus.FAIL.getAlias());
                    break;
                case 604:
                    developerSendDo.setSendPlatformResult(0);
                    developerSendDo.setCallResultMsg("发送成功！");
                    developerSendDo.setCallbackPlatformResult(3);
                    developerSendPhoneVo.setState(DeveloperSendStatus.DISPLAYED.getValue());
                    developerSendPhoneVo.setDesc(DeveloperSendStatus.DISPLAYED.getAlias());
                    break;
                case 605:
                    developerSendDo.setSendPlatformResult(0);
                    developerSendDo.setCallResultMsg("转短信发送成功！");
                    developerSendDo.setCallbackPlatformResult(4);
                    developerSendPhoneVo.setState(DeveloperSendStatus.DELIVERED_TO_NETWORK.getValue());
                    developerSendPhoneVo.setDesc(DeveloperSendStatus.DELIVERED_TO_NETWORK.getAlias());
                    break;
                default:
                    break;
            }
            this.saveOrUpdate(developerSendDo);

            //回调客户
            this.sendMessage(developerSendPhoneVo, developerSendDo);
        }
    }

    /**
     * 回调重试
     *
     * @param id
     */
    @Override
    public Boolean callBack(String id) {
        DeveloperSendDo developerSendDo = this.lambdaQuery().eq(DeveloperSendDo::getId, id).one();
        if (developerSendDo != null) {
            String callbackUrl = null;
            switch (developerSendDo.getAccountType()) {
                case 1:
                    DeveloperCustomer5gDo developerCustomer5gDo = developerCustomer5gService.lambdaQuery().eq(DeveloperCustomer5gDo::getCustomerId, developerSendDo.getCustomerId()).select(DeveloperCustomer5gDo::getCallbackUrl).one();
                    if (developerCustomer5gDo != null) {
                        callbackUrl = developerCustomer5gDo.getCallbackUrl();
                    }
                    break;
                case 2:
                    VideoDeveloperCustomerDo videoDeveloperCustomerDo = videoDeveloperCustomerService.lambdaQuery().eq(VideoDeveloperCustomerDo::getAppId, developerSendDo.getAppId()).select(VideoDeveloperCustomerDo::getCallbackUrl).one();
                    if (videoDeveloperCustomerDo != null) {
                        callbackUrl = videoDeveloperCustomerDo.getCallbackUrl();
                    }
                    break;
                case 3:

                    SmsDeveloperCustomerDo smsDeveloperCustomerDo = smsDeveloperCustomerService.lambdaQuery().eq(SmsDeveloperCustomerDo::getAppId, developerSendDo.getAppId()).select(SmsDeveloperCustomerDo::getCallbackUrl).one();
                    if (smsDeveloperCustomerDo != null) {
                        callbackUrl = smsDeveloperCustomerDo.getCallbackUrl();
                    }
                    break;
                default:
                    break;
            }

            DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
            developerSendPhoneVo.setDeveloperSenId(developerSendDo.getCustomSmsId());
            developerSendPhoneVo.setPhone(developerSendDo.getPhone());
            developerSendPhoneVo.setState(developerSendDo.getCallbackPlatformResult());
            developerSendPhoneVo.setDesc(developerSendDo.getCallResultMsg());
            developerSendPhoneVo.setCallBackUrl(callbackUrl);
            return this.sendMessage(developerSendPhoneVo, developerSendDo);
        } else {
            throw new BizException(500, "id错误");
        }
    }

    @Override
    public Boolean isDeveloperMessage(String messageId) {
        return this.lambdaQuery()
                .eq(DeveloperSendDo::getSmsId, messageId)
                .exists();
    }

    @Override
    public Long findSendRecord(String oldMessageId, String customerId) {
        LambdaQueryWrapper<DeveloperSendDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeveloperSendDo::getSmsId, oldMessageId)
                .eq(DeveloperSendDo::getCustomerId, customerId);

        DeveloperSendDo developerSendDo = getBaseMapper().selectOne(queryWrapper);
        String applicationUniqueId = developerSendDo.getApplicationUniqueId();
        //通过uniqueId查找主键Id
        LambdaQueryWrapper<DeveloperCustomer5gApplicationDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeveloperCustomer5gApplicationDo::getUniqueId, applicationUniqueId);

        DeveloperCustomer5gApplicationDo developerCustomer5gApplicationDo = developerCustomer5gApplicationMapper.selectOne(wrapper);
        return developerCustomer5gApplicationDo.getId();
    }

    @Async
    public void sendMessageAsync(DeveloperSendPhoneVo var1, DeveloperSendDo var2) {
        sendMessage(var1, var2);
    }

    /**
     * 发送回调消息
     *
     * @param developerSendPhoneVo
     * @return
     */

    public Boolean sendMessage(DeveloperSendPhoneVo developerSendPhoneVo, DeveloperSendDo developerSendDo) {
        if (StringUtils.isEmpty(developerSendPhoneVo.getCallBackUrl())) {
            developerSendDo.setCallbackResult(2);
            this.saveOrUpdate(developerSendDo);
            return false;
        }
        String callbackUrl = developerSendPhoneVo.getCallBackUrl();
        developerSendDo.setCallbackTime(LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        developerSendPhoneVo.setCallBackUrl(null);
        String s = JsonUtils.obj2String(developerSendPhoneVo);
        JSONObject jsonObject = JSONObject.parseObject(s);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, jsonObject, String.class);
            String body = response.getBody();
            JSONObject bodyObject = JSON.parseObject(body);
            Integer callbackCode = bodyObject.getInteger("code");
            if (callbackCode != 200) {
                developerSendDo.setCallbackResult(1);
                this.saveOrUpdate(developerSendDo);
                return false;
            }
        } catch (Exception e) {
            log.error("回调客户失败", e);
            developerSendDo.setCallbackResult(1);
            this.saveOrUpdate(developerSendDo);
            return false;
        }
        developerSendDo.setCallbackResult(0);
        this.saveOrUpdate(developerSendDo);
        return true;
    }
}
