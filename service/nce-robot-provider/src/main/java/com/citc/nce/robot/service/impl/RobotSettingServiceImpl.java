package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.auth.messagetemplate.vo.shortcutbutton.ButtonDetailInfo;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.service.ModuleService;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.common.ResponsePriority;
import com.citc.nce.robot.dao.RobotSettingDao;
import com.citc.nce.robot.entity.RobotProcessButtonDo;
import com.citc.nce.robot.entity.RobotSettingDo;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotProcessButtonService;
import com.citc.nce.robot.service.RobotSettingService;
import com.citc.nce.robot.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 17:12
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class RobotSettingServiceImpl implements RobotSettingService {
    @Resource
    private RobotSettingDao robotSettingDao;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RobotProcessButtonService robotProcessButtonService;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ModuleService moduleService;
    @Resource
    private RobotLastReplyTemplateService robotLastReplyTemplateService;

    @Override
    @Transactional
    public int saveRebotSettingReq(RebotSettingReq rebotSettingReq) {
        if (0 == rebotSettingReq.getReplyType() && StringUtils.isEmpty(rebotSettingReq.getLastReply())) {
            throw BizException.build(RobotErrorCode.POST_REQUEST);
        }
        try {
            String userId = SessionContextUtil.getUser().getUserId();
            //删除原有的机器人设置
            HashMap<String, Object> map = new HashMap<>();
            map.put("deleted", 1);
            map.put("deleteTime", DateUtil.date());
            map.put("creator", userId);
            robotSettingDao.deleteRebotSettingReq(map);
            //保存新的机器人设置
            RobotSettingDo robotSetting = new RobotSettingDo();
            robotSetting.setCreator(userId);
            robotSetting.setDeleted(0);
            robotSetting.setCreateTime(DateUtil.date());
            robotSetting.setUpdateTime(DateUtil.date());
            robotSetting.setReplySwitch(rebotSettingReq.getReplySwitch());
            robotSetting.setLastReply(rebotSettingReq.getLastReply());
            robotSetting.setLastReplyType(rebotSettingReq.getLastReplyType());
            robotSetting.setReplyType(rebotSettingReq.getReplyType());
            robotSetting.setModule(rebotSettingReq.getModule());
            robotSetting.setReplyMethod(rebotSettingReq.getReplyMethod());
            robotSetting.setApiKey(rebotSettingReq.getApiKey());
            robotSetting.setSecretKey(rebotSettingReq.getSecretKey());
            robotSetting.setServiceAddress(rebotSettingReq.getServiceAddress());
            robotSetting.setResponsePriorities(CollectionUtils.isEmpty(rebotSettingReq.getResponsePriorities()) ? "CHATBOT,KEYWORDS,DEFAULT" : rebotSettingReq.getResponsePriorities().stream().map(ResponsePriority::name).collect(Collectors.joining(",")));
            robotSetting.setGlobalType(rebotSettingReq.getGlobalType());
            robotSetting.setWaitTime(rebotSettingReq.getWaitTime());
            // 如果是大型回复，给默认回复设定一个默认值
            if (1 == robotSetting.getReplyType()) {
                robotSetting.setTemplateId("");
            } else {
                //兜底模板送审
                if (StringUtils.isNotEmpty(robotSetting.getLastReply())) {
                    List<String> templateIds = robotLastReplyTemplateService.auditLastReply(rebotSettingReq);
                    robotSetting.setLastReply(rebotSettingReq.getLastReply());
                    robotSetting.setTemplateId(String.join(",", templateIds));
                }
            }
            int insert = robotSettingDao.insert(robotSetting);
            // 发生变化，删除大型回复的token
            stringRedisTemplate.delete("robot:baiduWenxinToken:" + userId);

            Long id = robotSetting.getId();
            List<RobotProcessButtonReq> buttonList = rebotSettingReq.getButtonList();
            robotProcessButtonService.deleteRobotProcessButtonDoByNodeId(id, userId);
            if (CollectionUtils.isNotEmpty(buttonList)) {
                List<RobotProcessButtonDo> robotProcessButtonDos = new ArrayList<>(buttonList.size());
                List<Integer> moduleTypeList = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
                for (RobotProcessButtonReq robotProcessButtonReq : buttonList) {
                    RobotProcessButtonDo entity = new RobotProcessButtonDo();
                    BeanUtil.copyProperties(robotProcessButtonReq, entity);
                    entity.setNodeId(id);
                    entity.setOptionList(JSON.toJSONString(robotProcessButtonReq.getOption()));
                    robotProcessButtonDos.add(entity);
                    //保存组件和按钮的映射关系
                    if (moduleTypeList.contains(robotProcessButtonReq.getButtonType())) {
                        try {
                            ButtonDetailInfo buttonDetailInfo = objectMapper.readValue(robotProcessButtonReq.getButtonDetail(), ButtonDetailInfo.class);
                            String businessId = buttonDetailInfo.getBusinessId();
                            boolean result = moduleService.saveModuleButUuidRelation(robotProcessButtonReq.getButtonType(), businessId, robotProcessButtonReq.getUuid());
                            if (!result) {
                                throw new BizException("组件按钮保存异常！");
                            }
                        } catch (JsonProcessingException e) {
                            throw new BizException(e.getMessage());
                        }
                    }
                }
                robotProcessButtonService.saveRobotProcessButtonDoList(robotProcessButtonDos);
            }
            return insert;
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 新增chatbot时，兜底回复为他送审
     *
     * @param chatbotAccount
     */
    public void auditLastReplyForChatbot(String chatbotAccount) {
        log.info("新增chatbot时，兜底送审,chatbotAccount:{},userId:", chatbotAccount, SessionContextUtil.getUser().getUserId());
        RebotSettingQueryReq rebotSettingQueryReq = new RebotSettingQueryReq();
        rebotSettingQueryReq.setCreate(SessionContextUtil.getUser().getUserId());
        RebotSettingResp rebotSettingResp = getRebotSettingReq(rebotSettingQueryReq);
        //大模型，或者兜底回复内容为空
        if (0 == rebotSettingResp.getReplyType() || StringUtils.isEmpty(rebotSettingResp.getLastReply())) {
            return;
        }
        try {
            RebotSettingReq req = BeanUtil.copyProperties(rebotSettingResp, RebotSettingReq.class);
            robotLastReplyTemplateService.addAuditLastReplyForChatbot(req, chatbotAccount);
        } catch (Exception ex) {
            log.error("创建chatbot时，兜底回复送审出错，", ex);
        }
    }


    @Override
    public RebotSettingResp getRebotSettingReq(RebotSettingQueryReq rebotSettingQueryReq) {
        RebotSettingResp rebotSettingResp = new RebotSettingResp();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isBlank(rebotSettingQueryReq.getCreate())) {
            /**
             * 2023-12-26
             * 如果获取不到用户，则返回一个默认设置
             */
            String userId = null;
            try {
                userId = SessionContextUtil.getUser().getUserId();
            } catch (Exception e) {
                rebotSettingResp.setWaitTime(5);
                rebotSettingResp.setLastReply("[]");
                rebotSettingResp.setLastReplyType(0);
                rebotSettingResp.setGlobalType(0);
                rebotSettingResp.setReplySwitch(0);
                rebotSettingResp.setReplyType(0);
                rebotSettingResp.setResponsePriorities(ResponsePriority.resolve("default"));
                return rebotSettingResp;
            }
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                wrapper.eq("creator", userId);
            }
        } else {
            wrapper.eq("creator", rebotSettingQueryReq.getCreate());
        }
        wrapper.orderByDesc("update_time");

        List<RobotSettingDo> robotSettingDos = robotSettingDao.selectList(wrapper);

        if (robotSettingDos != null && robotSettingDos.size() > 0) {
            Long id = robotSettingDos.get(0).getId();
            List<RobotProcessButtonDo> list = robotProcessButtonService.getRobotProcessButtonList(id);
            if (CollectionUtils.isNotEmpty(list)) {
                List<RobotProcessButtonResp> robotProcessButtonResps = new ArrayList<>();
                for (RobotProcessButtonDo button : list) {
                    RobotProcessButtonResp entity = new RobotProcessButtonResp();
                    BeanUtil.copyProperties(button, entity);
                    entity.setOption(JSON.parseArray(button.getOptionList(), Integer.class));
                    robotProcessButtonResps.add(entity);
                }
                rebotSettingResp.setButtonList(robotProcessButtonResps);
            }
            RobotSettingDo robotSettingDo = robotSettingDos.get(0);
            rebotSettingResp.setId(robotSettingDo.getId());
            rebotSettingResp.setWaitTime(robotSettingDo.getWaitTime());
            rebotSettingResp.setLastReplyType(robotSettingDo.getLastReplyType());
            rebotSettingResp.setLastReply(robotSettingDo.getLastReply());
            rebotSettingResp.setTemplateId(robotSettingDo.getTemplateId());
            rebotSettingResp.setGlobalType(robotSettingDo.getGlobalType());
            rebotSettingResp.setCreator(robotSettingDo.getCreator());
            rebotSettingResp.setCreateTime(robotSettingDo.getCreateTime());
            rebotSettingResp.setReplySwitch(robotSettingDo.getReplySwitch());
            rebotSettingResp.setReplyType(robotSettingDo.getReplyType());
            rebotSettingResp.setModule(robotSettingDo.getModule());
            rebotSettingResp.setReplyMethod(robotSettingDo.getReplyMethod());
            rebotSettingResp.setApiKey(robotSettingDo.getApiKey());
            rebotSettingResp.setSecretKey(robotSettingDo.getSecretKey());
            rebotSettingResp.setServiceAddress(robotSettingDo.getServiceAddress());
            rebotSettingResp.setResponsePriorities(ResponsePriority.resolve(robotSettingDos.get(0).getResponsePriorities()));
        } else {
            /**
             * chatbot缺陷反馈修复 机器人设置
             * 2023/1/10 17:22
             * 机器人设置中，第一次进入时，机器人等待时间默认为5分钟，默认回复为空
             */
            rebotSettingResp.setWaitTime(5);
            rebotSettingResp.setLastReply("[]");
            rebotSettingResp.setLastReplyType(0);
            rebotSettingResp.setGlobalType(0);
            rebotSettingResp.setReplySwitch(0);
            rebotSettingResp.setReplyType(0);
            rebotSettingResp.setResponsePriorities(ResponsePriority.resolve("default"));
        }
        return rebotSettingResp;
    }

    @Override
    public RobotProcessButtonResp getButtonByUuid(String uuid) {
        return robotProcessButtonService.getButtonByUuid(uuid);
    }

    @Override
    public RobotBaseSettingVo getRobotBaseSetting() {
        LambdaQueryWrapper<RobotSettingDo> queryWrapper = new LambdaQueryWrapper<RobotSettingDo>()
                .eq(BaseDo::getCreator, SessionContextUtil.getUserId());
        RobotSettingDo robotSettingDo = robotSettingDao.selectOne(queryWrapper);
        if (null == robotSettingDo)
            return RobotBaseSettingVo.DEFAULT_SETTING;
        RobotBaseSettingVo baseConfig = new RobotBaseSettingVo();
        baseConfig.setId(robotSettingDo.getId());
        baseConfig.setTimeout(robotSettingDo.getWaitTime());
        baseConfig.setGlobalButtonShowStrategy(robotSettingDo.getGlobalType());
        baseConfig.setResponsePriorities(ResponsePriority.resolve(robotSettingDo.getResponsePriorities()));
        List<RobotProcessButtonDo> buttonDoList = robotProcessButtonService.getRobotProcessButtonList(robotSettingDo.getId());
        List<RobotProcessButtonResp> globalButtons = BeanUtil.copyToList(buttonDoList, RobotProcessButtonResp.class);
        baseConfig.setGlobalButtonList(globalButtons);
        return baseConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configBaseSetting(RobotBaseSettingVo baseConfig) {
        String userId = SessionContextUtil.getUserId();
        LambdaUpdateWrapper<RobotSettingDo> updateWrapper = new LambdaUpdateWrapper<RobotSettingDo>()
                .eq(BaseDo::getId, baseConfig.getId())
                .eq(BaseDo::getCreator, userId)
                .set(Objects.nonNull(baseConfig.getTimeout()), RobotSettingDo::getWaitTime, baseConfig.getTimeout())
                .set(Objects.nonNull(baseConfig.getGlobalButtonShowStrategy()), RobotSettingDo::getGlobalType, baseConfig.getGlobalButtonShowStrategy())
                .set(Objects.nonNull(baseConfig.getResponsePriorities()), RobotSettingDo::getPriority, baseConfig.getResponsePriorities().stream().map(Objects::toString).collect(Collectors.joining(",")));
        int updated = robotSettingDao.update(new RobotSettingDo(), updateWrapper);
        if (0 == updated)
            throw new BizException("保存失败");
        robotProcessButtonService.deleteRobotProcessButtonDoByNodeId(baseConfig.getId(), userId);
        List<RobotProcessButtonResp> globalButtonList = baseConfig.getGlobalButtonList();
        if (CollectionUtils.isNotEmpty(globalButtonList)) {
            List<RobotProcessButtonDo> robotProcessButtonDos = new ArrayList<>(globalButtonList.size());
            List<Integer> moduleTypeList = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
            for (RobotProcessButtonResp button : globalButtonList) {
                RobotProcessButtonDo buttonDo = new RobotProcessButtonDo();
                BeanUtil.copyProperties(button, buttonDo);
                buttonDo.setNodeId(baseConfig.getId());
                buttonDo.setOptionList(JSON.toJSONString(button.getOption()));
                robotProcessButtonDos.add(buttonDo);
                //保存组件和按钮的映射关系
                int moduleType = Integer.parseInt(button.getButtonType());
                if (moduleTypeList.contains(moduleType)) {
                    try {
                        ButtonDetailInfo buttonDetailInfo = objectMapper.readValue(button.getButtonDetail(), ButtonDetailInfo.class);
                        String businessId = buttonDetailInfo.getBusinessId();
                        boolean result = moduleService.saveModuleButUuidRelation(moduleType, businessId, button.getUuid());
                        if (!result) {
                            throw new BizException("组件按钮保存异常！");
                        }
                    } catch (JsonProcessingException e) {
                        throw new BizException(e.getMessage());
                    }
                }
            }
            robotProcessButtonService.saveRobotProcessButtonDoList(robotProcessButtonDos);
        }
    }

    @Override
    public RobotDefaultReplySettingVo getDefaultReplyConfig() {
        LambdaQueryWrapper<RobotSettingDo> queryWrapper = new LambdaQueryWrapper<RobotSettingDo>()
                .eq(BaseDo::getCreator, SessionContextUtil.getUserId());
        RobotSettingDo robotSettingDo = robotSettingDao.selectOne(queryWrapper);
        if (robotSettingDo == null)
            return RobotDefaultReplySettingVo.DEFAULT_REPLY_SETTING;
        RobotDefaultReplySettingVo defaultReplySettingVo = new RobotDefaultReplySettingVo();
        defaultReplySettingVo.setId(robotSettingDo.getId())
                .setReplySwitch(robotSettingDo.getReplySwitch() == 1)
                .setReplyType(robotSettingDo.getReplyType())
                .setModule(robotSettingDo.getModule())
                .setReplyMethod(robotSettingDo.getReplyMethod())
                .setApiKey(robotSettingDo.getApiKey())
                .setSecretKey(robotSettingDo.getSecretKey())
                .setServiceAddress(robotSettingDo.getServiceAddress());
        return defaultReplySettingVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configDefaultReply(RobotDefaultReplySettingVo settingVo) {
        String userId = SessionContextUtil.getUserId();
        if (0 == settingVo.getReplyType() && StringUtils.isEmpty(settingVo.getLastReply())) {
            throw BizException.build(RobotErrorCode.POST_REQUEST);
        }
        String templateId = "";
        if (0 == settingVo.getReplyType() && StringUtils.isEmpty(settingVo.getLastReply())) {
            //兜底模板送审
            List<RobotProcessButtonDo> buttonDoList = robotProcessButtonService.getRobotProcessButtonList(settingVo.getId());
            List<RobotProcessButtonReq> globalButtons = BeanUtil.copyToList(buttonDoList, RobotProcessButtonReq.class);
            List<String> templateIds = robotLastReplyTemplateService.auditLastReply(settingVo, globalButtons);
            templateId = String.join(",", templateIds);
        }
        LambdaUpdateWrapper<RobotSettingDo> updateWrapper = new LambdaUpdateWrapper<RobotSettingDo>()
                .eq(BaseDo::getId, settingVo.getId())
                .eq(BaseDo::getCreator, userId)
                .set(Objects.nonNull(settingVo.getReplySwitch()), RobotSettingDo::getReplySwitch, settingVo.getReplySwitch() ? 1 : 0)
                .set(Objects.nonNull(settingVo.getLastReply()), RobotSettingDo::getLastReply, settingVo.getLastReply())
                .set(Objects.nonNull(settingVo.getReplyType()), RobotSettingDo::getReplyType, settingVo.getReplyType())
                .set(Objects.nonNull(settingVo.getModule()), RobotSettingDo::getModule, settingVo.getModule())
                .set(Objects.nonNull(settingVo.getReplyMethod()), RobotSettingDo::getReplyMethod, settingVo.getReplyMethod())
                .set(Objects.nonNull(settingVo.getApiKey()), RobotSettingDo::getApiKey, settingVo.getApiKey())
                .set(Objects.nonNull(settingVo.getSecretKey()), RobotSettingDo::getSecretKey, settingVo.getSecretKey())
                .set(RobotSettingDo::getTemplateId, templateId)
                .set(Objects.nonNull(settingVo.getServiceAddress()), RobotSettingDo::getServiceAddress, settingVo.getServiceAddress());
        int updated = robotSettingDao.update(new RobotSettingDo(), updateWrapper);
        if (0 == updated)
            throw new BizException("保存失败");
        stringRedisTemplate.delete("robot:baiduWenxinToken:" + userId);
    }


}
