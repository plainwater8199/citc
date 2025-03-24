package com.citc.nce.auth.messagetemplate.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.cardstyle.CardStyleApi;
import com.citc.nce.auth.cardstyle.vo.CardStyleOneReq;
import com.citc.nce.auth.cardstyle.vo.CardStyleResp;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.messagetemplate.entity.*;
import com.citc.nce.auth.utils.RobotUtils;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.constants.TemplateButtonTypeConst;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.dto.*;
import com.citc.nce.fileApi.AudioApi;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.FileInfo;
import com.citc.nce.filecenter.vo.FileInfoReq;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.vo.*;
import com.citc.nce.robot.vo.directcustomer.Parameter;
import com.citc.nce.vo.AudioResp;
import com.citc.nce.vo.PictureResp;
import com.citc.nce.vo.VideoResp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yy
 * @date 2024-03-13 17:41:41
 */
@Data
@Component
@Slf4j
public class TemplateContentMakeUp {
    private static final String phoneRegex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    @Value("${front.customer.domain}")
    String origin_url;
    @Resource
    RedissonClient redissonClient;
    @Resource
    VideoApi videoApi;
    @Resource
    PictureApi pictureApi;
    @Resource
    AudioApi audioApi;
    @Resource
    FileApi fileApi;
    @Resource
    CardStyleApi cardStyleApi;
    @Resource
    AccountManagementApi accountManagementApi;
    @Resource
    SmsTemplateApi smsTemplateApi;
    @Resource
    CspSmsAccountApi cspSmsAccountApi;
    @Resource
    PlatformApi platformApi;
    //获取表单url
    @Value("${form.shareUrl}")
    private String formShareUrl;
    @Value("${cssDownLoadUrI}")
    private String cssDownLoadUrI;
    /**
     * 模板变量数组
     */
    JSONArray _variableJsonArray;

