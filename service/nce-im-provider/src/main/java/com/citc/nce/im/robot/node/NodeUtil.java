package com.citc.nce.im.robot.node;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.processor.NodeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 结点的工具类，主要结点的执行
 */
public class NodeUtil {
    Node node;

    public NodeUtil(Node node) {
        this.node = node;
    }

    /**
     * 结点的执行
     *
     * @param msgDto 信息
     */
    public void exec(MsgDto msgDto) {
        node.beforeExec(msgDto);//执行前
        node.exec(msgDto);//执行中
        node.afterExec(msgDto);//执行后
    }

    /**
     * 将DB中结点信息装换为结点对象
     * @param nodeListStr node list的json字符串
     * @return node list
     */
    public static List<Node> parse(String nodeListStr) {
        List<Node> nodeList = new ArrayList<>();
        ObjectMapper om = ApplicationContextUil.getBean(ObjectMapper.class);
        try {
            JsonNode nodes = om.readTree(nodeListStr).get("robotProcessNodeList");
            if (!nodes.isArray())
                throw new BizException(500, "node list str不是一个json array");
            for (JsonNode node : nodes) {
                if (!node.isObject())
                    throw new BizException(500, "node str不是一个json object");
                ObjectNode objectNode = (ObjectNode) node;
                NodeType nodeType = NodeType.getNodeType(objectNode.get("nodeType").asInt());
                if (nodeType == null)
                    throw new BizException(500, "未知节点类型");
                switch (nodeType) {
                    case MSG_SEND_NODE:
                        nodeList.add(parseMsgNode(objectNode));
                        break;
                    case QUESTION_NODE:
                        nodeList.add(parseQuestionNode(objectNode));
                        break;
                    case COMMAND_NODE:
                        nodeList.add(parseCommandNode(objectNode));
                        break;
                    case TEXT_RECOGNITION_NODE:
                        nodeList.add(parseTextRecognitionNode(objectNode));
                        break;
                    case VAR_ACT_NODE:
                        nodeList.add(parseVariableOperationNode(objectNode));
                        break;
                    case SUB_PROCESSOR_NODE:
                        nodeList.add(parseSubProcessorNode(objectNode));
                        break;
                    case BRANCH_NODE:
                        nodeList.add(parseBranchNode(objectNode));
                        break;
                    case CONTACT:
                        nodeList.add(parseConcatNode(objectNode));
                        break;
                    case BLANK_BRANCH_NODE:
                        nodeList.add(parseBlankBranchNode(objectNode));
                        break;
                    default:
                }
            }
            return nodeList;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConcatNode parseConcatNode(ObjectNode objectNode) {
        ConcatNode node = new ConcatNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.CONTACT);
        if (objectNode.get("presonConnect") != null) {
            setValue(node, "operateType",  objectNode.get("presonConnect").get("operatType").asInt());
            JsonNode personGroup = objectNode.get("presonConnect").get("presonGroup");
            if (personGroup.toString().startsWith("[")) {
                setValue(node, "contactGroup", JSONArray.parseArray(personGroup.toString(), Long.class));
            }else{
                setValue(node, "contactGroup", Collections.singletonList(personGroup.asLong()));
            }
        }
        return node;
    }

    private static BranchNode parseBranchNode(ObjectNode objectNode) {

        BranchNode node = new BranchNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.BRANCH_NODE);
        setValue(node, "executeType", objectNode.get("executeType") != null ? objectNode.get("executeType").intValue() : null);
        setValue(node, "conditionList", objectNode.get("conditionList") != null ? JSONArray.parseArray(objectNode.get("conditionList").toString(), Condition.class) : null);

        return node;
    }

    private static Node parseBlankBranchNode(ObjectNode objectNode) {

        Node node = new BlankBranchNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.BLANK_BRANCH_NODE);

