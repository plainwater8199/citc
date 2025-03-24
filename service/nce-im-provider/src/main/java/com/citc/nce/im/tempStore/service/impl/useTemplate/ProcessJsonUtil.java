package com.citc.nce.im.tempStore.service.impl.useTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.im.mall.snapshot.RobotInputUtil;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotVariableReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * bydud
 * 2024/2/7
 **/
@Slf4j
public class ProcessJsonUtil {

    private final static String img_prefix = "/fileApi/file/download/id?req=";
    private final Map<Long, Csp4CustomerFrom> fromMap = new HashMap<>();
    private final Map<Long, Csp4CustomerAudio> audioMap = new HashMap<>();
    private final Map<Long, Csp4CustomerImg> imgMap = new HashMap<>();
    private final Map<Long, Csp4CustomerVideo> videoMap = new HashMap<>();
    private final Map<Long, RobotOrderReq> orderDbMap;
    private final Map<Long, RobotVariableReq> variableDbMap;

    public ProcessJsonUtil(Map<Long, RobotOrderReq> orderDbMap, Map<Long, RobotVariableReq> variableDbMap) {
        this.orderDbMap = Objects.isNull(orderDbMap) ? new HashMap<>() : orderDbMap;
        this.variableDbMap = Objects.isNull(variableDbMap) ? new HashMap<>() : variableDbMap;
    }


    public Map<Long, Csp4CustomerAudio> getAudioMap() {
        return audioMap;
    }

    public Map<Long, Csp4CustomerImg> getImgMap() {
        return imgMap;
    }

    public Map<Long, Csp4CustomerVideo> getVideoMap() {
        return videoMap;
    }

    public void changeOrderList(JSONObject jo) {
        JSONArray orderList = jo.getJSONArray("orderList");
        if (Objects.nonNull(orderList) && !orderList.isEmpty()) {
            for (Object temp : orderList) {
                JSONObject order = (JSONObject) temp;
                Long oldId = order.getLong("orderContent");
                RobotOrderReq orderBean = orderDbMap.get(oldId);
                if (Objects.nonNull(orderBean)) {
                    Long newId = orderBean.getId();
                    order.put("orderContent", newId.toString());
                    JSONObject orderName = order.getJSONObject("orderName");
                    if (Objects.nonNull(orderName)) {
                        orderName.put("label", orderBean.getOrderName());
                        orderName.put("value", newId.toString());
                    }
                }

            }
        }
    }

    public void changeVarsList(JSONObject jo) {
        JSONArray varsList = jo.getJSONArray("varsList");
        if (Objects.nonNull(varsList) && !varsList.isEmpty()) {
            for (Object temp : varsList) {
                JSONObject vars = (JSONObject) temp;
                Long oldId = getLong(vars, "variableNameId");
                if (Objects.nonNull(oldId)) {
                    RobotVariableReq orderBean = variableDbMap.get(oldId);
                    if (ObjectUtils.isNotEmpty(orderBean)) {
                        Long newId = orderBean.getId();
                        vars.put("variableNameId", newId.toString());
                        JSONObject orderName = vars.getJSONObject("variableName");
                        orderName.put("label", orderBean.getVariableName());
                        orderName.put("value", newId.toString());
                        String variableValue = "{{custom-" + orderBean.getVariableName() + "}}";
                        vars.put("variableNameValue", variableValue);
                    }
                }
                List<RobotInputUtil.Variable> variables = RobotInputUtil.getVariables(vars.getString("variableRightName"));
                if (CollectionUtils.isNotEmpty(variables)) {

                    String rightName = vars.getString("variableValue");//无id
                    String variableVale = vars.getString("variableRightName");//有id
                    for (Map.Entry<RobotInputUtil.Variable, RobotInputUtil.Variable> entry : changeCustomVariables(variables).entrySet()) {
                        RobotInputUtil.Variable oldEntity = entry.getKey();
                        RobotInputUtil.Variable newEntity = entry.getValue();
                        rightName = rightName.replace(oldEntity.toDisplay(), newEntity.toDisplay());
                        variableVale = variableVale.replace(oldEntity.toCommonValue(), newEntity.toCommonValue());
                    }
                    vars.put("variableValue", rightName);
                    vars.put("variableRightName", variableVale);
                }
            }
        }
    }

