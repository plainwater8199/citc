package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountChatbotAccountQueryReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.DeleteTemplateForInvalidOfProcessReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateIdResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateReq;
import com.citc.nce.auth.messagetemplate.vo.shortcutbutton.ButtonDetailInfo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.constants.NodeTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.BizExposeStatusException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.JsonSameUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.service.ModuleService;
import com.citc.nce.robot.Consts.ProcessStatusEnum;
import com.citc.nce.robot.RobotOrderApi;
import com.citc.nce.robot.RobotProcessSettingNodeApi;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.bean.*;
import com.citc.nce.robot.dao.*;
import com.citc.nce.robot.entity.*;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotProcessTreeService;
import com.citc.nce.robot.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.service.impl
 * @Author: weilanglang
 * @CreateTime: 2022-07-06  17:39
 * @Description: 流程图操作    未发布保存在数据库    已发布保存在redis
 * @Version: 1.0
 */
@Service
@Slf4j
public class RobotProcessTreeServiceImpl implements RobotProcessTreeService {

    @Resource
    RobotProcessDesDao processDesDao;

    @Resource
    RobotProcessSettingNodeDao robotProcessSettingNodeDao;

    @Resource
    RobotProcessAskDao robotProcessAskDao;

    //流程图快捷按钮相关
    @Resource
    RobotShortcutButtonDao robotShortcutButtonDao;

    @Resource
    RobotProcessTreeApi robotProcessTreeApi;

    @Resource
    RobotVariableApi robotVariableApi;

    @Resource
    RobotOrderApi robotOrderApi;

    @Resource
    private RobotSceneNodeDao robotSceneNodeDao;

    @Resource
    RobotProcessSettingNodeApi robotProcessSettingNodeApi;
    @Resource
    MessageTemplateApi messageTemplateApi;
    @Resource
    AccountManagementApi accountManagementApi;
    @Resource
    ModuleService moduleService;


    @Resource
    RedisService redisService;
    String robot_template_audit_redis_key = "robot:template:audit:%s";

