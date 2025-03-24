package com.citc.nce.im.shortMsg;

import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.developer.DeveloperSendApi;
import com.citc.nce.developer.vo.DeveloperSendSaveDataVo;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import com.citc.nce.im.broadcast.properties.SmsProperties;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.sms.AES;
import com.citc.nce.robot.sms.BalanceRequest;
import com.citc.nce.robot.sms.BalanceResponse;
import com.citc.nce.robot.sms.GZIPUtils;
import com.citc.nce.robot.sms.HttpClient;
import com.citc.nce.robot.sms.HttpRequest;
import com.citc.nce.robot.sms.HttpRequestBytes;
import com.citc.nce.robot.sms.HttpRequestParams;
import com.citc.nce.robot.sms.HttpResponseBytes;
import com.citc.nce.robot.sms.HttpResponseBytesPraser;
import com.citc.nce.robot.sms.HttpResultCode;
import com.citc.nce.robot.sms.HttpsRequestBytes;
import com.citc.nce.robot.sms.JsonHelper;
import com.citc.nce.robot.sms.ReportResponse;
import com.citc.nce.robot.sms.ResultModel;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.SmsBalanceReq;
import com.citc.nce.robot.vo.SmsCreateTemplateResponse;
import com.citc.nce.robot.vo.SmsMessageResponse;
import com.citc.nce.robot.vo.SmsResponse;
import com.citc.nce.robot.vo.SmsTemplateAuditStatus;
import com.citc.nce.robot.vo.SmsTemplateReq;
import com.citc.nce.robot.vo.SmsTemplateSendNormalReq;
import com.citc.nce.robot.vo.SmsTemplateSendVariableReq;
import com.citc.nce.robot.vo.TemplateSmsSendRequest;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgIdMappingVo;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.citc.nce.tenant.vo.req.UpdateByPhoneAndMessageIdReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * 8.0富媒体平台
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortMsgPlatformService {
    private final SmsProperties smsProperties;
    private final String SUCCESS = "SUCCESS";
    private final MsgRecordApi msgRecordApi;
    private final DeveloperSendApi developerSendApi;
    private final RobotGroupSendPlansDetailService robotGroupSendPlansDetailService;
    private final ObjectMapper objectMapper;
    private final PrepaymentApi prepaymentApi;
    private final CspCustomerApi cspCustomerApi;
    private final FastGroupMessageService fastGroupMessageService;
    private final DeductionAndRefundApi deductionAndRefundApi;
    private final SmsTemplateApi smsTemplateApi;
    private final RedisService redisService;

    /**
     * 送审短信模板
     *
     * @return 平台模板ID
     */
    public String reportTemplate(SmsTemplateReq smsTemplateReq) {
        log.info("=============begin createTemplateSMS==================");
        String platformTemplateId = null;
        ResultModel result = request(
                smsTemplateReq.getAppId(),
                smsTemplateReq.getSecretKey(),
                smsTemplateReq.getAlgorithm(),
                smsTemplateReq.getTemplateMap(),
                smsProperties.getHost() + smsProperties.getTemplateReportUrl(),
                smsTemplateReq.isGzip(),
                smsTemplateReq.getEncode()
        );
        log.info("result:{}", result);
        if (SUCCESS.equals(result.getCode())) {
            SmsCreateTemplateResponse smsCreateTemplateResponse = JsonHelper.fromJson(SmsCreateTemplateResponse.class, result.getResult());
            platformTemplateId = smsCreateTemplateResponse.getTemplateId();
        }
        log.info("=============end createTemplateSMS==================");
        return platformTemplateId;
    }

    /**
     * 查询模板送审状态
     *
     * @return 模板送审状态
     */
    public SmsTemplateAuditStatus queryTemplateStatus(SmsTemplateReq smsTemplateReq) {
        log.info("=============begin queryTemplate==================");
        SmsTemplateAuditStatus smsTemplateAuditStatus = null;
        ResultModel result = request(
                smsTemplateReq.getAppId(),
                smsTemplateReq.getSecretKey(),
                smsTemplateReq.getAlgorithm(),
                smsTemplateReq.getTemplateMap(),
                smsProperties.getHost() + smsProperties.getQueryTemplateAuditStatusUrl(),
                smsTemplateReq.isGzip(),
                smsTemplateReq.getEncode()
        );
        log.info("result:{}", result);
        if (SUCCESS.equals(result.getCode())) {
            log.info(result.getResult());
            smsTemplateAuditStatus = JsonHelper.fromJson(SmsTemplateAuditStatus.class, result.getResult());
        }
        log.info("=============end queryTemplate==================");
        return smsTemplateAuditStatus;
    }


    /**
     * 发送个性模板短信
     *
     * @param smsTemplateSendVariableReq
     * @return
     */
    public SmsMessageResponse sendVariableSms(SmsTemplateSendVariableReq smsTemplateSendVariableReq) {
        log.info("=============begin sendTemplateVariableSms==================");
        TemplateSmsSendRequest req = new TemplateSmsSendRequest();
        req.setSmses(smsTemplateSendVariableReq.getCustomSmsIdAndMobiles());
        req.setTemplateId(smsTemplateSendVariableReq.getPlatformTemplateId());
        req.setRequestTime(System.currentTimeMillis());
        ResultModel result = request(
                smsTemplateSendVariableReq.getAppId(),
                smsTemplateSendVariableReq.getSecretKey(),
                smsTemplateSendVariableReq.getAlgorithm(),
                req,
                smsProperties.getHost() + smsProperties.getVariableSmsUrl(),
                smsTemplateSendVariableReq.isGzip(),
                smsTemplateSendVariableReq.getEncode()
        );
        log.info("result:{}", result);
        SmsMessageResponse resp = new SmsMessageResponse();
        resp.setSuccess(SUCCESS.equals(result.getCode()));
        if (resp.isSuccess()) {
            try {
                List<SmsResponse> data = objectMapper.readerForListOf(SmsResponse.class).readValue(result.getResult());
                resp.setData(data);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new BizException("读取sms调用结果失败");
            }
        }
        log.info("=============end sendTemplateVariableSms==================");
        return resp;
    }


    /**
     * 发送普通模板短信
     *
     * @param smsTemplateSendNormalReq
     */
    public SmsMessageResponse sendNormalSms(SmsTemplateSendNormalReq smsTemplateSendNormalReq) {
        log.info("=============begin sendTemplateNormalSms==================");
        log.info("smsTemplateSendNormalReq : PlatformTemplateId{}, TemplateId{}", smsTemplateSendNormalReq.getPlatformTemplateId(), smsTemplateSendNormalReq.getTemplateId());
        TemplateSmsSendRequest req = new TemplateSmsSendRequest();
        req.setSmses(smsTemplateSendNormalReq.getCustomSmsIdAndMobiles());
        req.setTemplateId(smsTemplateSendNormalReq.getPlatformTemplateId());
        req.setRequestTime(System.currentTimeMillis());
        log.info("TemplateSmsSendRequest : {}", JsonHelper.toJsonString(req));
        ResultModel result = request(
                smsTemplateSendNormalReq.getAppId(),
                smsTemplateSendNormalReq.getSecretKey(),
                smsTemplateSendNormalReq.getAlgorithm(),
                req,
                smsProperties.getHost() + smsProperties.getNormalSmsUrl(),
                smsTemplateSendNormalReq.isGzip(),
                smsTemplateSendNormalReq.getEncode()
        );
        log.info("result:{}", result);
        SmsMessageResponse resp = new SmsMessageResponse();
        resp.setSuccess(SUCCESS.equals(result.getCode()));
        if (resp.isSuccess()) {
            try {
                List<SmsResponse> data = objectMapper.readerForListOf(SmsResponse.class).readValue(result.getResult());
                resp.setData(data);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new BizException("读取sms调用结果失败");
            }
        }
        log.info("=============end sendTemplateNormalSms==================");
        return resp;
    }

    /**
     * 查询短信余额
     *
     * @return 短信余额
     */
    public Long queryBalance(SmsBalanceReq smsBalanceReq) {
        Long balance = null;
        log.info("=============begin getBalance==================");
        BalanceRequest balanceRequest = new BalanceRequest();
        ResultModel result = request(
                smsBalanceReq.getAppId(),
                smsBalanceReq.getSecretKey(),
                smsBalanceReq.getAlgorithm(),
                balanceRequest,
                smsProperties.getHost() + smsProperties.getQueryBalanceUrl(),
                smsBalanceReq.isGzip(),
                smsBalanceReq.getEncode()
        );
        log.info("result:{}", result);
        if (SUCCESS.equals(result.getCode())) {
            BalanceResponse response = JsonHelper.fromJson(BalanceResponse.class, result.getResult());
            if (response != null) {
                balance = response.getBalance();
            }
        }
        log.info("=============end getBalance==================");
        return balance;
    }

    public void handleSendResultCallback(List<ReportResponse> reports) {
        for (ReportResponse report : reports) {
            MsgRecordVo msgRecordVoUpdate = new MsgRecordVo();
            String messageId = report.getSmsId();
            String status = report.getState();
            String phoneNum = report.getMobile();
            int updatedRecordNumber = 0; //更新发送明细的数量（可能是一条一条更新的，也可能是整个messageId的一个批次一起更新的）

            while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId))) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            //检查消息状态是否已处理过
            MsgIdMappingVo msgIdMappingVo = msgRecordApi.queryMsgMapping(messageId);
            //查询发送记录
            MsgRecordVo msgRecordVo = msgRecordApi.selectByPhoneAndMessageId(MsgTypeEnum.SHORT_MSG.getCode(), phoneNum, messageId, msgIdMappingVo.getCustomerId());
            if (msgRecordVo != null && msgRecordVo.getSendResult() == DeliveryEnum.UN_KNOW.getCode()) {
                if (Objects.equals(status, "DELIVRD")) {//发送成功
                    //更新消息
                    msgRecordVoUpdate.setSendResult(DeliveryEnum.DELIVERED.getCode()).setReceiptTime(new Date());
                    msgRecordVoUpdate.setFinalResult(RequestEnum.SUCCESS.getCode());
                    updatedRecordNumber = updateMsgRecordInfo(phoneNum, messageId, msgRecordVoUpdate, MsgTypeEnum.SHORT_MSG.getCode(), msgRecordVo.getCreator());
                    //处理余额扣费记录中的数据
                    confirmDelivered(msgRecordVo);
                    //更新发送计划统计详细信息
                    updateMassSendNodeStatisticInfo(messageId, updatedRecordNumber, "SUCCESS", msgRecordVo.getCreator());
                    updateFastGroupMessageStatistic(messageId,updatedRecordNumber,DeliveryEnum.DELIVERED);
                } else {
                    //发送失败返还余额
                    UserInfoVo userInfo = cspCustomerApi.getByCustomerId(msgRecordVo.getCreator());
                    if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordVo.getConsumeCategory())) {
                        returnBalance(msgRecordVo);
                    } else if (Objects.equals(PaymentTypeEnum.SET_MEAL.getCode(), msgRecordVo.getConsumeCategory())) {
                        if (userInfo.getPayType() == CustomerPayType.PREPAY) {
                            // 查询短信发送条数
                            SmsTemplateDetailVo template = smsTemplateApi.getTemplateInfoInner(msgRecordVo.getTemplateId(), false);
                            Integer smsSendNum = BroadcastPlanUtils.getContentLength(template);
                            prepaymentApi.returnRemaining(msgRecordVo.getAccountId(), MsgTypeEnum.SHORT_MSG, null, (long) smsSendNum);
                        }
                    }

                    //处理余额扣费记录中的数据
                    //失败号码处理策略
                    msgRecordVoUpdate.setSendResult(DeliveryEnum.FAILED.getCode()).setReceiptTime(new Date());
                    msgRecordVoUpdate.setFinalResult(RequestEnum.FAILED.getCode());
                    updatedRecordNumber = updateMsgRecordInfo(phoneNum, messageId, msgRecordVoUpdate, MsgTypeEnum.SHORT_MSG.getCode(), msgRecordVo.getCreator());
                    //更新发送计划统计详细信息
                    updateMassSendNodeStatisticInfo(messageId, updatedRecordNumber, "FAIL", msgRecordVo.getCreator());
                    updateFastGroupMessageStatistic(messageId,updatedRecordNumber,DeliveryEnum.FAILED);

                }
            }
            //存储开发者数据
            DeveloperSendSaveDataVo developerSendSaveDataVo = new DeveloperSendSaveDataVo();
            developerSendSaveDataVo.setMessageId(messageId);
            developerSendSaveDataVo.setStatus(status);
            developerSendSaveDataVo.setPhoneNum(phoneNum);
            developerSendApi.saveData(developerSendSaveDataVo);
        }

    }


    private void confirmDelivered(MsgRecordVo msgRecordVo) {
        String platformMessageId = msgRecordVo.getMessageId();
        String customerId = msgRecordVo.getCreator();
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordVo.getConsumeCategory())) {
            String localMessageId = msgRecordApi.queryMsgIdMapping(platformMessageId);
            if (Objects.isNull(localMessageId)) {
                log.error("未找到对应的msgIdMapping记录, msgRecordVo:{}", msgRecordVo);
                return;
            }
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(TariffTypeEnum.SMS.getCode())
                    .phoneNumber(msgRecordVo.getPhoneNum())
                    .build());
        }
    }

    private void returnBalance(MsgRecordVo msgRecordVo) {
        String platformMessageId = msgRecordVo.getMessageId();
        String customerId = msgRecordVo.getCreator();
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordVo.getConsumeCategory())) {
            String localMessageId = msgRecordApi.queryMsgIdMapping(platformMessageId);
            if (Objects.isNull(localMessageId)) {
                log.error("未找到对应的msgIdMapping记录, msgRecordVo:{}", msgRecordVo);
                return;
            }
            deductionAndRefundApi.returnBalance(ReturnBalanceReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(TariffTypeEnum.SMS.getCode())
                    .phoneNumber(msgRecordVo.getPhoneNum())
                    .build());
        }
    }

    private Integer updateMsgRecordInfo(String phone, String messageId, MsgRecordVo msgRecordVoUpdate, int msgType, String customerId) {
        if (StringUtils.isNotEmpty(messageId)) {
            if (MsgTypeEnum.M5G_MSG.getCode() == msgType && phone != null && phone.length() > 13) {
                phone = phone.substring(7);
            }
            UpdateByPhoneAndMessageIdReq req = new UpdateByPhoneAndMessageIdReq();
            req.setMsgType(msgType);
            req.setPhoneNum(phone);
            req.setMessageId(messageId);
            req.setMsgRecordVoUpdate(msgRecordVoUpdate);
            req.setCustomerId(customerId);
            return msgRecordApi.updateByPhoneAndMessageId(req);
        }
        return 0;
    }

    /**
     * 更新快捷群发统计信息
     */
    private void updateFastGroupMessageStatistic(String messageId, Integer updatedRecordNumber, DeliveryEnum state) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber != null && updatedRecordNumber > 0) {
            fastGroupMessageService.handleSendStatistic(messageId, updatedRecordNumber,state);
        }
    }

    private void updateMassSendNodeStatisticInfo(String messageId, Integer updatedRecordNumber, String result, String customerId) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber != null && updatedRecordNumber > 0) {
            RobotGroupSendPlansDetailReq sendPlansDetailUpdate = queryMassSendNodeInfo(messageId, customerId);
            if (sendPlansDetailUpdate != null && sendPlansDetailUpdate.getUnknowAmount() != null && sendPlansDetailUpdate.getUnknowAmount() > 0) {
                if ("FAIL".equals(result)) {//更新失败和成功的数据
                    sendPlansDetailUpdate.setFailAmount(sendPlansDetailUpdate.getFailAmount() + updatedRecordNumber);
                } else {
                    sendPlansDetailUpdate.setSuccessAmount(sendPlansDetailUpdate.getSuccessAmount() + updatedRecordNumber);
                }
                //更新未知的数据
                sendPlansDetailUpdate.setUnknowAmount(sendPlansDetailUpdate.getUnknowAmount() - updatedRecordNumber);
                robotGroupSendPlansDetailService.updateNodeDetail(sendPlansDetailUpdate);
            }
        }
    }

    private RobotGroupSendPlansDetailReq queryMassSendNodeInfo(String messageId, String customerId) {
        RobotGroupSendPlansDetailReq groupSendPlansDetailUpdate = null;
        Long planDetailId = msgRecordApi.queryNodeIdByMessageId(messageId, customerId);
        if (planDetailId != null) {
            RobotGroupSendPlansDetail robotGroupSendPlansDetail = robotGroupSendPlansDetailService.queryById(planDetailId);
            groupSendPlansDetailUpdate = new RobotGroupSendPlansDetailReq();
            groupSendPlansDetailUpdate.setId(planDetailId);
            groupSendPlansDetailUpdate.setUnknowAmount(robotGroupSendPlansDetail.getUnknowAmount());
            groupSendPlansDetailUpdate.setFailAmount(robotGroupSendPlansDetail.getFailAmount());
            groupSendPlansDetailUpdate.setSuccessAmount(robotGroupSendPlansDetail.getSuccessAmount());
            groupSendPlansDetailUpdate.setSendAmount(robotGroupSendPlansDetail.getSendAmount());
        }
        return groupSendPlansDetailUpdate;
    }


    private ResultModel request(String appId, String secretKey, String algorithm, Object content, String url, Boolean gzip, String encode) {
        gzip = Optional.ofNullable(gzip).orElse(smsProperties.getGzip());
        Map<String, String> headers = new HashMap<String, String>();
        HttpRequest<byte[]> request;
        HttpClient client = new HttpClient();
        String code = null;
        String result = null;
        try {
            headers.put("appId", appId);
            headers.put("encode", encode);
            String requestJson = JsonHelper.toJsonString(content);
            byte[] bytes = requestJson.getBytes(encode);
            if (gzip) {
                headers.put("gzip", "on");
                bytes = GZIPUtils.compress(bytes);
            }
            byte[] parambytes = AES.encrypt(bytes, secretKey.getBytes(), algorithm);

            HttpRequestParams<byte[]> params = new HttpRequestParams<byte[]>();
            params.setCharSet("UTF-8");
            params.setMethod("POST");
            params.setHeaders(headers);
            params.setParams(parambytes);
            params.setUrl(url);
            if (url.startsWith("https://")) {
                request = new HttpsRequestBytes(params, null);
            } else {
                request = new HttpRequestBytes(params);
            }
        } catch (Exception e) {
            log.info("加密异常");
            return new ResultModel(code, result);
        }
        try {
            HttpResponseBytes res = client.service(request, new HttpResponseBytesPraser());
            if (res == null) {
                log.info("请求接口异常");
                return new ResultModel(code, result);
            }
            if (res.getResultCode().equals(HttpResultCode.SUCCESS)) {
                if (res.getHttpCode() == 200) {
                    code = res.getHeaders().get("result");
                    if (code.equals(SUCCESS)) {
                        byte[] data = res.getResult();
                        data = AES.decrypt(data, secretKey.getBytes(), algorithm);
                        if (gzip) {
                            data = GZIPUtils.decompress(data);
                        }
                        result = new String(data, encode);
                        log.info("response json: " + result);
                    }
                } else {
                    log.info("请求接口异常,请求码:{}", res.getHttpCode());
                }
            } else {
                log.info("请求接口网络异常:{}", res.getResultCode().getCode());
            }
        } catch (Exception e) {
            log.info("解析失败");
            return new ResultModel(code, result);
        }
        return new ResultModel(code, result);
    }

}