    public void changeButtonList(JSONObject jo) {
        JSONArray buttonList = jo.getJSONArray("buttonList");
        changeButtonList(buttonList);
    }

    public String changeButtonList(String shortcutButton) {
        if (StringUtils.isEmpty(shortcutButton)) return shortcutButton;
        JSONArray array = JSON.parseArray(shortcutButton);
        changeButtonList(array);
        return array.toJSONString();
    }

    private void changeButtonList(JSONArray buttonList) {
        if (Objects.nonNull(buttonList) && !buttonList.isEmpty()) {
            for (Object temp : buttonList) {
                JSONObject vars = (JSONObject) temp;
                changeButtonNode(vars);
            }
        }
    }


    /**
     * type 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
     */
    public void changeContentBody(JSONObject jo) {
        JSONArray contentBody = jo.getJSONArray("contentBody");
        if (Objects.nonNull(contentBody) && !contentBody.isEmpty()) {
            for (Object temp : contentBody) {
                JSONObject vars = (JSONObject) temp;
                JSONObject messageDetail = vars.getJSONObject("messageDetail");
                Integer type = vars.getInteger("type");
                if (Objects.nonNull(messageDetail)) {
                    changeInputNode(messageDetail);
                    changeDescriptionNode(messageDetail);
                    changeButtonList(messageDetail);
                    changeTitleNode(messageDetail);
                    changeLocationDetailNode(messageDetail);
                    changeCardList(messageDetail);
                    if (Objects.nonNull(type)) {
                        switch (type) {
                            case 2:
                                changeImgNode(messageDetail);
                                break;
                            case 3:
                                changeVideoNode(messageDetail);
                                break;
                            case 4:
                                changeAudioNode(messageDetail);
                                break;
                            case 5:
                                changeFileNode(messageDetail);
                                break;
                            case 6:
                                changeCard(messageDetail);
                                break;
                            case 7:
                                changeArrayCard(messageDetail);
                                break;
                            default:
                                break;
                        }
                    }
                }

            }
        }
    }

    public void changeConditionList(JSONObject entity) {
        JSONArray list = entity.getJSONArray("conditionList");
        if (CollectionUtils.isEmpty(list)) return;
        for (Object temp : list) {
            JSONObject var = (JSONObject) temp;
            String conditionCode = var.getString("conditionCode"); //条件
            if (StringUtils.isNotEmpty(conditionCode)) {
                List<RobotInputUtil.Variable> variables = RobotInputUtil.getVariables(conditionCode);
                if (CollectionUtils.isNotEmpty(variables)) {
                    String conditionName = var.getString("conditionCodeValue");
                    String conditionValue = var.getString("conditionCode");

                    for (Map.Entry<RobotInputUtil.Variable, RobotInputUtil.Variable> entry : changeCustomVariables(variables).entrySet()) {
                        RobotInputUtil.Variable oldEntity = entry.getKey();
                        RobotInputUtil.Variable newEntity = entry.getValue();
                        conditionName = conditionName.replace(oldEntity.toDisplay(), newEntity.toDisplay());
                        conditionValue = conditionValue.replace(oldEntity.toCommonValue(), newEntity.toCommonValue());
                    }

                    var.put("conditionCodeValue", conditionName);
                    var.put("conditionCode", conditionValue);
                }

            }
            String conditionContent = var.getString("conditionContent"); //判断值
            if (StringUtils.isNotEmpty(conditionContent)) {
                List<RobotInputUtil.Variable> variables = RobotInputUtil.getVariables(conditionContent);
                if (CollectionUtils.isNotEmpty(variables)) {
                    String anName = var.getString("conditionContentValue");
                    String anValue = var.getString("conditionContent");
                    for (Map.Entry<RobotInputUtil.Variable, RobotInputUtil.Variable> entry : changeCustomVariables(variables).entrySet()) {
                        RobotInputUtil.Variable oldEntity = entry.getKey();
                        RobotInputUtil.Variable newEntity = entry.getValue();
                        anName = anName.replace(oldEntity.toDisplay(), newEntity.toDisplay());
                        anValue = anValue.replace(oldEntity.toCommonValue(), newEntity.toCommonValue());
                    }
                    var.put("conditionContentValue", anName);
                    var.put("conditionContent", anValue);
                }
            }
        }

    }