    /**
     * 流程图保存
     * 枷锁防止多线程保存出错
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized void saveProcessDes(RobotProcessTreeReq processTreeReq) {
        String processDes = processTreeReq.getProcessDes();
        log.info("修改流程图------1 processDes{}", processDes);
        long processId = processTreeReq.getProcessId();
        String userId = SessionContextUtil.getUser().getUserId();
        RobotProcessSettingNodeDo robotProcessSettingNodeDo = getRobotProcessSettingNodeDo(processTreeReq.getProcessId(), userId);
        //处于发布状态的流程图   编辑后也不处于发布状态   只有点击发布后  才会覆盖之前的发布
        RobotProcessDesDo robotProcessDesDo = new RobotProcessDesDo();
        robotProcessDesDo.setProcessDes(processTreeReq.getProcessDes());
        robotProcessDesDo.setProcessId(processId);
        //待审核为-1

        robotProcessDesDo.setPoseNum(processTreeReq.getPoseNum());
        robotProcessDesDo.setBranchNum(processTreeReq.getBranchNum());
        robotProcessDesDo.setSendNum(processTreeReq.getSendNum());
        robotProcessDesDo.setInstructionNode(processTreeReq.getInstructionNode());
        robotProcessDesDo.setVariableOperation(processTreeReq.getVariableOperation());
        robotProcessDesDo.setSubProcess(processTreeReq.getSubProcess());
        robotProcessDesDo.setContactAction(processTreeReq.getContactAction());
        //是否有 没有送审通过的
        QueryWrapper<RobotProcessDesDo> robotProcessDesDoQueryWrapper = new QueryWrapper<>();
        robotProcessDesDoQueryWrapper.eq("process_id", processId);
        robotProcessDesDoQueryWrapper.ne("release_type", ProcessStatusEnum.Success.getCode());
        RobotProcessDesDo existProcessDes = processDesDao.selectOne(robotProcessDesDoQueryWrapper);

        QueryWrapper<RobotProcessDesDo> robotProcessDesExistProvedDoQueryWrapper = new QueryWrapper<>();
        robotProcessDesExistProvedDoQueryWrapper.eq("process_id", processId);
        robotProcessDesExistProvedDoQueryWrapper.eq("release_type", ProcessStatusEnum.Success.getCode());
        RobotProcessDesDo existProvedProcessDes = processDesDao.selectOne(robotProcessDesExistProvedDoQueryWrapper);

        JSONObject oldModuleJson = JSON.parseObject(ObjectUtil.isNotEmpty(existProcessDes) ? existProcessDes.getProcessDes() : (ObjectUtil.isNotEmpty(existProvedProcessDes) ? existProvedProcessDes.getProcessDes() : "{}"));
        JSONArray oldRobotProcessNodeList = oldModuleJson.getJSONArray("robotProcessNodeList");

        //提问节点集合
        JSONObject jsonObject = JSON.parseObject(processDes);
        JSONArray robotProcessNodeList = jsonObject.getJSONArray("robotProcessNodeList");
        boolean isDescSame = JsonSameUtil.same(JSONObject.toJSONString(robotProcessNodeList), JSONObject.toJSONString(oldRobotProcessNodeList), "top,left,width,color");
        //存在送审通过的这时候type为 3 更新未送审
        robotProcessDesDo.setReleaseType((robotProcessSettingNodeDo.getStatus() == ProcessStatusEnum.Success.getCode() || robotProcessSettingNodeDo.getStatus() == ProcessStatusEnum.Updated.getCode()) ? ProcessStatusEnum.Updated.getCode() : ProcessStatusEnum.Waiting.getCode());
        if (existProcessDes != null) {
            //该流程已存在   执行更新操作
            UpdateWrapper<RobotProcessDesDo> wrapper = new UpdateWrapper<RobotProcessDesDo>();
            wrapper.eq("id", existProcessDes.getId());
            log.info("修改流程图------2 processDes{}", processDes);
            processDesDao.update(robotProcessDesDo, wrapper);
        } else {
            processDesDao.insert(robotProcessDesDo);
        }
        //保存流程图中的快捷按钮
        if (StringUtils.isNotEmpty(processTreeReq.getRobotShortcutButtons())) {
            saveRobotShortcutButtonDo(processTreeReq);
        }


        ArrayList<String> nodeNameList = new ArrayList<String>();
        for (int i = 0; i < robotProcessNodeList.size(); i++) {
            JSONObject o = robotProcessNodeList.getJSONObject(i);
            if (0 == o.getLong("nodeType")) {
                //提问节点
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(o.getString("id"));
                stringBuffer.append("&");
                stringBuffer.append(o.getString("nodeName"));
                nodeNameList.add(stringBuffer.toString());
            }
        }

        RobotProcessAskDo robotProcessAskDo = new RobotProcessAskDo();
        robotProcessAskDo.setProcessId(processId);
        robotProcessAskDo.setNodeName(JSONObject.toJSONString(nodeNameList));
        RobotProcessAskDo existProcessAsk = robotProcessAskDao.selectOne("process_id", processId);
        if (existProcessAsk != null) {
            //如果有该流程的提问节点   即使没有提问节点也要更新更新
            UpdateWrapper<RobotProcessAskDo> wrapper = new UpdateWrapper<RobotProcessAskDo>();
            wrapper.eq("id", existProcessAsk.getId());
            log.info("有该流程的提问节点   即使没有提问节点也要更新更新");
            robotProcessAskDao.update(robotProcessAskDo, wrapper);
        } else {
            //直接新增
            if (!nodeNameList.isEmpty()) {
                robotProcessAskDao.insert(robotProcessAskDo);
            }
        }

        //更新修改时间
        Date currTime = DateUtil.date();
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", processId);
        robotProcessSettingNodeDo.setModifiedTime(currTime);
        if (!isDescSame) {
            robotProcessSettingNodeDo.setStatus((robotProcessSettingNodeDo.getStatus() == ProcessStatusEnum.Success.getCode() || robotProcessSettingNodeDo.getStatus() == ProcessStatusEnum.Updated.getCode()) ? ProcessStatusEnum.Updated.getCode() : ProcessStatusEnum.Waiting.getCode());
        }
        robotProcessSettingNodeDao.update(robotProcessSettingNodeDo, wrapper);
    }

    private void saveRobotShortcutButtonDo(RobotProcessTreeReq processTreeReq) {
        if (StringUtils.isNotEmpty(processTreeReq.getRobotShortcutButtons())) {
            QueryWrapper robotShortcutButtonWrapper = new QueryWrapper();
            robotShortcutButtonWrapper.eq("deleted", 0);
            robotShortcutButtonWrapper.eq("process_id", processTreeReq.getProcessId());
            robotShortcutButtonDao.delete(robotShortcutButtonWrapper);
            List<RobotShortcutButtonDo> robotShortcutButtonDos = JSONObject.parseArray(processTreeReq.getRobotShortcutButtons(), RobotShortcutButtonDo.class);
            if (!CollectionUtils.isEmpty(robotShortcutButtonDos)) {
                robotShortcutButtonDao.insertBatch(robotShortcutButtonDos);
                //保存按钮和组件的关联关系
                for (RobotShortcutButtonDo item : robotShortcutButtonDos) {
                    String buttonDetail = item.getButtonDetail();
                    ButtonDetailInfo buttonDetailInfo = JSON.parseObject(buttonDetail, ButtonDetailInfo.class);
                    if (buttonDetailInfo != null) {
                        moduleService.saveModuleButUuidRelation(buttonDetailInfo.getType(), buttonDetailInfo.getBusinessId(), item.getUuid());
                    }
                }
            }
        }
    }

    //发布模板
    public RobotProcessTemplateAuditResp publicTemplate(RobotProcessDesDo robotProcessDesDo, String operators) {
        RobotProcessTemplateAuditResp robotProcessTemplateAuditResp = new RobotProcessTemplateAuditResp();
        JSONObject processDetailJson = JSONObject.parseObject(robotProcessDesDo.getProcessDes());
        JSONArray nodeArray = processDetailJson.getJSONArray("robotProcessNodeList");
        String result = "";
        int totalCount = 0;
        int failCount = 0;
        int successCount = 0;
        List<JSONObject> failList = new ArrayList<>();
        for (int i = 0; i < nodeArray.size(); i++) {
            JSONObject nodeItem = (JSONObject) nodeArray.get(i);
            int nodeType = nodeItem.getIntValue("nodeType");
            //提问和发送节点才有模板处理
            if (nodeType != NodeTypeEnum.QUESTION_NODE.getCode() && nodeType != NodeTypeEnum.MSG_SEND_NODE.getCode()) {
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodeName", nodeItem.get("nodeName"));
            int itemFailCount = 0;
            JSONArray nodeMessageContentJsonArrays = nodeItem.getJSONArray("contentBody");
            for (int j = 0; j < nodeMessageContentJsonArrays.size(); j++) {
                totalCount++;

                JSONObject nodeMessageContentJson = (JSONObject) nodeMessageContentJsonArrays.get(j);
                Long id = nodeMessageContentJson.getLong("templateId");
                if (ObjectUtil.isNull(id)) {

                    log.info("模板未创建不能发布,{}", nodeMessageContentJson);
                    itemFailCount++;
                    continue;
                }
                try {

                    String itemResult = messageTemplateApi.publicTemplate(id, operators, 1);
                    successCount += StrUtil.isEmpty(itemResult) ? 1 : 0;
                    itemFailCount += StrUtil.isEmpty(itemResult) ? 0 : 1;
                    log.info("{}模板发布结果：{}", id, StrUtil.isEmpty(itemResult) ? "发布成功" : "发布失败" + itemResult);
                    //当为1时创建发布失败，为2时创建发布成功， 模板最后发布结果已回调为准
                    nodeMessageContentJson.put("auditStatus", StrUtil.isEmpty(itemResult) ? 1 : 2);
                } catch (Exception exception) {
                    itemFailCount++;
                    log.error("机器人流程模板送审出错，", exception);
                    log.error(String.format("nodeName:%s,moduleInfo:%s", nodeItem.get("nodeName"), JSONObject.toJSONString(nodeMessageContentJson)));
                }
            }
            jsonObject.put("failCount", itemFailCount);
            failList.add(jsonObject);
            failCount += itemFailCount;
        }
        robotProcessDesDo.setProcessDes(processDetailJson.toJSONString());
        robotProcessTemplateAuditResp.setTotalCount(totalCount);
        robotProcessTemplateAuditResp.setFailCount(failCount);
        robotProcessTemplateAuditResp.setFailList(failList);
        robotProcessTemplateAuditResp.setSuccessCount(successCount);
        log.info("机器人发布信息概要：{}", robotProcessTemplateAuditResp);
        return robotProcessTemplateAuditResp;
    }

    /**
     * 保存模板
     *
     * @param robotProcessTreeReq
     * @param needAudit           是否需要送审 1  2
     * @return
     */
    @Transactional
    public PrepareForReleaseProcessResp saveTemplate(RobotProcessSettingNodeDo robotProcessSettingNodeDo, RobotProcessTreeReq robotProcessTreeReq, int needAudit, RobotProcessDesDo existProcessDes, RobotSceneNodeDo robotSceneNodeDo) {

        PrepareForReleaseProcessResp prepareForReleaseProcessResp = new PrepareForReleaseProcessResp();
        prepareForReleaseProcessResp.setDescLastUpdateTime(robotProcessSettingNodeDo.getModifiedTime());
        //场景管理那里改过关联账号
        //robotProcessSettingNodeDo存放发布时的关联账号，robotSceneNodeDo存放最新修改的关联账户
        //比较为了看看流程里的模板需不需要为新增账号送审（在模板内容不变时），模板变化后都需要重新送
        //删除的账号需要删除已审核通过的模板
        String sceneNodeAccounts = robotSceneNodeDo.getAccounts().replace(";", ",");

        String[] robotSceneNodeDoAccountArr = sceneNodeAccounts.split(",");
        AccountChatbotAccountQueryReq accountChatbotAccountQueryReq = new AccountChatbotAccountQueryReq();
        accountChatbotAccountQueryReq.setChatbotAccountList(Arrays.asList(robotSceneNodeDoAccountArr));
        accountChatbotAccountQueryReq.setCreator(robotProcessSettingNodeDo.getCreator());
        List<AccountManagementResp> accountManagementResps = accountManagementApi.getListByChatbotAccounts(accountChatbotAccountQueryReq);
        if (ObjectUtil.isEmpty(accountManagementResps) || accountManagementResps.size() != robotSceneNodeDoAccountArr.length) {
            throw new BizException("场景关联chatbot账号未找到");
        }
        List<String> robotSceneNodeOperatorStr = accountManagementResps.stream().map(AccountManagementResp::getAccountType).collect(Collectors.toList());
        //返回给前端的场景关联账号最后修改时间
        prepareForReleaseProcessResp.setLastChangeSceneAccountTime(robotSceneNodeDo.getAccountChangeTime());
        String processDetailStr = robotProcessTreeReq.getProcessDes();
        List<Long> templateIds = new ArrayList<>();
        JSONObject processDetailJson = JSONObject.parseObject(processDetailStr);
        JSONArray nodeArray = processDetailJson.getJSONArray("robotProcessNodeList");
        //需要送审的模板数量
        int needToAuditTemplateNum = 0;
        for (int i = 0; i < nodeArray.size(); i++) {
            JSONObject nodeItem = (JSONObject) nodeArray.get(i);
            int nodeType = nodeItem.getIntValue("nodeType");
            //提问和发送节点才有模板处理
            if (nodeType != NodeTypeEnum.QUESTION_NODE.getCode() && nodeType != NodeTypeEnum.MSG_SEND_NODE.getCode()) {
                continue;
            }
            JSONArray nodeMessageContentJsonArrays = nodeItem.getJSONArray("contentBody");
            for (int j = 0; j < nodeMessageContentJsonArrays.size(); j++) {
                JSONObject nodeMessageContentJson = (JSONObject) nodeMessageContentJsonArrays.get(j);
                MessageTemplateReq messageTemplateReq = new MessageTemplateReq();
                messageTemplateReq.setProcessNodeId(nodeItem.getString("id"));
                messageTemplateReq.setTemplateName(IdUtil.nanoId(20));
                messageTemplateReq.setTemplateSource(Constants.TEMPLATE_SOURCE_ROBOT);
                String messageContentStr = nodeMessageContentJson.getString("messageDetail");
                messageTemplateReq.setId(nodeMessageContentJson.getLong("templateId"));
                messageTemplateReq.setProcessDescId(existProcessDes.getId());
                messageTemplateReq.setTemplateType(messageContentStr.contains("}}") ? Constants.TEMPLATE_TYPE_VARIABLE : Constants.TEMPLATE_TYPE_GENARAL);
                messageTemplateReq.setMessageType(nodeMessageContentJson.getInteger("type"));
                messageTemplateReq.setShortcutButton(nodeItem.getJSONArray("buttonList").toJSONString());
                messageTemplateReq.setStyleInformation(nodeMessageContentJson.getString("styleInfo"));
                messageTemplateReq.setModuleInformation(nodeMessageContentJson.getString("messageDetail"));
                messageTemplateReq.setIsChecked(robotProcessTreeReq.getIsChecked());
                messageTemplateReq.setOperators(StrUtil.join(",", robotSceneNodeOperatorStr));
                messageTemplateReq.setProcessId(robotProcessTreeReq.getProcessId());
                //是否需要送审，保存是不需要送审，发布时需要送审
                messageTemplateReq.setNeedAudit(needAudit);
                //  文本消息素材关联账号为空
//                if (StrUtil.isNotEmpty(messageTemplateReq.getOperators())) {
//                    //场景关联账号不包含在引用素材所属账号内，则账号引用不一致
//                    if (robotSceneNodeOperatorStr.stream().filter(accountItem -> !messageTemplateReq.getOperators().contains(accountItem)).count() > 0) {
//                        throw new BizException(RobotErrorCode.RELATED_ACCOUNT_ERROR);
//                    }
//                }
                try {
                    //新增模板
                    if (ObjectUtil.isNull(messageTemplateReq.getId())) {
                        needToAuditTemplateNum += 1;
                        MessageTemplateIdResp messageTemplateIdResp = messageTemplateApi.saveMessageTemplate(messageTemplateReq);
                        if (ObjectUtil.isNotNull(messageTemplateIdResp.getId())) {
                            nodeMessageContentJson.put("templateId", messageTemplateIdResp.getId() + "");
                            templateIds.add(messageTemplateIdResp.getId());
                        }
                    }
                    //修改模板
                    else {

                        MessageTemplateIdResp messageTemplateIdResp = messageTemplateApi.updateMessageTemplate(messageTemplateReq);
                        needToAuditTemplateNum += messageTemplateIdResp.isNeedAudit() ? 1 : 0;
                    }
                } catch (BizExposeStatusException ex) {
                    throw ex;
                } catch (Exception ex) {
                    if (ex.getCause() instanceof BizExposeStatusException) {
                        throw (BizExposeStatusException) ex.getCause();
                    }
                    if (ObjectUtil.isNotNull(ex.getCause()) && !"模板审核中不能编辑".equals(ex.getCause().getMessage()))
                        throw new BizException(ex.getCause().getMessage());
                }
            }
        }
        prepareForReleaseProcessResp.setNeedAuditForUpdatedTemplate(needToAuditTemplateNum);
        //流程涉及的所有模板id
        prepareForReleaseProcessResp.setTemplateIds(templateIds);
        prepareForReleaseProcessResp.setProcessDes(processDetailJson.toJSONString());
        return prepareForReleaseProcessResp;
    }

