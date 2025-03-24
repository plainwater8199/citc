package com.citc.nce.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.dao.SubscribeNamesDao;
import com.citc.nce.module.dto.SubscribeSendInfoForPhoneItemDto;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.module.entity.SubscribeModuleDo;
import com.citc.nce.module.entity.SubscribeNamesDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.*;
import com.citc.nce.module.vo.SubscribeInfoItem;
import com.citc.nce.module.vo.req.GetSubscribeInfoByUserReq;
import com.citc.nce.module.vo.req.SubscribeNamesReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.resp.GetSubscribeInfoByUserResp;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("subscribeNamesService")
public class SubscribeNamesServiceImpl implements SubscribeNamesService {

    @Resource
    private SubscribeNamesDao subscribeNamesDao;

    @Resource
    private SubscribeModuleService subscribeModuleService;

    @Resource
    private SubscribeContentService subscribeContentService;

    @Resource
    private ModuleService moduleService;
    @Autowired
    private SubscribeContentSendDetailServiceImpl subscribeContentSendDetailService;


    @Override
    public int saveSubscribeNames(SubscribeNamesReq req) {
        SubscribeModuleDo subscribeModuleDo = subscribeModuleService.getSubscribeModuleInfo(req.getSubscribeId());
        if (subscribeModuleDo != null) {
            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getPhone, req.getPhone());
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, req.getSubscribeId());
            queryWrapper.eq(SubscribeNamesDo::getStatus, ModuleStatusEnums.SUB_NAMES_STATUS_IS_SUB.getCode());//订阅状态：0 未/取消订阅 1 订阅
            SubscribeNamesDo result = subscribeNamesDao.selectOne(queryWrapper);
            if(result == null){//不存在订阅
                SubscribeNamesDo subscribeNamesDo = new SubscribeNamesDo();
                BeanUtil.copyProperties(req, subscribeNamesDo);
                subscribeNamesDo.setStatus(ModuleStatusEnums.SUB_NAMES_STATUS_IS_SUB.getCode());
                String uuid = UUID.randomUUID().toString();
                subscribeNamesDo.setSubscribeNamesId(uuid);
                subscribeNamesDo.setAdvance(obtainAdvanceContentIdForUser(req.getPhone(),req.getSubscribeId()));//获取订阅内容的历史记录
                int insertResult = subscribeNamesDao.insert(subscribeNamesDo);
                if(insertResult == 1 && (ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == subscribeModuleDo.getSubsSuccess() && subscribeModuleDo.getSuccess5gMsgId() != null)){
                        moduleService.sendMessage(subscribeModuleDo.getSuccess5gMsgId(),subscribeModuleDo.getName(),req.getPhone(),req.getChatbotId(), ModuleStatusEnums.MODULE_SUBSCRIBE.getCode());

                }
                return insertResult;
            }
            return 0;
        }else{
            throw new BizException("组件不存在！");
        }
    }

    private String obtainAdvanceContentIdForUser(String phone, String subscribeId) {
        LambdaQueryWrapper<SubscribeNamesDo> queryWrapperOld = new LambdaQueryWrapper<>();
        queryWrapperOld.eq(SubscribeNamesDo::getPhone, phone);
        queryWrapperOld.eq(SubscribeNamesDo::getSubscribeId, subscribeId);
        queryWrapperOld.eq(SubscribeNamesDo::getStatus, ModuleStatusEnums.SUB_NAMES_STATUS_IS_NOT_SUB.getCode());//订阅状态：0 未/取消订阅 1 订阅
        queryWrapperOld.orderByDesc(SubscribeNamesDo::getCreateTime);
        List<SubscribeNamesDo> result = subscribeNamesDao.selectList(queryWrapperOld);
        if(!CollectionUtils.isEmpty(result)){
            SubscribeNamesDo subscribeNamesDo = result.get(0);
            return subscribeNamesDo.getAdvance();
        }else{
            return "-1";
        }
    }


    @Override
    public int cancelSubscribeNames(SubscribeNamesReq req) {
        List<SubscribeNamesDo> subscribeNamesDos = new ArrayList<>();
        if (!StringUtils.isBlank(req.getSubscribeNamesId()) && !StringUtils.isBlank(req.getSubscribeId())) {//通过界面取消打卡
            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, req.getSubscribeId());
            queryWrapper.eq(SubscribeNamesDo::getSubscribeNamesId, req.getSubscribeNamesId());
            queryWrapper.eq(SubscribeNamesDo::getStatus, 1);
            subscribeNamesDos = subscribeNamesDao.selectList(queryWrapper);

        }else if(!StringUtils.isBlank(req.getPhone()) && !StringUtils.isBlank(req.getSubscribeId()) && !StringUtils.isBlank(req.getChatbotId())){//通过终端取消打卡
            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getPhone, req.getPhone());
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, req.getSubscribeId());
            queryWrapper.eq(SubscribeNamesDo::getStatus, 1);
            subscribeNamesDos = subscribeNamesDao.selectList(queryWrapper);
        }
        if(!CollectionUtils.isEmpty(subscribeNamesDos)){
            SubscribeNamesDo subscribeNamesDo = subscribeNamesDos.get(0);
            subscribeNamesDo.setStatus(0);
            subscribeNamesDao.updateById(subscribeNamesDo);

            // 取消订阅订阅后发送短信
            SubscribeModuleDo subscribeModuleDo = subscribeModuleService.getSubscribeModuleInfo(req.getSubscribeId());
            if(ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == subscribeModuleDo.getSubsCancel() && subscribeModuleDo.getCancel5gMsgId() != null){
                moduleService.sendMessage(subscribeModuleDo.getCancel5gMsgId(),subscribeModuleDo.getName(),subscribeNamesDo.getPhone(),subscribeNamesDo.getChatbotId(),ModuleStatusEnums.MODULE_SUBSCRIBE.getCode());
            }
        }
        return 0;
    }



    @Override
    public int updateSubscribeNames(SubscribeNamesReq req) {
        if (!StringUtils.isBlank(req.getSubscribeNamesId()) && !StringUtils.isBlank(req.getSubscribeId())) {
            SubscribeNamesDo subscribeNamesDo = new SubscribeNamesDo();
            subscribeNamesDo.setSubscribeId(req.getSubscribeId());
            subscribeNamesDo.setPhone(req.getPhone());
//            subscribeNamesDo.setAdvance(req.getAdvance());

            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, req.getSubscribeId());
            queryWrapper.eq(SubscribeNamesDo::getSubscribeNamesId, req.getSubscribeNamesId());
            return subscribeNamesDao.update(subscribeNamesDo, queryWrapper);
        }
        return 0;
    }

    @Override
    public PageResult<SubscribeNamesReq> getSubscribeNamesList(SubscribeNamesReq req) {
        PageResult<SubscribeNamesReq> subscribeNamesResult = new PageResult<>();
        if (StringUtils.isBlank(req.getSubscribeId())) {
            throw new BizException(200, "SubscribeId为空！");
        }
        QueryWrapper<SubscribeNamesDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "subscribe_id","subscribe_names_id", "phone", "advance", "status", "create_time");

        if (StringUtils.isNotBlank(req.getPhone())) {
            queryWrapper.like("phone", req.getPhone());
        }
        queryWrapper.eq("subscribe_id", req.getSubscribeId());
        // 创建时间倒序
        queryWrapper.orderByDesc("create_time");

        PageParam pageParam = new PageParam();
        pageParam.setPageNo(req.getPageNo());
        pageParam.setPageSize(req.getPageSize());
        PageResult<SubscribeNamesDo> result = subscribeNamesDao.selectPage(pageParam, queryWrapper);
        List<SubscribeNamesDo> respList = result.getList();
        List<SubscribeNamesReq> subscribeNamesList = new ArrayList<>();

        //查询订阅组件下所有的订阅内容
        Map<String,SubscribeContentDo> subContentNameMap = obtainSubContentNameMap(req.getSubscribeId());

        for (SubscribeNamesDo item : respList) {
            SubscribeNamesReq pageResp = new SubscribeNamesReq();
            BeanUtil.copyProperties(item, pageResp);
            pageResp.setAdvance(obtainAdvanceForUser(item.getStatus(),item.getAdvance(),subContentNameMap.get(item.getAdvance())));
            pageResp.setSubscribeDate(item.getCreateTime());
            subscribeNamesList.add(pageResp);
        }
        subscribeNamesResult.setList(subscribeNamesList);
        subscribeNamesResult.setTotal(result.getTotal());
        return subscribeNamesResult;
    }

    private Map<String, SubscribeContentDo> obtainSubContentNameMap(String subscribeId) {
        Map<String, SubscribeContentDo> subContentNameMap = new HashMap<>();
        if(!Strings.isNullOrEmpty(subscribeId)){
            List<SubscribeContentDo> subscribeContentDos = subscribeContentService.findListById(subscribeId);
            if(!CollectionUtils.isEmpty(subscribeContentDos)){
                subscribeContentDos.forEach(i->subContentNameMap.put(i.getSubContentId(),i));
            }
        }
        return subContentNameMap;
    }


    /**
     * 获取推送进度
     * @param userStatus 用户状态
     * @param advance 当前进度
     * @return 当前进度
     */
    private String obtainAdvanceForUser(Integer userStatus,String advance, SubscribeContentDo subContentInfo) {
        if(ModuleStatusEnums.SUB_NAMES_STATUS_IS_NOT_SUB.getCode() == userStatus){
            if("-1".equals(advance)){
                return "已取消";
            }else{
                if(subContentInfo != null){
                    if(ModuleStatusEnums.SUB_CONTENT_IS_THE_LAST.getCode() == subContentInfo.getIsTheLast()){
                        return "已结束";
                    }else{
                        return "已取消【"+ subContentInfo.getTitle() +"】";
                    }
                }else{
                    return "已取消";
                }
            }
        }else{
            if("-1".equals(advance)){
                return "未推送";
            }else{
                if(subContentInfo != null){
                    if(ModuleStatusEnums.SUB_CONTENT_IS_THE_LAST.getCode() == subContentInfo.getIsTheLast()){
                        return "已结束";
                    }else{
                        return "已推送【"+ subContentInfo.getTitle() +"】";
                    }
                }else{
                    return "未推送";
                }
            }
        }
    }


    @Override
    public Long getSubscribeNamesCount(String subscribeId) {
        if (!StringUtils.isBlank(subscribeId)) {
            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, subscribeId);
            queryWrapper.eq(SubscribeNamesDo::getStatus, 1);
            return subscribeNamesDao.selectCount(queryWrapper);
        }
        return 0L;
    }

    @Override
    public Map<String,Map<String,String>> getSendPhone2ChatbotIds(String subscribeId) {
        if(!Strings.isNullOrEmpty(subscribeId)){
            Map<String,Map<String,String>> sendingInfoMap = new HashMap<>();
            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, subscribeId);
            queryWrapper.isNotNull(SubscribeNamesDo::getPhone);
            queryWrapper.eq(SubscribeNamesDo::getStatus, ModuleStatusEnums.SUB_NAMES_STATUS_IS_SUB.getCode());
            List<SubscribeNamesDo> subscribeNamesDos = subscribeNamesDao.selectList(queryWrapper);
            if(!CollectionUtils.isEmpty(subscribeNamesDos)){
                Map<String,String> phone2ChatbotIdMap;
                String advance;
                for(SubscribeNamesDo item : subscribeNamesDos){
                    advance = item.getAdvance();
                    if(sendingInfoMap.containsKey(advance)){
                        phone2ChatbotIdMap = sendingInfoMap.get(advance);
                        phone2ChatbotIdMap.put(item.getPhone(),item.getChatbotId());
                    }else{
                        phone2ChatbotIdMap = new HashMap<>();
                        phone2ChatbotIdMap.put(item.getPhone(),item.getChatbotId());
                        sendingInfoMap.put(advance,phone2ChatbotIdMap);
                    }
                }
                return sendingInfoMap;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public void updateAdvance(String phone, String subscribeId, String subContentId) {
        LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeNamesDo::getSubscribeId, subscribeId);
        queryWrapper.eq(SubscribeNamesDo::getPhone,phone);
        queryWrapper.eq(SubscribeNamesDo::getStatus, 1);
        List<SubscribeNamesDo> subscribeNamesDos = subscribeNamesDao.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(subscribeNamesDos)){
            SubscribeNamesDo subscribeNamesDo = subscribeNamesDos.get(0);
            subscribeNamesDo.setAdvance(subContentId);
            subscribeNamesDao.updateById(subscribeNamesDo);
        }
    }

    @Override
    public GetSubscribeInfoByUserResp getSubscribeInfoByPhone(GetSubscribeInfoByUserReq req) {
        GetSubscribeInfoByUserResp resp = new GetSubscribeInfoByUserResp();
        String phone = req.getPhone();
        String subscribeId = req.getSubscribeId();
        if(!Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(subscribeId)){
            //1、获取组件的内容信息
            SubscribeModuleDo subscribeModuleInfo = subscribeModuleService.getSubscribeModuleInfo(subscribeId);
            if(subscribeModuleInfo != null && SessionContextUtil.getUserId().equals(subscribeModuleInfo.getCreator())){
                List<SubscribeSendInfoForPhoneItemDto> subscribeSendInfoForPhoneItems = subscribeContentSendDetailService.getSubscribeSendInfoForPhone(phone,subscribeId);
                if (!CollectionUtils.isEmpty(subscribeSendInfoForPhoneItems)){
                    List<SubscribeInfoItem> subscribeInfoItemList = new ArrayList<>();
                    SubscribeInfoItem subscribeInfoItem;
                    for(SubscribeSendInfoForPhoneItemDto item : subscribeSendInfoForPhoneItems){
                        subscribeInfoItem = new SubscribeInfoItem();
                        BeanUtils.copyProperties(item,subscribeInfoItem);
                        subscribeInfoItemList.add(subscribeInfoItem);
                    }
                    resp.setSubscribeInfoItemList(subscribeInfoItemList);
                }
            }
        }
        return resp;
    }

    @Override
    public Set<String> getSubscribePhones(String moduleId) {
        if (!StringUtils.isBlank(moduleId)) {
            LambdaQueryWrapper<SubscribeNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeNamesDo::getSubscribeId, moduleId);
            queryWrapper.eq(SubscribeNamesDo::getStatus, ModuleStatusEnums.SUB_NAMES_STATUS_IS_SUB);
            List<SubscribeNamesDo> subscribeNamesDos = subscribeNamesDao.selectList(queryWrapper);
            if(!CollectionUtils.isEmpty(subscribeNamesDos)){
                return subscribeNamesDos.stream().map(SubscribeNamesDo::getPhone).collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }


    private Date generateSendTime(String sendType, String sendTime, Date date) {
        Date conservtDate = null;
        // 根据发送类型和时间生成固定的发送时间
        if (date == null) {
            // send_time : 09:05
            if (sendType.equals("DAY")) { // 每天发送
                Date d = DateUtils.obtainDate(DateUtils.obtainDateStr(new Date(), "yyyy-MM-dd"));
                String dateStr = DateUtils.obtainDateStr(DateUtils.addDays(d, 1));
                String res = dateStr.replace("00:00:00", sendTime);
                conservtDate = DateUtils.obtainDate(res, "yyyy-MM-dd HH:mm");
            } else {
                Date resDate = null;
                // sendType :First,second,third,fourth,fifth,sixth,seventh(每周第几天)
                switch (sendType) {
                    case "FIRST":
                        // 获取下周的周一
                        resDate = DateUtils.getNextWeekDay(Calendar.MONDAY);
                        break;
                    case "SECOND":
                        // 获取下周的周二
                        resDate = DateUtils.getNextWeekDay(Calendar.TUESDAY);
                        break;
                    case "THIRD":
                        // 获取下周的周三
                        resDate = DateUtils.getNextWeekDay(Calendar.WEDNESDAY);
                        break;
                    case "FOURTH":
                        // 获取下周的周四
                        resDate = DateUtils.getNextWeekDay(Calendar.THURSDAY);
                        break;
                    case "FIFTH":
                        // 获取下周的周五
                        resDate = DateUtils.getNextWeekDay(Calendar.FRIDAY);
                        break;
                    case "SIXTH":
                        // 获取下周的周六
                        resDate = DateUtils.getNextWeekDay(Calendar.SATURDAY);
                        break;
                    case "SEVENTH":
                        // 获取下周的周日
                        resDate = DateUtils.getNextWeekDay(Calendar.SUNDAY);
                        break;
                    default:
                        return null;
                }
                Date d = DateUtils.obtainDate(DateUtils.obtainDateStr(resDate, "yyyy-MM-dd"));
                String dateStr = DateUtils.obtainDateStr(d);
                String res = dateStr.replace("00:00:00", sendTime);
                return DateUtils.obtainDate(res);
            }
        } else {
            if (sendType.equals("DAY")) { // 每天发送
                String dateStr = DateUtils.obtainDateStr(DateUtils.addDays(date, 1));
                String res = dateStr.replace("00:00:00", sendTime);
                conservtDate = DateUtils.obtainDate(res);
            } else {
                String dateStr = DateUtils.obtainDateStr(DateUtils.addDays(date, 7));
                String res = dateStr.replace("00:00:00", sendTime);
                conservtDate = DateUtils.obtainDate(res);
            }
        }
        return conservtDate;
    }
}
