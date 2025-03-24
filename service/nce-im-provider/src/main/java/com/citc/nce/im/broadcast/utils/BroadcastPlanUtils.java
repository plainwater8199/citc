package com.citc.nce.im.broadcast.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.auth.contactlist.vo.ContactListResp;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateContentVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDataVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.node.AbstractNode;
import com.citc.nce.im.broadcast.node.BroadcastNode;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.entity.RobotGroupSendPlanDescDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import com.citc.nce.im.service.RobotGroupSendPlanDescService;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.citc.nce.msgenum.SendStatus.EXPIRED;
import static com.citc.nce.msgenum.SendStatus.MATERIAL_REVIEW;
import static com.citc.nce.msgenum.SendStatus.NO_STATUS;
import static com.citc.nce.msgenum.SendStatus.SEND_SUCCESS;
import static com.citc.nce.msgenum.SendStatus.TO_BE_SEND;

/**
 * @author jcrenc
 * @since 2024/4/26 13:50
 */
@Slf4j
public class BroadcastPlanUtils {

    // 可执行节点状态
    public static final List<SendStatus> EXECUTABLE_NODE_STATUS = Arrays.asList(
            NO_STATUS,
            TO_BE_SEND,
            MATERIAL_REVIEW,
            EXPIRED);

    // 预计扣费节点状态
    public static final List<SendStatus> EXCEPT_DEDUCT_NODE_STATUS = Arrays.asList(
            NO_STATUS,
            TO_BE_SEND,
            MATERIAL_REVIEW);