    /**
     * 查询流程设计图（保存状态）
     */
    public RobotProcessTreeResp listProcessDesById(Long processId) {

        QueryWrapper<RobotProcessDesDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_id", processId);
        queryWrapper.ne("release_type", ProcessStatusEnum.Success.getCode());
        RobotProcessDesDo robotProcessDesDo = processDesDao.selectOne(queryWrapper);
        RobotProcessTreeResp robotProcessTreeResp = new RobotProcessTreeResp();

        if (ObjectUtil.isNotEmpty(robotProcessDesDo)) {
            String userId = SessionContextUtil.getUser().getUserId();
            if (!robotProcessDesDo.getCreator().equals(userId)) {
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }

            RobotProcessSettingNodeDo settingNodeDo = robotProcessSettingNodeDao.selectOne("id", processId);
            if (ObjectUtil.isNotEmpty(settingNodeDo)) {
                robotProcessTreeResp.setModifiedTime(settingNodeDo.getModifiedTime());
                robotProcessTreeResp.setReleaseTime(settingNodeDo.getReleaseTime());
            }
            BeanUtil.copyProperties(robotProcessDesDo, robotProcessTreeResp);
        }
        try {
            String processDes = robotProcessTreeResp.getProcessDes();
            robotProcessTreeResp.setProcessDes(buildProcessorConfig(processDes));
        } catch (Exception e) {
            log.error("buildProcessorConfig异常：", e);
        }
        return robotProcessTreeResp;
    }

