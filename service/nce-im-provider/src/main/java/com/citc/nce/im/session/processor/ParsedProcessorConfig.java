package com.citc.nce.im.session.processor;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.im.session.processor.ncenode.impl.Line;
import com.google.common.collect.ArrayListMultimap;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/6 15:39
 * @Version: 1.0
 * @Description:
 */
@Data
public class ParsedProcessorConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, NceRobotNode> nodeMaps = new HashMap<>();

    ArrayListMultimap<String, String> nodeRelations = ArrayListMultimap.create();

    ArrayListMultimap<String, String> nodeIdRelations = ArrayListMultimap.create();

    private Map<String,NceRobotNode> nodeInfoMap = new HashMap<>();

//    public void parse(ProcessorConfig processorConfig) {
//        parseNode(processorConfig.getRobotProcessQuestionNodeList());
//        parseNode(processorConfig.getRobotProcessBranchNodeList());
//        parseNode(processorConfig.getRobotProcessSendMessageNodeList());
//        parseNode(processorConfig.getRobotProcessOrderNodeList());
//        parseNode(processorConfig.getRobotProcessVariableNodeList());
//        parseNode(processorConfig.getRobotProcessSubProcessNodeList());
//        parseNode(processorConfig.getStartNodeList());
//        parseNode(processorConfig.getContactNodeList());
//        List<Line> robotProcessLineList = processorConfig.getRobotProcessLineList();
//        for (Line line : robotProcessLineList) {
//            nodeRelations.put(line.getFromName(), line.getToName());
//            nodeIdRelations.put(line.getFromId(),line.getToId());
//        }
//    }

    public <T extends NceRobotNode> void parseNode(List<T> nodes) {
        if (CollectionUtil.isEmpty(nodes)) {
            return;
        }
        for (NceRobotNode n : nodes) {
            nodeMaps.put(n.getNodeName(), n);
            nodeInfoMap.put(n.getId(),n);
        }
    }

    public List<String> takeChildrenName(String parentId) {
        return nodeIdRelations.get(parentId);
    }

    public NceRobotNode getNodeNameById(String id){
        return nodeInfoMap.get(id);
    }

}
