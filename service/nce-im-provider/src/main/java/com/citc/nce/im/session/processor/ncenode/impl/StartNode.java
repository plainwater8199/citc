package com.citc.nce.im.session.processor.ncenode.impl;

import com.citc.nce.im.session.processor.*;
import com.citc.nce.robot.vo.NodeActResult;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/6 10:19
 * @Version: 1.0
 * @Description:
 */
public class StartNode extends NceRobotNode {

    public StartNode() {
        setNodeName(NodeType.BEGIN_NODE.getDesc());
    }

    @Override
    public NodeActResult act(SessionContext sessionContext, NodeProcessor nodeProcessor) {
        return NodeActResult.builder().next(true).build();
    }

    @Override
    public Integer nodeType() {
        return NodeType.BEGIN_NODE.getCode();
    }
}