        return node;
    }

    private static SubProcessorNode parseSubProcessorNode(ObjectNode objectNode) {

        SubProcessorNode node = new SubProcessorNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.SUB_PROCESSOR_NODE);
        if (CollectionUtil.isNotEmpty(objectNode.get("subProcessList"))) {
            setValue(node, "subProcessId", objectNode.get("subProcessList").get(0).get("SubProcessValue").asText());
        }

        return node;
    }

    private static VariableOperationNode parseVariableOperationNode(ObjectNode objectNode) {

        VariableOperationNode node = new VariableOperationNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.VAR_ACT_NODE);
        setValue(node, "systemVariables", JSONArray.parseArray(objectNode.get("varsList").toString(), SystemVariable.class));

        return node;
    }

    private static CommandNode parseCommandNode(ObjectNode objectNode) {

        CommandNode node = new CommandNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.COMMAND_NODE);
        setValue(node, "orders", JSONArray.parseArray(objectNode.get("orderList").toString(), OrderInfo.class));
        setValue(node, "sucessId", objectNode.get("sucessId") != null ? objectNode.get("sucessId").asText() : null);
        setValue(node, "failId", objectNode.get("failId") != null ? objectNode.get("failId").asText() : null);

        return node;
    }

    private static TextRecognitionNode parseTextRecognitionNode(ObjectNode objectNode) {

        TextRecognitionNode node = new TextRecognitionNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.TEXT_RECOGNITION_NODE);
        JsonNode TextRecognitionSettingNode = objectNode.get("textRecognitionSetting");
        if (TextRecognitionSettingNode == null) {
            return node;
        }

        setValue(node, "textRecognitionSetting", JsonUtils.string2Obj(TextRecognitionSettingNode.toString(), TextRecognitionSetting.class));
        setValue(node, "sucessId", objectNode.get("sucessId").asText());
        setValue(node, "failId", objectNode.get("failId").asText());
        return node;
    }

    private static QuestionNode parseQuestionNode(ObjectNode objectNode) {

        QuestionNode node = new QuestionNode();
        node.setNodeId(objectNode.get("id").asText());
        node.setNodeName(objectNode.get("nodeName").asText());
        node.setNodeType(NodeType.QUESTION_NODE);
        setValue(node, "questionType", objectNode.get("contentType") != null ? objectNode.get("contentType").intValue() : null);
        setValue(node, "questionDetail", objectNode.get("contentBody").toString());
        setValue(node, "buttons", JSONArray.parseArray(objectNode.get("buttonList").toString(), JSONObject.class));
        setValue(node, "conditionList", JSONArray.parseArray(objectNode.get("verifyList").toString(), QuestionNode.Verify.class));
        //解析出templateId列表
        List<Long> templateIds = new ArrayList<>();
        List<JSONObject> contentBody = JSONArray.parseArray(objectNode.get("contentBody").toString(), JSONObject.class);
        for (JSONObject jsonObject : contentBody) {
            if (jsonObject.get("templateId") != null) {
                templateIds.add(jsonObject.getLong("templateId"));
            }
        }
        setValue(node, "templateIds", templateIds);
        String nodeLastStr = objectNode.get("nodeLast").toString();
        List<JSONObject> nodeLast = new ArrayList<>();
        nodeLast.add(JSONObject.parseObject(nodeLastStr));
        setValue(node, "nodeLast", nodeLast);

        return node;
    }

    private static SendMsgNode parseMsgNode(ObjectNode objectNode) {

//        {

//            "contentType": 1,
//                "contentBody": [
//            {
//                "type": 1,
//                "messageDetail": {
    //                "input": {
    //                    "name": "发送消息节点",
    //                     "value": "发送消息节点",
    //                     "names": [],
    //                    "length": 6
    //                }
//                }
//            }
//            ],
//            "buttonList": [
//            {
//                "type": 1,
//                    "uuid": "ac45503d7-acae-424c-9914-3ef1853f6ea8",
//                    "buttonDetail": {
//                "input": {
//                    "name": "1",
//                            "value": "1",
//                            "names": [],
//                    "length": 1
//                }
//            }
//            },
//            {
//                "type": 1,
//                    "uuid": "ade06e82b-8220-4b19-8b5f-36fd077e3978",
//                    "buttonDetail": {
//                "input": {
//                    "name": "2",
//                            "value": "2",
//                            "names": [],
//                    "length": 1
//                }
//            }
//            }
//            ],
//            "nodeType": 2,
//            "index": 1,
//            "i": 1,
//            "nodeName": "发送消息1",
//            "id": "a45d7acbe-9d0d-405d-9825-879cd33dcd1b",
//            "top": "289px",
//            "left": "767px",
//            "width": "218px",
//            "sucessId": "",
//            "failId": "",
//            "nodeLast": "",
//            "verifyList": [],
//            "orderList": [],
//            "varsList": [],
//            "subProcessList": [],
//            "conditionList": [],
//            "presonList": []
//        }

        SendMsgNode msgNode = new SendMsgNode();
        msgNode.setNodeId(objectNode.get("id").asText());
        msgNode.setNodeName(objectNode.get("nodeName").asText());
        msgNode.setNodeType(NodeType.MSG_SEND_NODE);
        setValue(msgNode, "sendType", objectNode.get("contentType") != null ? objectNode.get("contentType").intValue() : null);
        setValue(msgNode, "msgBody", JSONArray.parseArray(objectNode.get("contentBody").toString(), JSONObject.class));
        setValue(msgNode, "buttons", JSONArray.parseArray(objectNode.get("buttonList").toString(), JSONObject.class));
        List<Long> templateIds = new ArrayList<>();
        List<JSONObject> contentBody = JSONArray.parseArray(objectNode.get("contentBody").toString(), JSONObject.class);
        for (JSONObject jsonObject : contentBody) {
            if (jsonObject.get("templateId") != null) {
                templateIds.add(jsonObject.getLong("templateId"));
            }
        }
        msgNode.setTemplateIds(templateIds);
        /*其它属性*/
        return msgNode;
    }

    private static void setValue(Object dto, String name, Object value) {
        try {
            Method[] m = dto.getClass().getMethods();
            for (int i = 0; i < m.length; i++) {
                if (("set" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                    m[i].invoke(dto, value);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getValue(Object dto, String name) {
        try {
            Method[] m = dto.getClass().getMethods();
            for (int i = 0; i < m.length; i++) {
                if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                    return m[i].invoke(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
