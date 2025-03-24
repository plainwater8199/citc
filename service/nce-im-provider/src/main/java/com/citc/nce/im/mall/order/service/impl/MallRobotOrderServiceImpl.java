package com.citc.nce.im.mall.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.im.mall.order.entity.MallRobotOrderDo;
import com.citc.nce.im.mall.order.mapper.MallRobotOrderDao;
import com.citc.nce.im.mall.order.service.MallRobotOrderService;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderSaveReq;
import com.citc.nce.robot.api.mall.order.req.MallRobotOrderUpdateReq;
import com.citc.nce.robot.api.mall.order.resp.RobotOrderResp;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class MallRobotOrderServiceImpl implements MallRobotOrderService {

    @Resource
    MallRobotOrderDao dao;

    /**
     * 根据name 查询记录
     */
    public List<MallRobotOrderDo> listByName(String name, String userId, Long id) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("order_name", name);
        if (StringUtils.isNotEmpty(userId)) {
            wrapper.eq("creator", userId);
        }
        if (null != id) {
            wrapper.ne("id", id);
        }
        List<MallRobotOrderDo> list = dao.selectList(wrapper);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(MallRobotOrderSaveReq req) {
        List<MallRobotOrderDo> listByName = listByName(req.getOrderName(), SessionContextUtil.getUser().getUserId(), null);
        if (checkName(listByName)) {
            // 名称已经存在
            throw new BizException(MallError.ORDER_NAME_DUPLICATE);
        }
        MallRobotOrderDo mallRobotOrderDo = new MallRobotOrderDo();
        BeanUtil.copyProperties(req, mallRobotOrderDo);
        dao.insert(mallRobotOrderDo);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(MallRobotOrderUpdateReq req) {
        List<MallRobotOrderDo> MallRobotOrderDos = listByName(req.getOrderName(), SessionContextUtil.getUser().getUserId(), req.getId());
        if (checkName(MallRobotOrderDos)) {
            // 名称已经存在
            throw new BizException(MallError.ORDER_NAME_DUPLICATE);
        }
        MallRobotOrderDo orderDo = dao.selectById(req.getId());
        if (Objects.isNull(orderDo)) {
            throw new BizException("指令不存在");
        }
        SessionContextUtil.sameCsp(orderDo.getCreator());
        //属性映射
        MallRobotOrderDo order = new MallRobotOrderDo();
        BeanUtil.copyProperties(req, order);
        UpdateWrapper<MallRobotOrderDo> wrapper = new UpdateWrapper<MallRobotOrderDo>();
        wrapper.eq("id", req.getId());
        dao.update(order, wrapper);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        MallRobotOrderDo orderDo = dao.selectById(id);
        if (Objects.isNull(orderDo)) return 0;
        SessionContextUtil.sameCsp(orderDo.getCreator());
        LambdaUpdateWrapper<MallRobotOrderDo> update = new LambdaUpdateWrapper<>();
        update.eq(MallRobotOrderDo::getId, id);
        update.set(MallRobotOrderDo::getDeleted, 1);
        dao.update(new MallRobotOrderDo(), update);
        return 0;
    }

    @Override
    public RobotOrderResp queryDetail(Long id) {
        LambdaQueryWrapperX<MallRobotOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallRobotOrderDo::getId, id);
        MallRobotOrderDo MallRobotOrderDo = dao.selectOne(queryWrapperX);
        return BeanUtil.copyProperties(MallRobotOrderDo, RobotOrderResp.class);
    }

    @Override
    public PageResult<RobotOrderResp> queryList(Integer pageNo, Integer pageSize, String userId) {
        PageResult<RobotOrderResp> res = new PageResult<>();
        IPage<MallRobotOrderDo> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapperX<MallRobotOrderDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (StringUtils.isNotEmpty(userId)) {
            queryWrapperX.eq(MallRobotOrderDo::getCreator, userId);
        } else {
            queryWrapperX.eq(MallRobotOrderDo::getCreator, SessionContextUtil.getUser().getUserId());
        }
        queryWrapperX.eq(MallRobotOrderDo::getDeleted, 0);
        queryWrapperX.orderByDesc(MallRobotOrderDo::getCreateTime);
        IPage<MallRobotOrderDo> robotVariableDoIPage = dao.selectPage(page, queryWrapperX);
        if (CollectionUtils.isNotEmpty(robotVariableDoIPage.getRecords())) {
            res.setTotal(robotVariableDoIPage.getTotal());
            res.setList(BeanUtil.copyToList(robotVariableDoIPage.getRecords(), RobotOrderResp.class));
            return res;
        }else{
            res.setTotal(0L);
            res.setList(new ArrayList<>());
        }
        return res;
    }

    @Override
    public List<RobotOrderResp> listByIdsDel(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            List<Long> selectIds = new ArrayList<>();
            for (String id : ids) {
                selectIds.add(Long.parseLong(id));
            }
            List<MallRobotOrderDo> robotOrderDos = dao.listByIdsDel(selectIds);
            return BeanUtil.copyToList(robotOrderDos, RobotOrderResp.class);
        }
        return null;
    }

    private boolean checkName(List<MallRobotOrderDo> MallRobotOrderDos) {
        if (CollectionUtil.isEmpty(MallRobotOrderDos)) {
            return false;
        }
        return true;
    }
}
