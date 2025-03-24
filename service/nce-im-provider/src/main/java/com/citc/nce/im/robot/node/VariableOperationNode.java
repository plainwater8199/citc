package com.citc.nce.im.robot.node;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2023/7/17 10:27
 */
@Data
public class VariableOperationNode extends Node {

    private List<SystemVariable> systemVariables;

    /**
     * 结点执行前操作
     *
     * @param msgDto 上行消息
     */
    @Override
    void beforeExec(MsgDto msgDto) {

    }

    /**
     * 进行变量操作
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        Map<String, String> variables = new HashMap<>();
        String conversationId = msgDto.getConversationId();
        for (SystemVariable variable : systemVariables) {
            variables.put(variable.getVariableNameValue(), RobotUtils.translateVariable(conversationId, msgDto.getPhone(), variable.getVariableValue()));
        }
        StringRedisTemplate redisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        String variableKey = RedisUtil.getVariableKey(conversationId);
        RobotDto robot = RobotUtils.getLocalRobot(conversationId);
        redisTemplate.<String, String>opsForHash().putAll(variableKey, variables);
        redisTemplate.expire(variableKey, Duration.ofMinutes(robot.getExpireTime()));
    }

    /**
     * 结点执行后操作--执行结点的跳转
     *
     * @param msgDto 上行消息  -- 可不用
     */
    @Override
    void afterExec(MsgDto msgDto) {
        RobotDto robot = RobotUtils.getLocalRobot(msgDto.getConversationId());
        List<RouterInfo> outRouters = this.getOutRouters();
        if (!CollectionUtils.isEmpty(outRouters)) {
            if (outRouters.size() > 1)
                throw new BizException(500, "变量操作节点至多只能有一个出站路由，节点ID:" + this.getNodeId());
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
