package com.citc.nce.im.robot.service.impl;


import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.robot.common.RobotConstant;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.enums.ProcessStatusEnum;
import com.citc.nce.im.robot.node.CommandNode;
import com.citc.nce.im.robot.node.Node;
import com.citc.nce.im.robot.node.NodeUtil;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.service.ProcessService;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;


@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {
    @Override
    public void processExec(MsgDto msgDto, RobotDto robotDto) {
        String processId = robotDto.getCurrentProcessId();
        //1、流程执行前准备工作，流程统计埋点、日志输出
        processBeforeExec(robotDto, processId);
        //2、流程执行 -- 从redis中获取机器人执行最新信息

        processExecuting(msgDto, processId);
        //3、流程执行后准备 -- 从redis中获取机器人执行最新信息
        processAfterExec(msgDto, processId);
    }


    /**
     * 流程执行前的准备工作，数据埋点、日志输出
     * 1、首先根据会话id加载最新的机器人执行情况
     *
     * @param robotDto 机器人信息
     */
    private void processBeforeExec(RobotDto robotDto, String processId) {
        log.info("流程执行前 conversationId:{} processId:{}", robotDto.getConversationId(), processId);
    }


    /**
     * 流程执行中
     * 1、结点的执行
     * --1、从redis中获取最新的机器人执行信息
     * --2、如果机器人执行信息存在并且存在当前结点则继续执行
     * --3、如果机器人执行信息不存在或者没有当前结点或者当前节点的状态为非阻塞，说明流程执行结束，进入流程执行后
     *
     * @param msgDto 机器人信息
     */
    private void processExecuting(MsgDto msgDto, String processId) {
        String conversationId = msgDto.getConversationId();
        log.info("流程执行中 conversationId:{} processId:{}", conversationId, processId);
        log.info("流程执行中 msgDto: {}", msgDto);
        while (true) {
            RobotDto robot = RobotUtils.getRobot(conversationId);
            Process process = RobotUtils.getCurrentProcess(robot);
            //如果当前流程不是本流程或者本流程的状态变成阻塞了，跳出节点执行
            if (!Objects.equals(robot.getCurrentProcessId(), processId) || process.getProcessStatus() == ProcessStatusEnum.BLOCK.getCode()) {
                break;
            }
            String currentNodeId = robot.getCurrentNodeId();
            //当前流程执行完毕
            if (currentNodeId == null) {
                break;
            }
            Node node = process.getNodeMap().get(currentNodeId);
            if (node == null) {
                log.error("当前节点不存在！！！跳出流程");
                robot.setCurrentNodeId(null);
                RobotUtils.saveRobot(robot);
                break;
            }
            NodeUtil nodeUtil = new NodeUtil(node);
            try {
                log.info("{}节点执行 nodeId:{}", node.getNodeType().getDesc(), node.getNodeId());
                nodeUtil.exec(msgDto);
            } catch (Throwable throwable) {
                log.warn("节点{}执行异常，跳出流程", node.getNodeId(), throwable);
                robot = RobotUtils.getRobot(conversationId);
                robot.setCurrentNodeId(null);
                process = RobotUtils.getCurrentProcess(robot);
                process.setProcessStatus(ProcessStatusEnum.EXCEPTION.getCode());
                RobotUtils.saveRobot(robot);
                break;
            }
        }
    }


    /**
     * 流程执行后
     * 1、流程状态更新
     * 2、redis机器人信息更新
     *
     * @param msgDto 机器人信息
     */
    private void processAfterExec(MsgDto msgDto, String processId) {
        String conversationId = msgDto.getConversationId();
        log.info("流程执行后 conversationId:{} processId:{}", conversationId, processId);
        RobotDto robot = RobotUtils.getRobot(conversationId);
        //此时currentProcessId为子流程ID currentNodeId为子流程头结点ID
        if (!Objects.equals(processId, robot.getCurrentProcessId())) {
            log.info("机器人当前流程不是本流程，跳过流程{}执行后方法", processId);
            return;
        }
        //此时currentProcessId为当前流程ID currentNodeId为null
        Process process = robot.getProcessMap().get(processId);
        if (process.getProcessStatus() == ProcessStatusEnum.BLOCK.getCode()) {
            log.info("当前流程{}已阻塞 跳过执行后方法", processId);
            return;
        }
        //如果不是异常结束的流程则修改状态为完成
        if (process.getProcessStatus() != ProcessStatusEnum.EXCEPTION.getCode()) {
            process.setProcessStatus(ProcessStatusEnum.END.getCode());
        }
        RobotUtils.saveRobot(robot);
    }

}
