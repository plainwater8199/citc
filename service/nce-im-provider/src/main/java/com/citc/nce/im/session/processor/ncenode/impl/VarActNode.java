package com.citc.nce.im.session.processor.ncenode.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.processor.*;
import com.citc.nce.robot.vo.NodeActResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/4 19:25
 * @Version: 1.0
 * @Description: 变量操作节点
 */
public class VarActNode extends NceRobotNode {

    private Variable variable = ApplicationContextUil.getBean(Variable.class);

    private static Logger log= LoggerFactory.getLogger(VarActNode.class);

    private List<VarPair> varsList;

    @Override
    public NodeActResult act(SessionContext sessionContext, NodeProcessor nodeProcessor) {
        log.info("执行变量操作节点");
        if (CollectionUtil.isNotEmpty(varsList)) {
            for (VarPair varPair : varsList) {
                log.info("添加变量操作：{},{}",varPair.getVariableNameValue(), varPair.getVariableValue());
                sessionContext.addVar(nodeProcessor.getUpMsgReq(),varPair.getVariableNameValue(), variable.translate(varPair.getVariableValue(),nodeProcessor));
            }
        }
        ParsedProcessorConfig parsedProcessorConfig = nodeProcessor.getParsedProcessorConfig();
        List<String> list = parsedProcessorConfig.getNodeIdRelations().get(nodeProcessor.getCurrentNodeId());
        String childIdx = "";
        boolean hasNext = true;
        if(list.size()>0){
            childIdx = list.get(0);
        }else {
            nodeProcessor.checkAndClean();
            hasNext = false;
        }
        return NodeActResult.builder().childIdx(childIdx).next(hasNext).build();
    }

    @Override
    public Integer nodeType() {
        return NodeType.VAR_ACT_NODE.getCode();
    }

    public List<VarPair> getVarsList() {
        return varsList;
    }

    public void setVarsList(List<VarPair> varsList) {
        this.varsList = varsList;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VarPair {
        private String variableNameValue;
        private String variableValue ;
    }
}
