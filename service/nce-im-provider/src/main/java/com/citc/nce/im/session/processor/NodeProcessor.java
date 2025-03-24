package com.citc.nce.im.session.processor;

import com.alibaba.fastjson.annotation.JSONField;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.robot.vo.NodeActResult;
import com.citc.nce.robot.vo.UpMsgReq;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Strings;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/5 17:35
 * @Version: 1.0
 * @Description:
 */

//todo 带删除
@Data
public class NodeProcessor {
    private static Logger log = LoggerFactory.getLogger(NodeProcessor.class);

    public volatile long processorId;

    private String processorName;

    private ProcessorConfig processorConfig;

    private volatile String currentNodeId = "abcd";

    private String nextNodeId = "";

    //默认父流程id为-1
    public volatile long parentProcessorId = -1;

    /**
     * 变量信息
     */
    private SessionContext sessionContext;

    /**
     * 0 初始化
     * 1 待选择流程
     * 2 进入流程种
     */
    private Integer status;
    /**
     * 0 未进行中
     * 1 进行中，进行中会忽略上行消息
     */
    private Integer running;

    private static final Integer RUNNING_STATUS = 1;

    @JSONField(serialize = false)
    private boolean isEnd = false;


    private UpMsgReq upMsgReq;

    @JSONField(serialize = false)
    private boolean isLoad = true;

    @JSONField(serialize = false)
    private StringRedisTemplate stringRedisTemplate;

    @JSONField(serialize = false)
    private ParsedProcessorConfig parsedProcessorConfig;

    private static final int OVER_TIME = 30;

    /**
     * 2023-5-25
     * 当前流程标识：
     * 0：普通流程， 1：子流程
     */
    private int subFlag;


