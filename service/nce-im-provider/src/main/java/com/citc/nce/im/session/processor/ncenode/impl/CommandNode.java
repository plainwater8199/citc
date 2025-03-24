package com.citc.nce.im.session.processor.ncenode.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.common.HttpURLConnectionUtil;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.processor.NceRobotNode;
import com.citc.nce.im.session.processor.NodeProcessor;
import com.citc.nce.im.session.processor.NodeType;
import com.citc.nce.im.session.processor.ParsedProcessorConfig;
import com.citc.nce.im.session.processor.SessionContext;
import com.citc.nce.im.session.processor.Variable;
import com.citc.nce.im.session.processor.bizModel.OrderModel;
import com.citc.nce.im.session.processor.ncenode.CommandService;
import com.citc.nce.robot.RobotOrderApi;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.vo.NodeActResult;
import com.citc.nce.robot.vo.RobotOrderResp;
import com.citc.nce.robot.vo.UpMsgReq;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/5 15:34
 * @Version: 1.0
 * @Description: 指令节点
 */
public class CommandNode extends NceRobotNode {

    private Logger log = LoggerFactory.getLogger(BranchNode.class);

    private RobotOrderApi robotOrderApi = ApplicationContextUil.getBean(RobotOrderApi.class);

//    private RobotProcessorApi robotProcessorApi = ApplicationContextUil.getBean(RobotProcessorApi.class);

    private Variable variable = ApplicationContextUil.getBean(Variable.class);

    @JSONField(serialize = false)
    private StringRedisTemplate stringRedisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
    /**
     * 指令集合
     */
    public List<Order> orderList;

    public String sucessId;

    public String failId;

    private CommandService commandService = SpringUtil.getBean(CommandService.class);

    private int OVER_TIME = 60;

    @Override
    public boolean enterCondition(SessionContext sessionContext, NodeProcessor nodeProcessor) {

        return super.enterCondition(sessionContext, nodeProcessor);
    }

    @Override
    public NodeActResult act(SessionContext sessionContext, NodeProcessor nodeProcessor) throws Exception {
        Map<Object, Object> responseMap = Maps.newHashMap();
        String childIdx = "";
        AtomicBoolean hasNext = new AtomicBoolean(true);
        AtomicBoolean atomicBoolean = processCommandNode(sessionContext, nodeProcessor, responseMap, hasNext);
        return NodeActResult.builder().childIdx(setReturn(nodeProcessor, childIdx, atomicBoolean)).result(responseMap).next(atomicBoolean.get()).build();
    }


    private String setReturn(NodeProcessor nodeProcessor, String childIdx, AtomicBoolean hasNext) {
        if (hasNext.get()){
            ParsedProcessorConfig parsedProcessorConfig = nodeProcessor.getParsedProcessorConfig();
            String sucessId = this.sucessId;
            List<String> list = parsedProcessorConfig.getNodeIdRelations().get(sucessId);
            if (list.size() > 0) {
                childIdx = list.get(0);
                hasNext.set(true);
            } else {
                nodeProcessor.checkAndClean();
                hasNext.set(false);
            }
        }else {
            ParsedProcessorConfig parsedProcessorConfig = nodeProcessor.getParsedProcessorConfig();
            String failId = this.failId;
            List<String> list = parsedProcessorConfig.getNodeIdRelations().get(failId);
            if (list.size() > 0) {
                childIdx = list.get(0);
                hasNext.set(true);
            } else {
                nodeProcessor.checkAndClean();
                hasNext.set(false);
            }
        }
        return childIdx;
    }

