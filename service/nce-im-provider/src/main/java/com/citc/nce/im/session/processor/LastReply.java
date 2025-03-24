package com.citc.nce.im.session.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.common.SendMsgClient;
import com.citc.nce.im.session.entity.BaiduWenxinResultDto;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.robot.vo.RebotSettingResp;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.citc.nce.robot.vo.UpMsgReq;
import com.github.pagehelper.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.*;

@Component
public class LastReply {
    private static Logger log = LoggerFactory.getLogger(LastReply.class);

    @Resource
    SendMsgClient sendMsgClient;

    private SecureRandom random;

    public List<Object> action(StringRedisTemplate stringRedisTemplate, UpMsgReq upMsgReq) {
        String lastReplyJson = getLastReplyJsonForRedis(stringRedisTemplate, upMsgReq);
        /**
         * 默认消息为空不发送消息
         * @author zy.qiu
         * @createdTime 2023/2/16 18:05
         */
        JSONObject jsonObject = JSONObject.parseObject(lastReplyJson);
        JSONArray array1 = getJsonArray(jsonObject, "lastReply");
//        JSONArray array2 = getJsonArray(jsonObject, "buttonList");
        List<Object> lastReplyList = getLastReply(lastReplyJson);
        if (null != array1 && array1.size() > 0) {
            //兜底回复提问，发送客户消息
            sendMsgToClient(upMsgReq, lastReplyList);
        }
        return lastReplyList;
    }

    private static JSONArray getJsonArray(JSONObject jsonObject, String str) {
        Object obj = jsonObject.get(str);
        String s = JsonUtils.obj2String(obj);
        JSONArray array = JSONArray.parseArray(s);
        return array;
    }

    private String getLastReplyJsonForRedis(StringRedisTemplate stringRedisTemplate, UpMsgReq upMsgReq) {
        StringBuffer redisKey  = new StringBuffer();
        redisKey.append(upMsgReq.getChatbotAccount());
        if (StringUtil.isNotEmpty(upMsgReq.getPhone())) {
            redisKey.append("@");
            redisKey.append(upMsgReq.getPhone());
        }
        redisKey.append(":RebotSetting");
//        String expireTime = stringRedisTemplate.opsForValue().get(redisKey + "RebotSettingexpireTime" + upMsgReq.getConversationId());
        return stringRedisTemplate.opsForValue().get(redisKey + upMsgReq.getConversationId());
    }

    private List<Object> getLastReply(String lastReplyJsonStr) {
        List<Object> lastReplyList = new ArrayList<>();
        RebotSettingModel rebotSettingModel = JsonUtils.string2Obj(lastReplyJsonStr, RebotSettingModel.class);
        String lastReply = rebotSettingModel.getLastReply();
        //解析lastReply，转换为List<String>
        List<JSONObject> lastReplyJsonList = JSON.parseArray(lastReply, JSONObject.class);
        List<RobotProcessButtonResp> buttonList = rebotSettingModel.getButtonList();
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
        switch (rebotSettingModel.getLastReplyType()) {
            case 0:
                if (lastReplyJsonList != null && lastReplyJsonList.size() > 0) {
                    lastReplyJsonList.forEach(lastReplyJson -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("msg", lastReplyJson);
                        map.put("button", objectList);
                        lastReplyList.add(map);
                    });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", "");
                    map.put("button", objectList);
                    lastReplyList.add(map);
                }

                break;
            case 1:
                if (lastReplyJsonList != null && lastReplyJsonList.size() > 0) {
                    int num = getRandom(lastReplyJsonList.size());
                    JSONObject lastReplyJson = lastReplyJsonList.get(num);
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("msg", lastReplyJson);
                    map1.put("button", objectList);
                    lastReplyList.add(map1);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", "");
                    map.put("button", objectList);
                    lastReplyList.add(map);
                }
                break;
        }
        return lastReplyList;
    }

    public void sendBaiduWenxinMsg(RebotSettingResp rebotSettingResp, BaiduWenxinResultDto resultDto, UpMsgReq upMsgReq) {
        // 拼装消息准备发送
        List<RobotProcessButtonResp> buttonList = rebotSettingResp.getButtonList();
        List<Object> replyList = new ArrayList<>();
        // 按钮
        List<Object> objectList = new ArrayList<>();
        if (buttonList != null) {
            buttonList.forEach(robotProcessButtonResp -> {
                String buttonDetail = robotProcessButtonResp.getButtonDetail();
                String uuid = robotProcessButtonResp.getUuid();
                Object parse = JSONObject.parse(buttonDetail);
                JSONObject button = (JSONObject) JSON.toJSON(robotProcessButtonResp);
                button.put("buttonDetail", parse);
                button.put("uuid", uuid);
                objectList.add(button);
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 1);
        JSONObject jsonMessageDetail = new JSONObject();
        JSONObject jsonInput = getJsonInput(resultDto.getResult());
        jsonMessageDetail.put("input", jsonInput);
        jsonObject.put("messageDetail", jsonMessageDetail);

        Map<String, Object> map = new HashMap<>();
        map.put("msg", jsonObject);
        map.put("button", objectList);
        replyList.add(map);
        sendMsgToClient(upMsgReq, replyList);
    }

    private static JSONObject getJsonInput(String name) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("name", name);
        jsonInput.put("value", name);
        jsonInput.put("names", new ArrayList<>());
        return jsonInput;
    }

    private int getRandom(int seed) {
        return random.nextInt(seed);
    }

    private void sendMsgToClient(UpMsgReq upMsgReq, List<Object> msgList) {
        msgList.forEach(msg -> {
            WsResp wsResp = new WsResp();
            BeanUtils.copyProperties(upMsgReq, wsResp);
            wsResp.setConversationId(upMsgReq.getConversationId());
            wsResp.setBody(msg);
            wsResp.setMsgType(2);
            wsResp.setPocketBottom(true);
            wsResp.setUserId(upMsgReq.getCreate());
            /**
             * 迭代五 消息记录保存发送渠道
             * 2023/3/1
             */
            wsResp.setChannelType(upMsgReq.getAccountType());
            log.info("第一步 兜底发消息：{}", JsonUtils.obj2String(wsResp));

            sendMsgClient.sendMsg(wsResp);
        });
    }
}
