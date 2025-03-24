package com.citc.nce.im.robot.dto.robot;

import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import lombok.Data;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


/**
 * @author zhujy
 * @version 1.0
 * @date 2022/4/14 9:18
 * @description  此处的机器人代表了整个(每个上行的客户各自有一个)机器人流程流转状态;
 *   主要功能:
 *   conversationId 当前的会话id;
 *   currentNodeId  当前执行到的节点;
 *   currentProcessId  当前执行到的流程;
 *   processMap 流程组(本场景涉及到的所有流程的Map);
 *   processQueue 流程栈;
 *   rebotSettingModel 机器人兜底回复等设置.
 *
 */
@Data
public class RobotDto {
    /**
     * 会话id
     */
    private String conversationId;
    /**
     * 机器人执行流程状态
     */
    private Integer robotStatus;
    /**
     * 当前结点
     */
    private String currentNodeId;
    /**
     * 当前流程
     */
    private String currentProcessId;
    /**
     * 场景Id
     */
    private String sceneId;
    /**
     * 过期时间
     */
    private Long expireTime;
    /**
     * 流程组
     */
    private Map<String, Process> processMap;

    /**
     * 流程栈（栈顶流程就是当前流程）
     */
    private LinkedList<String> processQueue;

    /**
     * 机器人设置（主要是为了兜底回复）
     */
    private RebotSettingModel rebotSettingModel;

}