    public String buildProcessorConfig(String processDes) {
        JSONObject jsonObject = JSON.parseObject(processDes);
        if (ObjectUtil.isEmpty(jsonObject)) return processDes;
        JSONArray namesArray = jsonObject.getJSONArray("names");
        //提问
        RobotProcessAskNodeResp robotProcessAskNodeResp = robotProcessTreeApi.listAllAskNode();
        List<RobotAskNodeBean> list = robotProcessAskNodeResp.getList();
        Map<String, String> map = new HashMap<>();
        list.forEach(robotAskNodeBean -> {
            List<NodeBean> nodes = robotAskNodeBean.getNodes();
            nodes.forEach(nodeBean -> {
                map.put(nodeBean.getNodeId(), nodeBean.getNodeName());
            });
        });
        for (int i = 0; i < namesArray.size(); i++) {
            JSONObject namesObject = (JSONObject) namesArray.get(i);
            Object id = namesObject.get("id");
            String name = namesObject.get("name").toString();
            int type = Integer.parseInt(namesObject.get("type").toString());
            switch (type) {
                case 0:
                    //提问
                    String question = map.get(id);
                    String questionOldChar = name;
                    String questionNewChar = question;
                    if (!StringUtils.isBlank(questionOldChar)) {
                        processDes = processDes.replace(questionOldChar, questionNewChar);
                    }
                case 1:
                    //变量
                    RobotVariableReq robotVariableReq = new RobotVariableReq();
                    if (!NumberUtil.isLong(id.toString()))
                        break;
                    robotVariableReq.setId(Long.parseLong(id.toString()));
                    RobotVariableBean robotVariableBean = robotVariableApi.queryById(robotVariableReq);
                    if (robotVariableBean != null) {
                        String variableName = robotVariableBean.getVariableName();
                        String variableNameOldChar = name;
                        String variableNameNewChar = variableName;
                        processDes = processDes.replace(variableNameOldChar, variableNameNewChar);
                    }
                case 2:
                    //指令
                    if (!NumberUtil.isLong(id.toString())) {
                        break;
                    }
                    OrderPageParam orderPageParam = new OrderPageParam();
                    orderPageParam.setId(Long.valueOf(id.toString()));
                    RobotOrderResp robotOrderResp = robotOrderApi.listAllOrder(orderPageParam);
                    if (null != robotOrderResp && robotOrderResp.getList().size() > 0) {
                        RobotOrderBean robotOrderBean = robotOrderResp.getList().get(0);
                        String orderName = robotOrderBean.getOrderName();
                        String orderNameOldChar = "\"orderName\":\"" + name + "\"";
                        String orderNameNewChar = "\"orderName\":\"" + orderName + "\"";
                        processDes = processDes.replace(orderNameOldChar, orderNameNewChar);
                    }
                case 3:
                    //子流程
                    RobotProcessTreeReq robotProcessTreeReq = new RobotProcessTreeReq();
                    robotProcessTreeReq.setProcessId(Long.parseLong(id.toString()));
                    RobotProcessSettingNodeResp robotProcessSettingNodeResp = robotProcessSettingNodeApi.getRobotProcessSettingById(Long.valueOf(id.toString()));
                    if (robotProcessSettingNodeResp != null) {
                        String processName = robotProcessSettingNodeResp.getProcessName();
                        String processNameOldChar = "\"subProcessName\":\"" + name + "\"";
                        String processNameNewChar = "\"subProcessName\":\"" + processName + "\"";
                        processDes = processDes.replace(processNameOldChar, processNameNewChar);
                    }
            }
        }
        return processDes;
    }


