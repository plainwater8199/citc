package com.citc.nce.aim.privatenumber.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.constant.AimOrderStatusEnum;
import com.citc.nce.aim.privatenumber.dao.PrivateNumberOrderDao;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberOrderDo;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberProjectDo;
import com.citc.nce.aim.privatenumber.service.PrivateNumberDealRedisCacheService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberOrderService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberProjectService;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderDeleteReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderEditReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderQueryListReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderQueryReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderResp;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderSaveReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderUpdateStatusReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrivateNumberOrderServiceImpl implements PrivateNumberOrderService {

    @Resource
    private PrivateNumberOrderDao privateNumberOrderDao;
    @Lazy
    @Resource
    private PrivateNumberDealRedisCacheService privateNumberDealRedisCacheService;
    @Resource
    private PrivateNumberProjectService privateNumberProjectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(PrivateNumberOrderSaveReq req) {
        // 校验名称
        checkOrderName(req.getOrderName(), req.getProjectId(), null);
        // 插入数据
        PrivateNumberOrderDo insert = new PrivateNumberOrderDo();
        BeanUtils.copyProperties(req, insert);
        insert.setCreator(SessionContextUtil.getUserId());
        insert.setCreateTime(new Date());
        return privateNumberOrderDao.insert(insert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(PrivateNumberOrderEditReq req) {
        // 校验名称
        checkOrderName(req.getOrderName(), req.getProjectId(), req.getId());
        // 校验购买量
        checkOrderAmount(req.getOrderAmount(), req.getId());

        LambdaUpdateWrapper<PrivateNumberOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrivateNumberOrderDo::getId, req.getId());
        updateWrapper.set(PrivateNumberOrderDo::getOrderName, req.getOrderName())
                .set(PrivateNumberOrderDo::getOrderAmount, req.getOrderAmount());
        return privateNumberOrderDao.update(null, updateWrapper);
    }

    @Override
    public int delete(PrivateNumberOrderDeleteReq req) {
        PrivateNumberOrderDo privateNumberOrderDo = privateNumberOrderDao.selectById(req.getId());
        if (privateNumberOrderDo != null) {
            if (AimOrderStatusEnum.ENABLED.getCode() == privateNumberOrderDo.getOrderStatus()) {
                PrivateNumberProjectDo privateNumberProjectDo = privateNumberProjectService.queryByProjectId(privateNumberOrderDo.getProjectId());
                privateNumberDealRedisCacheService.deleteRedisCache(privateNumberProjectDo.getAppKey(), privateNumberOrderDo.getId());
            }
            privateNumberOrderDo.setDeleted(req.getDeleted());
            return privateNumberOrderDao.deleteById(privateNumberOrderDo);
        } else {
            throw new BizException(AimError.ORDER_NOT_EXIST);
        }
    }


    @Override
    public PrivateNumberOrderResp queryOrderById(PrivateNumberOrderQueryReq req) {
        PrivateNumberOrderResp resp = new PrivateNumberOrderResp();
        LambdaQueryWrapperX<PrivateNumberOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (req.getId() > 0L) {
            queryWrapperX.eq(PrivateNumberOrderDo::getId, req.getId());
        }
        if (Strings.isNotEmpty(req.getProjectId())) {
            queryWrapperX.eq(PrivateNumberOrderDo::getProjectId, req.getProjectId());
        }
        List<PrivateNumberOrderDo> privateNumberOrderDos = privateNumberOrderDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(privateNumberOrderDos)) {
            BeanUtils.copyProperties(privateNumberOrderDos.get(0), resp);
        }
        return resp;
    }


    @Override
    public PrivateNumberOrderResp queryEnabledOrderByProjectId(PrivateNumberOrderQueryReq req) {
        return queryEnabledOrderByProjectId(req.getProjectId());
    }

    @Override
    public PrivateNumberOrderResp queryEnabledOrderByProjectId(String projectId) {
        PrivateNumberOrderResp resp = new PrivateNumberOrderResp();
        if (StringUtils.isNotEmpty(projectId)) {
            LambdaQueryWrapperX<PrivateNumberOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(PrivateNumberOrderDo::getProjectId, projectId)
                    .eq(PrivateNumberOrderDo::getOrderStatus, AimOrderStatusEnum.ENABLED.getCode());
            PrivateNumberOrderDo privateNumberOrderDo = privateNumberOrderDao.selectOne(queryWrapperX);
            if (null != privateNumberOrderDo) {
                BeanUtils.copyProperties(privateNumberOrderDo, resp);
            }
        }
        return resp;
    }

    @Override
    public List<PrivateNumberOrderDo> queryOrderListByOrderIds(Set<Long> orderIds) {
        LambdaQueryWrapperX<PrivateNumberOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.in(PrivateNumberOrderDo::getId, orderIds);
        return privateNumberOrderDao.selectList(queryWrapperX);
    }

    @Override
    public void updateBatch(List<PrivateNumberOrderDo> updateList) {
        privateNumberOrderDao.updateBatch(updateList);
    }

    @Override
    public PageResult<PrivateNumberOrderResp> queryOrderList(PrivateNumberOrderQueryListReq req) {
        PageResult<PrivateNumberOrderResp> response = new PageResult<>();
        // 创建分页对象
        Page<PrivateNumberOrderDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapperX<PrivateNumberOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(PrivateNumberOrderDo::getProjectId, req.getProjectId());
        queryWrapperX.orderByDesc(PrivateNumberOrderDo::getCreateTime);

        Page<PrivateNumberOrderDo> aimOrderDoPageResult = privateNumberOrderDao.selectPage(page, queryWrapperX);
        // 将查询结果转换为响应对象
        List<PrivateNumberOrderResp> respList = aimOrderDoPageResult.getRecords().stream()
                .map(record -> {
                    PrivateNumberOrderResp respItem = new PrivateNumberOrderResp();
                    BeanUtils.copyProperties(record, respItem);
                    return respItem;
                })
                .collect(Collectors.toList());

        // 设置分页结果
        response.setList(respList);
        response.setTotal(aimOrderDoPageResult.getTotal());
        return response;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(PrivateNumberOrderUpdateStatusReq req) {
        // 首先要判断是不是把状态更新为 1:已启用
        if (AimOrderStatusEnum.ENABLED.getCode() == req.getOrderStatus()) {
            //查询原来已开启的订单，并删除缓存
            PrivateNumberOrderResp resp = queryEnabledOrderByProjectId(req.getProjectId());
            privateNumberDealRedisCacheService.deleteRedisCache(req.getProjectId(), resp.getId());
            // 已启用状态下，要关闭同一个项目下已启用的订单
            LambdaUpdateWrapper<PrivateNumberOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(PrivateNumberOrderDo::getOrderStatus, AimOrderStatusEnum.CLOSED.getCode());
            updateWrapper.eq(PrivateNumberOrderDo::getProjectId, req.getProjectId())
                    .eq(PrivateNumberOrderDo::getOrderStatus, AimOrderStatusEnum.ENABLED.getCode());
            privateNumberOrderDao.update(null, updateWrapper);
        }

        PrivateNumberProjectDo privateNumberProjectDo = privateNumberProjectService.queryByProjectId(req.getProjectId());
        if (AimOrderStatusEnum.CLOSED.getCode() == req.getOrderStatus()) {
            //查询原来已开启的订单，并删除缓存
            PrivateNumberOrderResp resp = queryEnabledOrderByProjectId(req.getProjectId());
            log.info("欲关闭订单: 原生效订单:{}", resp);
            if (resp.getId() == req.getId()) {
                log.info("删除原订单缓存");
                privateNumberDealRedisCacheService.deleteRedisCache(privateNumberProjectDo.getAppKey(), resp.getId());
            }
        }
        // 更新状态
        LambdaUpdateWrapper<PrivateNumberOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PrivateNumberOrderDo::getOrderStatus, req.getOrderStatus());
        updateWrapper.eq(PrivateNumberOrderDo::getId, req.getId());
        privateNumberOrderDao.update(null, updateWrapper);
        privateNumberDealRedisCacheService.updateProjectRedisCache(privateNumberProjectDo);
        return 0;
    }

    private void checkOrderAmount(long orderAmount, Long id) {
        LambdaQueryWrapperX<PrivateNumberOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(PrivateNumberOrderDo::getId, id)
                .ge(PrivateNumberOrderDo::getOrderConsumption, orderAmount);
        List<PrivateNumberOrderDo> aimOrderDoList = privateNumberOrderDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimOrderDoList)) {
            throw new BizException(AimError.ORDER_AMOUNT_ERROR);
        }
    }

    private void checkOrderName(String orderName, String projectId, Long id) {
        LambdaQueryWrapperX<PrivateNumberOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(PrivateNumberOrderDo::getProjectId, projectId)
                .eq(PrivateNumberOrderDo::getOrderName, orderName);
        if (null != id && id > 0L) {
            queryWrapperX.ne(PrivateNumberOrderDo::getId, id);
        }
        List<PrivateNumberOrderDo> aimOrderDoList = privateNumberOrderDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimOrderDoList)) {
            throw new BizException(AimError.ORDER_NAME_DUPLICATE);
        }
    }
}
