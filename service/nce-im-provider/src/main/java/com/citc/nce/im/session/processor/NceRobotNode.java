package com.citc.nce.im.session.processor;

import com.citc.nce.robot.vo.NodeActResult;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/4 19:18
 * @Version: 1.0
 * @Description:
 */
public abstract class NceRobotNode {

    private String nodeName;
    //0 提问节点1分支节点2发送消息3指令4变量操作5子流程
    private Integer nodeType;

    /**
     * 节点id
     */
    private String id;

    public NceRobotNode() {
        this.nodeType = nodeType();
    }

    /**
     * 进入条件
     *
     * @return
     */
    public boolean enterCondition(SessionContext sessionContext, NodeProcessor nodeProcessor) {

        return true;
    }

    /***
     * 每个节点内容处理,处理完了以后调用子节点后需要改变nodeProcessorContext的当前节点
     */
    public abstract NodeActResult act(SessionContext sessionContext, NodeProcessor nodeProcessor) throws Exception;


    public Integer nodeType() {
        return nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public String getId() {
        return id;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public void setId(String id) {
        this.id = id;
    }
}
