package com.citc.nce.aim.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.constant.AimOrderStatusEnum;
import com.citc.nce.aim.dao.AimOrderDao;
import com.citc.nce.aim.entity.AimOrderDo;
import com.citc.nce.aim.service.AimDealRedisCacheService;
import com.citc.nce.aim.service.AimOrderService;
import com.citc.nce.aim.vo.AimOrderDeleteReq;
import com.citc.nce.aim.vo.AimOrderEditReq;
import com.citc.nce.aim.vo.AimOrderQueryListReq;
import com.citc.nce.aim.vo.AimOrderQueryReq;
import com.citc.nce.aim.vo.AimOrderResp;
import com.citc.nce.aim.vo.AimOrderSaveReq;
import com.citc.nce.aim.vo.AimOrderUpdateStatusReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:05
 */
@Service
@Slf4j
public class AimOrderServiceImpl implements AimOrderService {

    @Resource
    private AimOrderDao aimOrderDao;
    @Lazy
    @Resource
    private AimDealRedisCacheService aimDealRedisCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(AimOrderSaveReq req) {
        // 校验名称
        boolean checkOrderName = checkOrderName(req.getOrderName(), req.getProjectId(), null);
        if (!checkOrderName) {
            throw new BizException(AimError.ORDER_NAME_DUPLICATE);
        }
        // 插入数据
        AimOrderDo insert = new AimOrderDo();
        BeanUtils.copyProperties(req, insert);

        aimOrderDao.insert(insert);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(AimOrderEditReq req) {
        // 校验名称
        boolean checkOrderName = checkOrderName(req.getOrderName(), req.getProjectId(), req.getId());
        if (!checkOrderName) {
            throw new BizException(AimError.ORDER_NAME_DUPLICATE);
        }
        // 校验购买量
        boolean checkOrderAmount = checkOrderAmount(req.getOrderAmount(), req.getId());
        if (!checkOrderAmount) {
            throw new BizException(AimError.ORDER_AMOUNT_ERROR);
        }
        LambdaUpdateWrapper<AimOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AimOrderDo::getId, req.getId());
        updateWrapper.set(AimOrderDo::getOrderName, req.getOrderName())
                        .set(AimOrderDo::getOrderAmount, req.getOrderAmount());
        aimOrderDao.update(null, updateWrapper);
        return 0;
    }

    @Override
    public int delete(AimOrderDeleteReq req) {
        LambdaUpdateWrapper<AimOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AimOrderDo::getDeleted, req.getDeleted());
        updateWrapper.eq(AimOrderDo::getId, req.getId());
        aimOrderDao.update(null, updateWrapper);
        AimOrderQueryReq queryReq = new AimOrderQueryReq();
        queryReq.setId(req.getId());
        AimOrderResp aimOrderResp = queryOrderById(queryReq);
        if ( AimOrderStatusEnum.ENABLED.getCode() == aimOrderResp.getOrderStatus()) {
            aimDealRedisCacheService.deleteRedisCache(aimOrderResp.getProjectId());
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrderConsumption(long orderId, long Consumption) {
        AimOrderQueryReq req = new AimOrderQueryReq();
        req.setId(orderId);
        AimOrderResp aimOrderResp = queryOrderById(req);
        LambdaUpdateWrapper<AimOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AimOrderDo::getOrderConsumption, Consumption);
        updateWrapper.eq(AimOrderDo::getId, orderId);
        aimOrderDao.update(null, updateWrapper);
        // 如果消耗量 >= 购买量 则把订单变为已完成
        if (Consumption >= aimOrderResp.getOrderAmount()) {
            aimDealRedisCacheService.deleteRedisCache(aimOrderResp.getProjectId());
            AimOrderUpdateStatusReq updateStatusReq = new AimOrderUpdateStatusReq();
            updateStatusReq.setId(aimOrderResp.getId());
            updateStatusReq.setOrderStatus(AimOrderStatusEnum.COMPLETED.getCode());
            updateStatus(updateStatusReq);
        }
        return 0;
    }

    @Override
    public AimOrderResp queryOrderById(AimOrderQueryReq req) {
        if (0 != req.getId()) {
            LambdaQueryWrapperX<AimOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(AimOrderDo::getId, req.getId());
            AimOrderDo aimOrderDo = aimOrderDao.selectOne(queryWrapperX);
            if (null != aimOrderDo) {
                AimOrderResp resp = new AimOrderResp();
                BeanUtils.copyProperties(aimOrderDo, resp);
                return resp;
            }
        }
        return null;
    }

