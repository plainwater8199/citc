package com.citc.nce.im.robot.process;

import com.citc.nce.im.robot.node.Node;
import lombok.Data;

import java.util.Map;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/7/13 17:44
 * @description
 * 主要保存的信息:
 * createdNodeId 被哪个节点创建（父流程的子流程节点）
 * nodeMap  包含的所有的Node的map
 * parentProcessId 父流程的ID
 * processStatus  流程状态 1-初始化，2-执行中，3-阻塞，4-兜底，5-结束，6-执行异常（？？？）
 *
 */
@Data
public class Process {
    /**
     * 当前流程id
     */
    private String id;

    private String conversationId;

    /**
     * 流程状态
     * 1-初始化，2-执行中，3-阻塞，4-兜底，5-结束，6-执行异常（？？？）
     */
    private int processStatus;

    /**
     * 父流程ID
     * 默认-1，表示主流程
     */
    private String parentProcessId;
    /**
     * 会话过期时间，单位分钟
     */
    private Long expireTime;

    private Map<String, Node> nodeMap;

    /**
     * 被创建的节点ID（父流程的子流程节点）
     */
    private String createdNodeId;

    /**
     * 流程创建时间戳
     */
    private Long createTimestamp;


}