    /**
     * 根据计划id查询计划定义和节点明细，解析节点并构建树形结构返回（默认从start节点开始解析）
     *
     * @param planId 计划id
     * @return 解析后的节点树
     */
    public static List<AbstractNode> getNodeTreeByPlanId(Long planId) {
        RobotGroupSendPlansDetailService nodeService = SpringUtils.getBean(RobotGroupSendPlansDetailService.class);
        RobotGroupSendPlanDescService planDefinitionService = SpringUtils.getBean(RobotGroupSendPlanDescService.class);
        ObjectMapper objectMapper = SpringUtils.getBean(ObjectMapper.class);
        List<RobotGroupSendPlansDetailDo> detailDos = nodeService.lambdaQuery().eq(RobotGroupSendPlansDetailDo::getPlanId, planId).list();
        String planDesc = planDefinitionService.lambdaQuery()
                .eq(RobotGroupSendPlanDescDo::getPlanId, planId).oneOpt()
                .map(RobotGroupSendPlanDescDo::getPlanDesc)
                .orElseThrow(() -> new BizException("计划定义不能为空"));
        List<BroadcastNode> broadcastNodeList;
        try {
            broadcastNodeList = objectMapper.readerForListOf(BroadcastNode.class).readValue(planDesc);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return convertToTree(broadcastNodeList, detailDos);
    }

    public static List<AbstractNode> convertToTree(List<BroadcastNode> broadcastNodeList, List<RobotGroupSendPlansDetailDo> detailDoList) {
        return convertToTree(broadcastNodeList, detailDoList, BroadcastConstants.START);
    }

    public static List<AbstractNode> convertToTree(List<BroadcastNode> broadcastNodeList, List<RobotGroupSendPlansDetailDo> detailDoList, String rootId) {
        Map<String, BroadcastNode> broadcastNodeMap = broadcastNodeList.stream()
                .filter(node -> !node.getLeftPoint().equals(BroadcastConstants.START))
                .collect(Collectors.toMap(BroadcastNode::getLeftPoint, Function.identity()));

        Map<Long, RobotGroupSendPlansDetailDo> detailDoMap = detailDoList.stream()
                .collect(Collectors.toMap(BaseDo::getId, Function.identity()));
        Map<String, AbstractNode> nodeMap = new HashMap<>();
        //解析节点，将robot_group_plan_detail表存储的节点信息和从robot_group_plans_desc计划描述表解析出的计划节点信息合并
        for (String leftPoint : broadcastNodeMap.keySet()) {
            BroadcastNode broadcastNode = broadcastNodeMap.get(leftPoint);
            Long nodeId = Long.valueOf(broadcastNode.getParams().getId());
            RobotGroupSendPlansDetailDo detailNode = detailDoMap.get(nodeId);
            nodeMap.put(leftPoint, NodeFactory.createNode(detailNode, broadcastNode));
        }
        //构建树形结构，设置parent和children，并筛选出parent为rootId的直系节点（方法返回值）
        Map<String, List<AbstractNode>> childrenListMap = new HashMap<>();
        for (AbstractNode node : nodeMap.values()) {
            childrenListMap.computeIfAbsent(node.getParentPoint(), k -> new ArrayList<>()).add(node);
        }

        List<AbstractNode> tree = new ArrayList<>();
        for (AbstractNode node : nodeMap.values()) {
            if (Objects.equals(node.getParentPoint(), rootId)) {
                tree.add(node);
            }
            List<AbstractNode> children = childrenListMap.get(node.getLeftPoint());
            if (children != null) {
                node.setChildren(children);
                children.forEach(child -> child.setParent(node));
            }
        }
        //查询计划涉及的联系人组信息，用于后面计算节点预估发送量
        ContactListApi contactApi = SpringUtils.getBean(ContactListApi.class);
        GroupNodeAccountDetailMapper nodeAccountDetailMapper = SpringUtils.getBean(GroupNodeAccountDetailMapper.class);
        List<Long> groupIds = nodeMap.values().stream()
                .map(AbstractNode::getGroupId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, List<ContactListResp>> groupContactMap = contactApi.getContactListByGroupIds(groupIds);
        Set<Long> nodeIds = nodeMap.values().stream()
                .map(AbstractNode::getId)
                .collect(Collectors.toSet());
        Map<Long, Map<String, GroupNodeAccountDetail>> detailMap = nodeAccountDetailMapper.selectList(
                        Wrappers.<GroupNodeAccountDetail>lambdaQuery()
                                .in(GroupNodeAccountDetail::getNodeId, nodeIds)
                ).stream()
                .collect(
                        Collectors.groupingBy(
                                GroupNodeAccountDetail::getNodeId,
                                Collectors.toMap(
                                        GroupNodeAccountDetail::getAccountId,
                                        Function.identity()
                                )
                        )
                );
        ArrayDeque<AbstractNode> queue = new ArrayDeque<>(tree);
        while (!queue.isEmpty()) {
            AbstractNode node = queue.pop();
            //计算节点最大发送量
            if (node.getParent() != null && isParentFilterNode(node, node.getParent())) {
                node.setMaxSendNumber(node.getParent().getMaxSendNumber());
            } else {
                node.setMaxSendNumber(groupContactMap.get(node.getGroupId()).size());
            }
            //将当前节点的子节点添加到队列末尾
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                queue.addAll(node.getChildren());
            }
            //设置账号发送详情,一个节点可能使用多个账号进行发送，从数据库读取每个账号的扣除、发送、返还数量
            Map<String, AbstractNode.AccountSendDetail> accountDetailMap = new HashMap<>();
            detailMap.getOrDefault(node.getId(), Collections.emptyMap())
                    .forEach((accountId, dbDetail) -> {
                        AbstractNode.AccountSendDetail detail = new AbstractNode.AccountSendDetail();
                        detail.setPreemptedNumber(dbDetail.getPreemptedNumber())
                                .setSendNumber(dbDetail.getActualSendNumber())
                                .setReturnNumber(dbDetail.getReturnNumber());
                        accountDetailMap.put(accountId, detail);
                    });
            node.setAccountSendDetail(accountDetailMap);
        }
        return tree;
    }

    /**
     * 将节点树形结构转换成列表
     */
    public static List<AbstractNode> flatNodeTree(List<AbstractNode> nodeTree) {
        List<AbstractNode> flatNodes = new ArrayList<>();
        List<AbstractNode> roots = new ArrayList<>(nodeTree);
        while (!roots.isEmpty()) {
            AbstractNode node = roots.remove(0);
            flatNodes.add(node);
            if (!CollectionUtils.isEmpty(node.getChildren()))
                roots.addAll(node.getChildren());
        }
        return flatNodes;
    }

    /**
     * 判断node是不是parent的人群筛选节点
     *
     * @return 是的话返回true
     */
    public static boolean isParentFilterNode(AbstractNode node, AbstractNode parent) {
        if (node == null || parent == null || !Objects.equals(node.getParentPoint(), parent.getLeftPoint()) || CollectionUtils.isEmpty(parent.getFilterRouters()))
            return false;
        return parent.getFilterRouters().stream().anyMatch(route -> route.getChildren().contains(node.getLeftPoint()));
    }

    /**
     * 批次发送
     *
     * @param phones            全部要发送的号码
     * @param batchSize         多少个号码一个批次
     * @param availableResource 可用资源数，具体发送的手机号不能超过此限制（为空表示不限制资源）
     * @param batchConsumer     consumer
     */
    public static void batchSend(List<String> phones, int batchSize, Integer availableResource, Consumer<List<String>> batchConsumer) {
        int totalBatch = (int) Math.ceil(phones.size() / (double) batchSize);
        for (int batch = 1; batch <= totalBatch; batch++) {
            List<String> batchSendPhoneList = phones.stream()
                    .skip((long) (batch - 1) * batchSize)
                    .limit(availableResource == null ? batchSize : Math.min(batchSize, availableResource))
                    .collect(Collectors.toList());
            if (batchConsumer != null && !CollectionUtils.isEmpty(batchSendPhoneList))
                batchConsumer.accept(batchSendPhoneList);

            if (availableResource != null) {
                availableResource -= batchSendPhoneList.size();
                if (availableResource == 0)
                    break;
            }
        }
    }


    public static Integer getContentLength(SmsTemplateDetailVo templateInfo) {
        String contentNew = "";
        //个性模板-变量模板
        SmsTemplateContentVo smsTemplateContentVo = JSONObject.parseObject(templateInfo.getContent(), SmsTemplateContentVo.class);
        List<SmsTemplateDataVo> smsTemplateDataVos = smsTemplateContentVo.getNames();
        contentNew = smsTemplateContentVo.getValue();
        if (templateInfo.getTemplateType() == 2) {
            if (smsTemplateDataVos != null && !smsTemplateDataVos.isEmpty()) {
                for (SmsTemplateDataVo smsTemplateDataVo : smsTemplateDataVos) {
                    contentNew = contentNew.replace("{{" + smsTemplateDataVo.getId() + "}}", "{#" + smsTemplateDataVo.getName() + "#}");
                }
            }
        }
        if (StringUtils.isNotBlank(templateInfo.getSignatureContent())) {
            // 发送计算字数，如果包含签名，需要包括中括号
            log.info("短信签名为{}", templateInfo.getSignatureContent());
            contentNew = "【" + templateInfo.getSignatureContent() + "】" + contentNew;
        }
        if (contentNew.length() > 70) {
            BigDecimal a = new BigDecimal(contentNew.length());
            BigDecimal b = new BigDecimal("67");
            BigDecimal result = a.divide(b, 0, RoundingMode.UP);
            return result.intValue();
        }
        return 1;
    }


    /**
     * 递归的遍历节点树，为可执行的节点应用function
     *
     * @param nodeTree 节点树
     * @param function 参数为节点，返回值表示是否应用当前节点的子节点
     */
    public static void applyExecutableNode(List<AbstractNode> nodeTree, Function<AbstractNode, Boolean> function) {
        for (AbstractNode node : nodeTree) {
            if (EXECUTABLE_NODE_STATUS.contains(node.getStatus())) {
                if (function.apply(node)) {
                    if (!CollectionUtils.isEmpty(node.getChildren())) {
                        applyExecutableNode(node.getChildren(), function);
                    }
                }
            } else if (SEND_SUCCESS == node.getStatus()) {
                if (!CollectionUtils.isEmpty(node.getChildren())) {
                    applyExecutableNode(node.getChildren(), function);
                }
            }
        }
    }

    /**
     * 递归的遍历节点树，为预计扣费的节点应用function
     *
     * @param nodeTree 节点树
     * @param function 参数为节点，返回值为是否应用当前节点的子节点
     */
    public static void applyExpectDeductNode(List<AbstractNode> nodeTree, Function<AbstractNode, Boolean> function) {
        for (AbstractNode node : nodeTree) {
            if (EXCEPT_DEDUCT_NODE_STATUS.contains(node.getStatus())) {
                if (function.apply(node)) {
                    if (!CollectionUtils.isEmpty(node.getChildren())) {
                        applyExpectDeductNode(node.getChildren(), function);
                    }
                }
            } else if (SEND_SUCCESS == node.getStatus()) {
                if (!CollectionUtils.isEmpty(node.getChildren())) {
                    applyExpectDeductNode(node.getChildren(), function);
                }
            }
        }
    }


    public static boolean hasExecutableNode(List<AbstractNode> nodeTree) {
        for (AbstractNode node : nodeTree) {
            if (EXECUTABLE_NODE_STATUS.contains(node.getStatus())) {
                return true; //如果当前节点包含了目标状态，则直接返回
            } else if (SEND_SUCCESS == node.getStatus()) {
                if (!CollectionUtils.isEmpty(node.getChildren())) {
                    if (hasExecutableNode(node.getChildren())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
