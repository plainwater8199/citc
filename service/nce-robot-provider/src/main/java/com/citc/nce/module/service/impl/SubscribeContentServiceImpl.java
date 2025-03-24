package com.citc.nce.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.dao.SubscribeContentDao;
import com.citc.nce.module.dto.SendContentForSubscribeDto;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.SubscribeContentService;
import com.citc.nce.module.vo.SubscribeContentInfo;
import com.citc.nce.module.vo.req.SubscribeContentDeleteReq;
import com.citc.nce.module.vo.req.SubscribeContentQueryListReq;
import com.citc.nce.module.vo.req.SubscribeContentSaveReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("subscribeContentService")
public class SubscribeContentServiceImpl implements SubscribeContentService {

    @Resource
    private SubscribeContentDao subscribeContentDao;


    /**
     * 订阅内容保存
     *
     * @param req 订阅信息
     * @return 响应信息
     */
    @Override
    public String saveSubscribeContent(SubscribeContentSaveReq req) {
        SubscribeContentDo subscribeContentDo = new SubscribeContentDo();
        BeanUtil.copyProperties(req, subscribeContentDo);
        subscribeContentDo.setCreator(SessionContextUtil.getUser().getUserId());
        subscribeContentDo.setCreateTime(new Date());
        subscribeContentDo.setSubContentId(UUID.randomUUID().toString());
        //查询该组件下的所有订阅内容
        LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SubscribeContentDo::getSubscribeId, req.getSubscribeId());
        queryWrapper.eq(SubscribeContentDo::getDeleted, 0);
        List<SubscribeContentDo> subscribeContentDos = subscribeContentDao.selectList(queryWrapper);
        Integer maxOrder = 0;
        if (!CollUtil.isEmpty(subscribeContentDos)) {
            List<SubscribeContentDo> updateList = new ArrayList<>();
            for (SubscribeContentDo item : subscribeContentDos) {
                maxOrder = (maxOrder > item.getSubscribeContentOrder()) ? maxOrder : item.getSubscribeContentOrder();
                if (item.getIsTheLast() == ModuleStatusEnums.SUB_CONTENT_IS_THE_LAST.getCode()) {
                    item.setIsTheLast(0);//非最后一个
                    updateList.add(item);
                }
            }
            subscribeContentDao.updateBatch(updateList);
        }
        subscribeContentDo.setIsTheLast(ModuleStatusEnums.SUB_CONTENT_IS_THE_LAST.getCode());
        subscribeContentDo.setSubscribeContentOrder(maxOrder + 1);
        subscribeContentDo.setSubscribeContentStatus(ModuleStatusEnums.SUB_CONTENT_STATUS_TO_BE_SEND.getCode());

