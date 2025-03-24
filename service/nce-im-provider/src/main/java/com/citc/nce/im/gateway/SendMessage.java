package com.citc.nce.im.gateway;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.cardstyle.CardStyleApi;
import com.citc.nce.auth.cardstyle.vo.CardStyleOneReq;
import com.citc.nce.auth.cardstyle.vo.CardStyleResp;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.dto.FileTidReq;
import com.citc.nce.dto.PictureReq;
import com.citc.nce.dto.VideoReq;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.im.robot.util.MsgUtils;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.im.config.LayoutStyleUtil;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.im.util.ChannelTypeUtil;
import com.citc.nce.im.util.MyFileUtil;
import com.citc.nce.robot.RobotAccountApi;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.TemporaryStatisticsApi;
import com.citc.nce.robot.constant.MessagePaymentTypeConstant;
import com.citc.nce.robot.vo.BaseMessage;
import com.citc.nce.robot.vo.CardObject;
import com.citc.nce.robot.vo.FileIdResp;
import com.citc.nce.robot.vo.FileMessage;
import com.citc.nce.robot.vo.FileObject;
import com.citc.nce.robot.vo.FontdoFileMessage;
import com.citc.nce.robot.vo.Layout;
import com.citc.nce.robot.vo.Media;
import com.citc.nce.robot.vo.RobotAccountPageReq;
import com.citc.nce.robot.vo.RobotAccountReq;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.citc.nce.robot.vo.RobotRecordReq;
import com.citc.nce.robot.vo.Suggestions;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;
import com.citc.nce.robot.vo.TextObject;
import com.citc.nce.robot.vo.directcustomer.Parameter;
import com.citc.nce.vo.PictureResp;
import com.citc.nce.vo.VideoResp;
import com.github.pagehelper.util.StringUtil;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SendMessage {
    private static Logger log = LoggerFactory.getLogger(SendMessage.class);
    @Resource
    AccountManagementApi accountManagementApi;
    @Resource
    DeductionAndRefundApi deductionAndRefundApi;
    @Resource
    PlatformApi platformApi;
    @Resource
    RedisTemplate<Object, Object> redisTemplate;

    //获取表单url
    @Value("${form.shareUrl}")
    private String formShareUrl;

    @Resource
    CardStyleApi cardStyleApi;

    @Resource
    FileApi fileApi;
    @Resource
    private FifthSendClient fifthSendClient;

    @Resource
    TemporaryStatisticsApi temporaryStatisticsApi;
    @Resource
    VideoApi videoApi;
    @Resource
    PictureApi pictureApi;

    private String messageReplyWord = "请问，下方哪个选项更符合您的需要?";
    @Resource
    RobotRecordApi robotRecordApi;
    @Resource
    RobotAccountApi robotAccountApi;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    private PrepaymentApi prepaymentApi;
    @Resource
    private RechargeTariffApi rechargeTariffApi;


    private static final long OVER_TIME = 30;


    public void send(SendParams sendParams) {
        log.info("第三步    准备拼装");
        Object object = sendParams.getContent();
        JSONArray jsonArray = new JSONArray();
        ObjectUtil.isEmpty(sendParams.getButtonList());
        //全局按钮
        List<Suggestions> suggestionList = new ArrayList<>();
        HashMap<String, String> buttonIdMap = new HashMap<>();
        JSONArray button = new JSONArray();
        try {
            log.info("进入数据整理 {} 是否提问环节{}", object, sendParams.getQuestion());
            log.info("sendParams.getButtonList(): {}", sendParams.getButtonList());
            if (sendParams.getPocketBottom()) {
                JSONObject jsonObject = JsonUtils.string2Obj(JsonUtils.obj2String(object), JSONObject.class);
                button = jsonObject.getJSONArray("buttonList");
                log.info("分支1 button: {}", button);
                suggestionList.addAll(buildButtonPocketBottom(button, buttonIdMap));
                JSONObject msg = jsonObject.getJSONObject("msg");
                /**
                 * 默认回复为空，但是有按钮也要发送消息
                 */
                jsonArray.add(msg);
            } else {
                if (sendParams.getQuestion()) {
                    JSONArray array = JsonUtils.string2Obj(JsonUtils.obj2String(object), JSONArray.class);
                    for (Object tmp : array) {
                        JSONObject jsonObject = (JSONObject) tmp;
                        button = jsonObject.getJSONArray("buttonList");

                        /**
                         * todo
                         * 临时修复 流程中通过手机发送消息，按钮不见的bug
                         * 后期统一消息标准时修改
                         * @createdTime 2023/2/7 17:28
                         */

                        log.info("分支2 button: {}", button);
                        jsonObject.put("buttonList", button);
                        suggestionList.addAll(buildButton(jsonObject.getJSONArray("buttonList"), buttonIdMap));
                        jsonArray.add(jsonObject.getJSONObject("msg"));
                    }
                } else {
                    JSONObject jsonObject = JsonUtils.string2Obj(JsonUtils.obj2String(object), JSONObject.class);
                    button = jsonObject.getJSONArray("buttonList");
                    log.info("分支3 jsonObject: {}", jsonObject);
                    suggestionList.addAll(buildButton(jsonObject.getJSONArray("buttonList"), buttonIdMap));
                    jsonArray = jsonObject.getJSONArray("contentBody");
                }
            }

        } catch (Exception e) {
            log.error("数据整理异常：", e);
        }

        if (jsonArray.size() > 0) {
            log.info("数据整理后端的JSON数组 {} ", jsonArray);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject content = jsonArray.getJSONObject(i);
                //判断发送的内容类型  type 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
                String type = JsonPath.read(content, "$.type").toString();
                switch (type) {
                    case "1":
                        sendText(sendParams, content, suggestionList, button);
                        break;
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                        sendFile(sendParams, content, suggestionList, button);
                        break;
                    case "6":
                    case "7":
                        sendCard(sendParams, content, suggestionList, buttonIdMap, button);
                        break;
                    case "8":
                        sendLocation(sendParams, content, suggestionList, button);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 一个关键词触发多个流程
     *
     * @param sendParams
     * @return none
     * @author zy.qiu
     * @createdTime 2023/2/10 11:01
     */
    public void sendMessageWhenMultipleProcess(SendParams sendParams) {
        log.info("关键词触发多个流程，准备发送消息 sendParams {}", sendParams);
        FileMessage fileMessage = new FileMessage();
        fileMessage.setConversationId(sendParams.getConversationId());
        fileMessage.setContributionId(sendParams.getContributionId());
        List<String> phoneNumList = new ArrayList<>();
        phoneNumList.add(sendParams.getPhoneNum());
        fileMessage.setDestinationAddress(phoneNumList);
        JSONObject body = JSONObject.parseObject(JsonUtils.obj2String(sendParams.getContent()), JSONObject.class);
        JSONArray button = JSONArray.parseArray(JsonUtils.obj2String(body.get("button")));
        // 处理按钮
        List<Suggestions> suggestions = new ArrayList<>();
        for (Object o : button) {
            JSONObject jo = (JSONObject) o;
            String uuid = jo.get("uuid").toString();
            String processId = jo.get("processId").toString();
            JSONObject buttonDetail = (JSONObject) jo.get("buttonDetail");
            JSONObject input = (JSONObject) buttonDetail.get("input");
            String sceneName = input.get("value").toString();

            Suggestions suggestion = new Suggestions();
            suggestion.setDisplayText(sceneName);
            suggestion.setPostbackData(uuid);
            suggestion.setType("reply");
            suggestions.add(suggestion);
        }

        // 消息内容
        TextObject textObject = new TextObject();
        // 消息文本内容
        textObject.setText(messageReplyWord);
        // 消息携带的按钮
        textObject.setSuggestions(suggestions);
        fileMessage.setMessageType("text");
        fileMessage.setContent(textObject);

        // 用于保存记录，不参与发送
        MessageTemplateResp template = new MessageTemplateResp();
        template.setMessageType(1);
        template.setTemplateName(getMessageType(1));
        String suggestionsLists = JSONArray.toJSONString(suggestions);
        template.setModuleInformation(suggestionsLists);

        // 发送消息
        send(fileMessage, sendParams.getAccount(), template, MsgSubTypeEnum.TEXT);
    }

    private void sendLocation(SendParams sendParams, JSONObject jsonObjects, List<Suggestions> suggestionsList, JSONArray button) {
        JSONObject jsonObject = jsonObjects.getJSONObject("messageDetail");
        TextObject textObject = new TextObject();
        FileMessage fileMessage = new FileMessage();
        fileMessage.setConversationId(sendParams.getConversationId());
        fileMessage.setContributionId(sendParams.getContributionId());
        List<String> phoneNumList = new ArrayList<>();
        phoneNumList.add(sendParams.getPhoneNum());
        fileMessage.setDestinationAddress(phoneNumList);
        String locationDetail = jsonObject.getJSONObject("locationDetail").getString("value");
        String latitude = jsonObject.getJSONObject("baidu").getString("lat");
        String longitude = jsonObject.getJSONObject("baidu").getString("lng");
        String location = "geo:" + latitude + "," + longitude + ";crs=gcj02;u=10;rcs-l=" + locationDetail;
        textObject.setText(location);
        //发送消息
        MessageTemplateResp template = new MessageTemplateResp();
        if (!CollectionUtil.isEmpty(suggestionsList)) {
            textObject.setSuggestions(suggestionsList);
            String suggestionsLists = JSONArray.toJSONString(button);
            template.setShortcutButton(suggestionsLists);
        }
        fileMessage.setMessageType("text");
        fileMessage.setContent(textObject);

        template.setMessageType(jsonObjects.getInteger("type"));
        template.setModuleInformation(jsonObject.toJSONString());
        template.setTemplateName(getMessageType(jsonObjects.getInteger("type")));
        send(fileMessage, sendParams.getAccount(), template, MsgSubTypeEnum.TEXT);
    }

    private void sendCard(SendParams sendParams, JSONObject content, List<Suggestions> suggestionsList, Map<String, String> buttonIdMap, JSONArray button) {
        MessageTemplateResp template = new MessageTemplateResp();
        JSONObject jsonObject = content.getJSONObject("messageDetail");
        template.setMessageType(content.getInteger("type"));
        template.setModuleInformation(jsonObject.toJSONString());
        template.setTemplateName(getMessageType(content.getInteger("type")));
        String operator = sendParams.getAccountType();
        FileMessage fileMessage = new FileMessage();
        fileMessage.setConversationId(sendParams.getConversationId());
        fileMessage.setContributionId(sendParams.getContributionId());
        List<String> phoneNumList = new ArrayList<>();
        phoneNumList.add(sendParams.getPhoneNum());
        fileMessage.setDestinationAddress(phoneNumList);
        CardObject cardObject = new CardObject();
        /**
         * 范艺钢反馈BUG  提问节点 解析问题
         * @author zy.qiu
         * @createdTime 2023/2/9 19:09
         */
        Long styleId = jsonObject.getLong("style");
        //根据文件id上传到平台
        String fileUuid = "";
        if (ObjectUtil.isNotEmpty(styleId)) {
            CardStyleOneReq cardReq = new CardStyleOneReq();
            cardReq.setId(styleId);
            CardStyleResp cardStyleResp = cardStyleApi.getCardStyleByIdInner(cardReq);
            if (cardStyleResp != null) {
                fileUuid = cardStyleResp.getFileId();
                log.info("样式文件id为{}", fileUuid);
                UploadReq uploadReq = new UploadReq();
                uploadReq.setFile(MyFileUtil.getMultipartFile(getFile(fileUuid)));
                uploadReq.setList(operator);
                uploadReq.setCreator(sendParams.getUserId());
                uploadReq.setSceneId("codeincodeservice");
                UpReceiveData upReceiveData = fileApi.upToConsumer(uploadReq);
                log.info("网关上传后的返回结果为{}", upReceiveData);
                if (upReceiveData.getCode() != 200) {
                    throw new BizException(SendGroupExp.FILE_UPLOAD_ERROR);
                }
            }
        }

        List<Media> medias = new ArrayList<>();
        JSONArray cardList = jsonObject.getJSONArray("cardList");
        //通过cardList判断是单卡还是多卡
        if (CollectionUtil.isEmpty(cardList)) {
            //单卡
            medias = getMedias(jsonObject, operator, buttonIdMap);
            //横向还是纵向
            String orientation = jsonObject.getString("layout");
            Layout layout = new Layout();
            if (StringUtils.equals(orientation, "transverse")) {
                //横向
                layout.setCardOrientation("HORIZONTAL");
                //设置媒体左右
                layout.setImageAlignment(jsonObject.getString("position").toUpperCase());
            } else {
                //纵向
                layout.setCardOrientation("VERTICAL");
            }
            if (StringUtils.isNotEmpty(fileUuid)) {
                layout.setStyle(LayoutStyleUtil.appendPrefix(fileUuid));
            }
            cardObject.setLayout(layout);
            if (medias.size() == 1) {
                setCardHeight(jsonObject, medias.get(0));
            }
        } else {
            //多卡
            for (int i = 0; i < cardList.size(); i++) {
                List<Media> list = getMedias(cardList.getJSONObject(i), operator, buttonIdMap);
                list.forEach(media -> {
                    setCardHeight(jsonObject, media);
                });
                medias.addAll(list);
                Layout layout = new Layout();
                String width = jsonObject.getString("width");
                setCardWidth(layout, width);
                if (StringUtils.isNotEmpty(fileUuid)) {
                    layout.setStyle(LayoutStyleUtil.appendPrefix(fileUuid));
                }
                cardObject.setLayout(layout);
            }
        }
        cardObject.setMedia(medias);
        if (!CollectionUtil.isEmpty(suggestionsList)) {
            cardObject.setSuggestions(suggestionsList);
            String suggestionsLists = JSONArray.toJSONString(button);
            template.setShortcutButton(suggestionsLists);
        }
        fileMessage.setMessageType("card");
        fileMessage.setContent(cardObject);
        //发送消息
        send(fileMessage, sendParams.getAccount(), template, MsgSubTypeEnum.RICH);
    }

    private void sendFile(SendParams sendParams, JSONObject jsonObject, List<Suggestions> suggestionsList, JSONArray button) {
        try {
            MessageTemplateResp template = new MessageTemplateResp();
            JSONObject jsonObjects = jsonObject.getJSONObject("messageDetail");
            template.setMessageType(jsonObject.getInteger("type"));
            template.setModuleInformation(jsonObjects.toJSONString());
            template.setTemplateName(getMessageType(jsonObject.getInteger("type")));
            FileMessage fileMessage = new FileMessage();
            fileMessage.setConversationId(sendParams.getConversationId());
            fileMessage.setContributionId(sendParams.getContributionId());
            List<String> phoneNumList = new ArrayList<>();
            phoneNumList.add(sendParams.getPhoneNum());
            fileMessage.setDestinationAddress(phoneNumList);
            fileMessage.setMessageType("file");
            FileObject fileObject = new FileObject();
            FileIdResp fileIdResp = getFileTid(jsonObjects, sendParams.getAccountType());
            if (StringUtils.isNotEmpty(fileIdResp.getThumbnailId())) {
                fileObject.setThumbnailId(fileIdResp.getThumbnailId());
            }
            fileObject.setFileId(fileIdResp.getFileTid());
            if (!CollectionUtil.isEmpty(suggestionsList)) {
                fileObject.setSuggestions(suggestionsList);
                String suggestionsLists = JSONArray.toJSONString(button);
                template.setShortcutButton(suggestionsLists);
            }
            fileMessage.setContent(fileObject);
            //发送消息
            send(fileMessage, sendParams.getAccount(), template, MsgSubTypeEnum.RICH);
        } catch (Exception e) {
            log.error("发送文件至网关异常：{0}", e);
        }
    }

    private void sendText(SendParams sendParams, JSONObject jsonObject, List<Suggestions> suggestionsList, JSONArray button) {
        MessageTemplateResp template = new MessageTemplateResp();
        JSONObject jsonObjects = jsonObject.getJSONObject("messageDetail");
        template.setMessageType(jsonObject.getInteger("type"));
        template.setModuleInformation(jsonObjects.toJSONString());
        template.setTemplateName(getMessageType(jsonObject.getInteger("type")));
        FileMessage fileMessage = new FileMessage();
        fileMessage.setConversationId(sendParams.getConversationId());
        fileMessage.setContributionId(sendParams.getContributionId());
        List<String> phoneNumList = new ArrayList<>();
        phoneNumList.add(sendParams.getPhoneNum());
        fileMessage.setDestinationAddress(phoneNumList);
        //消息内容
        String textMsg = JsonPath.read(jsonObject, "$.messageDetail.input.value").toString();
        TextObject textObject = new TextObject();
        textObject.setText(textMsg);
        fileMessage.setMessageType("text");
        fileMessage.setContent(textObject);
        //全局按钮与快捷按钮进行合并
        if (!CollectionUtil.isEmpty(suggestionsList)) {
            textObject.setSuggestions(suggestionsList);
            String suggestionsLists = JSONArray.toJSONString(button);
            template.setShortcutButton(suggestionsLists);
        }
        fileMessage.setContent(textObject);
        //发送消息
        send(fileMessage, sendParams.getAccount(), template, MsgSubTypeEnum.TEXT);
    }

    //填充快捷回复按钮
    private List<Suggestions> buildButton(JSONArray buttonJson, Map<String, String> buttonIdMap) {
        log.info("填充快捷回复按钮 buttonJson is {}", buttonJson);
        List<Suggestions> suggestions = new ArrayList<>();
        if (buttonJson == null || buttonJson.size() == 0) {
            return suggestions;
        }
        if (!CollectionUtil.isEmpty(buttonJson)) {
            for (int i = 0; i < buttonJson.size(); i++) {
                Suggestions suggestion = new Suggestions();
                JSONObject button = buttonJson.getJSONObject(i);
                String buttonUUID = button.getString("uuid");
                if (buttonIdMap.get(buttonUUID) != null) {
                    continue;
                } else {
                    buttonIdMap.put(buttonUUID, buttonUUID);
                }
                //按钮对象
                Map<String, String> actionParams = getActionParams(button);
                log.info("actionParams is {}", actionParams);
                String buttonText = JsonPath.read(button, "$.buttonDetail.input.value");

                /**
                 * todo
                 * 临时修复 流程中通过手机发送消息，按钮不见的bug
                 * 后期统一消息标准时修改
                 * @createdTime 2023/2/7 17:28
                 */
                Integer buttonType = 0;
                if (ObjectUtil.isNotEmpty(button.get("type"))) {
                    buttonType = button.getInteger("type");
                } else {
                    buttonType = button.getInteger("buttonType");
                }

                suggestion.setType(getButtonType(buttonType));
                //打开app设置为urlAction
                if (StringUtils.equals("3", buttonType.toString())) {
                    suggestion.setType("urlAction");
                }
                //自定义数据
                suggestion.setPostbackData(buttonUUID + "#&#&" + 0 + "#&#&" + buttonType + "#&#&" + buttonText + "#&#&" + "-1");
                suggestion.setDisplayText(buttonText);
                suggestion.setActionParams(actionParams);
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    private void send(BaseMessage message, String account, MessageTemplateResp template, MsgSubTypeEnum subType) {
        try {
            log.info("开始发送网关消息，消息体为{}", message.toString());
            AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByAccountId(account);
            CSPOperatorCodeEnum operator = CSPOperatorCodeEnum.byCode(accountManagementResp.getAccountTypeCode());
            //返回是否要下行会话消息，是的话返回true
            boolean sessionMessage = MsgUtils.trySendSessionMessage(message, operator);
            if (sessionMessage) {
                FileMessage fileMessage = (FileMessage) message;
                fileMessage.setInReplyTo(fileMessage.getContributionId());
            }
            DeductResult deductResult = null;
            if (Objects.isNull(SessionContextUtil.getUser())) {
                //通过ChatbotAccount来判断是不是预付费
                if (message instanceof FontdoFileMessage) {
                    FontdoFileMessage fontdoMessage = (FontdoFileMessage) message;
                    String payType = stringRedisTemplate.opsForValue().get(Constants.FONTDO_NEW_TEMPLATE_PAY_TYPE_PREFIX + (fontdoMessage.getTemplateId() + ""));
                    if (Objects.isNull(payType)) {
                        log.warn("该模板没有找到发送任务, TemplateId: {}", fontdoMessage.getTemplateId());
                        throw new BizException("获取模板支付类型失败");
                    }
                    CustomerPayType customerPayTypeByCode = CustomerPayType.getCustomerPayTypeByCode(Integer.parseInt(payType));
                    deductResult = deductMessageBalance(accountManagementResp, subType, operator, false, fontdoMessage.getDestinationAddress(), customerPayTypeByCode.getCode());
                    if (!deductResult.success) {
                        log.warn("5G消息额度扣除失败，取消发送");
                        return;
                    }
                    message.setMessageId(deductResult.getMessageId());
                }
            } else {
                List<String> phoneNumbers = message.getDestinationAddress();
                deductResult = deductMessageBalance(accountManagementResp, subType, operator, sessionMessage, phoneNumbers, SessionContextUtil.getUser().getPayType().getCode());
                if (!deductResult.isSuccess()) {
                    log.warn("5G消息额度扣除失败，取消发送");
                    return;
                }
                message.setMessageId(deductResult.getMessageId());
            }
            PaymentTypeEnum paymentType = Optional.ofNullable(deductResult).map(DeductResult::getPaymentType).orElse(null);

            fifthSendClient.sendByMessage(message, accountManagementResp, template, MessageResourceType.CHATBOT, paymentType);
            if (!CollectionUtils.isEmpty(message.getDestinationAddress())) {
                TemporaryStatisticsReq temporaryStatisticsReq = new TemporaryStatisticsReq();
                temporaryStatisticsReq.setType(8);
                temporaryStatisticsReq.setCreator(accountManagementResp.getCustomerId());
                temporaryStatisticsReq.setUpdater(accountManagementResp.getCustomerId());
                temporaryStatisticsReq.setChatbotAccountId(accountManagementResp.getChatbotAccountId());
                temporaryStatisticsReq.setChatbotType(accountManagementResp.getAccountTypeCode());
                temporaryStatisticsApi.saveTemporaryStatisticsApi(temporaryStatisticsReq);
            }
        } catch (
                Exception e) {
            log.error("网关上传失败：{0}", e);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class DeductResult {
        private boolean success;
        private String messageId;
        private PaymentTypeEnum paymentType;
    }

    /**
     * @return 扣除消息余额，返回是否扣除成功，成功返回true
     */
    private DeductResult deductMessageBalance(AccountManagementResp accountManagementResp, MsgSubTypeEnum subType, CSPOperatorCodeEnum operator, boolean isConversationMessage, List<String> phoneNumbers, Integer customerPayType) {
        try {
            DeductResult deductResult;
            switch (operator) {
                case CMCC:
                case CUNC:
                case DEFAULT: {
                    //先尝试扣除会话消息，扣除成功则直接返回，失败则退回到5G消息
                    if (isConversationMessage) {
                        try {
                            deductResult = defaultModeDeduct(accountManagementResp, MsgSubTypeEnum.CONVERSATION, phoneNumbers, customerPayType);
                            break;
                        }catch (Exception e){
                            log.warn("先尝试扣除会话消息失败，即将降级为普通5G消息进行扣费，失败原因: {}",e.getMessage());
                        }
                    }
                    deductResult = defaultModeDeduct(accountManagementResp, subType, phoneNumbers, customerPayType);
                    break;
                }
                case CT: {
                    deductResult = defaultModeDeduct(accountManagementResp, subType, phoneNumbers, customerPayType);
                    break;
                }
                default:
                    throw new BizException("不支持的运营商");
            }
            return deductResult;
        } catch (Throwable throwable) {
            log.error("{} 扣除5G消息额度失败。reason:{}", accountManagementResp.getChatbotAccount(), throwable.getMessage());
            return new DeductResult(false, null, null);
        }
    }

    //先扣套餐,再扣余额
    private DeductResult defaultModeDeduct(AccountManagementResp accountManagementResp, MsgSubTypeEnum subType, List<String> phoneNumbers, Integer customerPayType) {
        //校验是否设定了chatbot资费
        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(accountManagementResp.getChatbotAccountId());
        if (rechargeTariff == null) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), accountManagementResp.getAccountName()));
        }

        String messageId = UUIDUtils.generateUUID();
        if (Objects.equals(customerPayType, CustomerPayType.PREPAY.getCode())) {
            try {
                prepaymentApi.deductRemaining(accountManagementResp.getChatbotAccount(), MsgTypeEnum.M5G_MSG, subType, 1L, 1L);
                return new DeductResult(true, messageId, PaymentTypeEnum.SET_MEAL);
            } catch (Exception e) {
                log.error("扣除5G消息额度失败,尝试扣除余额。reason:{}", e.getMessage());
            }
        }
        //后付费以及套餐不足的预付费用户
        FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                .accountType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                .tariffType(subType.getCode())
                .payType(customerPayType)
                .accountId(accountManagementResp.getChatbotAccountId())
                .messageId(messageId)
                .customerId(accountManagementResp.getCustomerId())
                .phoneNumbers(phoneNumbers)
                .chargeNum(1)
                .build();
        List<String> successPhones = deductionAndRefundApi.tryDeductFee(feeDeductReq);
        if (CollectionUtil.isEmpty(successPhones)) {
            throw new BizException("扣除余额失败");
        }
        return new DeductResult(true, messageId, PaymentTypeEnum.BALANCE);
    }

    /**
     * 获取按钮的信息
     *
     * @param button 按钮内容
     * @return 按钮信息map
     */
    private Map<String, String> getActionParams(JSONObject button) {
        log.info("getActionParams button {}", button);
        Map<String, String> actionParams = new HashMap<>();
        /**
         * todo
         * 临时修复 流程中通过手机发送消息，按钮不见的bug
         * 后期统一消息标准时修改
         * @createdTime 2023/2/7 17:28
         */
        String buttonType = "";
        if (ObjectUtil.isNotEmpty(button.get("type"))) {
            buttonType = button.get("type").toString();
        } else {
            buttonType = button.get("buttonType").toString();
        }
        //1 回复按钮 2 跳转连接 3打开app 4打电话 5发送地址 6地址定位 7拍摄按钮 8调起指定联系人
        JSONObject buttonDetail = button.getJSONObject("buttonDetail");
        //满足下面条件的不加参数 回复按钮1、发送地址5  机器人需要这种类型的按钮
        //根据不同按钮构建不通参数
        switch (buttonType) {
            case "6":
                // 地址定位
                actionParams.put("latitude", buttonDetail.getString("localLatitude"));
                actionParams.put("longitude", buttonDetail.getString("localLongitude"));
                actionParams.put("label", buttonDetail.getJSONObject("localLocation").getString("name"));
                break;
            case "2":
                //跳转链接
                Long formId = buttonDetail.getLong("formId");
                if (ObjectUtil.isEmpty(formId)) {
                    //为空就按照linkUrl
                    actionParams.put("url", buttonDetail.getString("linkUrl"));
                } else {
                    //构建表单url
                    actionParams.put("url", formShareUrl + formId);
                }
                if (buttonDetail.getInteger("localOpenMethod") == 1) {
                    actionParams.put("application", "webview");
                } else {
                    actionParams.put("application", "browser");
                }
                actionParams.put("viewMode", "full");
                break;
            case "4":
                //打电话
                actionParams.put("phoneNumber", buttonDetail.getString("phoneNum"));
                break;
            case "8":
                //调起指定联系人
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNum"));
                actionParams.put("text", buttonDetail.getJSONObject("previewInput").getString("name"));
                break;
            case "7":
                //拍摄
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNumForPhoto"));
                break;
            //打开APP
            case "3":
                log.info("打开App按钮匹配");
                actionParams.put("url", buttonDetail.getString("linkUrl"));
                actionParams.put("application", "browser");
                actionParams.put("viewMode", "full");
                break;
            case "9":
                log.info("打开日历");
                actionParams.put("startTime", buildCalendar(buttonDetail.getString("calendarStartTime")));
                actionParams.put("endTime", buildCalendar(buttonDetail.getString("calendarEndTime")));
                actionParams.put("title", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                actionParams.put("description", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                break;
            default:
                return null;
        }
        return actionParams;
    }

    private String buildCalendar(String date) {
        date = date.replace(" ", "T");
        StringBuffer sb = new StringBuffer();
        sb.append(date);
        sb.append("Z");
        return sb.toString();
    }

    private FileIdResp getFileTid(JSONObject jsonObject, String operator) {
        log.info("发送文件消息的节点信息为：{},operator is {}", jsonObject, operator);
        FileIdResp fileIdResp = new FileIdResp();
        FileTidReq tidReq = new FileTidReq();
        tidReq.setOperator(operator);
        String fileTid = null;
        if (StringUtils.isNotEmpty(jsonObject.getString("pictureUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("pictureUrlId"));
            PictureReq pictureReq = new PictureReq();
            pictureReq.setPictureUrlId(jsonObject.getString("pictureUrlId"));
            fileTid = platformApi.getFileTid(tidReq).getFileId();
            log.info("fileTid is {}", fileTid);
            PictureResp pictureResp = pictureApi.findByUuid(pictureReq);
            if (StringUtils.isNotEmpty(pictureResp.getThumbnailTid())) {
                fileIdResp.setThumbnailId(pictureResp.getThumbnailTid());
            }
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("videoUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("videoUrlId"));
            fileTid = platformApi.getFileTid(tidReq).getFileId();
            VideoReq req = new VideoReq();
            req.setVideoUrlId(jsonObject.getString("videoUrlId"));
            VideoResp videoResp = videoApi.findOneByUuid(req);
            if (StringUtils.isNotEmpty(videoResp.getThumbnailTid())) {
                fileIdResp.setThumbnailId(videoResp.getThumbnailTid());
            }
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("fileUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("fileUrlId"));
            fileTid = platformApi.getFileTid(tidReq).getFileId();
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("audioUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("audioUrlId"));
            fileTid = platformApi.getFileTid(tidReq).getFileId();
        }
        fileIdResp.setFileTid(fileTid);
        log.info("构建fileIdResp {}", fileIdResp);
        return fileIdResp;
    }

    private File getFile(String fileUuid) {
        try {
            DownloadReq downloadReq = new DownloadReq();
            downloadReq.setFileUUID(fileUuid);
            ResponseEntity<byte[]> responseEntity = fileApi.download(downloadReq);
            byte[] bytes = responseEntity.getBody();
            return MyFileUtil.bytesToFile(bytes, "css");
        } catch (Exception e) {
            log.error("下载文件失败:{0}", e);
        }
        return null;
    }

    private List<Media> getMedias(JSONObject jsonObject, String operator, Map<String, String> buttonIdMap) {
        List<Media> mediaList = new ArrayList<>();
        Media media = new Media();
        String description = jsonObject.getObject("description", JSONObject.class).getString("value");
        String title = jsonObject.getObject("title", JSONObject.class).getString("value");
        media.setTitle(title);
        media.setDescription(description);
        media.setContentDescription(description);
        media.setTitle(title);
        if (null != jsonObject.getJSONArray("buttonList")) {
            JSONArray btnArray = jsonObject.getJSONArray("buttonList");
            List<Suggestions> suggestions = buildButton(btnArray, buttonIdMap);
            FileIdResp fileIdResp = getFileTid(jsonObject, operator);
            media.setMediaId(fileIdResp.getFileTid());
            if (StringUtils.isNotEmpty(fileIdResp.getThumbnailId())) {
                media.setThumbnailId(fileIdResp.getThumbnailId());
            }
            media.setSuggestions(suggestions);
        }
        mediaList.add(media);
        return mediaList;
    }

    /**
     * 获取卡片的高度
     *
     * @param jsonObject
     * @param media
     */
    private void setCardHeight(JSONObject jsonObject, Media media) {
        String height = jsonObject.getString("height");
        //根据不同的高度匹配网关枚举
        switch (height) {
            case "max":
                media.setHeight("TALL_HEIGHT");
                break;
            case "min":
                media.setHeight("SHORT_HEIGHT");
                break;
            default:
                media.setHeight("MEDIUM_HEIGHT");
        }
    }

    private void setCardWidth(Layout layout, String width) {
        switch (width) {
            case "max":
                layout.setCardWidth("MEDIUM_WIDTH");
                break;
            case "min":
                layout.setCardWidth("SMALL_WIDTH");
                break;
            default:
                break;
        }
    }

    private String getButtonType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "reply");
        map.put(2, "urlAction");
        map.put(3, "openApp");
        map.put(4, "dialerAction");
        map.put(5, "ownMapAction");
        map.put(6, "mapAction");
        map.put(7, "composeVideoAction");
        map.put(8, "composeTextAction");
        map.put(9, "calendarAction");
        map.put(10, "deviceAction");
        map.put(11, "reply");
        map.put(12, "reply");
        map.put(13, "reply");
        map.put(14, "reply");
        return map.get(type);
    }

    //兜底回复按钮
    private List<Suggestions> buildButtonPocketBottom(JSONArray buttonJson, Map<String, String> buttonIdMap) {
        log.info("兜底回复按钮 buttonJson is {}", buttonJson);
        List<Suggestions> suggestions = new ArrayList<>();
        if (buttonJson == null || buttonJson.size() == 0) {
            return suggestions;
        }
        if (!CollectionUtil.isEmpty(buttonJson)) {
            for (int i = 0; i < buttonJson.size(); i++) {
                Suggestions suggestion = new Suggestions();
                JSONObject button = buttonJson.getJSONObject(i);
                String buttonText = JsonPath.read(button, "$.buttonDetail.input.value");
                String buttonUUID = button.getString("uuid");
                if (buttonIdMap.get(buttonUUID) != null) {
                    continue;
                } else {
                    buttonIdMap.put(buttonUUID, buttonUUID);
                }
                //按钮对象
                Map<String, String> actionParams = getActionParamPocketBottoms(button);
                log.info("actionParams is {}", actionParams);

                Integer buttonType = button.getInteger("buttonType");
                suggestion.setType(getButtonType(buttonType));
                //打开app设置为urlAction
                if (StringUtils.equals("3", buttonType.toString())) {
                    suggestion.setType("urlAction");
                }
                //自定义数据
                suggestion.setPostbackData(buttonUUID + "#&#&" + 0 + "#&#&" + buttonType + "#&#&" + buttonText + "#&#&" + "-1");
                suggestion.setDisplayText(buttonText);
                suggestion.setActionParams(actionParams);
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    /**
     * 兜底按钮的信息
     *
     * @param button 按钮内容
     * @return 按钮信息map
     */
    private Map<String, String> getActionParamPocketBottoms(JSONObject button) {
        Map<String, String> actionParams = new HashMap<>();
        //1 回复按钮 2 跳转连接 3打开app 4打电话 5发送地址 6地址定位 7拍摄按钮 8调起指定联系人
        String buttonType = button.get("buttonType").toString();
        JSONObject buttonDetail = button.getJSONObject("buttonDetail");
        //满足下面条件的不加参数 回复按钮1、发送地址5  机器人需要这种类型的按钮
        //根据不同按钮构建不通参数
        switch (buttonType) {
            case "6":
                // 地址定位
                actionParams.put("latitude", buttonDetail.getString("localLatitude"));
                actionParams.put("longitude", buttonDetail.getString("localLongitude"));
                actionParams.put("label", buttonDetail.getJSONObject("localLocation").getString("name"));
                break;
            case "2":
                //跳转链接
                Long formId = buttonDetail.getLong("formId");
                if (ObjectUtil.isEmpty(formId)) {
                    //为空就按照linkUrl
                    actionParams.put("url", buttonDetail.getString("linkUrl"));
                } else {
                    //构建表单url
                    actionParams.put("url", formShareUrl + formId);
                }
                if (buttonDetail.getInteger("localOpenMethod") == 1) {
                    actionParams.put("application", "webview");
                } else {
                    actionParams.put("application", "browser");
                }
                actionParams.put("viewMode", "full");
                break;
            case "4":
                //打电话
                actionParams.put("phoneNumber", buttonDetail.getString("phoneNum"));
                break;
            case "8":
                //调起指定联系人
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNum"));
                actionParams.put("text", buttonDetail.getJSONObject("previewInput").getString("name"));
                break;
            case "7":
                //拍摄
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNumForPhoto"));
                break;
            //打开APP
            case "3":
                log.info("打开App按钮匹配");
                actionParams.put("url", buttonDetail.getString("linkUrl"));
                actionParams.put("application", "browser");
                actionParams.put("viewMode", "full");
                break;
            case "9":
                log.info("打开日历");
                actionParams.put("startTime", buildCalendar(buttonDetail.getString("calendarStartTime")));
                actionParams.put("endTime", buildCalendar(buttonDetail.getString("calendarEndTime")));
                actionParams.put("title", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                actionParams.put("description", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                break;
            default:
                return null;
        }
        return actionParams;
    }

    private String getMessageType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "文本");
        map.put(2, "图片");
        map.put(3, "视频");
        map.put(4, "音频");
        map.put(5, "文件");
        map.put(6, "单卡");
        map.put(7, "多卡");
        map.put(8, "位置");
        return map.get(type);
    }

    /**
     * 供应商发送节点
     *
     * @param templates 提问节点和发送节点的此 templates 参数为List集合, 兜底则只有一个对象
     * @param wsResp    提问节点和发送节点的参数的body中 已经是确定好了选择哪一项发送, 但是兜底的body是null
     * @return 按钮信息map
     */
    public void send(MsgDto msgDto, Integer sendType, List<MessageTemplateProvedResp> templates, WsResp wsResp, RobotDto robotDto) {
        if (CollectionUtils.isEmpty(templates)) {
            log.info("没有找到通过审核的模板，跳过发送，ChatbotAccount:{}", msgDto.getChatbotAccount());
            return;
        }

        FontdoFileMessage fontdoFileMessage = new FontdoFileMessage();
        //设置手机号
        fontdoFileMessage.setNumbers(CollectionUtil.newArrayList(msgDto.getPhone()));
        fontdoFileMessage.setDestinationAddress(CollectionUtil.newArrayList(msgDto.getPhone()));
        fontdoFileMessage.setSupplierTag(msgDto.getSupplierTag());
        fontdoFileMessage.setTemplateType("RCS");
        log.info("发送类型{}", sendType);

        //此参数不为空, 说明不是测试发送,需要记录聊天记录
        if (Objects.nonNull(wsResp)) {
            wsResp.setButtonList(getGlobalButtonList(wsResp.getConversationId()));
            //兜底消息创建wsRespBody(兜底消息的templates只有一条)
            if (Objects.isNull(wsResp.getBody())) {
                buildWsRespBody(wsResp, templates.get(0), robotDto);
            }
            //提问或发送节点,需要根据wsResp.body来判断是发送哪一条模板消息
            else {
                //发送节点
                Object body = wsResp.getBody();
                if (body instanceof Map) {
                    Map<String, List<JSONObject>> bodyMap = (Map<String, List<JSONObject>>) body;
                    List<JSONObject> contentBody = bodyMap.get("contentBody");
                    log.info("send node contentBody:{}", contentBody);
                    List<String> selectTemplateIds = contentBody.stream().map(obj -> (String) obj.get("templateId")).collect(Collectors.toList());
                    log.info("send node selectTemplateIds:{}", selectTemplateIds);
                    log.info("send node templates:{}", templates);
                    templates = templates.stream().filter(t -> selectTemplateIds.contains(t.getId().toString())).collect(Collectors.toList());
                    log.info("send node select templates:{}", templates);
                }
                //提问节点
                else if (body instanceof JSONArray) {
                    JSONArray bodyArray = (JSONArray) body;
                    List<String> selectTemplateIds = bodyArray.stream().map(obj -> {
                        JSONObject jsonObject = (JSONObject) obj;
                        JSONObject msg = (JSONObject) jsonObject.get("msg");
                        return msg.get("templateId").toString();
                    }).collect(Collectors.toList());
                    log.info("question node body:{}", body);
                    log.info("question node selectTemplateIds:{}", selectTemplateIds);
                    log.info("question node templates:{}", templates);
                    templates = templates.stream().filter(t -> selectTemplateIds.contains(t.getId().toString())).collect(Collectors.toList());
                    log.info("question node select templates:{}", templates);
                }
            }
            String originalMsg = JsonUtils.obj2String(wsResp);
            //!!!替换系统参数等占位符
            String translatedMsg = RobotUtils.translateVariable(wsResp.getConversationId(), wsResp.getPhone(), originalMsg);
            log.info("存储fontdo兜底发送记录robotRecord msg:{}", translatedMsg);
            saveRecord(wsResp, translatedMsg);
        }
        Assert.notNull(wsResp, "wsResp is null");
        for (MessageTemplateProvedResp template : templates) {
            fontdoFileMessage.setTemplateId(Long.parseLong(template.getPlatformTemplateId()));
            List<Parameter> params = new ArrayList<>();
            RobotUtils.setVariableParam(msgDto.getConversationId(), msgDto.getPhone(), template.getModuleInformation(), params);
            //替换变量过后的moduleInformation
            String moduleInformation = RobotUtils.translateVariable(wsResp.getConversationId(), wsResp.getPhone(), template.getModuleInformation());
            template.setModuleInformation(moduleInformation);

            params.add(new Parameter("STRING", "detailId", "0"));
            fontdoFileMessage.setParams(params);
            send(fontdoFileMessage, msgDto.getChatbotAccount(), template, MsgSubTypeEnum.convertTemplateType2MsgSubType(template.getMessageType()));
        }
    }


    //给兜底 装配WsResp Body
    private void buildWsRespBody(WsResp wsResp, MessageTemplateProvedResp template, RobotDto robotDto) {
        RebotSettingModel model = robotDto.getRebotSettingModel();
        String lastReply = model.getLastReply();
        //解析lastReply，转换为List<String>
        List<JSONObject> lastReplyJsonList = JSON.parseArray(lastReply, JSONObject.class);
        List<RobotProcessButtonResp> buttonList = model.getButtonList();
        List<Object> objectList = new ArrayList<>();
        if (buttonList != null) {
            buttonList.forEach(robotProcessButtonResp -> {
                String buttonDetail = robotProcessButtonResp.getButtonDetail();
                String uuid = robotProcessButtonResp.getUuid();
                Object parse = JSONObject.parse(buttonDetail);
                JSONObject jsonObject = (JSONObject) JSON.toJSON(robotProcessButtonResp);
                jsonObject.put("buttonDetail", parse);
                jsonObject.put("uuid", uuid);
                objectList.add(jsonObject);
            });
        }
        if (lastReplyJsonList != null && lastReplyJsonList.size() > 0) {
            for (JSONObject lastReplyJson : lastReplyJsonList) {
                String templateId = lastReplyJson.getString("templateId");
                if (templateId.equals(template.getId().toString())) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", lastReplyJson);
                    map.put("button", objectList);

                    wsResp.setBody(map);
                    return;
                }
            }
        }
    }


    private List<RobotProcessButtonResp> getGlobalButtonList(String conversationId) {
        List<RobotProcessButtonResp> buttonList = new ArrayList<>();
        RebotSettingModel rebotSettingModel = RobotUtils.getRobot(conversationId).getRebotSettingModel();
        //0代表仅在兜底回复显示   1代表在机器人所有回复显示
        if (0 == rebotSettingModel.getGlobalType()) {
            return buttonList;
        }
        buttonList = rebotSettingModel.getButtonList();
        return buttonList;
    }

    /*
     * @describe 保存聊天记录,此方法copy于SendMsgClient#saveRecord
     * @Param
     * @param wsResp
     * @param msg
     * @return void
     **/
    public void saveRecord(WsResp wsResp, String msg) {
        try {
            String phone = wsResp.getPhone();
            String account = wsResp.getChatbotAccount();
            StringBuilder redisKey = new StringBuilder();
            redisKey.append(account);
            if (StringUtil.isNotEmpty(phone)) {
                redisKey.append("@");
                redisKey.append(phone);
            }
            redisKey.append(":SerialNum");
            if (!checkRecordList(phone, account, wsResp.getUserId())) {
                RobotAccountReq robotAccountReq = new RobotAccountReq();
                robotAccountReq.setAccount(wsResp.getChatbotAccount());
                robotAccountReq.setChatbotAccountId(wsResp.getChatbotAccountId());
                robotAccountReq.setChannelType(ChannelTypeUtil.getChannelType(wsResp.getChannelType()));
                robotAccountReq.setMobileNum(phone);
                robotAccountReq.setConversationId(phone);
                robotAccountReq.setCreator(wsResp.getUserId());
                robotAccountReq.setUpdater(wsResp.getUserId());
                robotAccountApi.saveRobotAccount(robotAccountReq);
                Long serialNum = 1L;
                stringRedisTemplate.opsForValue().set(redisKey.toString(), String.valueOf(serialNum), OVER_TIME, TimeUnit.MINUTES);
                RobotRecordReq robotRecordReq = new RobotRecordReq();
                robotRecordReq.setConversationId(phone);
                robotRecordReq.setSerialNum(serialNum);
                robotRecordReq.setMessage(msg);
                robotRecordReq.setSendPerson("1");
                robotRecordReq.setMobileNum(phone);
                robotRecordReq.setAccount(account);
                robotRecordReq.setCreator(wsResp.getUserId());
                robotRecordReq.setUpdater(wsResp.getUserId());
                robotRecordReq.setChannelType(ChannelTypeUtil.getChannelType(wsResp.getChannelType()));
                robotRecordReq.setMessageType(wsResp.getMsgType());
                robotRecordApi.saveRobotRecord(robotRecordReq);
            } else {
                String serialNumStr = stringRedisTemplate.opsForValue().get(redisKey.toString());
                Long serialNum = 1L;
                if (StringUtils.isNotEmpty(serialNumStr)) {
                    serialNum = Long.parseLong(serialNumStr) + 1;
                }
                RobotRecordReq robotRecordReq = new RobotRecordReq();
                robotRecordReq.setConversationId(phone);
                robotRecordReq.setSerialNum(serialNum);
                robotRecordReq.setMessage(msg);
                robotRecordReq.setSendPerson("1");
                robotRecordReq.setMobileNum(phone);
                robotRecordReq.setAccount(account);
                robotRecordReq.setCreator(wsResp.getUserId());
                robotRecordReq.setUpdater(wsResp.getUserId());
                robotRecordReq.setChannelType(ChannelTypeUtil.getChannelType(wsResp.getChannelType()));
                robotRecordReq.setMessageType(wsResp.getMsgType());
                robotRecordApi.saveRobotRecord(robotRecordReq);
                stringRedisTemplate.opsForValue().set(redisKey.toString(), String.valueOf(serialNum), OVER_TIME, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("保存聊天记录异常：{}", e.getMessage(), e);
        }
    }

    /*
     * @describe 检查 聊天会话账户
     * @Param
     * @param MobileNum
     * @param account
     * @param create
     * @return java.lang.Boolean
     **/
    private Boolean checkRecordList(String MobileNum, String account, String create) {
        RobotAccountPageReq robotAccountPageReq = new RobotAccountPageReq();
        robotAccountPageReq.setMobileNum(MobileNum);
        robotAccountPageReq.setAccount(account);
        robotAccountPageReq.setCreate(create);
        return robotAccountApi.getRobotAccountList(robotAccountPageReq).getList().size() > 0;
    }

    /*
     * @describe
     * @Param  当蜂动上行出发多个机器人流程时,需要先把提示内容审核以后再进行发送
     * @param msg
     * @return void
     **/
    public void sendMultiTrigger(String msg) {
        log.info("蜂动机器人流程多触发或大数据兜底回复发送5G消息, msg:{}", msg);
        String[] split = msg.split(":");
        String phone = split[0];
        String chatbotAccount = split[1];
        String platformTemplateId = split[2];
        FontdoFileMessage fontdoFileMessage = new FontdoFileMessage();
        //设置手机号
        fontdoFileMessage.setNumbers(CollectionUtil.newArrayList(phone));
        fontdoFileMessage.setDestinationAddress(CollectionUtil.newArrayList(phone));
        fontdoFileMessage.setSupplierTag(Constants.SUPPLIER_TAG_FONTDO);
        fontdoFileMessage.setTemplateType("RCS");
        //全部发送(发送节点和提问节点都是)
        fontdoFileMessage.setTemplateId(Long.parseLong(platformTemplateId));
        List<Parameter> params = new ArrayList<>();

        params.add(new Parameter("STRING", "detailId", "0"));
        fontdoFileMessage.setParams(params);

        MsgDto msgDto = new MsgDto();
        msgDto.setChatbotAccount(chatbotAccount);
        MessageTemplateResp template = new MessageTemplateResp();
        String moduleInformation = stringRedisTemplate.opsForValue().get(Constants.FONTDO_NEW_TEMPLATE_MODULE_INFORMATION_PREFIX + platformTemplateId);
        String button = stringRedisTemplate.opsForValue().get(Constants.FONTDO_NEW_TEMPLATE_BUTTON_PREFIX + platformTemplateId);

        template.setModuleInformation(moduleInformation);
        template.setShortcutButton(button);
        template.setMessageType(1);
        template.setTemplateName("文本");
        template.setId(0L);
        log.info("蜂动机器人流程多触发或大数据兜底回复发送5G消息, task:{}", Constants.FONTDO_NEW_TEMPLATE_SEND_TASK_PREFIX + platformTemplateId);
        send(fontdoFileMessage, chatbotAccount, template, MsgSubTypeEnum.TEXT);
        stringRedisTemplate.delete(Constants.FONTDO_NEW_TEMPLATE_MODULE_INFORMATION_PREFIX + platformTemplateId);
        stringRedisTemplate.delete(Constants.FONTDO_NEW_TEMPLATE_BUTTON_PREFIX + platformTemplateId);
        stringRedisTemplate.delete(Constants.FONTDO_NEW_TEMPLATE_SEND_TASK_PREFIX + platformTemplateId);
        stringRedisTemplate.delete(Constants.FONTDO_NEW_TEMPLATE_PAY_TYPE_PREFIX + platformTemplateId);

    }
}
