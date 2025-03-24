package com.citc.nce.im.robot.node;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.ConditionOperator;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.enums.RouteConditionStrategy;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RobotUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author jcrenc
 * @since 2023/7/17 10:25
 */
@Slf4j
@Data
public class BranchNode extends Node {

    /**
     * 0任意 1所有
     */
    private Integer executeType;

    /**
     * 条件集合
     */
    private List<Condition> conditionList;

    @Override
    protected void beforeExec(MsgDto msgDto) {
    }

    /**
     * 分支节点在执行中处理节点跳转逻辑，因为节点跳转就是分支节点的业务
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        String conversationId = msgDto.getConversationId();
        RobotDto robot = RobotUtils.getLocalRobot(conversationId);
        List<RouterInfo> outRouters = this.getOutRouters();
        if (!CollectionUtils.isEmpty(outRouters)) {
            RouterInfo outRoute = this.findOutRoute(msgDto);
            //分支节点总会执行成功，但是如果一个分支都没匹配到则流程结束
            this.setNodeStatus(NodeStatus.SUCCESS);
            if (outRoute != null) {
                robot.setCurrentNodeId(outRoute.getTailNodeId());
            } else {
                log.warn("流程:{}分支节点:{}未能匹配成功", this.getProcessorId(), this.getNodeId());
                robot.setCurrentNodeId(null);
            }
        }
        Process process = RobotUtils.getCurrentProcess(robot);
        process.getNodeMap().put(this.getNodeId(), this);
        RobotUtils.saveRobot(robot);
    }

    /**
     * 分支节点执行后不做任何逻辑，因为在执行中已经处理完节点跳转
     */
    @Override
    void afterExec(MsgDto msgDto) {
    }

    /**
     * @return 返回匹配成功的出站路由，如果匹配失败则返回null
     */
    private RouterInfo findOutRoute(MsgDto msgDto) {
        String phone = msgDto.getPhone();
        String conversationId = msgDto.getConversationId();
        for (RouterInfo outRouter : this.getOutRouters()) {
            //如果路由没有条件 则直接成功
            if (CollectionUtils.isEmpty(outRouter.getExecConditions()))
                return outRouter;
            RouteConditionStrategy conditionStrategy = outRouter.getConditionStrategy();
            switch (conditionStrategy) {
                case ANY_MATCH: {
                    if (outRouter.getExecConditions().stream().anyMatch(condition ->
                            this.match(condition.getOperator(),
                                    condition.getConditionSettingValue(),
                                    RobotUtils.translateVariable(conversationId, phone, condition.getConditionVariableName()))
                    )) {
                        return outRouter;
                    }
                    break;
                }
                case ALL_MATCH: {
                    if (outRouter.getExecConditions().stream().allMatch(condition ->
                            this.match(condition.getOperator(),
                                    condition.getConditionSettingValue(),
                                    RobotUtils.translateVariable(conversationId, phone, condition.getConditionVariableName()))
                    )) {
                        return outRouter;
                    }
                    break;
                }
                default: {
                    log.warn("分支节点条件匹配策略异常! processId:{} nodeId:{} conditionStrategy:{}", this.getProcessorId(), this.getNodeId(), conditionStrategy);
                    break;
                }
            }
        }
        return null;
    }

    /**
     * 返回条件表达式中设置变量的值与当前会话中变量的值是否匹配
     *
     * @param operator     运算方式
     * @param settingValue 设定值
     * @param actualValue  实际值
     * @return 匹配成功则返回true，否则false
     */
    private boolean match(ConditionOperator operator, String settingValue, String actualValue) {
        switch (operator) {
            //包含
            case IN:
                return actualValue.contains(settingValue);

            //不包含
            case NOT_IN:
                return !actualValue.contains(settingValue);

            //等于
            case EQUALS:
                return Objects.equals(settingValue, actualValue);

            //不等于
            case NOT_EQUALS:
                return !Objects.equals(settingValue, actualValue);

            //大于
            case GT: {
                if (StringUtils.isNumeric(settingValue) && StringUtils.isNumeric(actualValue)) {
                    return Double.parseDouble(actualValue) > Double.parseDouble(settingValue);
                } else {
                    try {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        return LocalDateTime.parse(actualValue, df).isAfter(LocalDateTime.parse(settingValue, df));
                    } catch (Exception ignore) {
                    }
                }
                break;
            }

            //大于等于
            case GE: {
                if (StringUtils.isNumeric(settingValue) && StringUtils.isNumeric(actualValue)) {
                    return Double.parseDouble(actualValue) >= Double.parseDouble(settingValue);
                } else {
                    try {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                        return !LocalDateTime.parse(actualValue, df).isBefore(LocalDateTime.parse(settingValue, df));
                    } catch (Exception ignore) {
                    }
                }
                break;
            }

            //小于
            case LT: {
                if (StringUtils.isNumeric(settingValue) && StringUtils.isNumeric(actualValue)) {
                    return Double.parseDouble(actualValue) < Double.parseDouble(settingValue);
                } else {
                    try {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                        return LocalDateTime.parse(actualValue, df).isBefore(LocalDateTime.parse(settingValue, df));
                    } catch (Exception ignore) {
                    }
                }
                break;
            }

            //正则匹配
            case REGEX_MATCH: {
                Pattern pattern = Pattern.compile(settingValue);
                boolean matches = pattern.matcher(actualValue).matches();
                if (!matches) {
                    matches = pattern.matcher(actualValue).find();
                }
                return matches;
            }

            //正则不匹配
            case REGEX_NOT_MATCH: {
                Pattern pattern = Pattern.compile(settingValue);
                boolean matches = pattern.matcher(actualValue).matches();
                if (!matches) {
                    matches = pattern.matcher(actualValue).find();
                }
                return !matches;
            }

            //小于等于
            case LE: {
                if (StringUtils.isNumeric(settingValue) && StringUtils.isNumeric(actualValue)) {
                    return Double.parseDouble(actualValue) <= Double.parseDouble(settingValue);
                } else {
                    try {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                        return !LocalDateTime.parse(actualValue, df).isAfter(LocalDateTime.parse(settingValue, df));
                    } catch (Exception ignore) {
                    }
                }
                break;
            }

            //为空
            case IS_NULL:
                return StringUtils.isEmpty(actualValue);

            //不为空
            case IS_NOT_NULL:
                return !StringUtils.isEmpty(actualValue);
        }
        return false;
    }
}
