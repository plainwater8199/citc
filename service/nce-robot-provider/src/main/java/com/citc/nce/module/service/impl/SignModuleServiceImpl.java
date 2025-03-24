package com.citc.nce.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.module.constant.ModuleError;
import com.citc.nce.module.dao.SignModuleDao;
import com.citc.nce.module.entity.SignModuleDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.SignModuleService;
import com.citc.nce.module.service.SignNamesService;
import com.citc.nce.module.vo.req.SignModuleReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.vo.req.SignModuleUpdateReq;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("signModuleService")
public class SignModuleServiceImpl implements SignModuleService {

    @Resource
    private SignModuleDao signModuleDao;

    @Resource
    @Lazy
    private SignNamesService signNamesService;

    @Override
    public int saveSignModule(SignModuleReq req) {
        SignModuleDo signModuleDo = new SignModuleDo();
        checkTime(req.getSignTimeType(), req.getSignStartTime(), req.getSignEndTime());
        BeanUtil.copyProperties(req,signModuleDo);
        String uuid = UUID.randomUUID().toString();
        signModuleDo.setDeleted(0);
        signModuleDo.setSignModuleId(uuid);
        return signModuleDao.insert(signModuleDo);
    }

    private void checkTime(String signTimeType, String signStartTime, String signEndTime) {
        if(!Strings.isNullOrEmpty(signTimeType)){
            List<String> dayList = Arrays.asList("DAY","FIRST","SECOND","THIRD","FOURTH","FIFTH","SIXTH","SEVENTH","CUSTOM");
            if(!dayList.contains(signTimeType)){
                throw new BizException(ModuleError.SIGN_TIME_TYPE_ERROR);
            }
            if("CUSTOM".equals(signTimeType)){
                Date startDate = DateUtils.obtainDate(signStartTime,"yyyy-MM-dd HH:mm");
                Date endDate = DateUtils.obtainDate(signEndTime,"yyyy-MM-dd HH:mm");
                if(startDate == null || endDate == null || startDate.after(endDate)){
                    throw new BizException(ModuleError.SIGN_TIME_ERROR);
                }
                if(startDate.before(new Date())){
                    throw new BizException(ModuleError.SIGN_TIME_START_ERROR);
                }

            }
        }

    }

    @Override
    public int deleteSignModule(SignModuleReq req) {
        if(req == null || StringUtils.isBlank(req.getSignModuleId())){
            return 0;
        }
        QueryWrapper<SignModuleDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sign_module_id", req.getSignModuleId());
        queryWrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        List<SignModuleDo> signModuleDos = signModuleDao.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(signModuleDos)){
            SignModuleDo signModuleDo = signModuleDos.get(0);
            return signModuleDao.logicDeleteByIds(Arrays.asList(signModuleDo.getId()));
        }else{
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

    }

    @Override
    public int updateSignModule(SignModuleUpdateReq req) {
        checkReq(req);
        QueryWrapper<SignModuleDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sign_module_id", req.getSignModuleId());
        queryWrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        List<SignModuleDo> signModuleDos = signModuleDao.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(signModuleDos)){
            SignModuleDo signModuleDo = new SignModuleDo();
            BeanUtil.copyProperties(req,signModuleDo);
            return signModuleDao.update(signModuleDo,queryWrapper);
        }else{
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
    }

    private void checkReq(SignModuleUpdateReq req) {
        LocalTime startTime = null;
        LocalTime endTime = null;

        checkTime(req.getSignTimeType(), req.getSignStartTime(), req.getSignEndTime());
        try{
            if(!Strings.isNullOrEmpty(req.getSignStartTime()) && !Strings.isNullOrEmpty(req.getSignEndTime())){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                startTime = LocalTime.parse(req.getSignStartTime(), formatter);
                endTime = LocalTime.parse(req.getSignEndTime(), formatter);
            }
        }catch (Exception e){
            throw new BizException(ModuleError.SIGN_TIME_STYLE_ERROR);
        }
        if(startTime != null && startTime.isAfter(endTime)){
            throw new BizException(ModuleError.SIGN_TIME_ERROR);
        }

        if(req.getSignSuccess() != null && req.getSignSuccess() == ModuleStatusEnums.MODULE_IS_PROMPT.getCode() && req.getSignSuccess5gTmpId() == null){
            throw new BizException(ModuleError.SIGN_SUCCESS_TEMP_ID_IS_NULL);
        }
        if(req.getJoinSuccess() != null && req.getJoinSuccess() == ModuleStatusEnums.MODULE_IS_PROMPT.getCode() && req.getJoinSuccess5gTmpId() == null){
            throw new BizException(ModuleError.SIGN_JOIN_TEMP_ID_IS_NULL);
        }
        if(req.getSignTimeWarn() != null && req.getSignTimeWarn() == ModuleStatusEnums.MODULE_IS_PROMPT.getCode() && req.getSignTime5gTmpId() == null){
            throw new BizException(ModuleError.SIGN_TIME_TEMP_ID_IS_NULL);
        }
        if(req.getSignRepeatWarn() != null && req.getSignRepeatWarn() == ModuleStatusEnums.MODULE_IS_PROMPT.getCode() && req.getSignRepeat5gTmpId() == null){
            throw new BizException(ModuleError.SIGN_REPEAT_TEMP_ID_IS_NULL);
        }
    }