    /**
     * 根据模板拼接平台所需请求参数结构体
     *
     * @param messageTemplateResp
     * @param templateOwnershipReflect
     * @return
     */
    public TemplateAuditReq buildMessageParams(MessageTemplateDo messageTemplateResp, TemplateOwnershipReflect templateOwnershipReflect) {
        //模板中的占位符转换
        replaceTemplateInfo(messageTemplateResp);
        String moduleInformation = messageTemplateResp.getModuleInformation();
        //按钮信息
        String shortcutButton = "";
        JSONArray buttonJson = new JSONArray();
        if (null != messageTemplateResp.getShortcutButton()) {
            shortcutButton = messageTemplateResp.getShortcutButton();
            buttonJson = JSONObject.parseArray(shortcutButton);
        }
        log.info("shortcutButton information is {}", shortcutButton);
        //获取模板的类型 消息类型 1 文本  2 图片 3 视频 4 音频  6 单卡 7 多卡 8  位置
        int type = messageTemplateResp.getMessageType();
        JSONObject jsonObject = JSONObject.parseObject(moduleInformation, JSONObject.class);
        String styleId = jsonObject.getString("style");
        Integer styleStatus = jsonObject.getInteger("styleStatus");
        TemplateAuditReq fileMessage = new TemplateAuditReq();
        fileMessage.setTemplateId(messageTemplateResp.getId());
        fileMessage.setName(messageTemplateResp.getTemplateName());
        SupplierTemplateContentReq supplierTemplateContentReq = new SupplierTemplateContentReq();
        fileMessage.setTemplateContent(supplierTemplateContentReq);
        fileMessage.setStyle(makeTemplateStyleReq(messageTemplateResp.getStyleInformation(), styleId, styleStatus));
        List<Suggestions> suggestions = buildButton(buttonJson, 1, -1, messageTemplateResp.getTemplateSource(), templateOwnershipReflect.getSupplierTag());
        supplierTemplateContentReq.setMessageType(type);
        switch (type) {
            //发送文件消息
            case 2:
            case 3:
            case 4:
            case 5:
                FileIdResp fileIdResp = getFileTid(jsonObject, templateOwnershipReflect);
                supplierTemplateContentReq.setFileId(fileIdResp.getFileTid());
                supplierTemplateContentReq.setSuggestions(suggestions);
                fileMessage.setTemplateContent(supplierTemplateContentReq);
                break;
            //发送卡片消息
            case 6:
            case 7:
                //CardObject cardObject = new CardObject();
                JSONArray cardList = jsonObject.getJSONArray("cardList");
                List<Media> medias = new ArrayList<>();
                //单卡
                if (CollectionUtil.isEmpty(cardList)) {
                    medias = getMedias(jsonObject, templateOwnershipReflect, -1, messageTemplateResp.getTemplateSource());
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
                    //cardObject.setLayout(layout);
                    supplierTemplateContentReq.setLayout(layout);
                    if (medias.size() == 1) {
                        setCardHeight(jsonObject, medias.get(0));
                    }
                } else {
                    //多卡
                    for (int i = 0; i < cardList.size(); i++) {
                        List<Media> list = getMedias(cardList.getJSONObject(i), templateOwnershipReflect, i + 1, messageTemplateResp.getTemplateSource());
                        list.forEach(media -> {
                            setCardHeight(jsonObject, media);
                        });
                        medias.addAll(list);
                        Layout layout = new Layout();
                        String width = jsonObject.getString("width");
                        setCardWidth(layout, width);
                        supplierTemplateContentReq.setLayout(layout);
                    }
                }
                supplierTemplateContentReq.setMedia(medias);
                if (!CollectionUtil.isEmpty(suggestions)) {
                    supplierTemplateContentReq.setSuggestions(suggestions);
                }
                break;
            case 1:
                //发送文本
                JSONObject input = jsonObject.getObject("input", JSONObject.class);
                String text = ObjectUtil.isEmpty(input) ? "" : input.getString("value");
                supplierTemplateContentReq.setText(text);
                //如果按钮信息不为空就加按钮信息
                if (!CollectionUtil.isEmpty(suggestions)) {
                    supplierTemplateContentReq.setSuggestions(suggestions);
                }
                break;
            //发送地址
            case 8:
                JSONArray jsonArray = jsonObject.getJSONObject("locationDetail").getJSONArray("names");
                if (CollectionUtil.isNotEmpty(jsonArray)) {
                    replaceVariable(messageTemplateResp, jsonArray);
                }
                JSONObject newInfo = JSONObject.parseObject(messageTemplateResp.getModuleInformation());
                JSONObject locationDetailJson = newInfo.getJSONObject("locationDetail");
                String locationDetail = ObjectUtil.isEmpty(locationDetailJson) ? "" : locationDetailJson.getString("value");
                log.info("jsonObject is {}", newInfo);
                log.info("替换以后的模板 messageTemplateResp {}", messageTemplateResp.getModuleInformation());
                log.info("发送地理位置的描述信息为：{}", locationDetail);
                String latitude = jsonObject.getJSONObject("baidu").getString("lat");
                String longitude = jsonObject.getJSONObject("baidu").getString("lng");
                String location = "geo:" + latitude + "," + longitude + ";crs=gcj02;u=10;rcs-l=" + locationDetail;
                supplierTemplateContentReq.setText(location);
                if (!CollectionUtil.isEmpty(suggestions)) {
                    supplierTemplateContentReq.setSuggestions(suggestions);
                }
                break;
            default:
                break;
        }
        List<Parameter> parameters=new ArrayList<>();
        if( Constants.TEMPLATE_SOURCE_TEMPLATEMANAGE==messageTemplateResp.getTemplateSource()) {
            parameters.add(new Parameter("STRING", "detailId", "0"));
        }
        RobotUtils.setVariableParam( moduleInformation, parameters);
        fileMessage.setParameters(BeanUtil.copyToList(parameters,TemplateParameterReq.class));
        return fileMessage;
    }

