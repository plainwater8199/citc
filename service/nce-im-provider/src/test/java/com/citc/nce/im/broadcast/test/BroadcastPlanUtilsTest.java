package com.citc.nce.im.broadcast.test;

import com.citc.nce.im.broadcast.node.AbstractNode;
import com.citc.nce.im.broadcast.node.BroadcastNode;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.im.entity.RobotGroupSendPlanDescDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.service.RobotGroupSendPlanDescService;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/4/26 15:42
 */
@SpringBootTest
public class BroadcastPlanUtilsTest {
    @Autowired
    private RobotGroupSendPlansDetailService nodeService;
    @Autowired
    private RobotGroupSendPlanDescService planDefinitionService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void convertToTree() throws JsonProcessingException {
        long planId = 2248;
        List<RobotGroupSendPlansDetailDo> detailDos = nodeService.lambdaQuery().eq(RobotGroupSendPlansDetailDo::getPlanId, planId).list();
        String planDesc = planDefinitionService.lambdaQuery().eq(RobotGroupSendPlanDescDo::getPlanId, planId).oneOpt().map(RobotGroupSendPlanDescDo::getPlanDesc).orElse(null);
        List<BroadcastNode> broadcastNodeList = objectMapper.readerForListOf(BroadcastNode.class).readValue(planDesc);
        List<AbstractNode> tree = BroadcastPlanUtils.convertToTree(broadcastNodeList, detailDos);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(objectMapper.writeValueAsString(tree));
    }
}