    @Override
    public PageResult<SignModuleReq> getSignModuleList(SignModuleReq req) {
        PageResult<SignModuleReq> signModuleResult = new PageResult<>();
        QueryWrapper<SignModuleDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","sign_module_id","name","description","join_success","join_success_5g_tmp_id","sign_success","sign_success_5g_tmp_id","create_time","sign_time_warn","sign_repeat_warn","sign_time_5g_tmp_id","sign_repeat_5g_tmp_id","sign_time_type","sign_start_time","sign_end_time");
        if(StringUtils.isNotBlank(req.getName())){
            queryWrapper.like("name", req.getName());
        }
        queryWrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        // 创建时间倒序
        queryWrapper.orderByDesc("create_time");

        PageParam pageParam = new PageParam();
        pageParam.setPageNo(req.getPageNo());
        pageParam.setPageSize(req.getPageSize());
        PageResult<SignModuleDo> result = signModuleDao.selectPage(pageParam,queryWrapper);

        List<SignModuleDo> respList = result.getList();
        List<SignModuleReq> subscribeList = new ArrayList<>();
        for (SignModuleDo resp : respList) {
            SignModuleReq pageResp = new SignModuleReq();
            BeanUtil.copyProperties(resp,pageResp);
            // 打卡人数还需查打卡名单统计
            Long count = signNamesService.getSignNamesCount(resp.getSignModuleId());
            pageResp.setSignCount(count);
            subscribeList.add(pageResp);
        }
        signModuleResult.setList(subscribeList);
        signModuleResult.setTotal(result.getTotal());
        return signModuleResult;
    }

    @Override
    public List<SignModuleReq> getSignModules() {
        LambdaQueryWrapper<SignModuleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignModuleDo::getCreator, SessionContextUtil.getUser().getUserId()).orderByDesc(SignModuleDo::getCreateTime);
        List<SignModuleDo> result = signModuleDao.selectList(queryWrapper);
        List<SignModuleReq> signModuleList = new ArrayList<>();
        for (SignModuleDo resp : result) {
            SignModuleReq pageResp = new SignModuleReq();
            BeanUtil.copyProperties(resp,pageResp);
            signModuleList.add(pageResp);
        }
        return signModuleList;
    }

    @Override
    public SignModuleReq getSignModule(SignModuleReq req) {
        if(req == null || StringUtils.isBlank(req.getSignModuleId())){
            throw new BizException(200,"SignModuleId为空！");
        }
        QueryWrapper<SignModuleDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sign_module_id", req.getSignModuleId());
        queryWrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        SignModuleReq pageResp = new SignModuleReq();
        SignModuleDo result = signModuleDao.selectOne(queryWrapper);
        if(result == null){
            return pageResp;
        }
        BeanUtil.copyProperties(result,pageResp);
        return pageResp;
    }

    @Override
    public SignModuleDo getSignModuleById(String signModuleId) {
        if(!Strings.isNullOrEmpty(signModuleId)){
            LambdaQueryWrapper<SignModuleDo> signModuleDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            signModuleDoLambdaQueryWrapper.eq(SignModuleDo::getSignModuleId,signModuleId);
            signModuleDoLambdaQueryWrapper.eq(SignModuleDo::getDeleted,0);
            List<SignModuleDo> signModuleDoList = signModuleDao.selectList(signModuleDoLambdaQueryWrapper);
            if(CollectionUtils.isNotEmpty(signModuleDoList)){
                return signModuleDoList.get(0);
            }
        }
        return null;
    }
}