    public NodeActResult doProcess(ParsedProcessorConfig parsedProcessorConfig) throws Exception {
        if (isEnd) {
            return new NodeActResult();
        }
        String currentNodeId = getCurrentNodeId();
        NceRobotNode nceRobotNodeCurrent = null;
        if ("abcd".equals(currentNodeId)) {
            Collection<String> childrenName = parsedProcessorConfig.takeChildrenName(getCurrentNodeId());
            currentNodeId = childrenName.toArray(new String[0])[0];
            setCurrentNodeId(currentNodeId);
        }
        nceRobotNodeCurrent = getParsedProcessorConfig().getNodeInfoMap().get(currentNodeId);
        if (nceRobotNodeCurrent == null) {
            /**
             * 2023-5-26
             * 子流程结束，自身没有节点了，需要返回父流程
             */
            if (1 == subFlag) {
                log.info("子流程结束，自身没有节点了，返回父流程");
                checkAndClean();
            }
            log.warn("节点不存在，请检查配置");
            NodeActResult nodeActResult = new NodeActResult();
            nodeActResult.setResult("节点不存在，请检查配置");
            return nodeActResult;
        }
        SessionContext sessionContext = getSessionContext();
        //判断是否是子流程
        checkNodeIsSubProcess(nceRobotNodeCurrent);
        if (isEnd) {
            return new NodeActResult();
        }
        NodeActResult nodeActResult = nceRobotNodeCurrent.act(sessionContext, this);
        log.info("__________ 写入统计数据 nodeActResult： {}", nodeActResult);
        //只有当提问节点时，下一个节点允许child为空，其他节点均认为为最后一个节点，直接返回
        if (!nceRobotNodeCurrent.getNodeType().equals(NodeType.QUESTION_NODE.getCode())) {
            if (!StringUtils.hasText(nodeActResult.getChildIdx())) {
                // 此处算是流程结束，写入统计数据
                log.info("__________ 写入统计数据 点位1");
//                saveTemporaryStatistics(upMsgReq);
                return new NodeActResult();
            }
        }
        //记录processor信息，包括当前会话执行到那个流程
        loadDataToRedis(upMsgReq, processorName);
        String childIdx = currentNodeId;
        if (!nceRobotNodeCurrent.getNodeType().equals(NodeType.BLANK_BRANCH_NODE.getCode())) {
            childIdx = nodeActResult.getChildIdx();
        }
        log.info("__________ 写入统计数据 childIdx： {}", childIdx);
        if (nodeActResult.isNext() || StringUtils.hasText(childIdx)) {
            setCurrentNodeId(childIdx);
            nceRobotNodeCurrent = getParsedProcessorConfig().getNodeInfoMap().get(getCurrentNodeId());
            log.info("__________ 写入统计数据 nceRobotNodeCurrent： {}", nceRobotNodeCurrent);
            if (null == nceRobotNodeCurrent) {
                // 此处算是流程结束，写入统计数据
                log.info("__________ 写入统计数据 点位2");
//                saveTemporaryStatistics(upMsgReq);
//                return nodeActResult;
                /**
                 * 2023/1/16
                 * 修改迭代三BUG
                 * 节点无内容，应该退出时没有正确返回
                 */
                return NodeActResult.builder().next(false).result("end").childIdx(childIdx).build();
            }
            //处理提问节点，提问节点需要结合父节点条件判断
            if (nceRobotNodeCurrent.getNodeType().equals(NodeType.QUESTION_NODE.getCode())) {
                nodeActResult = nceRobotNodeCurrent.act(sessionContext, this);
            } else {
                if (nceRobotNodeCurrent.getNodeType().equals(NodeType.BLANK_BRANCH_NODE.getCode())) {
                    List<String> branchList = parsedProcessorConfig.getNodeIdRelations().get(getCurrentNodeId());
                    boolean isBreak = false;
                    for (int i = 0; i < branchList.size(); i++) {
                        nceRobotNodeCurrent = parsedProcessorConfig.getNodeNameById(branchList.get(i));
                        boolean fan = nceRobotNodeCurrent.enterCondition(sessionContext, this);
                        if (fan) {
                            isBreak = true;
                            List<String> list = parsedProcessorConfig.getNodeIdRelations().get(branchList.get(i));
                            if (list.size() == 0) {
                                checkAndClean();
                                nodeActResult.setChildIdx("");
                                return nodeActResult;
                            }
                            setCurrentNodeId(list.get(0));
                            nceRobotNodeCurrent = getParsedProcessorConfig().getNodeInfoMap().get(getCurrentNodeId());
                            //判断是否是子流程
                            checkNodeIsSubProcess(nceRobotNodeCurrent);
                            nodeActResult = nceRobotNodeCurrent.act(sessionContext, this);
                            //如果是子流程，返回
                            if (nceRobotNodeCurrent.getNodeType().equals(NodeType.SUB_PROCESSOR_NODE.getCode())) {
                                return nodeActResult;
                            }
                            String nextChildIdx = nodeActResult.getChildIdx();
                            if (nodeActResult.isNext() || StringUtils.hasText(nextChildIdx)) {
                                setCurrentNodeId(nextChildIdx);
                            }
                            break;
                        }
                        setCurrentNodeId("");
                    }
                    if (!isBreak) {
                        checkAndClean();
                        nodeActResult.setChildIdx("");
                        return nodeActResult;
                    }
                } else {
                    if (nceRobotNodeCurrent.enterCondition(sessionContext, this)) {
                        nceRobotNodeCurrent = parsedProcessorConfig.getNodeNameById(getCurrentNodeId());
                        //判断是否是子流程
                        checkNodeIsSubProcess(nceRobotNodeCurrent);
                        nodeActResult = nceRobotNodeCurrent.act(sessionContext, this);
                        String nextChildIdx = nodeActResult.getChildIdx();
                        if (nodeActResult.isNext() || StringUtils.hasText(nextChildIdx)) {
                            setCurrentNodeId(nextChildIdx);
                        } else {
                            return new NodeActResult();
                        }
                    }
                }
            }
        }
        loadDataToRedis(upMsgReq, processorName);
//        saveCacheUpMsg(upMsgReq);

        log.info("process流程处理结束:" + nceRobotNodeCurrent.getNodeName());
        if (checkNodeNext(nceRobotNodeCurrent) && nodeActResult.isNext() && !isEnd) {
            doProcess(parsedProcessorConfig);
        }
        saveCacheUpMsg(upMsgReq);
        return nodeActResult;
    }

    private void saveCacheUpMsg(UpMsgReq upMsgReq) {
        if (upMsgReq != null && !Strings.isNullOrEmpty(upMsgReq.getConversationId())) {
            System.out.println("--------------------------------------------------统计数据 存入redis-----------------------------------------------------------：" + upMsgReq.getConversationId());
            stringRedisTemplate.opsForValue().set("UP_MSG:" + upMsgReq.getConversationId(), JsonUtils.obj2String(upMsgReq), 60, TimeUnit.MINUTES);
        }
    }