    /**
     * @return map<文本中 ， 数据库></>
     */
    private Map<RobotInputUtil.Variable, RobotInputUtil.Variable> changeCustomVariables(List<RobotInputUtil.Variable> variables) {
        if (CollectionUtils.isEmpty(variables)) return new HashMap<>();
        List<RobotInputUtil.Variable> collect = variables.stream().filter(s -> RobotInputUtil.type_custom.equalsIgnoreCase(s.getType())).collect(Collectors.toList());
        Map<RobotInputUtil.Variable, RobotInputUtil.Variable> map = new HashMap<>(collect.size());
        for (RobotInputUtil.Variable variable : collect) {
            RobotVariableReq req = variableDbMap.get(Long.valueOf(variable.getId()));
            if (Objects.nonNull(req)) {
                map.put(variable, new RobotInputUtil.Variable(String.valueOf(req.getId()), req.getVariableName(), RobotInputUtil.type_custom));
            }
        }
        return map;
    }

    public void changeVerifyList(JSONObject entity) {
        JSONArray list = entity.getJSONArray("verifyList");
        if (CollectionUtils.isEmpty(list)) return;
        for (Object temp : list) {
            JSONObject var = (JSONObject) temp;
            List<RobotInputUtil.Variable> variables = RobotInputUtil.getVariables(var.getString("verifyContent"));
            if (CollectionUtils.isNotEmpty(variables)) {
                String verifyContent = var.getString("verifyContent");
                for (Map.Entry<RobotInputUtil.Variable, RobotInputUtil.Variable> entry : changeCustomVariables(variables).entrySet()) {
                    RobotInputUtil.Variable oldEntity = entry.getKey();
                    RobotInputUtil.Variable newEntity = entry.getValue();
                    verifyContent = verifyContent.replace(oldEntity.toCommonValue(), newEntity.toCommonValue());
                }
                var.put("verifyContent", verifyContent);
            }
        }

    }

    //多卡
    private void changeArrayCard(JSONObject messageDetail) {
        JSONArray cardList = messageDetail.getJSONArray("cardList");
        if (Objects.nonNull(cardList) && !cardList.isEmpty()) {
            for (Object object : cardList) {
                changeCard((JSONObject) object);
            }
        }
    }

    //单卡
    private void changeCard(JSONObject messageDetail) {
        changeTitleNode(messageDetail);
        changeDescriptionNode(messageDetail);
        changeButtonList(messageDetail);
        Long id = getId(messageDetail);
        if (Objects.nonNull(id)) {
            if (StringUtils.isNotEmpty(messageDetail.getString("pictureUrlId"))) {
                Csp4CustomerImg img = imgMap.get(id);
                if (Objects.nonNull(img)) {
                    messageDetail.put("id", img.getNewId().toString());
                    messageDetail.put("fileName", img.getPictureName());
                    messageDetail.put("imgSrc", img_prefix + img.getPictureUrlid());
                    messageDetail.put("pictureUrlId", img.getPictureUrlid());
                }
            }

            if (StringUtils.isNotEmpty(messageDetail.getString("videoUrlId"))) {
                Csp4CustomerVideo video = videoMap.get(id);
                if (Objects.nonNull(video)) {
                    messageDetail.put("id", video.getNewId().toString());
                    messageDetail.put("fileName", video.getName());
                    messageDetail.put("imgSrc", img_prefix + video.getCover());
                    messageDetail.put("videoUrlId", video.getFileId());
                }
            }


            if (StringUtils.isNotEmpty(messageDetail.getString("audioUrlId"))) {
                Csp4CustomerAudio audio = audioMap.get(id);
                if (Objects.nonNull(audio)) {
                    messageDetail.put("id", audio.getNewId().toString());
                    messageDetail.put("fileName", audio.getName());
                    messageDetail.put("audioUrlId", audio.getFileId());
                }
            }

        }
    }