    private AtomicBoolean processCommandNode(SessionContext sessionContext, NodeProcessor nodeProcessor, Map<Object, Object> responseMap, AtomicBoolean hasNext) {
        for (Order order:
        orderList) {
            OrderPageParam orderPageParam = new OrderPageParam();
            if (StringUtils.hasText(order.orderContent)) {
                orderPageParam.setId(Long.valueOf(order.orderContent));
                RobotOrderResp robotOrderResp = robotOrderApi.listAllOrder(orderPageParam);
                OrderModel orderModel = new OrderModel();
                BeanUtils.copyProperties(robotOrderResp.getList().get(0), orderModel);
                orderModel.setBodylist(robotOrderResp.getList().get(0).getBodyList());
                String httpResponse = "";
                switch (order.orderType) {
                    case 0:
                        try {
                            String headerList = orderModel.getHeaderList();
                            headerList = variable.translate(headerList, nodeProcessor);
                            List<JSONObject> headMapList = JSONObject.parseArray(headerList.replace("\\", ""), JSONObject.class);
                            Integer requestType = orderModel.getRequestType();
                            String requestUrl = orderModel.getRequestUrl();
                            requestUrl = variable.translate(requestUrl, nodeProcessor);
                            Integer requestBodyType = orderModel.getRequestBodyType();
                            //todo xml格式后期做
                            Integer requestRawType = orderModel.getRequestRawType();
                            if (0 == requestType) {
                                httpResponse = HttpURLConnectionUtil.doGet(requestUrl);
                            } else {
                                String contentType = "";
                                String body = "";
                                String bodyStr = orderModel.getBodylist();
                                bodyStr = variable.translate(bodyStr, nodeProcessor);
                                log.info("替换后的 bodyStr is {}",bodyStr);
                                switch (requestBodyType) {
                                    case 0:
                                        /**
                                         * 2023-5-24
                                         * http指令
                                         */
                                        contentType = "multipart/form-data; boundary=---------------------------" + System.currentTimeMillis();
                                        List<JSONObject> lists = JSONObject.parseArray(bodyStr.replace("\\", ""), JSONObject.class);
                                        Map<String, Object> map = new HashMap<>();
                                        if (null != lists){
                                            for (int i = 0; i < lists.size(); i++) {
                                                JSONObject jsonObject = lists.get(i);
                                                if (jsonObject.size()>0){
                                                    map.put(jsonObject.get("name").toString(), JsonPath.read(jsonObject, "$.value.value"));
                                                }
                                            }
                                        }
                                        body = JsonUtils.obj2String(map);
                                        break;
                                    case 1:
                                        JSONObject jsonObject = JsonUtils.string2Obj(bodyStr, JSONObject.class);
                                        switch (requestRawType){
                                            case 0:
                                                contentType = "text/plain;charset=utf-8";
                                                body = jsonObject.getString("value");
                                                break;
                                            case 1:
                                                /**
                                                 * 2023-5-6
                                                 * 接入baidu的API
                                                 * JSON解析报错，去除 replace
                                                 JSONObject jsonObject = JsonUtils.string2Obj(bodyStr.replace("\\", ""), JSONObject.class);
                                                 */
//                                                body = jsonObject.toJSONString();
                                                body = jsonObject.getString("value");
                                                body = body.replaceAll("\\n","");
                                                contentType = "application/json;charset=utf-8";
                                                break;
                                            default:
                                        }
                                    default:
                                }
                                httpResponse = HttpURLConnectionUtil.doPost(requestUrl, headMapList, body,contentType);
                            }
                        } catch (Exception e) {
                            log.info("指令节点执行异常：", e);
                            hasNext.set(false);
                        }finally {
                            if (!StringUtils.hasText(httpResponse) && order.orderType != 1){
                                log.info("http指令节点执行异常：");
                                hasNext.set(false);
                            }else {
                                try {
                                    //根据配置组装返回结果
                                    List<JSONObject> listMap = JSONArray.parseArray(orderModel.getResponseList(), JSONObject.class);
                                    String finalHttpResponse = httpResponse;
                                    if (StringUtils.hasText(finalHttpResponse)) {
                                        log.info(finalHttpResponse);
                                        listMap.forEach(jsonObject -> {
                                            /**
                                             * 当响应监听内容某个值获取失败时，不要影响其他变量的赋值
                                             * 2023/1/10 18:32
                                             */
                                            String value = Optional.ofNullable(jsonObject.getString("value")).orElse("");
                                            String variable = Optional.ofNullable(jsonObject.getString("variable")).orElse("");
                                            Object author = null;
                                            try {
                                                author = JsonPath.read(finalHttpResponse, "$." + value);
                                            } catch (Exception e) {
                                                author = " ";
                                            }
                                            responseMap.put(variable, author);
                                            sessionContext.addVar(nodeProcessor.getUpMsgReq(), "{{custom-" + variable + "}}", JsonUtils.obj2String(author));
                                        });
                                    }
                                }catch (Exception e){
                                    log.info("根据配置组装返回结果异常");
                                }
                            }
                        }
                        break;
                    // 自定义指令
                    case 1:
                        try {
                            UpMsgReq upMsgReq = nodeProcessor.getUpMsgReq();
                            StringBuffer redisKey  = new StringBuffer();
                            redisKey.append(upMsgReq.getChatbotAccount());
                            if (StringUtil.isNotEmpty(upMsgReq.getPhone())) {
                                redisKey.append("@");
                                redisKey.append(upMsgReq.getPhone());
                            }
                            redisKey.append("@").append(upMsgReq.getCreate());
                            // 把机器人Id + 当前毫秒生成UUID 作为key
                            StringBuffer sb = new StringBuffer();
                            sb.append(orderModel.getRobotId()).append(":");
                            String executeLoop = commandService.processCustomCommand(orderModel, nodeProcessor, redisKey.toString());
                            Map<String, String> stringMap = commandService.setVariable(redisKey.toString());
                            log.info("自定义指令执行完毕后的变量值 stringMap {}", stringMap);
                            nodeProcessor.getSessionContext().setVar(stringMap);
                            redisKey = new StringBuffer();
                            redisKey.append(upMsgReq.getChatbotAccount());
                            if (StringUtil.isNotEmpty(upMsgReq.getPhone())) {
                                redisKey.append("@");
                                redisKey.append(upMsgReq.getPhone());
                            }
                            redisKey.append(":");
                            Iterator<Map.Entry<String, String>> iterator = stringMap.entrySet().iterator();
                            if (iterator.hasNext()) {
//                                stringRedisTemplate.expire(redisKey + upMsgReq.getConversationId(), 1, TimeUnit.MILLISECONDS);
                                stringRedisTemplate.delete(redisKey + upMsgReq.getConversationId());
                            }
                            while (iterator.hasNext()) {
                                Map.Entry<String, String> next = iterator.next();
                                log.info("################ next.getValue() ################ : {}", next.getValue());
                                stringRedisTemplate.opsForHash().put(redisKey + upMsgReq.getConversationId(), next.getKey(), next.getValue());
                                sessionContext.addVar(nodeProcessor.getUpMsgReq(), next.getKey(), next.getValue());
                            }
                            if (org.apache.commons.lang.StringUtils.isEmpty(executeLoop)
                                    ||org.apache.commons.lang.StringUtils.equals("1", executeLoop)) {
                                hasNext.set(false);
                                if (org.apache.commons.lang.StringUtils.isEmpty(executeLoop)) {
                                    commandService.sendMsgByResult(redisKey.toString(), executeLoop);
                                }
                            }
                        } catch (Exception e) {
                            hasNext.set(false);
                            log.info("processCustomCommand Exception {}", e);
                            throw new RuntimeException(e);
                        }
                        break;
                    default:
                }
            }
        }
        return hasNext;
    }


    @Override
    public Integer nodeType() {
        return NodeType.COMMAND_NODE.getCode();
    }

    @Data
    public static class Order {
        /**
         * 0 http指令1自定义指令
         */
        private Integer orderType;

        /**
         * 指令内容
         */
        private String orderContent;
    }
}