    private UpMsgReq getUpMsg(String conversationId) {
        String upMsgStr = stringRedisTemplate.opsForValue().get("UP_MSG:" + conversationId);
        System.out.println("--------------------------------------------------统计数据  获取redis-----------------------------------------------------------：" + upMsgStr);
        return JsonUtils.string2Obj(upMsgStr, UpMsgReq.class);
    }


//    private void saveTemporaryStatistics(UpMsgReq upMsgReq) {
//        log.info("准备写入流程结束的统计数据");
//        log.info("upMsgReq: {}", upMsgReq);
//        // 这里需要网关数据才会进行统计
//        if(upMsgReq.getFalg()!=null&&upMsgReq.getFalg()==1){
//            /**
//             * 能进流程，是有场景ID和流程ID的
//             * 2023/3/24
//             */
//            if (null != upMsgReq.getProcessId() && null != upMsgReq.getSceneId()) {
//                TemporaryStatisticsReq temporaryStatisticsReq = new TemporaryStatisticsReq();
//                temporaryStatisticsReq.setSceneId(upMsgReq.getSceneId());
//                temporaryStatisticsReq.setProcessId(upMsgReq.getProcessId());
//                temporaryStatisticsReq.setType(2);
//                temporaryStatisticsReq.setCreator(upMsgReq.getCreate());
//                temporaryStatisticsReq.setUpdater(upMsgReq.getCreate());
//                temporaryStatisticsReq.setChatbotAccountId(upMsgReq.getChatbotAccount());
//                temporaryStatisticsReq.setChatbotType(ChannelTypeUtil.getChannelType(upMsgReq.getAccountType()));
//                log.info("写入统计数据 temporaryStatisticsReq {}", temporaryStatisticsReq);
//                ApplicationContextUil.getBean(TemporaryStatisticsApi.class).saveTemporaryStatisticsApi(temporaryStatisticsReq);
//            }
//        }
//    }

    private String getRedisKey(UpMsgReq upMsgReq) {
        StringBuffer redisKey = new StringBuffer();
        redisKey.append(upMsgReq.getChatbotAccount());
        if (StringUtil.isNotEmpty(upMsgReq.getPhone())) {
            redisKey.append("@");
            redisKey.append(upMsgReq.getPhone());
        }
        redisKey.append(":");
        return redisKey.toString();
    }

    private void loadDataToRedis(UpMsgReq upMsgReq, String processorName) {
        if (!isLoad) {
            return;
        }
        String dataStr = JsonUtils.obj2String(this);
        String key = getRedisKey(upMsgReq);
        //记录当前会话对应的流程id
        if ("-2".equals(stringRedisTemplate.opsForValue().get(key + upMsgReq.getConversationId() + "@currentProcessorId"))) {
            return;
        }
        StringBuffer redisKey = new StringBuffer();
        redisKey.append(upMsgReq.getChatbotAccount());
        if (StringUtil.isNotEmpty(upMsgReq.getPhone())) {
            redisKey.append("@");
            redisKey.append(upMsgReq.getPhone());
        }
        redisKey.append(":RebotSetting");
        //超时时间查询配置
        String expireTime = stringRedisTemplate.opsForValue().get(redisKey + "expireTime" + upMsgReq.getConversationId());
        stringRedisTemplate.opsForValue().set(key + upMsgReq.getConversationId() + "@currentProcessorId", String.valueOf(getProcessorId()), Integer.parseInt(expireTime), TimeUnit.MINUTES);
        //持久化到redis
        stringRedisTemplate.opsForHash().put(key + upMsgReq.getConversationId() + getProcessorId(), processorName, dataStr);
        stringRedisTemplate.opsForHash().put(key + "tmp_" + upMsgReq.getConversationId(), processorName, dataStr);
        /**
         * 2023-4-21
         * 设置redis过期时间
         */
        stringRedisTemplate.expire(key + "tmp_" + upMsgReq.getConversationId(), Integer.parseInt(expireTime), TimeUnit.MINUTES);
        stringRedisTemplate.expire(key + upMsgReq.getConversationId() + getProcessorId(), Integer.parseInt(expireTime), TimeUnit.MINUTES);
    }