    private void changeButtonNode(JSONObject vars) {
        JSONObject buttonDetail = vars.getJSONObject("buttonDetail");
        if (Objects.nonNull(buttonDetail)) {
            changeInputNode(buttonDetail);
            //按钮中的表单
            Long formId = getLong(buttonDetail, "formId");
            if (Objects.nonNull(formId)) {
                Csp4CustomerFrom from = fromMap.get(formId);
                if (Objects.nonNull(from)) {
                    buttonDetail.put("formId", from.getNewId().toString());
                    buttonDetail.put("linkUrl", from.getFormName());
                    buttonDetail.put("formShareUrl", from.getFormShareUrl());
                    buttonDetail.put("shortUrl", from.getShortUrl());
                }
            }
        }
    }

    private void changeVideoNode(JSONObject messageDetail) {
        Long id = getId(messageDetail);
        if (Objects.nonNull(id)) {
            Csp4CustomerVideo video = videoMap.get(id);
            if (Objects.nonNull(video)) {
                messageDetail.put("id", video.getNewId().toString());
                messageDetail.put("imgSrc", img_prefix + video.getCover());
                messageDetail.put("name", video.getName());
                messageDetail.put("mainCoverId", video.getCover());
                messageDetail.put("videoUrlId", video.getFileId());
            }
        }
    }


    private void changeAudioNode(JSONObject messageDetail) {
        Long id = getId(messageDetail);
        if (Objects.nonNull(id)) {
            Csp4CustomerAudio audio = audioMap.get(id);
            if (Objects.nonNull(audio)) {
                messageDetail.put("id", audio.getNewId().toString());
                messageDetail.put("audioUrlId", audio.getFileId());
                messageDetail.put("name", audio.getName());
                messageDetail.put("duration", audio.getDuration());
                messageDetail.put("size", audio.getSize());
                messageDetail.put("type", audio.getFormat());
            }
        }
    }

    private void changeImgNode(JSONObject messageDetail) {
        Long id = getId(messageDetail);
        if (Objects.nonNull(id)) {
            Csp4CustomerImg img = imgMap.get(id);
            if (Objects.nonNull(img)) {
                messageDetail.put("id", img.getNewId().toString());
                messageDetail.put("imgSrc", img_prefix + img.getPictureUrlid());
                messageDetail.put("name", img.getPictureName());
                messageDetail.put("pictureUrlId", img.getPictureUrlid());
            }
        }
    }


    private void changeFileNode(JSONObject messageDetail) {
        //取消
    }

    private void changeCardList(JSONObject messageDetail) {
        JSONArray cardList = messageDetail.getJSONArray("cardList");
        if (Objects.nonNull(cardList) && !cardList.isEmpty()) {
            for (Object entity : cardList) {
                JSONObject card = (JSONObject) entity;
                changeDescriptionNode(card);
                changeButtonList(card);
                changeTitleNode(card);
            }
        }
    }

    private void changeLocationDetailNode(JSONObject jsonObject) {
        JSONObject locationDetail = jsonObject.getJSONObject("locationDetail");
        namesChild(locationDetail);
    }

    private void changeTitleNode(JSONObject jsonObject) {
        JSONObject title = jsonObject.getJSONObject("title");
        namesChild(title);
    }

    private void changeDescriptionNode(JSONObject jo) {
        JSONObject description = jo.getJSONObject("description");
        namesChild(description);
    }

    private void changeInputNode(JSONObject buttonDetail) {
        JSONObject input = buttonDetail.getJSONObject("input");
        namesChild(input);
        if (Objects.nonNull(input)) {
            List<RobotInputUtil.Variable> cspVariables = RobotInputUtil.getVariables(input.getString("name"));
            if (CollectionUtils.isNotEmpty(cspVariables)) {
                String name = input.getString("name");
                String value = input.getString("value");

                for (Map.Entry<RobotInputUtil.Variable, RobotInputUtil.Variable> entry : changeCustomVariables(cspVariables).entrySet()) {
                    RobotInputUtil.Variable oldEntity = entry.getKey();
                    RobotInputUtil.Variable newEntity = entry.getValue();
                    name = name.replace(oldEntity.toDisplay(), newEntity.toDisplay());
                    value = value.replace(oldEntity.toCommonValue(), newEntity.toCommonValue());
                }

                input.put("name", name);
                input.put("value", value);
            }
        }
    }