        subscribeContentDao.insert(subscribeContentDo);
        return subscribeContentDo.getSubContentId();
    }

    @Override
    public int deleteSubscribeContent(SubscribeContentDeleteReq req) {
        if (req == null || StringUtils.isBlank(req.getSubContentId())) {
            return 0;
        }
        //删除指定订阅内容
        LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeContentDo::getCreator, SessionContextUtil.getUser().getUserId());
        queryWrapper.eq(SubscribeContentDo::getSubContentId, req.getSubContentId());
        queryWrapper.eq(SubscribeContentDo::getDeleted, 0);
        List<SubscribeContentDo> subscribeContentDos = subscribeContentDao.selectList(queryWrapper);
        if (!CollUtil.isEmpty(subscribeContentDos)) {
            SubscribeContentDo subscribeContentDo = subscribeContentDos.get(0);
            subscribeContentDo.setDeleted(1);
            subscribeContentDo.setDeletedTime(new Date());
            subscribeContentDo.setIsTheLast(0);
            subscribeContentDo.setSubscribeContentOrder(-1);
            subscribeContentDao.updateById(subscribeContentDo);
            subscribeContentDao.deleteById(subscribeContentDo);
            //重新排序，并且获取最后的发布内容
            LambdaQueryWrapper<SubscribeContentDo> queryNewWrapper = new LambdaQueryWrapper<>();
            queryNewWrapper.eq(SubscribeContentDo::getCreator, SessionContextUtil.getUser().getUserId());
            queryNewWrapper.eq(SubscribeContentDo::getSubscribeId, subscribeContentDo.getSubscribeId());
            queryNewWrapper.eq(SubscribeContentDo::getDeleted, 0);
            queryNewWrapper.isNull(SubscribeContentDo::getDeletedTime);
            queryNewWrapper.orderByAsc(SubscribeContentDo::getSubscribeContentOrder);
            List<SubscribeContentDo> subscribeContentNewDos = subscribeContentDao.selectList(queryNewWrapper);
            if (!CollUtil.isEmpty(subscribeContentNewDos)) {
                int size = subscribeContentNewDos.size();
                List<SubscribeContentDo> updateList = new ArrayList<>();
                SubscribeContentDo updateItem;
                for (int i = 0; i < size; i++) {
                    updateItem = subscribeContentNewDos.get(i);
                    updateItem.setSubscribeContentOrder(i + 1);
                    if (i + 1 == size) {
                        updateItem.setIsTheLast(ModuleStatusEnums.SUB_CONTENT_IS_THE_LAST.getCode());
                    } else {
                        updateItem.setIsTheLast(0);
                    }
                    updateList.add(updateItem);
                }
                subscribeContentDao.updateBatch(updateList);
            }

        } else {
            throw new BizException("订阅组件内容不存在！");
        }
        return 1;
    }

    @Override
    public int updateSubscribeContent(SubscribeContentSaveReq req) {
        if (req == null || StringUtils.isBlank(req.getSubContentId())) {
            return 0;
        }
        LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeContentDo::getSubContentId, req.getSubContentId());
        queryWrapper.eq(SubscribeContentDo::getCreator, SessionContextUtil.getUser().getUserId());
        queryWrapper.eq(SubscribeContentDo::getDeleted, 0);
        List<SubscribeContentDo> subscribeContentDos = subscribeContentDao.selectList(queryWrapper);
        if (!CollUtil.isEmpty(subscribeContentDos)) {
            SubscribeContentDo subscribeContentDo = subscribeContentDos.get(0);
            subscribeContentDo.setUpdater(SessionContextUtil.getUser().getUserId());
            subscribeContentDo.setUpdateTime(new Date());
            BeanUtil.copyProperties(req, subscribeContentDo);
            return subscribeContentDao.update(subscribeContentDo, queryWrapper);
        } else {
            throw new BizException("订阅组件内容不存在！");
        }

    }


    @Override
    public PageResult<SubscribeContentInfo> getSubscribeContentList(SubscribeContentQueryListReq req) {
        PageResult<SubscribeContentInfo> response = new PageResult<>();
        LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscribeContentDo::getSubscribeId, req.getSubscribeId());
        queryWrapper.eq(SubscribeContentDo::getCreator, SessionContextUtil.getUser().getUserId());
        queryWrapper.eq(SubscribeContentDo::getDeleted, 0);
        queryWrapper.orderByAsc(SubscribeContentDo::getSubscribeContentOrder);//排序
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(req.getPageNo());
        pageParam.setPageSize(req.getPageSize());
        PageResult<SubscribeContentDo> pageResult = subscribeContentDao.selectPage(pageParam, queryWrapper);
        List<SubscribeContentDo> subscribeContentDos = pageResult.getList();
        if (!CollUtil.isEmpty(subscribeContentDos)) {
            List<SubscribeContentInfo> subscribeModuleInfos = new ArrayList<>();
            SubscribeContentInfo subscribeContentInfo;
            for (SubscribeContentDo item : subscribeContentDos) {
                subscribeContentInfo = new SubscribeContentInfo();
                BeanUtil.copyProperties(item, subscribeContentInfo);
                subscribeModuleInfos.add(subscribeContentInfo);
            }
            response.setList(subscribeModuleInfos);
        }
        response.setTotal(pageResult.getTotal());
        return response;
    }

    /**
     * 查询指定的订阅组件的最新要发送的订阅内容
     *
     * @param subscribeIds 订阅主键id列表
     * @return 需要发送的订阅内容
     */
    @Override
    public List<SendContentForSubscribeDto> getSendContentForSubscribeIds(List<String> subscribeIds) {
        List<SendContentForSubscribeDto> sendContentForSubscribeDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(subscribeIds)) {
            LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SubscribeContentDo::getSubscribeId, subscribeIds);
            List<SubscribeContentDo> subscribeContentDos = subscribeContentDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(subscribeContentDos)) {
                Map<String, Optional<SubscribeContentDo>> collect = subscribeContentDos.stream().collect(Collectors.groupingBy(SubscribeContentDo::getSubscribeId, Collectors.minBy(Comparator.comparingInt(SubscribeContentDo::getSubscribeContentOrder))));
                List<SubscribeContentDo> subscribeContentForSendList = collect.values().stream().map(Optional::get).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(subscribeContentForSendList)) {
                    SendContentForSubscribeDto sendContentForSubscribeDto;
                    for (SubscribeContentDo item : subscribeContentForSendList) {
                        sendContentForSubscribeDto = new SendContentForSubscribeDto();
                        sendContentForSubscribeDto.setMsg5GId(item.getMsg5gId());
                        sendContentForSubscribeDto.setSubContentId(item.getSubContentId());
                        sendContentForSubscribeDto.setSubscribeId(item.getSubscribeId());
                        sendContentForSubscribeDto.setTitle(item.getTitle());
                        sendContentForSubscribeDto.setIsTheLast(item.getIsTheLast());
                        sendContentForSubscribeDtoList.add(sendContentForSubscribeDto);
                    }
                }
            }
        }

        return sendContentForSubscribeDtoList;
    }

    @Override
    public SubscribeContentDo getSubscribeContentInfoById(String subscribeContentId) {
        if (Strings.isNotBlank(subscribeContentId)) {
            LambdaQueryWrapper<SubscribeContentDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubscribeContentDo::getSubContentId, subscribeContentId);
            List<SubscribeContentDo> subscribeContentDos = subscribeContentDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(subscribeContentDos)) {
                return subscribeContentDos.get(0);
            }
        }
        return null;
    }

    /**
     * 更改订阅内容的状态
     *
     * @param subscribeContentDo     订阅内容信息
     * @param subscribeContentStatus 订阅状态
     */
    @Override
    public void updateSubscribeContentStatus(SubscribeContentDo subscribeContentDo, Integer subscribeContentStatus) {
        if (subscribeContentStatus != null) {
            subscribeContentDo.setSubscribeContentStatus(subscribeContentStatus);
            subscribeContentDao.updateById(subscribeContentDo);
        }
    }

    @Override
    public SubscribeContentDo getSubContentInfoById(String subscribeContentId) {
        return subscribeContentDao.querySubScribeContent(subscribeContentId);
    }

    @Override
    public List<SubscribeContentDo> findListById(String subscribeId) {
        return subscribeContentDao.findListById(subscribeId);
    }
}
