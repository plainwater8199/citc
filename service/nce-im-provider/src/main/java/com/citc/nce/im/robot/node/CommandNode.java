package com.citc.nce.im.robot.node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.common.HttpURLConnectionUtil;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.OrderDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.enums.RouteConditionStrategy;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.processor.bizModel.OrderModel;
import com.citc.nce.im.session.processor.ncenode.CommandService;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Slf4j
public class CommandNode extends Node {
    /**
     * 指令集合
     */
    private List<OrderInfo> orders;
    /**
     * 执行结果
     */
    private Boolean execResult;

    /**
     * 成功和失败的路由。
     */
    private String sucessId;

    private String failId;


    /**
     * 没有特殊操作
     *
     * @param msgDto 上行消息
     */
    @Override
    void beforeExec(MsgDto msgDto) {
    }

    /**
     * 执行指令
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        try {
            StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
            String conversationId = msgDto.getConversationId();
            String phone = msgDto.getPhone();
            for (OrderInfo orderInfo : orders) {
                //根据指令ID查询到指令详情
                String orderId = orderInfo.getOrderContent();
                String orderKey = RedisUtil.getOrderKey(conversationId);
                if (!redisTemplate.opsForHash().hasKey(orderKey, orderId)) {
                    log.error("未能查询到对应指令,跳过执行,id：{}", orderId);
                    this.execResult = false;
                    break;
                }
                String orderStr = (String) redisTemplate.opsForHash().get(orderKey, orderId);
                OrderDto orderDto = JsonUtils.string2Obj(orderStr, OrderDto.class);
                OrderModel order = new OrderModel();
                BeanUtils.copyProperties(orderDto, order);
                order.setBodylist(orderDto.getBodyList());
                switch (orderInfo.getOrderType()) {
                    case 0: {
                        try {
                            //先替换请求头、请求路径以及body里的变量
                            order.setHeaderList(RobotUtils.translateVariable(conversationId, phone, order.getHeaderList()));
                            order.setRequestUrl(RobotUtils.translateVariable(conversationId, phone, order.getRequestUrl()));
                            order.setBodylist(RobotUtils.translateVariable(conversationId, phone, order.getBodylist()));
                            executeHttp(order, conversationId);
                        } catch (Throwable e) {
                            log.error("执行http指令失败：{}", e.getMessage(), e);
                            this.execResult = false;
                        }
                        break;
                    }
                    case 1:
                        if (null == order.getActive() || !order.getActive()) {
                            this.execResult = false;
                        } else {
                            try {
                                executePython(order, msgDto);
                            } catch (Throwable e) {
                                log.error("执行py指令失败：{}", e.getMessage(), e);
                                this.execResult = false;
                            }
                        }
                        break;
                    default: {
                        log.error("未知指令类型:{}", orderInfo.getOrderType());
                        this.execResult = false;
                    }
                }
            }
        } catch (Throwable throwable) {
            log.error("指令节点执行失败:{}", throwable.getMessage(), throwable);
            this.execResult = false;
        }
    }

    /**
     * 执行http指令，如果配置了使用系统变量接收则把响应的变量值更新到redis
     *
     * @param order
     */
    private void executeHttp(OrderModel order, String conversationId) {
        Integer requestType = order.getRequestType();//0(GET)、1(POST)
        String response;
        if (requestType == 0) {
            response = sendGetReq(order);
        } else if (requestType == 1) {
            response = sendPostReq(order);
        } else {
            throw new BizException(500, "非法的http指令requestType：" + requestType);
        }
        //只要http指令发送成功 节点就算执行成功
        this.setExecResult(true);
        log.info("########## response : {} ##########", response);
        if (StringUtils.isNotEmpty(response)) {
            RobotDto robot = RobotUtils.getLocalRobot(conversationId);
            Map<String, String> variables = new HashMap<>();
            List<JSONObject> listMap = JSONArray.parseArray(order.getResponseList(), JSONObject.class);
            for (JSONObject jsonObject : listMap) {
                String extractKey = Optional.ofNullable(jsonObject.getString("value")).orElse("");
                String variableName = Optional.ofNullable(jsonObject.getString("variable")).orElse("");
                StringBuffer variable = new StringBuffer();
                variable.append("{{custom-").append(variableName).append("}}");
                log.info("########## extractKey : {} ##########", extractKey);
                log.info("########## variableName : {} ##########", variableName);
                log.info("########## variable : {} ##########", variable);
                Object variableValue = "";
                try {
                    variableValue = JsonPath.read(response, "$." + extractKey);
                    log.info("########## variableValue : {} ##########", variableValue);
                } catch (Exception ignore) {
                }
                variables.put(variable.toString(), JsonUtils.obj2String(variableValue));
            }
            StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
            String variableKey = RedisUtil.getVariableKey(conversationId);
            redisTemplate.<String, String>opsForHash().putAll(variableKey, variables);
            redisTemplate.expire(variableKey, Duration.ofMinutes(robot.getExpireTime()));
        }
    }