    /**
     * 查询流程图（发布状态）
     */
    @Override
    public RobotProcessTreeResp listReleaseProcessDesById(Long processId) {
        RobotProcessDesDo robotProcessDesDo = processDesDao.selectOne("process_id", processId, "release_type", 1);
        RobotProcessTreeResp robotProcessTreeResp = new RobotProcessTreeResp();

        if (ObjectUtil.isNotEmpty(robotProcessDesDo)) {
            BeanUtil.copyProperties(robotProcessDesDo, robotProcessTreeResp);
        }
        return robotProcessTreeResp;
    }

    //模板的状态值 -1待审核 PENDING 1 审核通过,SUCCESS 0 审核中,FAILED  2 审核不通过  跟流程状态值不一致
    public void processStatusCallback(ProcessStatusReq processStatusReq) {
        String record = JSONObject.toJSONString(processStatusReq);
        //等待流程发布完成后，更新状态
        while (redisService.hasKey(String.format(robot_template_audit_redis_key, processStatusReq.getProcessId()))) {
            try {
                log.info("等待释放流程审核锁，processId：{}", processStatusReq.getProcessId());
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        log.info("模板审核后，流程状态回填开始," + record);
        int processStatus = -1;
        //一个审核失败，就算失败
        if (processStatusReq.getStatus() == Constants.TEMPLATE_STATUS_FAILED) {
            processStatus = ProcessStatusEnum.Fail.getCode();
            log.info(("机器人流程模板,审核不通过"));
        }
        //如果成功，看看是不是所有审核都成功了
        //模板审核状态和 流程状态值不一致   0 模板为审核通过，流程为审核中   1模板为审核中，流程为审核通过
        else if (!messageTemplateApi.existAuditNotPassTemplateForProcess(processStatusReq.getProcessId())) {
            processStatus = ProcessStatusEnum.Success.getCode();
            log.info(("机器人流程模板,审核通过了"));
        } else {
            log.info("机器人流程模板还未全部审核");
            return;
        }

        QueryWrapper<RobotProcessDesDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_id", processStatusReq.getProcessId());
        queryWrapper.ne("release_type", ProcessStatusEnum.Success.getCode());
        RobotProcessDesDo theUnProvedProcessDes = processDesDao.selectOne(queryWrapper);
        if (null == theUnProvedProcessDes) {
            log.error("流程状态回调时未找到流程配置{}", record);
            return;
        }

        //审核失败，填写原记录
        if (ProcessStatusEnum.Fail.getCode() == processStatus) {
            theUnProvedProcessDes.setReleaseType(processStatus);
            processDesDao.updateById(theUnProvedProcessDes);
        }
        if (ProcessStatusEnum.Success.getCode() == processStatus) {
            QueryWrapper<RobotProcessDesDo> provedQueryWrapper = new QueryWrapper<>();
            provedQueryWrapper.eq("process_id", processStatusReq.getProcessId());
            provedQueryWrapper.eq("release_type", ProcessStatusEnum.Success.getCode());
            RobotProcessDesDo theProvedProcessDes = processDesDao.selectOne(provedQueryWrapper);

            if (ObjectUtil.isNull(theProvedProcessDes)) {
                //审核通过的流程不存在，则复制一份设置为审核成功
                theUnProvedProcessDes.setReleaseType(processStatus);
                theUnProvedProcessDes.setId(null);
                processDesDao.insert(theUnProvedProcessDes);
            } else {
                //该流程已存在   执行覆盖以前审核通过的
                UpdateWrapper<RobotProcessDesDo> ProvedUpdateWrapper = new UpdateWrapper<RobotProcessDesDo>();
                ProvedUpdateWrapper.eq("id", theProvedProcessDes.getId());
                theUnProvedProcessDes.setId(null);
                theUnProvedProcessDes.setReleaseType(processStatus);
                processDesDao.update(theUnProvedProcessDes, ProvedUpdateWrapper);
            }
        }
        //更新流程状态修改时间
        Date currTime = DateUtil.date();
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", processStatusReq.getProcessId());
        RobotProcessSettingNodeDo robotProcessSettingNodeDo = getRobotProcessSettingNodeDo(theUnProvedProcessDes.getProcessId(), theUnProvedProcessDes.getCreator());
        robotProcessSettingNodeDo.setModifiedTime(currTime);
        robotProcessSettingNodeDo.setStatus(processStatus);
        robotProcessSettingNodeDao.update(robotProcessSettingNodeDo, wrapper);
        log.info("模板审核后，流程状态回填完成," + record);
        clearProcessInvalidTemplate(theUnProvedProcessDes.getProcessId(), theUnProvedProcessDes.getTemplateIds(), false);

    }

    /**
     * 预发布，点击发布按钮
     *
     * @param processTreeReq
     * @param processId
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PrepareForReleaseProcessResp prepareForReleaseProcess(RobotProcessTreeReq processTreeReq, long processId, BaseUser user) {
        if (redisService.hasKey(String.format(robot_template_audit_redis_key, processId))) {
            throw new BizException("流程预发布中，请稍候再试");
        }
        redisService.setCacheObject(String.format(robot_template_audit_redis_key, processId), processId, 3600L, TimeUnit.SECONDS);
        try {
            RobotProcessSettingNodeDo robotProcessSettingNodeDo = getRobotProcessSettingNodeDo(processId, user.getUserId());
            RobotProcessDesDo existProcessDes = getRobotProcessDesDo(processId);
            RobotSceneNodeDo robotSceneNodeDo = getRobotSceneNodeDo(robotProcessSettingNodeDo.getSceneId());

            //创建模板，并把模板id填充到流程内容里,也有可能是已经发布过，已经保存过的
            PrepareForReleaseProcessResp prepareForReleaseProcessResp = saveTemplate(robotProcessSettingNodeDo, processTreeReq, Constants.TEMPLATE_AUDIT_NOT, existProcessDes, robotSceneNodeDo);
            //该流程已存在   执行更新操作

            UpdateWrapper<RobotProcessDesDo> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", existProcessDes.getId());
            existProcessDes.setProcessDes(prepareForReleaseProcessResp.getProcessDes());
            existProcessDes.setTemplateIds(StrUtil.join(",", prepareForReleaseProcessResp.getTemplateIds()));
            processDesDao.update(existProcessDes, wrapper);
            robotProcessSettingNodeDao.updateById(robotProcessSettingNodeDo);
            //配置信息返回
            prepareForReleaseProcessResp.setProcessDes("");
            return prepareForReleaseProcessResp;
        } catch (BizExposeStatusException ex) {
            throw new BizExposeStatusException(ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            throw new BizException(ex.getMessage());
        } finally {
            if (redisService.deleteObject(String.format(robot_template_audit_redis_key, processId))) {
                log.info("删除流程预发布锁成功！processId:{}", processId);
            } else {
                log.info("删除流程预发布锁失败！processId:{}", processId);
            }
        }
    }

    RobotSceneNodeDo getRobotSceneNodeDo(Long sceneId) {
        RobotSceneNodeDo robotSceneNodeDo = robotSceneNodeDao.selectById(sceneId);
        if (null == robotSceneNodeDo) {
            throw new BizException("场景信息未找到");
        }
        return robotSceneNodeDo;
    }

    private RobotProcessSettingNodeDo getRobotProcessSettingNodeDo(Long processId, String userId) {
        RobotProcessSettingNodeDo robotProcessSettingNodeDo = robotProcessSettingNodeDao.selectOne(
                Wrappers.<RobotProcessSettingNodeDo>lambdaQuery()
                        .eq(RobotProcessSettingNodeDo::getDeleted, 0)
                        .eq(RobotProcessSettingNodeDo::getId, processId)
                        .eq(RobotProcessSettingNodeDo::getCreator, userId)
        );
        if (robotProcessSettingNodeDo == null) {
            //该流程处于删除状态  直接报异常
            throw new BizException(RobotErrorCode.RELEASE_PROCESSDES_NOT_EXIST);
        }
        if (!robotProcessSettingNodeDo.getCreator().equals(userId)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        return robotProcessSettingNodeDo;
    }

    RobotProcessDesDo getRobotProcessDesDo(Long processId) {
        QueryWrapper<RobotProcessDesDo> robotProcessDesDoQueryWrapper = new QueryWrapper<>();
        robotProcessDesDoQueryWrapper.eq("process_id", processId);
        robotProcessDesDoQueryWrapper.ne("release_type", ProcessStatusEnum.Success.getCode());
        RobotProcessDesDo existProcessDes = processDesDao.selectOne(robotProcessDesDoQueryWrapper);
        //还没有保存流程，不能发布
        if (ObjectUtil.isNull(existProcessDes)) {
            throw new BizException("流程信息找不到");
        }
        return existProcessDes;
    }

    /**
     * 流程图发布
     */
    public RobotProcessTemplateAuditResp releaseProcessDesById(long processId, String userId) {
        log.info("流程发布开始：{}", processId);
        if (redisService.hasKey(String.format(robot_template_audit_redis_key, processId))) {
            throw new BizException("流程发布中，请稍候再试");
        }
        redisService.setCacheObject(String.format(robot_template_audit_redis_key, processId), processId, 3600L, TimeUnit.SECONDS);
        RobotProcessSettingNodeDo robotProcessSettingNodeDo = getRobotProcessSettingNodeDo(processId, userId);
        RobotProcessDesDo existProcessDes = getRobotProcessDesDo(processId);
        RobotSceneNodeDo robotSceneNodeDo = getRobotSceneNodeDo(robotProcessSettingNodeDo.getSceneId());
        //发布模板
        AccountChatbotAccountQueryReq accountChatbotAccountQueryReq = new AccountChatbotAccountQueryReq();
        accountChatbotAccountQueryReq.setChatbotAccountList(Arrays.asList(robotSceneNodeDo.getAccounts().split(";")));
        accountChatbotAccountQueryReq.setCreator(existProcessDes.getCreator());
        List<AccountManagementResp> accountManagementResps = accountManagementApi.getListByChatbotAccounts(accountChatbotAccountQueryReq);
        if (ObjectUtil.isEmpty(accountManagementResps) || accountManagementResps.size() != robotSceneNodeDo.getAccounts().split(";").length) {
            throw new BizException("场景关联chatbot账号未找到");
        }
        List<String> robotSceneNodeOperatorStr = accountManagementResps.stream().map(AccountManagementResp::getAccountType).collect(Collectors.toList());
        try {
            RobotProcessTemplateAuditResp robotProcessTemplateAuditResp = publicTemplate(existProcessDes, robotSceneNodeOperatorStr.stream().collect(Collectors.joining(",")));
            //更新发布时间
            setProcessDesReleaseType(robotProcessTemplateAuditResp.getFailCount() == 0 ? ProcessStatusEnum.Appending.getCode() : ProcessStatusEnum.Fail.getCode(), existProcessDes);
            robotProcessSettingNodeDo.setAuditResult(JSONObject.toJSONString(robotProcessTemplateAuditResp.getFailList()));
            robotProcessSettingNodeDo.setAccounts(robotSceneNodeDo.getAccounts());
            setRobotProcessSettingNodeStatus(robotProcessSettingNodeDo, robotProcessTemplateAuditResp.getTotalCount() == 0 ? ProcessStatusEnum.Success.getCode() : robotProcessTemplateAuditResp.getFailCount() == 0 ? ProcessStatusEnum.Appending.getCode() : ProcessStatusEnum.Fail.getCode());
            robotProcessTemplateAuditResp.setCurrentTime(DateTime.now());
            log.info("流程发布结束");
            return robotProcessTemplateAuditResp;
        } catch (Exception ex) {
            throw new BizException(ex.getMessage());
        } finally {
            if (redisService.deleteObject(String.format(robot_template_audit_redis_key, processId))) {
                log.info("删除流程送审锁成功！processId:{}", processId);
            } else {
                log.info("删除流程送审锁失败！processId:{}", processId);
            }
        }

    }

    /**
     * 清理机器人流程中包含的无效模板
     *
     * @param emptyIdToClear 模板id为空时是否清除流程中包含的所有模板
     */
    void clearProcessInvalidTemplate(Long processId, String templateIds, boolean emptyIdToClear) {
        if (!emptyIdToClear && StrUtil.isEmpty(templateIds)) return;
        DeleteTemplateForInvalidOfProcessReq deleteTemplateForInvalidOfProcessReq = new DeleteTemplateForInvalidOfProcessReq();
        String[] templateIdsStrArr = templateIds.split(",");
        List<Long> templateIdList = StrUtil.isEmpty(templateIds) ? null : Arrays.stream(templateIdsStrArr).map(Long::parseLong).collect(Collectors.toList());
        deleteTemplateForInvalidOfProcessReq.setTemplateIds(templateIdList);
        deleteTemplateForInvalidOfProcessReq.setProcessId(processId);
        messageTemplateApi.deleteTemplateForInvalidOfProcess(deleteTemplateForInvalidOfProcessReq);
    }

    /**
     * 蜂动版本发布时，已有的所有流程重新审核
     */
    @Transactional(rollbackFor = Exception.class)
    public void oldProcessToAuditInit() {
        LambdaQueryWrapper<RobotProcessSettingNodeDo> robotProcessSettingNodeDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        robotProcessSettingNodeDoLambdaQueryWrapper.eq(RobotProcessSettingNodeDo::getDeleted, 0);
        robotProcessSettingNodeDoLambdaQueryWrapper.isNull(RobotProcessSettingNodeDo::getStatus);
        List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectList();
        log.info("需要初始化流程的数量：{}", robotProcessSettingNodeDos.size());
        robotProcessSettingNodeDos.forEach(item -> {
            log.info("初始化流程：{},场景id:{}", item.getProcessName(), item.getSceneId());
            LambdaQueryWrapper<RobotProcessDesDo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RobotProcessDesDo::getProcessId, item.getId());
            lambdaQueryWrapper.eq(RobotProcessDesDo::getReleaseType, 1);
            lambdaQueryWrapper.eq(RobotProcessDesDo::getDeleted, 0);
            RobotProcessDesDo robotProcessDesDo = processDesDao.selectOne(lambdaQueryWrapper);
            if (ObjectUtil.isNotEmpty(robotProcessDesDo)) {
                RobotProcessTreeReq robotProcessTreeReq = BeanUtil.copyProperties(robotProcessDesDo, RobotProcessTreeReq.class);
                BaseUser baseUser = new BaseUser();
                baseUser.setUserId(item.getCreator());
                prepareForReleaseProcess(robotProcessTreeReq, item.getId(), baseUser);
                releaseProcessDesById(item.getId(), item.getCreator());
                log.info("初始化流程：{},场景id:{},完成。", item.getProcessName(), item.getSceneId());
            } else {
                log.info("初始化流程：{},场景id:{},不需要初始化。", item.getProcessName(), item.getSceneId());
            }
        });

        log.info("初始化流程完成");
    }

    void setProcessDesReleaseType(int type, RobotProcessDesDo robotProcessDesDo) {
        UpdateWrapper<RobotProcessDesDo> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", robotProcessDesDo.getId());
        robotProcessDesDo.setReleaseType(type);
        processDesDao.update(robotProcessDesDo, wrapper);
    }

    void setRobotProcessSettingNodeStatus(RobotProcessSettingNodeDo robotProcessSettingNodeDo, int status) {
        Date currTime = DateUtil.date();
        UpdateWrapper robotProcessSettingWrapper = new UpdateWrapper();
        robotProcessSettingWrapper.eq("id", robotProcessSettingNodeDo.getId());
        robotProcessSettingNodeDo.setReleaseTime(currTime);
        robotProcessSettingNodeDo.setStatus(status);
        robotProcessSettingNodeDao.update(robotProcessSettingNodeDo, robotProcessSettingWrapper);

    }

    /**
     * 查询提问节点
     */
    @Override
    public RobotProcessAskNodeResp listAllAskNode() {
        RobotProcessAskNodeResp robotProcessAskNodeResp = new RobotProcessAskNodeResp();

        HashMap<Long, List<String>> askHashMap = new HashMap<Long, List<String>>();
        List<RobotProcessAskDo> robotProcessAskDos = robotProcessAskDao.selectList();
        for (RobotProcessAskDo robotProcessAskDo : robotProcessAskDos) {
            String nodeInfos = robotProcessAskDo.getNodeName();
            long processId = robotProcessAskDo.getProcessId();
            List<String> list = JSONObject.parseArray(nodeInfos, String.class);
            askHashMap.put(processId, list);
        }
        ArrayList<RobotAskNodeBean> robotAskNodeBeans = new ArrayList<>();
        Iterator<Long> iterator = askHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            RobotAskNodeBean robotAskNodeBean = new RobotAskNodeBean();
            Long key = iterator.next();//名称id
            robotAskNodeBean.setProcessId(key);
            List<String> nodeNameList = askHashMap.get(key);

            ArrayList<NodeBean> nodeBeans = new ArrayList<NodeBean>();
            for (int i = 0; i < nodeNameList.size(); i++) {
                String nodeInformation = nodeNameList.get(i);
                String[] split = nodeInformation.split("&");
                /**
                 * 2023-12-26
                 * 避免空数据造成异常
                 */
                if (split.length > 0) {
                    NodeBean nodeBean = new NodeBean();
                    nodeBean.setNodeId(split[0]);
                    nodeBean.setNodeName(split[1]);
                    nodeBeans.add(nodeBean);
                }
            }
            robotAskNodeBean.setNodes(nodeBeans);
            robotAskNodeBeans.add(robotAskNodeBean);
        }
        robotProcessAskNodeResp.setList(robotAskNodeBeans);
        return robotProcessAskNodeResp;
    }