    /**
     * 获取卡片的高度
     *
     * @param jsonObject
     * @param media
     */
    private void setCardHeight(JSONObject jsonObject, Media media) {
        String height = jsonObject.getString("height");
        if (StrUtil.isEmpty(height)) {
            media.setHeight("MEDIUM_HEIGHT");
            return;
        }
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

    /**
     * 构建按钮信息
     *
     * @param buttonJson 按钮数组
     * @return 构建参数
     */
    private List<Suggestions> buildButton(JSONArray buttonJson, Integer btnType, Integer cardNum, Integer templateSource, String supplierTag) {
        log.info("buttonJson is {}", buttonJson);
        List<Suggestions> suggestions = new ArrayList<>();
        if (!CollectionUtil.isEmpty(buttonJson)) {
            for (int i = 0; i < buttonJson.size(); i++) {

                Suggestions suggestion = new Suggestions();
                JSONObject button = buttonJson.getJSONObject(i);
                //按钮类型
                Integer type = button.getInteger("type");
                //兼容大模型回复时生成的json
                if (ObjectUtil.isNull(type)) {
                    type = button.getInteger("buttonType");
                }
                String buttonType = getButtonType(type);
                log.info("type is {}", type);
                log.info("buttonType is {}", buttonType);
                //蜂动不支持按钮忽略
                if (StrUtil.equals(supplierTag, Constants.SUPPLIER_TAG_FONTDO) && (TemplateButtonTypeConst.capture.equals(type) || TemplateButtonTypeConst.sendMsg.equals(type)))
                    continue;
                //按钮的内容
                Map<String, String> actionParams = getActionParams(buttonType, button);
                log.info("actionParams is {}", actionParams);
                String buttonText = button.getJSONObject("buttonDetail").getJSONObject("input").getString("value");
                String buttonUUID = button.getString("uuid");
                suggestion.setType(buttonType);
                //打开app设置为urlAction
                if (StringUtils.equals("openApp", buttonType)) {
                    suggestion.setType("urlAction");
                }
                if (templateSource == Constants.TEMPLATE_SOURCE_TEMPLATEMANAGE) {
                    //自定义数据加上detailId
                    suggestion.setPostbackData(buttonUUID + "#&#&${" + Constants.TEMPLATE_BUTTON_DETAILID_PLACEHOLDER + "}#&#&" + type + "#&#&" + buttonText + "#&#&" + cardNum);
                } else {
                    suggestion.setPostbackData(buttonUUID);
                }
                suggestion.setDisplayText(buttonText);
                suggestion.setActionParams(actionParams);
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    /**
     * 获取按钮的信息
     *
     * @param buttonType 按钮的类型
     * @return 按钮信息map
     */
    private Map<String, String> getActionParams(String buttonType, JSONObject button) {
        Map<String, String> actionParams = new HashMap<>();
        JSONObject buttonDetail = button.getJSONObject("buttonDetail");
        //满足下面条件的不加参数 回复按钮、发送地址
        //根据不同按钮构建不通参数
        switch (buttonType) {
            case "mapAction":
                // 地址定位
                actionParams.put("latitude", buttonDetail.getString("localLatitude"));
                actionParams.put("longitude", buttonDetail.getString("localLongitude"));
                JSONObject locationJson = buttonDetail.getJSONObject("localLocation");
                actionParams.put("label", ObjectUtil.isEmpty(locationJson) ? "" : locationJson.getString("name"));
                break;
            case "urlAction":
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
                actionParams.put("origin",origin_url);
                actionParams.put("viewMode", "full");
                break;
            case "dialerAction":
                //打电话
                actionParams.put("phoneNumber", buttonDetail.getString("phoneNum"));
                break;
            case "composeTextAction":
                //调起指定联系人
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNum"));
                JSONObject previewInputJson = buttonDetail.getJSONObject("previewInput");
                actionParams.put("text", ObjectUtil.isEmpty(previewInputJson) ? "" : previewInputJson.getString("name"));
                break;
            case "composeVideoAction":
                //拍摄
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNumForPhoto"));
                break;
            //打开APP
            case "openApp":
                log.info("打开App按钮匹配");
                actionParams.put("url", buttonDetail.getString("linkUrl"));
                actionParams.put("application", "browser");
                actionParams.put("viewMode", "full");
                actionParams.put("origin",origin_url);
                break;
            case "calendarAction":
                log.info("打开日历");
                actionParams.put("startTime", buildCalendar(buttonDetail.getString("calendarStartTime")));
                actionParams.put("endTime", buildCalendar(buttonDetail.getString("calendarEndTime")));
                JSONObject calendarTitleJson = buttonDetail.getJSONObject("calendarTitle");
                actionParams.put("title", ObjectUtil.isEmpty(calendarTitleJson) ? "" : calendarTitleJson.getString("name"));
                JSONObject calendarMessageJson = buttonDetail.getJSONObject("calendarMessage");
                actionParams.put("description", ObjectUtil.isEmpty(calendarMessageJson) ? "" : calendarMessageJson.getString("name"));
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

    /**
     * 获取上传运营商素材，运营商返回的id
     *
     * @param jsonObject
     * @param templateOwnershipReflect
     * @return
     */
    private FileIdResp getFileTid(JSONObject jsonObject, TemplateOwnershipReflect templateOwnershipReflect) {
        log.info("发送模板消息为：{}", jsonObject);
        FileIdResp fileIdResp = new FileIdResp();
        FileTidReq tidReq = new FileTidReq();
        tidReq.setOperator(templateOwnershipReflect.getOperator());
        tidReq.setSupplierTag(templateOwnershipReflect.getSupplierTag());
        String fileTid = null;
        if (StringUtils.isNotEmpty(jsonObject.getString("pictureUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("pictureUrlId"));
            PictureReq pictureReq = new PictureReq();
            pictureReq.setPictureUrlId(jsonObject.getString("pictureUrlId"));
            fileIdResp.setType("image");
            fileTid = platformApi.getFileTid(tidReq).getFileId();
            PictureResp pictureResp = pictureApi.findByUuid(pictureReq);
            if (StringUtils.isNotEmpty(pictureResp.getThumbnailTid())) {
                fileIdResp.setThumbnailId(pictureResp.getThumbnailTid());
            }
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("videoUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("videoUrlId"));
            fileTid = platformApi.getFileTid(tidReq).getFileId();
            fileIdResp.setType("video");
            VideoReq req = new VideoReq();
            req.setVideoUrlId(jsonObject.getString("videoUrlId"));
            VideoResp videoResp = videoApi.findOneByUuid(req);
            if (StringUtils.isNotEmpty(videoResp.getThumbnailTid())) {
                fileIdResp.setThumbnailId(videoResp.getThumbnailTid());
            }
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("fileUrlId"))) {
            tidReq.setFileUrlId(jsonObject.getString("fileUrlId"));
            fileIdResp.setType("file");
            fileTid = platformApi.getFileTid(tidReq).getFileId();
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("audioUrlId"))) {
            fileIdResp.setType("audio");
            tidReq.setFileUrlId(jsonObject.getString("audioUrlId"));
            fileTid = platformApi.getFileTid(tidReq).getFileId();
        }
        fileIdResp.setFileTid(fileTid);
        return fileIdResp;
    }

    private String getType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "text");
        map.put(2, "file");
        map.put(3, "file");
        map.put(4, "file");
        map.put(5, "file");
        map.put(6, "card");
        map.put(7, "card");
        map.put(8, "location");
        return map.get(type);
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
        //组件按钮
        map.put(ButtonType.SUBSCRIBE_BTN.getCode(), "reply");
        map.put(ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), "reply");
        map.put(ButtonType.JOIN_SIGN_BTN.getCode(), "reply");
        map.put(ButtonType.SIGN_BTN.getCode(), "reply");
        return map.get(type);
    }

    private void replaceTemplateInfo(MessageTemplateDo template) {
        JSONArray jsonArray = getVariableJSONArray(template.getModuleInformation());
        if (CollectionUtils.isEmpty(jsonArray)) {
            return;
        }
        replaceVariable(template, jsonArray);
    }

    public JSONArray getVariableJSONArray(String templateInfo) {
        JSONObject templateJsonObject = JSONObject.parseObject(templateInfo);
        //读取和转换参数
        JSONArray jsonArray = new JSONArray();
        if (null != templateJsonObject.getJSONObject("input")) {
            jsonArray.addAll(templateJsonObject.getJSONObject("input").getJSONArray("names"));
        }
        getVariables(templateJsonObject, jsonArray);
        //多卡
        if (!CollectionUtil.isEmpty(templateJsonObject.getJSONArray("cardList"))) {
            JSONArray cardList = templateJsonObject.getJSONArray("cardList");
            for (int i = 0; i < cardList.size(); i++) {
                JSONObject cardJsonObject = cardList.getJSONObject(i);
                getVariables(cardJsonObject, jsonArray);
            }
        }
        return jsonArray;
    }

    private void getVariables(JSONObject templateJsonObject, JSONArray jsonArray) {
        if (null != templateJsonObject.getJSONObject("description")) {
            jsonArray.addAll(templateJsonObject.getJSONObject("description").getJSONArray("names"));
        }
        if (null != templateJsonObject.getJSONObject("title")) {
            jsonArray.addAll(templateJsonObject.getJSONObject("title").getJSONArray("names"));
        }
    }

    /**
     * 系统模板变量占位符变为供应商模板占位符
     *
     * @param template  模板
     * @param jsonArray 变量集合
     */
    private void replaceVariable(MessageTemplateDo template, JSONArray jsonArray) {
        String templateInfo = template.getModuleInformation();
        for (int i = 0; i < jsonArray.size(); i++) {
            String uuid = jsonArray.getJSONObject(i).getString("id");
            String name = jsonArray.getJSONObject(i).getString("name");
            String questionStr = String.format("question-(%s)", uuid);
            if (StrUtil.contains(templateInfo, questionStr)) {
                String reg = "\\{\\{" + String.format("question-\\(%s\\)", uuid) + "}}";
                templateInfo = templateInfo.replaceAll(reg, java.util.regex.Matcher.quoteReplacement("${" + uuid + "}"));
                continue;
            }
            String custom = String.format("custom-%s", name);
            if (StrUtil.contains(templateInfo, custom)) {
                String reg = "\\{\\{" + custom + "}}";
                templateInfo = templateInfo.replaceAll(reg, java.util.regex.Matcher.quoteReplacement("${" + uuid + "}"));
                continue;
            }
            //k  变量uuid   v name 变量名
            String reg = "\\{\\{" + uuid + "}}";
            String replaceStr = "${" + uuid + "}";
            templateInfo = templateInfo.replaceAll(reg, java.util.regex.Matcher.quoteReplacement(replaceStr));
        }
        template.setModuleInformation(templateInfo);
    }

    /**
     * 组装卡片信息
     *
     * @param jsonObject
     * @param templateOwnershipReflect
     * @param cardNum
     * @return
     */
    private List<Media> getMedias(JSONObject jsonObject, TemplateOwnershipReflect templateOwnershipReflect, Integer cardNum, Integer templateSource) {
        List<Media> mediaList = new ArrayList<>();
        Media media = new Media();
        JSONObject desJsonObj = jsonObject.getObject("description", JSONObject.class);
        String description = ObjectUtil.isEmpty(desJsonObj) ? "" : desJsonObj.getString("value");
        JSONObject titleJsonObj = jsonObject.getObject("title", JSONObject.class);
        String title = ObjectUtil.isEmpty(titleJsonObj) ? "" : titleJsonObj.getString("value");
        media.setTitle(title);
        media.setDescription(description);
        media.setContentDescription(description);
        if (null != jsonObject.getJSONArray("buttonList")) {
            JSONArray btnArray = jsonObject.getJSONArray("buttonList");
            List<Suggestions> suggestions = buildButton(btnArray, 2, cardNum, templateSource, templateOwnershipReflect.getSupplierTag());
            FileIdResp fileIdResp = getFileTid(jsonObject, templateOwnershipReflect);
            media.setMediaId(fileIdResp.getFileTid());
            media.setMediaType(fileIdResp.getType());
            media.setSuggestions(suggestions);
        }
        mediaList.add(media);
        return mediaList;
    }

    /**
     * 组装style
     *
     * @param styleInfo
     * @param styleId
     * @param styleStatus css模式  1 选择模板    2  自定义  3 无
     * @return
     */
    public TemplateStyleReq makeTemplateStyleReq(String styleInfo, String styleId, Integer styleStatus) {
        TemplateStyleReq templateStyleReq = new TemplateStyleReq();
        if (ObjectUtil.isNull(styleStatus) && StrUtil.isEmpty(styleInfo))
            return templateStyleReq;
        if (StrUtil.isEmpty(styleInfo) && 2 == styleStatus) return templateStyleReq;
        if (StrUtil.isNotEmpty(styleId)) {
            CardStyleOneReq cardStyleReq = new CardStyleOneReq();
            cardStyleReq.setId(Long.valueOf(styleId));
            CardStyleResp cardStyleResp = cardStyleApi.getCardStyleById(cardStyleReq);
            styleInfo = cardStyleResp.getStyleInfo();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(styleInfo);
        } catch (Exception ex) {
            log.warn("模板样式有问题内容为：{}", styleInfo);
            return templateStyleReq;
        }
        JSONObject background = jsonObject.getJSONObject("background");
        if (ObjectUtil.isNotNull(background)) {
            String type = background.getString("type");
            String content = background.getString("content");
            if (type.equals("color") && StrUtil.isNotEmpty(content)) {
                templateStyleReq.setBackgroundColor(content);
            }
            if (type.equals("images") && StrUtil.isNotEmpty(content)) {
                templateStyleReq.setBackgroundImage(cssDownLoadUrI + content);
            }
        }
        TemplateBodyStyleReq templateBodyStyleReq = new TemplateBodyStyleReq();
        templateStyleReq.setStyle(templateBodyStyleReq);
        templateBodyStyleReq.setTitleStyle(makeUnitStyle("title", jsonObject));
        templateBodyStyleReq.setDescriptionStyle(makeUnitStyle("desc", jsonObject));
        templateBodyStyleReq.setSuggestionsStyle(makeUnitStyle("button", jsonObject));
        return templateStyleReq;
    }

    public TemplateStyleCellReq makeUnitStyle(String key, JSONObject styleJson) {
        TemplateStyleCellReq templateStyleCellReq = new TemplateStyleCellReq();
        JSONObject jsonObject = styleJson.getJSONObject(key);
        JSONObject stylejsonObject = jsonObject.getJSONObject("style");
        if (ObjectUtil.isNotNull(stylejsonObject)) {
            BeanUtil.copyProperties(stylejsonObject, templateStyleCellReq);
        }
        return templateStyleCellReq;
    }

    /**
     * 素材校验，不通过 ，则送审
     *
     * @param templateDo
     * @return
     */
    public List<TemplateOwnershipReflect> checkMaterialAndProve(MessageTemplateDo templateDo, String operators) {
        log.info("开始检验素材是否审核通过，模板id：{},operators:{}", templateDo.getId(), operators);
        String moduleInformation = templateDo.getModuleInformation();
        JSONObject templateJson = JSONObject.parseObject(moduleInformation);
        List<String> fileUrlIds = new ArrayList<>();
        switch (templateDo.getMessageType()) {
            //图片
            case 2:
                checkPicExist(templateJson.getString("pictureUrlId"));
                fileUrlIds.add(templateJson.getString("pictureUrlId"));
                break;
            //视频
            case 3:
                checkVideoExist(templateJson.getString("videoUrlId"));
                fileUrlIds.add(templateJson.getString("videoUrlId"));
                break;
            //音频
            case 4:
                checkAudioExist(templateJson.getString("audioUrlId"));
                fileUrlIds.add(templateJson.getString("audioUrlId"));
                break;
            //文件
            case 5:
                checkFileExist(templateJson.getString("fileUrlId"));
                fileUrlIds.add(templateJson.getString("fileUrlId"));
                break;
            //单卡
            case 6:
                fileUrlIds.addAll(getTemplateFileIds(templateJson));
                break;
            //多卡
            case 7:
                JSONArray cardList = templateJson.getJSONArray("cardList");
                for (int i = 0; i < cardList.size(); i++) {
                    fileUrlIds.addAll(getTemplateFileIds(cardList.getJSONObject(i)));
                }
                break;
        }
        //卡片没有选择素材时，模板为该客户下所有机器人共有
        if (fileUrlIds.isEmpty()) {
            log.info("检验素材是否过期时，素材数量为0");
            return getTemplateOwnershipReflects(operators, templateDo);
        }
        //去素材库校验
        return checkToMaterialMysql(fileUrlIds, operators, templateDo.getTemplateSource());

    }

    void checkAudioExist(String audioId) {
        AudioReq audioReq = new AudioReq();
        audioReq.setAudioUrlId(audioId);
        AudioResp audioResp = audioApi.getFileInfoByAudioUrlId(audioReq);
        if (ObjectUtil.isEmpty(audioResp)) {
            log.info("音频不存在，{}", audioId);
            throw new BizException("资源不存在");
        }
    }

    void checkFileExist(String fileId) {
        FileInfoReq fileReq = new FileInfoReq();
        fileReq.setUuid(fileId);
        FileInfo audioResp = fileApi.getFileInfo(fileReq);
        if (ObjectUtil.isEmpty(audioResp)) {
            log.info("文件不存在，{}", fileId);
            throw new BizException("资源不存在");
        }
    }

    void checkVideoExist(String videoId) {
        VideoReq videoReq = new VideoReq();
        videoReq.setVideoUrlId(videoId);
        VideoResp videoResp = videoApi.findOneByUuid(videoReq);
        if (ObjectUtil.isEmpty(videoResp)) {
            log.info("视频不存在，{}", videoId);
            throw new BizException("资源不存在");
        }
    }

    void checkPicExist(String picId) {
        PictureReq pictureReq = new PictureReq();
        pictureReq.setPictureUrlId(picId);
        PictureResp pictureResp = pictureApi.findByUuid(pictureReq);
        if (ObjectUtil.isEmpty(pictureResp)) {
            log.info("图片不存在，{}", picId);
            throw new BizException("资源不存在");
        }
    }

    public List<TemplateOwnershipReflect> getTemplateOwnershipReflects(String operators, MessageTemplateDo messageTemplateDo) {
        List<AccountManagementResp> accountManagementResps = null;
        if (StrUtil.isNotEmpty(operators)) {
            AccountManagementTypeReq accountManagementTypeReq = new AccountManagementTypeReq();
            accountManagementTypeReq.setAccountType(operators);
            accountManagementTypeReq.setCreator(messageTemplateDo.getCreator());
            accountManagementResps = accountManagementApi.getAccountManagementByAccountTypes(accountManagementTypeReq);
        } else {
            accountManagementResps = accountManagementApi.getChatbotAccountInfoByCustomerId(messageTemplateDo.getCreator());
        }
        if (ObjectUtil.isNotNull(accountManagementResps)) {
            accountManagementResps = accountManagementResps.stream().filter(item -> CSPChatbotStatusEnum.getUseableStatus().contains(item.getChatbotStatus())).collect(Collectors.toList());
        }
        if (ObjectUtil.isEmpty(accountManagementResps)) {
            throw new BizException("没有找到可用的chatbot账号");
        }
        return makeTemplateOwnershipReflectsFromAccountManagementResps(accountManagementResps);
    }

    public List<TemplateOwnershipReflect> makeTemplateOwnershipReflectsFromAccountManagementResps(List<AccountManagementResp> accountManagementResps) {
        List<TemplateOwnershipReflect> templateOwnershipReflects = new ArrayList<>();
        for (int i = 0; i < accountManagementResps.size(); i++) {
            AccountManagementResp accountManagementRes = accountManagementResps.get(i);
            TemplateOwnershipReflect templateOwnershipReflect = new TemplateOwnershipReflect();
            templateOwnershipReflect.setSupplierTag(accountManagementRes.getSupplierTag());
            templateOwnershipReflect.setChatbotName(accountManagementRes.getAccountName());
            templateOwnershipReflect.setAccountTypeCode(accountManagementRes.getAccountTypeCode());
            templateOwnershipReflect.setOperator(accountManagementRes.getAccountType());
            templateOwnershipReflect.setChatbotAccount(accountManagementRes.getChatbotAccount());
            templateOwnershipReflects.add(templateOwnershipReflect);
        }
        return templateOwnershipReflects;
    }

    private List<TemplateOwnershipReflect> checkToMaterialMysql(List<String> fileUrlIds, String operators, Integer templateSource) {
        if (CollectionUtils.isEmpty(fileUrlIds)) {
            return new ArrayList<>();
        }
        VerificationReq req = new VerificationReq();
        req.setFileIds(fileUrlIds);
        req.setOperators(operators);
        req.setTemplateSource(templateSource);
        return platformApi.verificationShare(req);
    }

    private List<String> getTemplateFileIds(JSONObject templateJson) {
        List<String> fileIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(templateJson.getString("pictureUrlId"))) {
            checkPicExist(templateJson.getString("pictureUrlId"));
            fileIds.add(templateJson.getString("pictureUrlId"));
        }
        if (StringUtils.isNotEmpty(templateJson.getString("videoUrlId"))) {
            checkVideoExist(templateJson.getString("videoUrlId"));
            fileIds.add(templateJson.getString("videoUrlId"));
        }
        if (StringUtils.isNotEmpty(templateJson.getString("audioUrlId"))) {
            checkAudioExist(templateJson.getString("audioUrlId"));
            fileIds.add(templateJson.getString("audioUrlId"));
        }
        return fileIds;
    }
}
