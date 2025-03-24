package com.citc.nce.im.robot.node;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.session.processor.NodeType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

/**
 * 结点父类，定义结点的基本属性
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "nodeType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SendMsgNode.class, name = "MSG_SEND_NODE"),
        @JsonSubTypes.Type(value = CommandNode.class, name = "COMMAND_NODE"),
        @JsonSubTypes.Type(value = TextRecognitionNode.class, name = "TEXT_RECOGNITION_NODE"),
        @JsonSubTypes.Type(value = ConcatNode.class, name = "CONTACT"),
        @JsonSubTypes.Type(value = QuestionNode.class, name = "QUESTION_NODE"),
        @JsonSubTypes.Type(value = SubProcessorNode.class, name = "SUB_PROCESSOR_NODE"),
        @JsonSubTypes.Type(value = VariableOperationNode.class, name = "VAR_ACT_NODE"),
        @JsonSubTypes.Type(value = BranchNode.class, name = "BRANCH_NODE")
})
public abstract class Node {

    /**
     * 是否是第一个节点
     */
    private boolean begin;
    /**
     * 结点ID
     */
    private String nodeId;
    /**
     * 结点名称
     */
    private String nodeName;
    /**
     * 流程ID
     */
    private String processorId;
    /**
     * 结点状态
     */
    private NodeStatus nodeStatus;
    /**
     * 结点类型
     */
    private NodeType nodeType;
    /**
     * 入线集合
     */
    private List<RouterInfo> inRouters;
    /**
     * 出线集合
     */
    private List<RouterInfo> outRouters;


    /**
     * 结点执行前操作
     *
     * @param msgDto 上行消息
     */
    abstract void beforeExec(MsgDto msgDto);

    /**
     * 结点执行操作--主要完成结点的功能
     *
     * @param msgDto 上行消息
     */
    abstract void exec(MsgDto msgDto);

    /**
     * 结点执行后操作--执行结点的跳转
     *
     * @param msgDto 上行消息  -- 可不用
     */
    abstract void afterExec(MsgDto msgDto);

}