    public void checkAndClean() {
//        try {
//            log.info("最后一个节点，清理本次对话"+processorId+","+parentProcessorId);
//            /**
//             * 2023-5-26
//             * 解决不能返回父流程的问题
//             * 用redis控制版本，避免子流程互相调用时，相互删除。
//             */
//            String redisKey = getRedisKey(upMsgReq);
//            String increment = stringRedisTemplate.opsForValue().get(redisKey + upMsgReq.getConversationId() + getProcessorId() + "version");
//            if (StringUtil.isNotEmpty(increment) && Integer.parseInt(increment) > 0) {
//                stringRedisTemplate.delete(redisKey + upMsgReq.getConversationId() + processorId + increment);
//            }
//            stringRedisTemplate.opsForValue().increment(redisKey + upMsgReq.getConversationId() + getProcessorId() + "version", -1);
//            //超时时间查询配置
//            String expireTime = stringRedisTemplate.opsForValue().get(redisKey + "RebotSettingexpireTime" + upMsgReq.getConversationId());
//            stringRedisTemplate.expire(redisKey + upMsgReq.getConversationId() + getProcessorId() + "version", Integer.parseInt(expireTime),TimeUnit.MINUTES);
//            isLoad = false;
//            //判断是子流程结束还是父流程结束
//            if(-1 == (parentProcessorId)){
//                isEnd = true;
//                String conversationId = upMsgReq.getConversationId();
//                UpMsgReq upMsgReq = getUpMsg(conversationId);
//                saveTemporaryStatistics(upMsgReq);
//                stringRedisTemplate.opsForValue().set(redisKey + upMsgReq.getConversationId() + "@currentProcessorId","-2",Integer.parseInt(expireTime),TimeUnit.MINUTES);
//                return;
//            }
//            increment = stringRedisTemplate.opsForValue().get(redisKey + upMsgReq.getConversationId() + getParentProcessorId() + "version");
//            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(redisKey + upMsgReq.getConversationId() + parentProcessorId + increment);
//            if (!CollectionUtil.isEmpty(entries)) {
//                currentNodeId = stringRedisTemplate.opsForValue().get(redisKey + upMsgReq.getConversationId() + parentProcessorId + "index");
//                String desc = (String) entries.get(NodeType.PROCESSOR_NODE.getDesc());
//                NodeProcessor nodeProcessor = new NodeProcessor();
//                nodeProcessor = JsonUtils.string2Obj(desc, NodeProcessor.class);
//                nodeProcessor.setProcessorName(NodeType.PROCESSOR_NODE.getDesc());
//                nodeProcessor.setSessionContext(this.getSessionContext());
//                nodeProcessor.setCurrentNodeId(currentNodeId);
//                nodeProcessor.setProcessorId(parentProcessorId);
//                nodeProcessor.setStringRedisTemplate(this.getStringRedisTemplate());
//                nodeProcessor.setUpMsgReq(this.getUpMsgReq());
//                nodeProcessor.process();
//            } else {
//                /**
//                 * 2023-6-9
//                 * 一个会话中，子流程结束也要算是流程完成
//                 */
//                String conversationId = upMsgReq.getConversationId();
//                UpMsgReq upMsgReq = getUpMsg(conversationId);
//                if (this.getParentProcessorId() == upMsgReq.getProcessId()) {
//                    upMsgReq.setProcessId(this.getProcessorId());
//                }
//                saveTemporaryStatistics(upMsgReq);
//            }
//        } catch (Exception e) {
//            log.error("回到父节点异常：", e.getMessage());
//        }
    }


    //判断节点是否是子流程，如果是，记录父节点当前的运行位置,以及子流程后面要执行的节点id
    private void checkNodeIsSubProcess(NceRobotNode nceRobotNodeCurrent) {
        if (nceRobotNodeCurrent.getNodeType().equals(NodeType.SUB_PROCESSOR_NODE.getCode())) {
            List<String> list = parsedProcessorConfig.getNodeIdRelations().get(getCurrentNodeId());
            String childIdx = "";
            if (list.size() > 0) {
                childIdx = list.get(0);
            }
            String redisKey = getRedisKey(upMsgReq);
            stringRedisTemplate.opsForValue().set(redisKey + upMsgReq.getConversationId() + processorId + "index", childIdx, OVER_TIME, TimeUnit.MINUTES);
        }
    }

    //以下节点，直接向下一个节点移动
    private boolean checkNodeNext(NceRobotNode nceRobotNode) {
        List<NodeType> nodeTypeList = new ArrayList<>();
        nodeTypeList.add(NodeType.MSG_SEND_NODE);
        nodeTypeList.add(NodeType.COMMAND_NODE);
        nodeTypeList.add(NodeType.VAR_ACT_NODE);
        nodeTypeList.add(NodeType.SUB_PROCESSOR_NODE);
        nodeTypeList.add(NodeType.BRANCH_NODE);
        nodeTypeList.add(NodeType.CONTACT);
        for (NodeType nodeType : nodeTypeList) {
            if (nceRobotNode.getNodeType().equals(nodeType.getCode())) {
                return true;
            }
        }
        return false;
    }

}