    @Override
    public List<AimOrderResp> queryOrderByIdList(List<Long> orderIdList) {
        if (CollectionUtil.isNotEmpty(orderIdList)) {
            LambdaQueryWrapperX<AimOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(AimOrderDo::getId, orderIdList);
            List<AimOrderDo> aimOrderDoList = aimOrderDao.selectList(queryWrapperX);
            if (CollectionUtil.isNotEmpty(aimOrderDoList)) {
                List<AimOrderResp> respList = BeanUtil.copyToList(aimOrderDoList, AimOrderResp.class);
                return respList;
            }
        }
        return null;
    }

    @Override
    public AimOrderResp queryEnabledOrderByProjectId(AimOrderQueryReq req) {
        if (StringUtils.isNotEmpty(req.getProjectId())) {
            LambdaQueryWrapperX<AimOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(AimOrderDo::getProjectId, req.getProjectId())
                    .eq(AimOrderDo::getOrderStatus, AimOrderStatusEnum.ENABLED.getCode());
            AimOrderDo aimOrderDo = aimOrderDao.selectOne(queryWrapperX);
            if (null != aimOrderDo) {
                AimOrderResp resp = new AimOrderResp();
                BeanUtils.copyProperties(aimOrderDo, resp);
                return resp;
            }
        }
        return null;
    }

    @Override
    public PageResult<AimOrderResp> queryOrderList(AimOrderQueryListReq req) {
        if (null == req.getPageSize()) {
            req.setPageSize(20);
        }
        if (null == req.getPageNo()) {
            req.setPageNo(1);
        }
        LambdaQueryWrapperX<AimOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AimOrderDo::getProjectId, req.getProjectId());
        queryWrapperX.orderByDesc(AimOrderDo::getCreateTime);
        PageParam pageParam = new PageParam();
        BeanUtils.copyProperties(req, pageParam);
        PageResult<AimOrderDo> aimOrderDoPageResult = aimOrderDao.selectPage(pageParam, queryWrapperX);
//        List<AimOrderDo> aimOrderDoList = aimOrderDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimOrderDoPageResult.getList())) {
            List<AimOrderResp> orderResps = BeanUtil.copyToList(aimOrderDoPageResult.getList(), AimOrderResp.class);
            return new PageResult(orderResps, aimOrderDoPageResult.getTotal());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(AimOrderUpdateStatusReq req) {
        // 首先要判断是不是把状态更新为 1:已启用
        if (AimOrderStatusEnum.ENABLED.getCode() == req.getOrderStatus()) {
            // 已启用状态下，要关闭同一个项目下已启用的订单
            LambdaUpdateWrapper<AimOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(AimOrderDo::getOrderStatus, AimOrderStatusEnum.CLOSED.getCode());
            updateWrapper.eq(AimOrderDo::getProjectId, req.getProjectId())
                    .eq(AimOrderDo::getOrderStatus, AimOrderStatusEnum.ENABLED.getCode());
            aimOrderDao.update(null, updateWrapper);
        }
        // 更新状态
        LambdaUpdateWrapper<AimOrderDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AimOrderDo::getOrderStatus, req.getOrderStatus());
        updateWrapper.eq(AimOrderDo::getId, req.getId());

        // 如果状态改为已完成，则把消耗量使用完
//        if (AimOrderStatusEnum.COMPLETED.getCode() == req.getOrderStatus()) {
//            AimOrderQueryReq queryReq = new AimOrderQueryReq();
//            queryReq.setId(req.getId());
//            AimOrderResp aimOrderResp = queryOrderById(queryReq);
//            if (null != aimOrderResp) {
//                updateWrapper.set(AimOrderDo::getOrderConsumption, aimOrderResp.getOrderAmount());
//            }
//        }
        aimOrderDao.update(null, updateWrapper);
        aimDealRedisCacheService.processRedisCache(req.getId());
        return 0;
    }

    private boolean checkOrderAmount(long orderAmount, Long id) {
        LambdaQueryWrapperX<AimOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AimOrderDo::getId, id)
                .ge(AimOrderDo::getOrderConsumption, orderAmount);
        List<AimOrderDo> aimOrderDoList = aimOrderDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimOrderDoList)) {
            return false;
        }
        return true;
    }

    private boolean checkOrderName(String orderName, String projectId, Long id) {
        LambdaQueryWrapperX<AimOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AimOrderDo::getProjectId, projectId)
                .eq(AimOrderDo::getOrderName, orderName);
        if (null != id && id > 0L) {
            queryWrapperX.ne(AimOrderDo::getId, id);
        }
        List<AimOrderDo> aimOrderDoList = aimOrderDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimOrderDoList)) {
            return false;
        }
        return true;
    }
}
