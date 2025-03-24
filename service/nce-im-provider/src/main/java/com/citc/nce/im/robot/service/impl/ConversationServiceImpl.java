package com.citc.nce.im.robot.service.impl;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.customcommand.CustomCommandApi;
import com.citc.nce.customcommand.vo.MyAvailableCustomCommandReq;
import com.citc.nce.customcommand.vo.MyAvailableCustomCommandVo;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.OrderDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.service.ConversationService;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.robot.RobotOrderApi;
import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.mall.common.Robot;
import com.citc.nce.robot.api.mall.common.RobotOrder;
import com.citc.nce.robot.api.mall.common.RobotVariable;
import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.bean.RobotVariableBean;
import com.citc.nce.robot.vo.RobotVariablePageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.exception.GlobalErrorCode.INTERNAL_SERVER_ERROR;

/**
 * @author jcrenc
 * @since 2023/7/13 15:36
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final RobotVariableApi robotVariableApi;
    private final StringRedisTemplate redisTemplate;
    private final RobotOrderApi robotOrderApi;

    private final CustomCommandApi customCommandApi;

    @Resource
    private MallSnapshotService mallSnapshotService;

    /**
     * 加载会话资源，--防止流程执行过程中，会话资源发送变动。
     * 1、从Redis中根据会话id获取系统资源
     * 2、如果存在则不做任何处理
     * 3、如果不存在则通过userId、手机号去数据库中加载用户的所有系统资源（变量、指令），然后缓存在Redis中
     *
     * @param msgDto 消息体
     */
    @Override
    public void loadResources(MsgDto msgDto) {
        String conversationId = msgDto.getConversationId();
        String creator = msgDto.getCreate();
        String customerId = msgDto.getCustomerId();
        if (conversationId == null || creator == null)
            throw new BizException(INTERNAL_SERVER_ERROR.getCode(), "会话ID和creator不能为空");
        RobotDto robot = RobotUtils.getRobot(conversationId);
        Long expireTime = robot.getExpireTime();
        String variableKey = RedisUtil.getVariableKey(conversationId);
        String orderKey = RedisUtil.getOrderKey(conversationId);
        //加载变量(每个对话都有一个)
        // 从扩展商城预览窗口进来
        if (StringUtils.isNotEmpty(msgDto.getSnapshotUuid())) {
            whenMallViewInto(msgDto, expireTime, variableKey, orderKey);
            // 从调试窗口或网关
        } else {
            //加载变量
            loadVariables(customerId, variableKey);
            redisTemplate.expire(variableKey, expireTime, TimeUnit.MINUTES);
            //加载指令
            loadOrders(customerId, orderKey);
            redisTemplate.expire(orderKey, expireTime, TimeUnit.MINUTES);
        }
    }

    private void whenMallViewInto(MsgDto msgDto, Long expireTime, String variableKey, String orderKey) {
        List<RobotOrder> orderList = new ArrayList<>();
        List<RobotVariable> variableList = new ArrayList<>();
        MallCommonContent mallCommonContent = mallSnapshotService.queryContent(msgDto.getSnapshotUuid());
        if (ObjectUtils.isEmpty(mallCommonContent)) {
            throw new BizException(MallError.SNAPSHOT_NOT_EXIST);
        }
        if (CollectionUtils.isNotEmpty(mallCommonContent.getRobot())) {
            for (Robot resources :
                    mallCommonContent.getRobot()) {
                if (CollectionUtils.isNotEmpty(resources.getOrders())) {
                    orderList.addAll(resources.getOrders());
                }
                if (CollectionUtils.isNotEmpty(resources.getVariables())) {
                    variableList.addAll(resources.getVariables());
                }
            }
            // 变量
            if (CollectionUtils.isNotEmpty(variableList)) {
                Map<String, String> variables = new HashMap<>();
                variables = variableList.stream()
                        .peek(var -> {
                            if (Objects.isNull(var.getVariableValue()))
                                var.setVariableValue("");
                        })
                        .collect(Collectors.toMap(var -> "{{custom-" + var.getVariableName() + "}}", RobotVariable::getVariableValue));
                redisTemplate.opsForHash().putAll(variableKey, variables);
                redisTemplate.expire(variableKey, expireTime, TimeUnit.MINUTES);
            }
            // 指令
            if (CollectionUtils.isNotEmpty(orderList)) {
                Map<String, String> orders = orderList.stream()
                        .collect(Collectors.toMap(order -> String.valueOf(order.getId()), order -> {
                            OrderDto dto = new OrderDto();
                            BeanUtils.copyProperties(order, dto);
                            return JsonUtils.obj2String(dto);
                        }));
                redisTemplate.opsForHash().putAll(orderKey, orders);
                redisTemplate.expire(orderKey, expireTime, TimeUnit.MINUTES);
            }
        }
    }

    private void loadVariables(String customerId, String variableKey) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(variableKey)))
            return;
        Map<String, String> variables;
        RobotVariablePageReq variablePageReq = new RobotVariablePageReq();
        variablePageReq.setCreator(customerId);
        variablePageReq.setPageSize(1000);
        variables = robotVariableApi.listAll(variablePageReq).getList().stream()
                .peek(var -> {
                    if (Objects.isNull(var.getVariableValue()))
                        var.setVariableValue("");
                })
                .collect(Collectors.toMap(var -> "{{custom-" + var.getVariableName() + "}}", RobotVariableBean::getVariableValue));
        redisTemplate.opsForHash().putAll(variableKey, variables);
    }

    private void loadOrders(String customerId, String orderKey) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(orderKey)))
            return;
        OrderPageParam orderPageReq = new OrderPageParam();
        orderPageReq.setUserId(customerId);
        orderPageReq.setPageSize(1000);
        Map<String, String> orders = robotOrderApi.listAllOrder(orderPageReq).getList().stream()
                .collect(Collectors.toMap(order -> String.valueOf(order.getId()), order -> {
                    OrderDto dto = new OrderDto();
                    BeanUtils.copyProperties(order, dto);
                    return JsonUtils.obj2String(dto);
                }));
        // 新自定义指令生产体系
        MyAvailableCustomCommandReq req = new MyAvailableCustomCommandReq();
        req.setNeedContent(true);
        req.setPageSize(Integer.MAX_VALUE);
        List<MyAvailableCustomCommandVo> commandList = customCommandApi.getMyAvailableCommand(req).getList();
        if (CollectionUtils.isNotEmpty(commandList)) {
            commandList.forEach(command -> {
                OrderDto dto = new OrderDto();
                dto.setBodyList(command.getContent());
                dto.setOrderName(command.getName());
                dto.setActive(command.getActive());
                orders.put(command.getUuid(), JsonUtils.obj2String(dto));
            });
        }
        redisTemplate.opsForHash().putAll(orderKey, orders);
    }
}