    /**
     * 检查所有未删除流程占用的素材
     */
    public boolean checkUsedByAll(String fileUuid) {
        String creator = SessionContextUtil.getUser().getUserId();
        LambdaQueryWrapper<RobotProcessDesDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotProcessDesDo::getCreator, creator);
        wrapper.eq(RobotProcessDesDo::getDeleted, 0);//查询未删除的
        wrapper.select(RobotProcessDesDo::getProcessDes);
        List<String> processDes = processDesDao.selectList(wrapper).stream().map(RobotProcessDesDo::getProcessDes).collect(Collectors.toList());
        return processDes.stream().anyMatch(templateJson -> templateJson.contains(fileUuid));
    }

    public int deleteProcessDesc(HashMap<String, Object> map) {
        Long processId = (Long) map.get("id");
        DeleteTemplateForInvalidOfProcessReq deleteTemplateForInvalidOfProcessReq = new DeleteTemplateForInvalidOfProcessReq();
        deleteTemplateForInvalidOfProcessReq.setProcessId(processId);
        clearProcessInvalidTemplate(processId, "", true);
        return processDesDao.delRobotProcessDescProcessId(map);
    }

    @Override
    public List<String> checkUsed() {
        //获取json解析出所有的素材urlId
        LambdaQueryWrapper<RobotProcessDesDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotProcessDesDo::getReleaseType, 1);
        wrapper.eq(RobotProcessDesDo::getDeleted, 0);//查询未删除的
        List<String> jsons = processDesDao.selectList(wrapper).stream().map(RobotProcessDesDo::getProcessDes).collect(Collectors.toList());
        List<String> sourceIdStrList = jsons.stream().map(s -> {
            if (null != JSONObject.parseObject(s).getJSONArray("sourceIds")) {
                return StringUtils.strip(JSONObject.parseObject(s).getJSONArray("sourceIds").toString(), "[]");
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceIdStrList)) {
            sourceIdStrList.forEach(s -> {
                result.addAll(Arrays.asList(s.split(",")));
            });
        }
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        return result.stream().distinct().map(s -> s.replaceAll("\"", "")).collect(Collectors.toList());
    }

    @Override
    public RobotShortcutButtonResp getRobotShortcutButtonResp(String uuid) {
        QueryWrapper robotShortcutButtonWrapper = new QueryWrapper();
        robotShortcutButtonWrapper.eq("deleted", 0);
        robotShortcutButtonWrapper.eq("uuid", uuid);
        RobotShortcutButtonDo robotShortcutButtonDo = robotShortcutButtonDao.selectOne(robotShortcutButtonWrapper);
        if (robotShortcutButtonDo != null) {
            RobotShortcutButtonResp robotShortcutButtonResp = BeanUtil.copyProperties(robotShortcutButtonDo, RobotShortcutButtonResp.class);
            return robotShortcutButtonResp;
        }
        return null;
    }
}
