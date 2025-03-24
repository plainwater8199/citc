package com.citc.nce.tenant.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.tenant.robot.dao.*;
import com.citc.nce.tenant.robot.entity.*;
import com.citc.nce.tenant.service.CreateTenantTableService;
import com.citc.nce.tenant.vo.req.CreateTableReq;
import com.citc.nce.authcenter.userDataSyn.vo.TenantUserUpdateInfo;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class CreateTenantTableServiceImpl implements CreateTenantTableService {


    @Resource
    private TemporaryStatistics1Dao temporaryStatistics1Dao;

    @Resource
    private CspOrderDeduct1Dao cspOrderDeduct1Dao;
    @Resource
    private CspOrderRecharge1Dao cspOrderRecharge1Dao;
    @Resource
    private CspSmsOrderDeduct1Dao cspSmsOrderDeduct1Dao;
    @Resource
    private CspSmsOrderRecharge1Dao cspSmsOrderRecharge1Dao;
    @Resource
    private RobotAccount1Dao robotAccount1Dao;
    @Resource
    private RobotClickResult1Dao robotClickResult1Dao;
    @Resource
    private RobotGroupSendPlanDesc1Dao robotGroupSendPlanDesc1Dao;
    @Resource
    private RobotGroupSendPlansDetail1Dao robotGroupSendPlansDetail1Dao;
    @Resource
    private RobotGroupSendPlans1Dao robotGroupSendPlans1Dao;
    @Resource
    private RobotMediaPhoneResult1Dao robotMediaPhoneResult1Dao;
    @Resource
    private RobotOrder1Dao robotOrder1Dao;
    @Resource
    private RobotPhoneResult1Dao robotPhoneResult1Dao;
    @Resource
    private RobotPhoneUplinkResult1Dao robotPhoneUplinkResult1Dao;
    @Resource
    private RobotProcessAsk1Dao robotProcessAsk1Dao;
    @Resource
    private RobotProcessButton1Dao robotProcessButton1Dao;
    @Resource
    private RobotProcessDes1Dao robotProcessDes1Dao;
    @Resource
    private RobotProcessSettingNode1Dao robotProcessSettingNode1Dao;
    @Resource
    private RobotProcessTriggerNode1Dao robotProcessTriggerNode1Dao;
    @Resource
    private RobotSceneMaterial1Dao robotSceneMaterial1Dao;
    @Resource
    private RobotSceneNode1Dao robotSceneNode1Dao;
    @Resource
    private RobotSetting1Dao robotSetting1Dao;
    @Resource
    private RobotShortcutButton1Dao robotShortcutButton1Dao;
    @Resource
    private RobotVariable1Dao robotVariable1Dao;




    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDate(CreateTableReq req) {
        List<TenantUserUpdateInfo> tenantUserUpdateInfos = req.getTenantUserUpdateInfos();
        if(!CollectionUtils.isEmpty(tenantUserUpdateInfos)){

            for(TenantUserUpdateInfo userUpdateInfo : tenantUserUpdateInfos){
                String customerId = userUpdateInfo.getCustomerId();
                String cspId = userUpdateInfo.getCspId();
                String customerUserId = userUpdateInfo.getCustomerUserId();
                String cspUserId = userUpdateInfo.getCspUserId();
                Map<String,String> chatbotAccountIdMap = userUpdateInfo.getChatbotAccountIdMap();


                //csp_order_deduct -- customerId
                LambdaQueryWrapper<CspOrderDeductDo> cspOrderDeductDoQueryWrapper = new LambdaQueryWrapper<>();
                cspOrderDeductDoQueryWrapper.eq(CspOrderDeductDo::getUserId,customerUserId);//customer的userId
                cspOrderDeductDoQueryWrapper.and(wq->wq.eq(CspOrderDeductDo::getCreator,cspUserId).or().eq(CspOrderDeductDo::getCreatorOld,cspUserId));//customer的userId
                List<CspOrderDeductDo> cspOrderDeductDos = cspOrderDeduct1Dao.selectList(cspOrderDeductDoQueryWrapper);
                List<CspOrderDeductDo> cspOrderDeductDos1 = new ArrayList<>();
                for(CspOrderDeductDo item : cspOrderDeductDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(cspId);
                    item.setUpdater(cspId);
                    item.setChatbotAccountId("");
                    item.setCustomerId(customerId);
                    cspOrderDeductDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(cspOrderDeductDos1)){
                    cspOrderDeduct1Dao.updateBatch(cspOrderDeductDos1);
                }


                //csp_order_recharge -- customerId

                LambdaQueryWrapper<CspOrderRechargeDo> cspOrderRechargeDoQueryWrapper = new LambdaQueryWrapper<>();
                cspOrderRechargeDoQueryWrapper.eq(CspOrderRechargeDo::getUserId,customerUserId);
                cspOrderRechargeDoQueryWrapper.and(wq->wq.eq(CspOrderRechargeDo::getCreator,cspUserId).or().eq(CspOrderRechargeDo::getCreatorOld,cspUserId));//customer的userId
                List<CspOrderRechargeDo> cspOrderRechargeDos = cspOrderRecharge1Dao.selectList(cspOrderRechargeDoQueryWrapper);
                List<CspOrderRechargeDo> cspOrderRechargeDos1 = new ArrayList<>();
                for(CspOrderRechargeDo item : cspOrderRechargeDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(cspId);
                    item.setUpdater(cspId);
                    item.setChatbotAccountId("");
                    item.setCustomerId(customerId);
                    cspOrderRechargeDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(cspOrderRechargeDos1)){
                    cspOrderRecharge1Dao.updateBatch(cspOrderRechargeDos1);
                }


                //csp_sms_order_deduct -- customerId
                LambdaQueryWrapper<CspSmsOrderDeductDo> cspSmsOrderDeductDoQueryWrapper = new LambdaQueryWrapper<>();
                cspSmsOrderDeductDoQueryWrapper.eq(CspSmsOrderDeductDo::getUserId,customerUserId);
                cspSmsOrderDeductDoQueryWrapper.and(wq->wq.eq(CspSmsOrderDeductDo::getCreator,cspUserId).or().eq(CspSmsOrderDeductDo::getCreatorOld,cspUserId));//customer的userId
                List<CspSmsOrderDeductDo> cspSmsOrderDeductDos = cspSmsOrderDeduct1Dao.selectList(cspSmsOrderDeductDoQueryWrapper);
                List<CspSmsOrderDeductDo> cspSmsOrderDeductDos1 = new ArrayList<>();
                for(CspSmsOrderDeductDo item : cspSmsOrderDeductDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(cspId);
                    item.setUpdater(cspId);
                    item.setChatbotAccountId("");
                    item.setCustomerId(customerId);
                    cspSmsOrderDeductDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(cspSmsOrderDeductDos1)){
                    cspSmsOrderDeduct1Dao.updateBatch(cspSmsOrderDeductDos1);
                }


                //csp_sms_order_recharge -- customerId
                LambdaQueryWrapper<CspSmsOrderRechargeDo> cspSmsOrderRechargeDoQueryWrapper = new LambdaQueryWrapper<>();
                cspSmsOrderRechargeDoQueryWrapper.eq(CspSmsOrderRechargeDo::getUserId,customerUserId);//customer的userId
                cspSmsOrderRechargeDoQueryWrapper.and(wq->wq.eq(CspSmsOrderRechargeDo::getCreator,cspUserId).or().eq(CspSmsOrderRechargeDo::getCreatorOld,cspUserId));//customer的userId
                List<CspSmsOrderRechargeDo> cspSmsOrderRechargeDos = cspSmsOrderRecharge1Dao.selectList(cspSmsOrderRechargeDoQueryWrapper);
                List<CspSmsOrderRechargeDo> cspSmsOrderRechargeDos1 = new ArrayList<>();
                for(CspSmsOrderRechargeDo item : cspSmsOrderRechargeDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(cspId);
                    item.setUpdater(cspId);
                    item.setCustomerId(customerId);
                    cspSmsOrderRechargeDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(cspSmsOrderRechargeDos1)){
                    cspSmsOrderRecharge1Dao.updateBatch(cspSmsOrderRechargeDos1);
                }


                //robot_account -- customerId
                LambdaQueryWrapper<RobotAccountDo> robotAccountDoQueryWrapper = new LambdaQueryWrapper<>();
                robotAccountDoQueryWrapper.eq(RobotAccountDo::getCreator,customerUserId).or().eq(RobotAccountDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotAccountDo> robotAccountDos = robotAccount1Dao.selectList(robotAccountDoQueryWrapper);
                List<RobotAccountDo> robotAccountDos1 = new ArrayList<>();
                for(RobotAccountDo item : robotAccountDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    item.setChatbotAccountId(chatbotAccountIdMap.get(item.getAccount()));
                    robotAccountDos1.add(item);

                }
                if(!CollectionUtils.isEmpty(robotAccountDos1)){
                    robotAccount1Dao.updateBatch(robotAccountDos1);
                }


                //robot_click_result -- customerId
                LambdaQueryWrapper<RobotClickResultDo> robotClickResultDoQueryWrapper = new LambdaQueryWrapper<>();
                robotClickResultDoQueryWrapper.eq(RobotClickResultDo::getCreator,customerUserId).or().eq(RobotClickResultDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotClickResultDo> robotClickResultDos = robotClickResult1Dao.selectList(robotClickResultDoQueryWrapper);
                for(RobotClickResultDo item : robotClickResultDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotClickResult1Dao.updateById(item);
                }


                //robot_group_plans_desc -- customerId
                LambdaQueryWrapper<RobotGroupSendPlanDescDo> robotGroupSendPlanDescDoQueryWrapper = new LambdaQueryWrapper<>();
                robotGroupSendPlanDescDoQueryWrapper.eq(RobotGroupSendPlanDescDo::getCreator,customerUserId).or().eq(RobotGroupSendPlanDescDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotGroupSendPlanDescDo> robotGroupSendPlanDescDos = robotGroupSendPlanDesc1Dao.selectList(robotGroupSendPlanDescDoQueryWrapper);
                List<RobotGroupSendPlanDescDo> robotGroupSendPlanDescDos1 = new ArrayList<>();
                for(RobotGroupSendPlanDescDo item : robotGroupSendPlanDescDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotGroupSendPlanDescDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotGroupSendPlanDescDos1)){
                    robotGroupSendPlanDesc1Dao.updateBatch(robotGroupSendPlanDescDos1);
                }

                //robot_group_plan_detail -- customerId
                LambdaQueryWrapper<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDoQueryWrapper = new LambdaQueryWrapper<>();
                robotGroupSendPlansDetailDoQueryWrapper.eq(RobotGroupSendPlansDetailDo::getCreator,customerUserId).or().eq(RobotGroupSendPlansDetailDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDos = robotGroupSendPlansDetail1Dao.selectList(robotGroupSendPlansDetailDoQueryWrapper);
                List<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDos1 = new ArrayList<>();
                for(RobotGroupSendPlansDetailDo item : robotGroupSendPlansDetailDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotGroupSendPlansDetailDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotGroupSendPlansDetailDos1)){
                    robotGroupSendPlansDetail1Dao.updateBatch(robotGroupSendPlansDetailDos1);
                }



                //robot_group_plan_send -- customerId
                LambdaQueryWrapper<RobotGroupSendPlansDo> robotGroupSendPlansDoQueryWrapper = new LambdaQueryWrapper<>();
                robotGroupSendPlansDoQueryWrapper.eq(RobotGroupSendPlansDo::getCreator,customerUserId).or().eq(RobotGroupSendPlansDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotGroupSendPlansDo> robotGroupSendPlansDos = robotGroupSendPlans1Dao.selectList(robotGroupSendPlansDoQueryWrapper);
                List<RobotGroupSendPlansDo> robotGroupSendPlansDos1 = new ArrayList<>();
                for(RobotGroupSendPlansDo item : robotGroupSendPlansDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotGroupSendPlansDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotGroupSendPlansDos1)){
                    robotGroupSendPlans1Dao.updateBatch(robotGroupSendPlansDos1);
                }



                //robot_order -- customerId
                LambdaQueryWrapper<RobotOrderDo> robotOrderDoQueryWrapper = new LambdaQueryWrapper<>();
                robotOrderDoQueryWrapper.eq(RobotOrderDo::getCreator,customerUserId).or().eq(RobotOrderDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotOrderDo> robotOrderDos = robotOrder1Dao.selectList(robotOrderDoQueryWrapper);
                List<RobotOrderDo> robotOrderDos1 = new ArrayList<>();
                for(RobotOrderDo item : robotOrderDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotOrderDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotOrderDos1)){
                    robotOrder1Dao.updateBatch(robotOrderDos1);
                }




                //robot_phone_uplink_result -- customerId
                LambdaQueryWrapper<RobotPhoneUplinkResult> robotPhoneUplinkResultWrapper = new LambdaQueryWrapper<>();
                robotPhoneUplinkResultWrapper.eq(RobotPhoneUplinkResult::getCreator,customerUserId).or().eq(RobotPhoneUplinkResult::getCreatorOld, customerUserId);//customer的userId
                List<RobotPhoneUplinkResult> robotPhoneUplinkResults = robotPhoneUplinkResult1Dao.selectList(robotPhoneUplinkResultWrapper);
                for(RobotPhoneUplinkResult item : robotPhoneUplinkResults){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotPhoneUplinkResult1Dao.updateById(item);
                }


                //robot_process_ask -- customerId
                LambdaQueryWrapper<RobotProcessAskDo> robotProcessAskDoQueryWrapper = new LambdaQueryWrapper<>();
                robotProcessAskDoQueryWrapper.eq(RobotProcessAskDo::getCreator,customerUserId).or().eq(RobotProcessAskDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotProcessAskDo> robotProcessAskDos = robotProcessAsk1Dao.selectList(robotProcessAskDoQueryWrapper);
                List<RobotProcessAskDo> robotProcessAskDos1 = new ArrayList<>();
                for(RobotProcessAskDo item : robotProcessAskDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotProcessAskDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotProcessAskDos1)){
                    robotProcessAsk1Dao.updateBatch(robotProcessAskDos1);
                }


                //robot_process_button -- customerId
                LambdaQueryWrapper<RobotProcessButtonDo> robotProcessButtonDoQueryWrapper = new LambdaQueryWrapper<>();
                robotProcessButtonDoQueryWrapper.eq(RobotProcessButtonDo::getCreator,customerUserId).or().eq(RobotProcessButtonDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotProcessButtonDo> robotProcessButtonDos = robotProcessButton1Dao.selectList(robotProcessButtonDoQueryWrapper);
                List<RobotProcessButtonDo> robotProcessButtonDos1 = new ArrayList<>();
                for(RobotProcessButtonDo item : robotProcessButtonDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotProcessButtonDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotProcessButtonDos1)){
                    robotProcessButton1Dao.updateBatch(robotProcessButtonDos1);
                }


                //robot_process_des -- customerId
                LambdaQueryWrapper<RobotProcessDesDo> robotProcessDesDoQueryWrapper = new LambdaQueryWrapper<>();
                robotProcessDesDoQueryWrapper.eq(RobotProcessDesDo::getCreator,customerUserId).or().eq(RobotProcessDesDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotProcessDesDo> robotProcessDesDos = robotProcessDes1Dao.selectList(robotProcessDesDoQueryWrapper);
                List<RobotProcessDesDo> robotProcessDesDos1 = new ArrayList<>();
                for(RobotProcessDesDo item : robotProcessDesDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotProcessDesDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotProcessDesDos1)){
                    robotProcessDes1Dao.updateBatch(robotProcessDesDos1);
                }


                //robot_process_setting_node -- customerId
                LambdaQueryWrapper<RobotProcessSettingNodeDo> robotProcessSettingNodeDoQueryWrapper = new LambdaQueryWrapper<>();
                robotProcessSettingNodeDoQueryWrapper.eq(RobotProcessSettingNodeDo::getCreator,customerUserId).or().eq(RobotProcessSettingNodeDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNode1Dao.selectList(robotProcessSettingNodeDoQueryWrapper);
                List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos1 = new ArrayList<>();
                for(RobotProcessSettingNodeDo item : robotProcessSettingNodeDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotProcessSettingNodeDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotProcessSettingNodeDos1)){
                    robotProcessSettingNode1Dao.updateBatch(robotProcessSettingNodeDos1);
                }


                //robot_process_trigger_node -- customerId
                LambdaQueryWrapper<RobotProcessTriggerNodeDo> robotProcessTriggerNodeDoQueryWrapper = new LambdaQueryWrapper<>();
                robotProcessTriggerNodeDoQueryWrapper.eq(RobotProcessTriggerNodeDo::getCreator,customerUserId).or().eq(RobotProcessTriggerNodeDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotProcessTriggerNodeDo> robotProcessTriggerNodeDos = robotProcessTriggerNode1Dao.selectList(robotProcessTriggerNodeDoQueryWrapper);
                List<RobotProcessTriggerNodeDo> robotProcessTriggerNodeDos1 = new ArrayList<>();
                for(RobotProcessTriggerNodeDo item : robotProcessTriggerNodeDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotProcessTriggerNodeDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotProcessTriggerNodeDos1)){
                    robotProcessTriggerNode1Dao.updateBatch(robotProcessTriggerNodeDos1);
                }


                //robot_scene_material -- customerId
                LambdaQueryWrapper<RobotSceneMaterialDo> robotSceneMaterialDoQueryWrapper = new LambdaQueryWrapper<>();
                robotSceneMaterialDoQueryWrapper.eq(RobotSceneMaterialDo::getCreator,customerUserId).or().eq(RobotSceneMaterialDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotSceneMaterialDo> robotSceneMaterialDos = robotSceneMaterial1Dao.selectList(robotSceneMaterialDoQueryWrapper);
                List<RobotSceneMaterialDo> robotSceneMaterialDos1 = new ArrayList<>();
                for(RobotSceneMaterialDo item : robotSceneMaterialDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotSceneMaterialDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotSceneMaterialDos1)){
                    robotSceneMaterial1Dao.updateBatch(robotSceneMaterialDos1);
                }


                //robot_scene_node -- customerId
                LambdaQueryWrapper<RobotSceneNodeDo> robotSceneNodeDoQueryWrapper = new LambdaQueryWrapper<>();
                robotSceneNodeDoQueryWrapper.eq(RobotSceneNodeDo::getCreator,customerUserId).or().eq(RobotSceneNodeDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotSceneNodeDo> robotSceneNodeDos = robotSceneNode1Dao.selectList(robotSceneNodeDoQueryWrapper);
                List<RobotSceneNodeDo> robotSceneNodeDos1 = new ArrayList<>();
                for(RobotSceneNodeDo item : robotSceneNodeDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotSceneNodeDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotSceneNodeDos1)){
                    robotSceneNode1Dao.updateBatch(robotSceneNodeDos1);
                }



                //robot_setting -- customerId
                LambdaQueryWrapper<RobotSettingDo> robotSettingDoQueryWrapper = new LambdaQueryWrapper<>();
                robotSettingDoQueryWrapper.eq(RobotSettingDo::getCreator,customerUserId).or().eq(RobotSettingDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotSettingDo> robotSettingDos = robotSetting1Dao.selectList(robotSettingDoQueryWrapper);
                List<RobotSettingDo> robotSettingDos1 = new ArrayList<>();
                for(RobotSettingDo item : robotSettingDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotSettingDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotSettingDos1)){
                    robotSetting1Dao.updateBatch(robotSettingDos1);
                }


                //robot_shortcut_button -- customerId
                LambdaQueryWrapper<RobotShortcutButtonDo> robotShortcutButtonDoQueryWrapper = new LambdaQueryWrapper<>();
                robotShortcutButtonDoQueryWrapper.eq(RobotShortcutButtonDo::getCreator,customerUserId).or().eq(RobotShortcutButtonDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotShortcutButtonDo> robotShortcutButtonDos = robotShortcutButton1Dao.selectList(robotShortcutButtonDoQueryWrapper);
                List<RobotShortcutButtonDo> robotShortcutButtonDos1 = new ArrayList<>();
                for(RobotShortcutButtonDo item : robotShortcutButtonDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotShortcutButtonDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotShortcutButtonDos1)){
                    robotShortcutButton1Dao.updateBatch(robotShortcutButtonDos1);
                }



                //robot_variable -- customerId
                LambdaQueryWrapper<RobotVariableDo> robotVariableDoQueryWrapper = new LambdaQueryWrapper<>();
                robotVariableDoQueryWrapper.eq(RobotVariableDo::getCreator,customerUserId).or().eq(RobotVariableDo::getCreatorOld, customerUserId);//customer的userId
                List<RobotVariableDo> robotVariableDos = robotVariable1Dao.selectList(robotVariableDoQueryWrapper);
                List<RobotVariableDo> robotVariableDos1 = new ArrayList<>();
                for(RobotVariableDo item : robotVariableDos){
                    item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                    item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                    item.setCreator(customerId);
                    item.setUpdater(customerId);
                    robotVariableDos1.add(item);
                }
                if(!CollectionUtils.isEmpty(robotVariableDos1)){
                    robotVariable1Dao.updateBatch(robotVariableDos1);
                }

            }
        }
        log.info("--------------------------------robot服务数据同步完成--------------------------------");

    }
}
