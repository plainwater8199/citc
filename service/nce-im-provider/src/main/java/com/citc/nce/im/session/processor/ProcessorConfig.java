package com.citc.nce.im.session.processor;

import com.citc.nce.im.robot.node.QuestionNode;
import com.citc.nce.im.robot.node.SendMsgNode;
import com.citc.nce.im.robot.node.SubProcessorNode;
import com.citc.nce.im.session.processor.ncenode.impl.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/6 15:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class ProcessorConfig {

//    private List<QuestionNode> robotProcessQuestionNodeList = new ArrayList<>();

    private List<BranchNode> robotProcessBranchNodeList = new ArrayList<>();

//    private List<SendMsgNode> robotProcessSendMessageNodeList = new ArrayList<>();

    private List<CommandNode> robotProcessOrderNodeList = new ArrayList<>();

    private List<VarActNode> robotProcessVariableNodeList = new ArrayList<>();

//    private List<SubProcessorNode> robotProcessSubProcessNodeList = new ArrayList<>();

    private List<StartNode> startNodeList = new ArrayList<>();

    private List<Line> robotProcessLineList = new ArrayList<>();

    private List<ContactNode> contactNodeList = new ArrayList<>();


}
