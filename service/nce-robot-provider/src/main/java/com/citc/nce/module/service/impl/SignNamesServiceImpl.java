package com.citc.nce.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.module.constant.ModuleError;
import com.citc.nce.module.dao.SignNamesDao;
import com.citc.nce.module.dao.SignRecordDao;
import com.citc.nce.module.entity.SignModuleDo;
import com.citc.nce.module.entity.SignNamesDo;
import com.citc.nce.module.entity.SignRecordDo;
import com.citc.nce.module.entity.SubscribeNamesDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.ModuleService;
import com.citc.nce.module.service.SignModuleService;
import com.citc.nce.module.service.SignNamesService;
import com.citc.nce.module.service.SubscribeContentSendDetailService;
import com.citc.nce.module.vo.req.SignNamesReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("signNamesService")
public class SignNamesServiceImpl implements SignNamesService {

    @Resource
    private SignNamesDao signNamesDao;

    @Resource
    @Lazy
    private SignModuleService signModuleService;

    @Resource
    private ModuleService moduleService;

    @Resource
    private SignRecordDao signRecordDao;

    @Override
    public int saveSignNamesForButton(SignNamesReq req) {
        //检查组件是否存在
        SignModuleDo signModuleDo = signModuleService.getSignModuleById(req.getSignModuleId());
        if(signModuleDo != null){
            //保存参与打卡
            SignNamesDo signNamesDo = getSignNamesInfo(req.getPhone(),req.getSignModuleId());
            if(signNamesDo == null){
                signNamesDo = new SignNamesDo();
                BeanUtil.copyProperties(req,signNamesDo);
                String uuid = UUID.randomUUID().toString();
                signNamesDo.setSignNamesId(uuid);
                signNamesDo.setSignCount(0L);
                int insert = signNamesDao.insert(signNamesDo);
                if(insert == 1 && (ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == signModuleDo.getJoinSuccess() && signModuleDo.getJoinSuccess5gTmpId() != null)){
                    moduleService.sendMessage(signModuleDo.getJoinSuccess5gTmpId(),signModuleDo.getName(),req.getPhone(),req.getChatbotId(), ModuleStatusEnums.MODULE_SIGN.getCode());
                }
                return insert;
            }
        }else{
            throw new BizException(ModuleError.MODULE_NOT_EXIST);
        }
        return 0;
    }

    @Override
    public Set<String> getSubscribePhones(String moduleId) {
        if (!StringUtils.isBlank(moduleId)) {
            LambdaQueryWrapper<SignNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SignNamesDo::getSignModuleId, moduleId);
            List<SignNamesDo> signNamesDos = signNamesDao.selectList(queryWrapper);
            if(!CollectionUtils.isEmpty(signNamesDos)){
                return signNamesDos.stream().map(SignNamesDo::getPhone).collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    private SignNamesDo getSignNamesInfo(String phone, String signModuleId) {
        if(!Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(signModuleId)){
            LambdaQueryWrapper<SignNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SignNamesDo::getPhone,phone);
            queryWrapper.eq(SignNamesDo::getSignModuleId,signModuleId);
            List<SignNamesDo> signNamesDos = signNamesDao.selectList(queryWrapper);
            if(!CollectionUtils.isEmpty(signNamesDos)){
                return signNamesDos.get(0);
            }
        }
        return null;
    }


    @Override
    public int saveSignNames(SignNamesReq req) {
        return saveSignNamesForButton(req);
    }

    @Override
    @Transactional
    public int updateSignNames(SignNamesReq req) {
        if(!StringUtil.isNullOrEmpty(req.getPhone()) && !StringUtil.isNullOrEmpty(req.getSignModuleId()) && !StringUtil.isNullOrEmpty(req.getChatbotId())){
            SignModuleDo signModuleDo = signModuleService.getSignModuleById(req.getSignModuleId());
            if(signModuleDo != null){
                SignNamesDo signNamesDo = getSignNamesInfo(req.getPhone(),req.getSignModuleId());
                if(signNamesDo != null){
                    //设置时间打卡现在
                    if(signModuleDo.getSignTimeType() != null && (!StringUtil.isNullOrEmpty(signModuleDo.getSignStartTime()) && !StringUtil.isNullOrEmpty(signModuleDo.getSignEndTime()))){
                        //1、判断当前时间和打卡设置时间是否匹配
                        if(checkSignTime(signModuleDo.getSignTimeType(),signModuleDo.getSignStartTime(),signModuleDo.getSignEndTime())){
                            //2、检查是否重复打卡
                            if(checkSignRepeat(signModuleDo,req.getPhone())){
                                //3、打卡
                                signExec(signNamesDo,signModuleDo,req.getPhone(),req.getChatbotId());
                            }else{
                                if(ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == signModuleDo.getSignRepeatWarn() && signModuleDo.getSignRepeat5gTmpId() != null){
                                    moduleService.sendMessage(signModuleDo.getSignRepeat5gTmpId(),null,req.getPhone(),req.getChatbotId(),ModuleStatusEnums.MODULE_SIGN.getCode());//打卡成功发送的是打卡次数。
                                }
                            }
                        }else{
                            if(ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == signModuleDo.getSignTimeWarn() && signModuleDo.getSignTime5gTmpId() != null){
                                moduleService.sendMessage(signModuleDo.getSignTime5gTmpId(),null,req.getPhone(),req.getChatbotId(),ModuleStatusEnums.MODULE_SIGN.getCode());//打卡成功发送的是打卡次数。
                            }
                        }
                    }else{//未设置打卡时间限制
                        signExec(signNamesDo,signModuleDo,req.getPhone(),req.getChatbotId());
                    }
                }else{
                    throw new BizException(ModuleError.USER_NOT_JOIN_SIGN);
                }
            }else{
                throw new BizException(ModuleError.MODULE_NOT_EXIST);
            }
        }else{
            throw new BizException(ModuleError.SIGN_INFO_ERROR);
        }
        return 1;
    }

    /**
     * 判断是否已经打卡
     * @param signModuleDo 打卡组件
     * @param phone 打卡手机号
     * @return 是否已经打卡
     */
    private boolean checkSignRepeat(SignModuleDo signModuleDo,String phone) {
        //1、获取时间段
        Date startTime = getTimeForNow(signModuleDo.getSignStartTime());
        Date endTime = getTimeForNow(signModuleDo.getSignEndTime());
        LambdaQueryWrapper<SignRecordDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignRecordDo::getSignModuleId,signModuleDo.getSignModuleId()).eq(SignRecordDo::getPhone,phone).between(SignRecordDo::getSignTime,startTime,endTime);
        List<SignRecordDo> signRecordDos = signRecordDao.selectList(queryWrapper);
        return CollectionUtils.isEmpty(signRecordDos);
    }