    private void namesChild(JSONObject description) {
        if (Objects.nonNull(description)) {
            JSONArray names = description.getJSONArray("names");
            if (Objects.nonNull(names) && !names.isEmpty()) {
                for (Object var : names) {
                    JSONObject entity = (JSONObject) var;
                    //变量或者指令的id
                    String idStr = entity.getString("id");
                    try {
                        if (idStr.startsWith("sys-")) continue;
                        Long oldId = Long.valueOf(idStr);
                        RobotVariableReq variable = variableDbMap.get(oldId);
                        if (Objects.nonNull(variable)) {
                            entity.put("name", variable.getVariableName());
                            entity.put("id", variable.getId());
                        }
                        RobotOrderReq order = orderDbMap.get(oldId);
                        if (Objects.nonNull(order)) {
                            entity.put("name", order.getOrderName());
                            entity.put("id", order.getId());
                        }
                    } catch (Exception e) {
                        log.warn("child 解析id失败 idStr：{}", idStr);
                    }
                }
            }

            List<RobotInputUtil.Variable> variables = RobotInputUtil.getVariables(description.getString("name"));
            if (CollectionUtils.isNotEmpty(variables)) {
                String name = description.getString("value");
                String value = description.getString("name");
                for (Map.Entry<RobotInputUtil.Variable, RobotInputUtil.Variable> entry : changeCustomVariables(variables).entrySet()) {
                    RobotInputUtil.Variable oldVariable = entry.getKey();
                    RobotInputUtil.Variable newVariable = entry.getKey();
                    name = name.replace(oldVariable.toDisplay(), newVariable.toDisplay());
                    value = value.replace(oldVariable.toCommonValue(), newVariable.toCommonValue());
                }
                description.put("value", name);
                description.put("name", value);
            }
        }
    }

    public void putForm(Map<Long, Csp4CustomerFrom> map) {
        fromMap.putAll(map);
        log.info("fromMap {}", fromMap);
    }

    public void putAudio(Map<Long, Csp4CustomerAudio> map) {
        audioMap.putAll(map);
        log.info("audioMap {}", audioMap);
    }

    public void putImg(Map<Long, Csp4CustomerImg> map) {
        imgMap.putAll(map);
        log.info("imgMap {}", imgMap);
    }

    public void putVideo(Map<Long, Csp4CustomerVideo> map) {
        videoMap.putAll(map);
        log.info("videoMap {}", videoMap);
    }

    /**
     * 获取JSONObject 中的id
     */
    private static Long getId(JSONObject messageDetail) {
        return getLong(messageDetail, "id");
    }

    /**
     * 获取JSONObject 中的id
     */
    private static Long getLong(JSONObject messageDetail, String filed) {
        try {
            if (Objects.isNull(messageDetail)) return null;
            return messageDetail.getLong(filed);
        } catch (NumberFormatException | JSONException formatException) {

        } catch (Exception e) {
            log.error("getLong error messageDetail:{}  filed:{}", messageDetail, filed);
        }
        return null;
    }


    public String changeFiveG(String moduleInformation) {
        JSONObject parse = JSON.parseObject(moduleInformation);
        if (Objects.isNull(parse)) return moduleInformation;
        changeCard(parse);
        changeArrayCard(parse);
        return parse.toJSONString();
    }

    public List<String> getCustomMaterialSourceIds() {
        List<String> list = new ArrayList<>();
        if (!org.springframework.util.CollectionUtils.isEmpty(imgMap)) {
            list.addAll(imgMap.values().stream().map(Csp4CustomerImg::getPictureUrlid)
                    .collect(Collectors.toList()));
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(audioMap)) {
            list.addAll(audioMap.values().stream().map(Csp4CustomerAudio::getFileId)
                    .collect(Collectors.toList()));
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(videoMap)) {
            list.addAll(videoMap.values().stream().map(Csp4CustomerVideo::getFileId)
                    .collect(Collectors.toList()));
        }
        return list;
    }

    public Map<Long, RobotOrderReq> getOrderDbMap() {
        return orderDbMap;
    }

    public Map<Long, RobotVariableReq> getVariableDbMap() {
        return variableDbMap;
    }


}
