package com.citc.nce.im.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.contactgroup.ContactGroupApi;
import com.citc.nce.auth.contactgroup.vo.ContactGroupResp;
import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateDetailVo;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dataStatistics.MsgDataStatisticsApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.FileInfoReq;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.im.mapper.RobotGroupSendPlansDetailDao;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailResp;
import com.citc.nce.robot.vo.PlanSend;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetailPageParam;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.exception.GlobalErrorCode.USER_AUTH_ERROR;

@Service
@Slf4j
public class RobotGroupSendPlansDetailServiceImpl extends ServiceImpl<RobotGroupSendPlansDetailDao, RobotGroupSendPlansDetailDo> implements RobotGroupSendPlansDetailService {

    @Resource
    RobotGroupSendPlansDetailDao robotGroupSendPlansDetailDao;

    @Resource
    ContactGroupApi groupApi;

    @Resource
    MessageTemplateApi messageTemplateApi;

    @Resource
    MediaSmsTemplateApi mediaSmsTemplateApi;

    @Resource
    SmsTemplateApi smsTemplateApi;

    @Resource
    FileApi fileApi;

    @Resource
    MsgRecordApi msgRecordApi;

    @Resource
    RobotGroupSendPlansDao robotGroupSendPlansDao;

    @Value("${userId.superAdministrator}")
    private String superAdministrator;
    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    MsgDataStatisticsApi msgDataStatisticsApi;


    /**
     * 增加注释
     *
     * @param id 主键
     * @return
     */
    @Override
    public RobotGroupSendPlansDetail queryById(Long id) {
        RobotGroupSendPlansDetail robotGroupSendPlansDetail = new RobotGroupSendPlansDetail();
        RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo = robotGroupSendPlansDetailDao.selectById(id);
        if (null != robotGroupSendPlansDetailDo) {
            BeanUtils.copyProperties(robotGroupSendPlansDetailDo, robotGroupSendPlansDetail);
        }
        return robotGroupSendPlansDetail;
    }