    private static String sendGetReq(OrderModel order) {
        return HttpURLConnectionUtil.doGet(order.getRequestUrl());
    }

    private static String sendPostReq(OrderModel order) {
        Integer bodyType = order.getRequestBodyType(); //body type 0(FORM-DATA)、1(RAW)
        String body = order.getBodylist();
        Integer rawType = order.getRequestRawType();//0(TEXT)、1(JSON)、2(XML)
        List<JSONObject> headMapList = JSONObject.parseArray(order.getHeaderList().replace("\\", ""), JSONObject.class);
        String contentType = null;
        if (Integer.valueOf(0).equals(bodyType)) {
            contentType = "multipart/form-data; boundary=---------------------------" + System.currentTimeMillis();
            List<JSONObject> lists = JSONObject.parseArray(body.replace("\\", ""), JSONObject.class);
            Map<String, Object> map = new HashMap<>();
            if (null != lists) {
                for (JSONObject jsonObject : lists) {
                    if (jsonObject.size() > 0) {
                        map.put(jsonObject.get("name").toString(), JsonPath.read(jsonObject, "$.value.value"));
                    }
                }
            }
            body = JsonUtils.obj2String(map);
        } else if (Integer.valueOf(1).equals(bodyType)) {
            JSONObject jsonObject = JsonUtils.string2Obj(body, JSONObject.class);
            Assert.notNull(jsonObject, "post http指令请求body不能为空");
            switch (rawType) {
                case 0: {
                    contentType = "text/plain;charset=utf-8";
                    body = jsonObject.getString("value");
                    break;
                }
                case 1: {
                    body = jsonObject.getString("value");
                    contentType = "application/json;charset=utf-8";
                    break;
                }
            }
        } else {
            throw new BizException(500, "非法的http指令bodyType：" + bodyType);
        }
        return HttpURLConnectionUtil.doPost(order.getRequestUrl(), headMapList, body, contentType);
    }

    /**
     * 执行python指令
     *
     * @param order
     */
    private void executePython(OrderModel order, MsgDto msgDto) throws Exception {
        StringBuilder rkp = new StringBuilder(); //redis key prefix
        rkp.append(msgDto.getChatbotAccount());
        if (StringUtils.isNotEmpty(msgDto.getPhone())) {
            rkp.append("@");
            rkp.append(msgDto.getPhone());
        }
        rkp.append("@").append(msgDto.getCreate());
        CommandService commandService = ApplicationContextUil.getBean(CommandService.class);
        // 0 成功 1 失败
        String pyExecuteResult = commandService.processCustomCommand(order, msgDto, rkp.toString());
        this.execResult = !StringUtils.isEmpty(pyExecuteResult) && !Boolean.parseBoolean(pyExecuteResult);
    }


    @Override
    void afterExec(MsgDto msgDto) {
        RobotDto robot = RobotUtils.getLocalRobot(msgDto.getConversationId());
        Process process = RobotUtils.getCurrentProcess(robot);
        process.getNodeMap().put(this.getNodeId(), this);
        List<RouterInfo> outRouters = this.getOutRouters();
        if (!CollectionUtils.isEmpty(outRouters)) {
            if (outRouters.size() > 2) {
                log.warn("指令节点至多只能有两个出站路由，节点ID:{}", this.getNodeId());
            }
            String nextNodeId = null;
            for (RouterInfo outRouter : outRouters) {
                RouteConditionStrategy strategy = outRouter.getConditionStrategy();
                if (strategy == RouteConditionStrategy.EXECUTE_SUCCESS && this.execResult) {
                    nextNodeId = outRouter.getTailNodeId();
                    break;
                }
                if (strategy == RouteConditionStrategy.EXECUTE_FAIL && !this.execResult) {
                    nextNodeId = outRouter.getTailNodeId();
                    break;
                }
            }
            robot.setCurrentNodeId(nextNodeId);
        } else {
            robot.setCurrentNodeId(null);
        }
        this.setNodeStatus(this.execResult ? NodeStatus.SUCCESS : NodeStatus.ERROR);
        RobotUtils.saveRobot(robot);
    }
}