    private Date getTimeForNow(String signStartTime) {
        String dateStr = DateUtils.obtainDateStr(new Date(),"yyyy-MM-dd");
        return DateUtils.obtainDate(dateStr+" "+signStartTime+":00");
    }

    /**
     * 判断是否在打卡设置时间内打卡
     * @param signTimeType 打卡时间类型 DAY每天/First,second,third,fourth,fifth,sixth,seventh(每周第几天)
     * @param signStartTime 打卡开始时间
     * @param signEndTime 打卡结束时间
     * @return 是否在打卡设置时间内打卡
     */
    private boolean checkSignTime(String signTimeType, String signStartTime, String signEndTime) {
        //1、判断是否是当天，
        if("DAY".equals(signTimeType)) {
            return checkTime(signStartTime, signEndTime,"HH:mm");
        }else if("CUSTOM".equals(signTimeType)){
            return checkTime(signStartTime, signEndTime,"yyyy-MM-dd HH:mm:ss");
        }else{//获取今天星期几
            String weekDay = moduleService.getNowDay(new Date());
            if(weekDay.equals(signTimeType)){
                return checkTime(signStartTime,signEndTime,"HH:mm");
            }
        }
        return false;
    }

    private boolean checkTime(String signStartTime, String signEndTime,String timeFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        LocalTime startTime = LocalTime.parse(signStartTime, formatter);
        LocalTime endTime = LocalTime.parse(signEndTime, formatter);
        LocalTime now = LocalTime.now();
        return (now.isAfter(startTime) && now.isBefore(endTime));
    }


    //执行用户打卡相关操作
    private void signExec(SignNamesDo signNamesDo, SignModuleDo signModuleDo, String phone, String chatbotId) {
        signNamesDo.setSignCount(signNamesDo.getSignCount()+1);
        signNamesDo.setUpdateTime(new Date());
        int update = signNamesDao.updateById(signNamesDo);
        if(update == 1){
            //保存记录
            recordSign(signModuleDo.getSignModuleId(),phone,chatbotId);
            if((ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == signModuleDo.getSignSuccess() && signModuleDo.getSignSuccess5gTmpId() != null)){
                moduleService.sendMessage(signModuleDo.getSignSuccess5gTmpId(),signNamesDo.getSignCount()+"",phone,chatbotId,ModuleStatusEnums.MODULE_SIGN.getCode());//打卡成功发送的是打卡次数。
            }
        }
    }

    private void recordSign(String signModuleId, String phone, String chatbotId) {
        if(!Strings.isNullOrEmpty(signModuleId) && !Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(chatbotId)){
            SignRecordDo signRecordDo = new SignRecordDo();
            signRecordDo.setSignModuleId(signModuleId);
            signRecordDo.setPhone(phone);
            signRecordDo.setChatbotId(chatbotId);
            signRecordDo.setSignTime(new Date());
            signRecordDo.setCreator("system");
            signRecordDo.setCreateTime(new Date());
            signRecordDao.insert(signRecordDo);
        }
    }

    @Override
    public PageResult<SignNamesReq> getSignNamesList(SignNamesReq req) {
        PageResult<SignNamesReq> signNamesResult = new PageResult<>();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("id","sign_module_id","sign_names_id","phone","sign_count","create_time");
        if(StringUtils.isBlank(req.getSignModuleId())){
            throw new BizException(200,"打卡组件ID为空！");
        }
        if(StringUtils.isNotBlank(req.getPhone())){
            queryWrapper.like("phone", req.getPhone());
        }
        queryWrapper.eq("sign_module_id", req.getSignModuleId());
        // 创建时间倒序
        queryWrapper.orderByDesc("create_time");

        PageParam pageParam = new PageParam();
        pageParam.setPageNo(req.getPageNo());
        pageParam.setPageSize(req.getPageSize());
        PageResult<SignNamesDo> result = signNamesDao.selectPage(pageParam,queryWrapper);
        List<SignNamesDo> respList = result.getList();
        List<SignNamesReq> signNamesList = new ArrayList();
        for (SignNamesDo resp : respList) {
            SignNamesReq pageResp = new SignNamesReq();
            BeanUtil.copyProperties(resp,pageResp);
            pageResp.setSigneDate(resp.getCreateTime());
            signNamesList.add(pageResp);
        }
        signNamesResult.setList(signNamesList);
        signNamesResult.setTotal(result.getTotal());
        return signNamesResult;
    }


    @Override
    public Long getSignNamesCount(String signModuleId) {
        if(!StringUtils.isBlank(signModuleId)){
            LambdaQueryWrapper<SignNamesDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SignNamesDo::getSignModuleId, signModuleId);
            return signNamesDao.selectCount(queryWrapper);
        }
        return 0L;

    }
}
