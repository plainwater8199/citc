package com.citc.nce.im.robot.node;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotConfigProperties;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.enums.ProcessStatusEnum;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author jcrenc
 * @since 2023/7/17 10:27
 */
@Data
@Slf4j
public class SubProcessorNode extends Node {

    /**
     * 子流程ID
     */
    private String subProcessId;

    @Override
    void beforeExec(MsgDto msgDto) {

    }


    /**
     * 子流程节点，复制一个新流程入栈，设置当前节点阻塞
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        if (this.getNodeStatus() == NodeStatus.BLOCK) {
            log.info("父流程阻塞子流程节点恢复执行");
            return;
        }
        String conversationId = msgDto.getConversationId();
        RobotDto robot = RobotUtils.getLocalRobot(conversationId);
        if (StringUtils.isEmpty(subProcessId)) {
            log.info("子流程ID为空，跳过执行子流程节点");
            return;
        }
        Process template = robot.getProcessMap().get(subProcessId);
        if (template == null) {
            throw new BizException(500, "子流程模板不存在:" + subProcessId);
        }
        if (MapUtils.isNotEmpty(template.getNodeMap())) {
            //每次都从nodeMap里取出模版复制
            Process subProcess = initialzeSubProcess(template, robot);
            Map<String, Node> subProcessNodeMap = subProcess.getNodeMap();
            //1、阻塞当前节点和当前流程
            this.setNodeStatus(NodeStatus.BLOCK);
            RobotUtils.getCurrentProcess(robot).setProcessStatus(ProcessStatusEnum.BLOCK.getCode());
            Process currentProcess = RobotUtils.getCurrentProcess(robot);
            currentProcess.getNodeMap().put(this.getNodeId(), this);
            //2、子流程入栈，更新当前节点和当前流程为子流程
            robot.getProcessQueue().add(subProcess.getId());
            robot.getProcessMap().put(subProcess.getId(), subProcess);
            robot.setCurrentProcessId(subProcess.getId());
            robot.setCurrentNodeId(subProcessNodeMap.values().stream()
                    .filter(Node::isBegin)
                    .findAny()
                    .map(Node::getNodeId)
                    .orElseThrow(() -> {
                                log.error("子流程没有头结点,subProcess:{}", JsonUtils.obj2String(subProcess));
                                return new BizException(500, "子流程没有头结点，subProcessId：" + subProcessId);
                            }
                    ));
            RobotUtils.saveRobot(robot);
            log.info("子流程入栈:{}，当前流程:{}阻塞执行", subProcess.getId(), this.getProcessorId());
        } else {
            log.info("子流程可执行节点为空，跳过执行");
        }
    }

    /**
     * 拷贝流程，设置流程状态为初始状态
     *
     * @param template 拷贝的模板
     * @param robot    robot信息
     * @return 拷贝了完成初始化的流程
     */
    private Process initialzeSubProcess(Process template, RobotDto robot) {
        Process process = new Process();
        String parentId = template.getId();
        String templateId = parentId;
        if (parentId.contains("+")) {
            templateId = parentId.substring(0, parentId.indexOf("+"));
        }
        int count = 0;
        for (String id : robot.getProcessQueue()) {
            if (id.startsWith(templateId))
                count++;
        }
        limitCreateRate(robot, count);
        process.setId(count > 0 ? templateId + "+" + count : templateId);
        process.setProcessStatus(ProcessStatusEnum.INIT.getCode());
        process.setParentProcessId(this.getProcessorId());
        process.setConversationId(template.getConversationId());
        process.setExpireTime(template.getExpireTime());
        Map<String, Node> nodeMap = new HashMap<>(template.getNodeMap().size());
        template.getNodeMap().values()
                .forEach(node -> {
                    Node copy = copyAndResetNode(node, process.getId());
                    if (copy == null)
                        log.warn("节点拷贝失败:{}", JsonUtils.obj2String(node));
                    else
                        nodeMap.put(copy.getNodeId(), copy);
                });
        process.setNodeMap(nodeMap);
        process.setCreateTimestamp(System.currentTimeMillis());
        process.setCreatedNodeId(this.getNodeId());
        return process;
    }