    @Override
    public PageResult queryByPage(RobotGroupSendPlansDetailPageParam robotGroupSendPlansDetailPageParam) {
        QueryWrapper<RobotGroupSendPlansDetailDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id", robotGroupSendPlansDetailPageParam.getRobotGroupSendPlansDetail().getPlanId());
        queryWrapper.in("plan_status", 1, 2, 4);
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("send_time");
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministrator)) {
            queryWrapper.eq("creator", userId);
        }
        Integer pageNo = robotGroupSendPlansDetailPageParam.getPageNo();
        Integer pageSize = robotGroupSendPlansDetailPageParam.getPageSize();
        Page<RobotGroupSendPlansDetailDo> page = new Page<>(pageNo, pageSize);
        Page<RobotGroupSendPlansDetailDo> selectPage = robotGroupSendPlansDetailDao.selectPage(page, queryWrapper);
        PageResult<RobotGroupSendPlansDetailResp> pageResult = new PageResult<>();
        pageResult.setTotal(selectPage.getTotal());
        List<RobotGroupSendPlansDetailDo> records = selectPage.getRecords();
        List<Long> planDetailIds = records.stream().map(RobotGroupSendPlansDetailDo::getId).collect(Collectors.toList());
        List<MsgRecordVo> sendDetails = CollectionUtils.isEmpty(planDetailIds) ? new ArrayList<>() : msgRecordApi.selectSendDetailByPlanDetailIds(planDetailIds);
        Map<Long, Integer> messageTypeCollects = sendDetails.stream().filter(msg -> msg.getMessageType() != null).collect(Collectors.toMap(MsgRecordVo::getPlanDetailId, MsgRecordVo::getMessageType, (existing, replacement) -> existing));
        Map<Long, String> messageCollects = sendDetails.stream().filter(msg -> msg.getMessageContent() != null).collect(Collectors.toMap(MsgRecordVo::getPlanDetailId, MsgRecordVo::getMessageContent, (existing, replacement) -> existing));
        Map<Long, String> buttonCollects = sendDetails.stream().filter(msg -> msg.getButtonContent() != null).collect(Collectors.toMap(MsgRecordVo::getPlanDetailId, MsgRecordVo::getButtonContent, (existing, replacement) -> existing));
        List<RobotGroupSendPlansDetailResp> result = new ArrayList<>();
        records.forEach(robotGroupSendPlansDetailDo -> {
            RobotGroupSendPlansDetailResp resp = new RobotGroupSendPlansDetailResp();
            BeanUtils.copyProperties(robotGroupSendPlansDetailDo, resp);
            resp.setStatus(robotGroupSendPlansDetailDo.getPlanStatus());
            resp.setTemplateName(robotGroupSendPlansDetailDo.getMessage());
            resp.setMessage(Arrays.asList("短信", "视频短信").contains(robotGroupSendPlansDetailDo.getMsgType()) ? robotGroupSendPlansDetailDo.getDetailMsg() : messageCollects.get(robotGroupSendPlansDetailDo.getId()));
            resp.setMessageType(getMessageType(robotGroupSendPlansDetailDo.getDetailMsg(), messageTypeCollects.get(robotGroupSendPlansDetailDo.getId())));
            resp.setButtonContent(buttonCollects.get(robotGroupSendPlansDetailDo.getId()));
            result.add(resp);
        });
        pageResult.setList(result);
        return pageResult;
    }

    private Integer getMessageType(String message, Integer messageType) {
        if (messageType != null)
            return messageType;
        // 解析JSON字符串
        if (!StringUtils.isEmpty(message)) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                // 获取指定key的值
                return Integer.valueOf(jsonObject.getString("messageType"));
            } catch (JSONException e) {
                log.error(e.getMessage(), e);
            }
        }
        return messageType;
    }

    @Override
    public Long insert(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        Long planId = robotGroupSendPlansDetailReq.getPlanId();
        String userId = SessionContextUtil.getUser().getUserId();
        RobotGroupSendPlansDo robotGroupSendPlansDo = robotGroupSendPlansDao.selectById(planId);
        if (robotGroupSendPlansDo != null) {
            if (userId.equals(robotGroupSendPlansDo.getCreator())) {
                this.checkTemplate(robotGroupSendPlansDetailReq, robotGroupSendPlansDo);
                RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo = new RobotGroupSendPlansDetailDo();
                robotGroupSendPlansDetailReq.setIsEdit(true);
                BeanUtils.copyProperties(robotGroupSendPlansDetailReq, robotGroupSendPlansDetailDo);
                buildMsg(robotGroupSendPlansDetailReq, robotGroupSendPlansDetailDo);
                saveSendTime(robotGroupSendPlansDetailReq, robotGroupSendPlansDetailDo);
                robotGroupSendPlansDetailDao.insert(robotGroupSendPlansDetailDo);
                return robotGroupSendPlansDetailDo.getId();
            } else {
                throw new BizException(USER_AUTH_ERROR);
            }
        } else {
            throw new BizException("计划不存在:" + planId);
        }
    }

    public void checkTemplate(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq, RobotGroupSendPlansDo robotGroupSendPlansDo) {
        if (robotGroupSendPlansDetailReq.getTemplateId() != null) {
            if (StrUtil.isEmpty(robotGroupSendPlansDo.getPlanAccount())) {
                throw new BizException("群发计划没有绑定发送账号");
            }
            ;
            MessageTemplateProvedReq messageTemplateProvedReq = new MessageTemplateProvedReq();
            messageTemplateProvedReq.setAccountType(robotGroupSendPlansDo.getPlanAccount());
            messageTemplateProvedReq.setTemplateId(robotGroupSendPlansDetailReq.getTemplateId());
            MessageTemplateResp template = messageTemplateApi.getProvedTemplate(messageTemplateProvedReq);
            if (ObjectUtil.isEmpty(template)) {
                throw new BizException(500, "模板不存在");
            }
            if (template.getDeleteTime() != null) {
                throw new BizException(500, "模本已被删除");
            }
        }
        if (robotGroupSendPlansDetailReq.getMediaTemplateId() != null) {
            MediaSmsTemplateDetailVo templateInfo = mediaSmsTemplateApi.getTemplateInfo(robotGroupSendPlansDetailReq.getMediaTemplateId());
            if (ObjectUtil.isEmpty(templateInfo)) {
                throw new BizException(500, "模板不存在");
            }
            if (templateInfo.getDeletedTime() != null) {
                throw new BizException(500, "模本已被删除");
            }
        }
        if (robotGroupSendPlansDetailReq.getShortMsgTemplateId() != null) {
            SmsTemplateDetailVo smsTemplateDetailVo = smsTemplateApi.getTemplateInfo(robotGroupSendPlansDetailReq.getShortMsgTemplateId(), false);
            if (ObjectUtil.isEmpty(smsTemplateDetailVo)) {
                throw new BizException(500, "模板不存在");
            }
            if (smsTemplateDetailVo.getDeletedTime() != null) {
                throw new BizException(500, "模本已被删除");
            }
        }
    }

    private void buildMsg(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq, RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo) {
        if (ObjectUtil.isNotEmpty(robotGroupSendPlansDetailReq.getTemplateId())) {
            MessageTemplateResp template = messageTemplateApi.getMessageTemplateById(robotGroupSendPlansDetailReq.getTemplateId());
            if (ObjectUtil.isEmpty(template)) {
                throw new BizException(SendGroupExp.NODE_ERROR);
            }
            robotGroupSendPlansDetailDo.setMessage(template.getTemplateName());
            try {
                robotGroupSendPlansDetailDo.setDetailMsg(objectMapper.writeValueAsString(template));
            } catch (Exception ignore) {
            }
            robotGroupSendPlansDetailDo.setMsgType("5g消息-" + getMessageType(template.getMessageType()));
        } else if (ObjectUtil.isNotEmpty(robotGroupSendPlansDetailReq.getShortMsgTemplateId())) {
            SmsTemplateDetailVo template = smsTemplateApi.getTemplateInfoInner(robotGroupSendPlansDetailReq.getShortMsgTemplateId(), false);
            if (ObjectUtil.isEmpty(template)) {
                throw new BizException(SendGroupExp.NODE_ERROR);
            }
            robotGroupSendPlansDetailDo.setMsgType("短信");
            robotGroupSendPlansDetailDo.setMessage(template.getTemplateName());
            try {
                robotGroupSendPlansDetailDo.setDetailMsg(objectMapper.writeValueAsString(template));
            } catch (Exception ignore) {
            }
        } else if (ObjectUtil.isNotEmpty(robotGroupSendPlansDetailReq.getMediaTemplateId())) {
            MediaSmsTemplatePreviewVo templateInfo = mediaSmsTemplateApi.getContents(robotGroupSendPlansDetailReq.getMediaTemplateId(), true);
            if (ObjectUtil.isEmpty(templateInfo)) {
                throw new BizException(SendGroupExp.NODE_ERROR);
            }
            robotGroupSendPlansDetailDo.setMsgType("视频短信");
            robotGroupSendPlansDetailDo.setMessage(templateInfo.getTemplateName());
            try {
                robotGroupSendPlansDetailDo.setDetailMsg(objectMapper.writeValueAsString(templateInfo));
            } catch (Exception ignore) {
            }
        }
        String condition = "";
        if (StringUtils.isNotEmpty(robotGroupSendPlansDetailReq.getCondition())) {
            condition = robotGroupSendPlansDetailReq.getCondition();
        }
        String btnUuid = "";
        if (StringUtils.isNotEmpty(robotGroupSendPlansDetailReq.getBtnUuid())) {
            btnUuid = robotGroupSendPlansDetailReq.getBtnUuid();
        }
        if (ObjectUtil.isNotEmpty(robotGroupSendPlansDetailDo.getPlanGroup())) {
            ContactGroupResp contactGroupResp = groupApi.getContactGroupById(robotGroupSendPlansDetailDo.getPlanGroup());
            String groupName = contactGroupResp.getGroupName();
            robotGroupSendPlansDetailDo.setGroupName(groupName);
        }
        if (StringUtils.isNotEmpty(robotGroupSendPlansDetailReq.getTemplateFileUuid())) {
            FileInfoReq fileInfoReq = new FileInfoReq();
            fileInfoReq.setUuid(robotGroupSendPlansDetailReq.getTemplateFileUuid());
            String fileName = fileApi.getFileInfo(fileInfoReq).getFileName();
            String groupName = fileName.substring(0, fileName.lastIndexOf("."));
            robotGroupSendPlansDetailDo.setGroupName(groupName);
        }
        if (condition.equals("click") && StringUtils.isNotEmpty(btnUuid) && !StringUtils.equals("any", btnUuid)) {
            //根据按钮分类
            robotGroupSendPlansDetailDo.setGroupName("点击" + robotGroupSendPlansDetailReq.getBtnName());
        } else if (condition.equals("unread")) {
            //消息未读
            robotGroupSendPlansDetailDo.setGroupName("消息未读");
        } else if (condition.equals("notClick")) {
            //未点击按钮
            robotGroupSendPlansDetailDo.setGroupName("未点击按钮");
        } else if (condition.equals("click") && StringUtils.equals("any", btnUuid)) {
            //点击任意按钮
            robotGroupSendPlansDetailDo.setGroupName("点击任意按钮");
        } else if (condition.equals("failed")) {
            //失败
            robotGroupSendPlansDetailDo.setGroupName("发送失败");
        } else if (condition.equals("displayed")) {
            //消息已阅
            robotGroupSendPlansDetailDo.setGroupName("消息已阅");
        } else if (condition.equals("delivered")) {
            //成功
            robotGroupSendPlansDetailDo.setGroupName("发送成功");
        }
    }


    private String getMessageType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "文本");
        map.put(2, "图片");
        map.put(3, "视频");
        map.put(4, "音频");
        map.put(5, "文件");
        map.put(6, "单卡");
        map.put(7, "多卡");
        map.put(8, "位置");
        return map.get(type);
    }

    private void saveSendTime(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq, RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo) {
        if (StringUtils.isNotEmpty(robotGroupSendPlansDetailReq.getSendTime())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date sendTime = sdf.parse(robotGroupSendPlansDetailReq.getSendTime());
                robotGroupSendPlansDetailDo.setSendTime(sendTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateNodeDetail(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
//        log.info("更新计划发送详情信息: planId:{} 发送数量:{} 成功数量:{} 失败数量:{} 未知数量:{}",robotGroupSendPlansDetailReq.getPlanId(),robotGroupSendPlansDetailReq.getSendAmount(),robotGroupSendPlansDetailReq.getSuccessAmount(),robotGroupSendPlansDetailReq.getFailAmount(),robotGroupSendPlansDetailReq.getUnknowAmount());
        this.processingData(robotGroupSendPlansDetailReq);
    }

    @Override
    public void updateDate(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        Long planId = robotGroupSendPlansDetailReq.getPlanId();
        String userId = SessionContextUtil.getUser().getUserId();
        RobotGroupSendPlansDo robotGroupSendPlansDo = robotGroupSendPlansDao.selectById(planId);
        if (robotGroupSendPlansDo != null) {
            if (userId.equals(robotGroupSendPlansDo.getCreator())) {
                this.checkTemplate(robotGroupSendPlansDetailReq, robotGroupSendPlansDo);
                this.processingData(robotGroupSendPlansDetailReq);
            } else {
                throw new BizException(USER_AUTH_ERROR);
            }
        } else {
            throw new BizException("计划不存在:" + planId);
        }
    }

    public void processingData(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo = new RobotGroupSendPlansDetailDo();
        BeanUtils.copyProperties(robotGroupSendPlansDetailReq, robotGroupSendPlansDetailDo);
        buildMsg(robotGroupSendPlansDetailReq, robotGroupSendPlansDetailDo);
        saveSendTime(robotGroupSendPlansDetailReq, robotGroupSendPlansDetailDo);
        if (ObjectUtil.isNotEmpty(robotGroupSendPlansDetailReq.getIsEdit())) {//是否编辑
            List<Integer> status = Arrays.asList(1, 2, 4);//0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭，6已过期，7无状态
            //如果节点状态不是执行中、执行完毕、执行失败，就修改状态到无状态
            if (!status.contains(queryById(robotGroupSendPlansDetailReq.getId()).getPlanStatus())) {
                robotGroupSendPlansDetailDo.setPlanStatus(SendStatus.NO_STATUS.getCode());
            }
        }
        log.info("修改群发计划中的发送数据:" + robotGroupSendPlansDetailDo);
        robotGroupSendPlansDetailDao.updateById(robotGroupSendPlansDetailDo);
    }

    @Override
    public void deleteById(Long id) {
        RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo = new RobotGroupSendPlansDetailDo();
        robotGroupSendPlansDetailDo.setId(id);
        robotGroupSendPlansDetailDao.updateById(robotGroupSendPlansDetailDo);
    }

    @Override
    public void updateAmount(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        String messageId = robotGroupSendPlansDetailReq.getMessage();
        QueryWrapper<RobotGroupSendPlansDetailDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("message", messageId);
        RobotGroupSendPlansDetailDo detailDo = robotGroupSendPlansDetailDao.selectOne(queryWrapper);
        if (robotGroupSendPlansDetailReq.getFailAmount() != 0) {
            detailDo.setFailAmount(detailDo.getFailAmount() + 1);
        } else if (robotGroupSendPlansDetailReq.getSuccessAmount() != 0) {
            detailDo.setSuccessAmount(detailDo.getFailAmount() + 1);
        }
        robotGroupSendPlansDetailDao.updateById(detailDo);
    }

    @Override
    public List<RobotGroupSendPlansDetail> selectAllByPlanId(RobotGroupSendPlansDetailReq req) {
        LambdaQueryWrapper<RobotGroupSendPlansDetailDo> queryWrapper = new LambdaQueryWrapper<>();
//        if (ObjectUtil.isNotEmpty(req.getPlanStatus()) && req.getPlanStatus() == -1) {
//            queryWrapper.in(RobotGroupSendPlansDetailDo::getPlanStatus, 1, 2);
//            queryWrapper.ne(RobotGroupSendPlansDetailDo::getTemplateId, "");
//        }
        queryWrapper.eq(RobotGroupSendPlansDetailDo::getPlanId, req.getPlanId());
        queryWrapper.orderByDesc(RobotGroupSendPlansDetailDo::getCreateTime);
        List<RobotGroupSendPlansDetail> robotGroupSendPlansDetails = new ArrayList<>();
        List<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDos = robotGroupSendPlansDetailDao.selectList(queryWrapper);

        if(req.getPlanStatus() != null && req.getPlanStatus() == -1){
            //查询所有计划是否有发送记录，如果没有就删除改计划
            Map<Long,Long> planSendList = msgDataStatisticsApi.queryIsSendPlanId();
            for (RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo : robotGroupSendPlansDetailDos) {
                if(planSendList.containsKey(robotGroupSendPlansDetailDo.getId())) {
                    RobotGroupSendPlansDetail robotGroupSendPlansDetail = new RobotGroupSendPlansDetail();
                    BeanUtil.copyProperties(robotGroupSendPlansDetailDo, robotGroupSendPlansDetail);
                    robotGroupSendPlansDetails.add(robotGroupSendPlansDetail);
                }
            }
        }else{
            for (RobotGroupSendPlansDetailDo robotGroupSendPlansDetailDo : robotGroupSendPlansDetailDos) {
                RobotGroupSendPlansDetail robotGroupSendPlansDetail = new RobotGroupSendPlansDetail();
                BeanUtil.copyProperties(robotGroupSendPlansDetailDo, robotGroupSendPlansDetail);
                robotGroupSendPlansDetails.add(robotGroupSendPlansDetail);
            }
        }
        return robotGroupSendPlansDetails;
    }

    @Override
    public List<Long> updateExpiredData(List<Long> ids) {
        UpdateWrapper<RobotGroupSendPlansDetailDo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        RobotGroupSendPlansDetailDo detailDo = new RobotGroupSendPlansDetailDo();
        detailDo.setPlanStatus(6);
        for (Long id : ids) {
            detailDo.setId(id);
            robotGroupSendPlansDetailDao.updateById(detailDo);
        }
        LambdaQueryWrapper<RobotGroupSendPlansDetailDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RobotGroupSendPlansDetailDo::getId, ids);
        return robotGroupSendPlansDetailDao.selectList(wrapper).stream().map(RobotGroupSendPlansDetailDo::getPlanId).collect(Collectors.toList());
    }

    @Override
    public List<Long> selectAllSchedule() {
        LambdaQueryWrapper<RobotGroupSendPlansDetailDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RobotGroupSendPlansDetailDo::getPlanStatus, 0);
        String nowDateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        queryWrapper.apply("UNIX_TIMESTAMP(send_time) < UNIX_TIMESTAMP('" + nowDateStr + "')");
        List<RobotGroupSendPlansDetailDo> dos = robotGroupSendPlansDetailDao.selectList(queryWrapper);
        return dos.stream().map(RobotGroupSendPlansDetailDo::getId).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> sumSendAmountByPlanId() {
        Map<Long, Long> sendMap = new HashMap<>();
        List<PlanSend> planSends = robotGroupSendPlansDetailDao.queryPlanSend();
        for (PlanSend planSend : planSends) {
            sendMap.put(planSend.getPlanId(), planSend.getCount());
        }

        return sendMap;
    }


}
