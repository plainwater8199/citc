package com.citc.nce.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.module.constant.ModuleError;
import com.citc.nce.module.dao.SubscribeContentDao;
import com.citc.nce.module.dao.SubscribeModuleDao;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.module.entity.SubscribeModuleDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.ModuleService;
import com.citc.nce.module.service.SubscribeModuleService;
import com.citc.nce.module.service.SubscribeNamesService;
import com.citc.nce.module.vo.SubscribeModuleInfo;
import com.citc.nce.module.vo.req.SubscribeModuleReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.vo.req.SubscribeModuleSaveReq;
import com.citc.nce.module.vo.resp.SubscribeModuleQueryResp;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("subscribeModuleService")
public class SubscribeModuleServiceImpl implements SubscribeModuleService {

    @Resource
    private SubscribeModuleDao subscribeModuleDao;

    @Resource
    private SubscribeNamesService subscribeNamesService;

    @Resource
    private SubscribeContentDao subscribeContentDao;

    @Resource
    private ModuleService moduleService;

    @Override
    public int saveSubscribeModule(SubscribeModuleSaveReq req) {
        SubscribeModuleDo subscribeModuleDo = new SubscribeModuleDo();
        BeanUtil.copyProperties(req,subscribeModuleDo);
        String uuid = UUID.randomUUID().toString();
        subscribeModuleDo.setSubscribeId(uuid);
        subscribeModuleDo.setCreator(SessionContextUtil.getUser().getUserId());
        subscribeModuleDo.setDeleted(0);
        subscribeModuleDo.setSubscribeStatus(ModuleStatusEnums.SUB_MODULE_STATUS_TO_BE_SEND.getCode());//默认为待发送
        //更新今天待发送的订阅组件
        moduleService.sendSubscribeToMQ();
        return subscribeModuleDao.insert(subscribeModuleDo);
    }