    /**
     * 限制流程创建速率
     *
     * @param robot 机器人
     * @param count 流程栈中已存在的该流程合计
     */
    private void limitCreateRate(RobotDto robot, int count) {
        RobotConfigProperties robotConfigProperties = ApplicationContextUil.getBean(RobotConfigProperties.class);
        if (count >= robotConfigProperties.getCopyTime())
            throw new BizException(500, "流程copy time达到限制:" + count);
        Integer timeSpan = robotConfigProperties.getTimeSpan();
        Integer intervalCount = robotConfigProperties.getIntervalCount();
        long timestamp = System.currentTimeMillis();
        long counted = robot.getProcessMap().values().stream()
                .filter(p -> p.getCreateTimestamp() != null && p.getCreatedNodeId().equals(this.getNodeId()) && p.getCreateTimestamp() >= timestamp - (timeSpan * 1000))
                .count();
        if (counted >= intervalCount)
            throw new BizException(500, "流程interval count达到限制:" + timeSpan + "秒内最多允许创建循环流程" + intervalCount + "次");
    }

    /**
     * 拷贝节点并设置为初始化
     *
     * @param node 模板节点
     * @return 拷贝好的副本节点
     */
    private static Node copyAndResetNode(Node node, String processId) {
        Node copy = null;
        if (node instanceof SendMsgNode) {
            copy = new SendMsgNode();
            BeanUtils.copyProperties(node, copy);
        }

        if (node instanceof BranchNode) {
            copy = new BranchNode();
            BeanUtils.copyProperties(node, copy);
        }

        if (node instanceof CommandNode) {
            copy = new CommandNode();
            BeanUtils.copyProperties(node, copy);
            ((CommandNode) copy).setExecResult(null);
        }

        if (node instanceof ConcatNode) {
            copy = new ConcatNode();
            BeanUtils.copyProperties(node, copy);
        }

        if (node instanceof VariableOperationNode) {
            copy = new VariableOperationNode();
            BeanUtils.copyProperties(node, copy);
        }

        if (node instanceof SubProcessorNode) {
            copy = new SubProcessorNode();
            BeanUtils.copyProperties(node, copy);
        }

        if (node instanceof QuestionNode) {
            copy = new QuestionNode();
            BeanUtils.copyProperties(node, copy);
        }
        //初始化
        if (copy != null) {
            copy.setNodeStatus(NodeStatus.INIT);
            copy.setProcessorId(processId);
        }
        return copy;
    }

    /**
     * 结点执行后操作--执行结点的跳转
     *
     * @param msgDto 上行消息  -- 可不用
     */
    @Override
    void afterExec(MsgDto msgDto) {
        String conversationId = msgDto.getConversationId();
        RobotDto robot = RobotUtils.getLocalRobot(conversationId);
        if (!Objects.equals(robot.getCurrentProcessId(), this.getProcessorId())) {
            //流程已阻塞，跳过节点执行后方法
//            log.info("流程已阻塞，跳过节点执行后方法");
            return;
        }
        //子流程已经出栈 跳转到下一个节点
        List<RouterInfo> outRouters = this.getOutRouters();
        if (!CollectionUtils.isEmpty(outRouters)) {
            if (outRouters.size() > 1)
                throw new BizException(500, "子流程节点至多只能有一个出站路由，节点ID:" + this.getNodeId());
            RouterInfo routerInfo = outRouters.get(0);
            String nextNodeId = routerInfo.getTailNodeId();
            robot.setCurrentNodeId(nextNodeId);
        } else {
            robot.setCurrentNodeId(null);
        }
        this.setNodeStatus(NodeStatus.SUCCESS);
        Process process = RobotUtils.getCurrentProcess(robot);
        process.getNodeMap().put(this.getNodeId(), this);
        RobotUtils.saveRobot(robot);
    }
}