    @Override
    public int deleteSubscribeModule(SubscribeModuleReq req) {
        if(req == null || StringUtils.isBlank(req.getSubscribeId())){
            return 0;
        }
        LambdaQueryWrapper<SubscribeModuleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeModuleDo::getSubscribeId, req.getSubscribeId());
        queryWrapper.eq(SubscribeModuleDo::getCreator, SessionContextUtil.getUser().getUserId());
        List<SubscribeModuleDo> signModuleDos = subscribeModuleDao.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(signModuleDos)){
            SubscribeModuleDo subscribeModuleDo = signModuleDos.get(0);
            return subscribeModuleDao.logicDeleteByIds(Arrays.asList(subscribeModuleDo.getId()));
        }else{
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

    }

    @Override
    public int updateSubscribeModule(SubscribeModuleSaveReq req) {
        if(!StringUtils.isBlank(req.getSubscribeId())){
            LambdaQueryWrapper<SubscribeModuleDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeModuleDo::getSubscribeId, req.getSubscribeId());
            queryWrapper.eq(SubscribeModuleDo::getCreator, SessionContextUtil.getUser().getUserId());
            List<SubscribeModuleDo> signModuleDos = subscribeModuleDao.selectList(queryWrapper);
            if(CollectionUtils.isNotEmpty(signModuleDos)){
                SubscribeModuleDo subscribeModuleDo = signModuleDos.get(0);
                subscribeModuleDo.setName(req.getName());
                subscribeModuleDo.setDescription(req.getDescription());
                subscribeModuleDo.setSendType(req.getSendType());
                subscribeModuleDo.setSendTime(req.getSendTime());
                subscribeModuleDo.setSubsSuccess(req.getSubsSuccess());
                subscribeModuleDo.setSuccess5gMsgId(getMsgTemplateIdByType(req.getSubsSuccess(),req.getSuccess5gMsgId()));
                subscribeModuleDo.setSubsCancel(req.getSubsCancel());
                subscribeModuleDo.setCancel5gMsgId(getMsgTemplateIdByType(req.getSubsCancel(),req.getCancel5gMsgId()));
                subscribeModuleDo.setSubsEnd(req.getSubsEnd());
                subscribeModuleDo.setEnd5gMsgId(getMsgTemplateIdByType(req.getSubsEnd(),req.getEnd5gMsgId()));
                int result = subscribeModuleDao.updateById(subscribeModuleDo);
                //更新今天待发送的订阅组件
                moduleService.sendSubscribeToMQ();
                return result;

            }else{
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        }else{
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 根据类型获取类型模板id
     * @param isSelf 是否是自定义
     * @param msgTemplateIdForSelf  自定义模板的ID
     * @return 消息模板id
     */
    private Long getMsgTemplateIdByType(Integer isSelf, Long msgTemplateIdForSelf) {
        if(isSelf == ModuleStatusEnums.MODULE_IS_PROMPT.getCode()){
            if(msgTemplateIdForSelf != null){
                return msgTemplateIdForSelf;
            }else{
                throw new BizException(ModuleError.SUBSCRIBE_TEMP_ID_IS_NULL);
            }
        }else{
            return msgTemplateIdForSelf;
        }
    }


    @Override
    public PageResult<SubscribeModuleInfo> getSubscribeModuleList(SubscribeModuleReq req) {
        PageResult<SubscribeModuleInfo> queryResponse = new PageResult<>();
        LambdaQueryWrapper<SubscribeModuleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeModuleDo::getCreator, SessionContextUtil.getUser().getUserId());
        if(StringUtils.isNotBlank(req.getName())){
            queryWrapper.like(SubscribeModuleDo::getName, req.getName());
        }
        // 创建时间倒序
        queryWrapper.orderByDesc(SubscribeModuleDo::getCreateTime);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(req.getPageNo());
        pageParam.setPageSize(req.getPageSize());
        PageResult<SubscribeModuleDo> result = subscribeModuleDao.selectPage(pageParam,queryWrapper);
        List<SubscribeModuleDo> queryDBList = result.getList();
        if(CollectionUtils.isNotEmpty(queryDBList)){
            List<SubscribeModuleInfo> subscribeList = new ArrayList<>();
            for (SubscribeModuleDo item : queryDBList) {
                SubscribeModuleInfo subscribeModuleInfo = new SubscribeModuleInfo();
                BeanUtil.copyProperties(item,subscribeModuleInfo);
                // 订阅量还需查订阅名单统计
                Long count = subscribeNamesService.getSubscribeNamesCount(item.getSubscribeId());
                subscribeModuleInfo.setSubscribeCount(count);
                subscribeList.add(subscribeModuleInfo);
            }
            queryResponse.setList(subscribeList);
        }
        queryResponse.setTotal(result.getTotal());
        return queryResponse;
    }

    /**
     * 查询订阅组件详情
     * @param req 请求信息
     * @return 组件信息
     */
    @Override
    public SubscribeModuleQueryResp getSubscribeModule(SubscribeModuleReq req) {
        SubscribeModuleQueryResp resp = new SubscribeModuleQueryResp();
        LambdaQueryWrapper<SubscribeModuleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeModuleDo::getCreator, SessionContextUtil.getUser().getUserId());
        if(StringUtils.isNotBlank(req.getSubscribeId())){
            queryWrapper.eq(SubscribeModuleDo::getSubscribeId, req.getSubscribeId());
        }
        if(StringUtils.isNotBlank(req.getName())){
            queryWrapper.eq(SubscribeModuleDo::getName, req.getName());
        }
        SubscribeModuleDo result = subscribeModuleDao.selectOne(queryWrapper);
        if(result != null){
            BeanUtil.copyProperties(result,resp);
        }
        return resp;

    }

    @Override
    public List<SubscribeModuleReq> getSubscribeModules() {
        List<SubscribeModuleReq> subscribeList = new ArrayList<>();
        LambdaQueryWrapper<SubscribeModuleDo> subscribeModuleDoQueryWrapper = new LambdaQueryWrapper<>();
        subscribeModuleDoQueryWrapper.eq(SubscribeModuleDo::getCreator, SessionContextUtil.getUser().getUserId()).orderByDesc(SubscribeModuleDo::getCreateTime);
        List<SubscribeModuleDo> list = subscribeModuleDao.selectList(subscribeModuleDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            for (SubscribeModuleDo resp : list) {
                LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SubscribeContentDo::getSubscribeId, resp.getSubscribeId());
                List<SubscribeContentDo> contentDos = subscribeContentDao.selectList(queryWrapper);
                if(CollectionUtils.isNotEmpty(contentDos)){
                    SubscribeModuleReq pageResp = new SubscribeModuleReq();
                    BeanUtil.copyProperties(resp,pageResp);
                    subscribeList.add(pageResp);
                }
            }
        }
        return subscribeList;
    }

    @Override
    public SubscribeModuleDo getSubscribeModuleInfo(String subscribeId) {
        LambdaQueryWrapper<SubscribeModuleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeModuleDo::getSubscribeId, subscribeId);
        return subscribeModuleDao.selectOne(queryWrapper);
    }

    /**
     * 查询在指定某一天需要发送的订阅内容
     * @param date 时间
     * @return 需要发送的订阅内容
     */
    @Override
    public List<SubscribeModuleDo> getSendSubscribeForToday(Date date) {
        //获取今天星期几
        String weekDay = moduleService.getNowDay(date);

        //查询今天需要发送的订阅内容
        LambdaQueryWrapper<SubscribeModuleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(SubscribeModuleDo::getSendTime);//发送时间不能为空
        queryWrapper.in(SubscribeModuleDo::getSendType, Strings.isNullOrEmpty(weekDay) ? Collections.singletonList("DAY") : Arrays.asList("DAY",weekDay));
        List<SubscribeModuleDo> subscribeDos = subscribeModuleDao.selectList(queryWrapper);
        if(!org.springframework.util.CollectionUtils.isEmpty(subscribeDos)){
            return subscribeDos;
        }
        return Collections.emptyList();

    }

    /**
     * 更新模板状态
     * @param subscribeModuleInfo 组件信息
     * @param subscribeModuleStatus 组件状态
     */
    @Override
    public void updateSubscribeModuleStatus(SubscribeModuleDo subscribeModuleInfo, Integer subscribeModuleStatus) {
        if(subscribeModuleStatus != null){
            subscribeModuleInfo.setSubscribeStatus(subscribeModuleStatus);
            subscribeModuleDao.updateById(subscribeModuleInfo);
        }
    }

    /**
     * 更新订阅组件状态
     * @param subscribeId 组件ID
     * @param subscribeModuleStatus 组件状态
     */
    @Override
    public void updateSubscribeModuleStatusById(String subscribeId, int subscribeModuleStatus) {
        if(!Strings.isNullOrEmpty(subscribeId)){
            SubscribeModuleDo subscribeModuleDo = subscribeModuleDao.selectById(subscribeId);
            if(subscribeModuleDo != null){
                updateSubscribeModuleStatus(subscribeModuleDo,subscribeModuleStatus);
            }
        }
    }

}
