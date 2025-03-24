package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.bean.RobotOrderBean;
import com.citc.nce.robot.dao.RobotOrderDao;
import com.citc.nce.robot.entity.RobotOrderDo;
import com.citc.nce.robot.service.RobotOrderService;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotOrderResp;
import com.citc.nce.tempStore.vo.UseTempVariableOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.service.impl
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:22
 * @Description: 机器人设置--指令管理Service层
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class RobotOrderServiceImpl implements RobotOrderService {

    @Resource
    RobotOrderDao robotOrderDao;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    public void saveOrder(RobotOrderReq robotOrderReq, BaseUser user) {

        RobotOrderDo robotOrderDo = new RobotOrderDo();
        BeanUtil.copyProperties(robotOrderReq, robotOrderDo);
        robotOrderDao.insert(robotOrderDo);
    }

    public void removeOrder(RobotOrderReq robotOrderReq, BaseUser user) {
        Long id = robotOrderReq.getId();
        Date currTime = DateUtil.date();
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", id);
        RobotOrderDo robotOrderDo = robotOrderDao.selectOne(wrapper);
        if (Objects.isNull(robotOrderDo)) return;
        if (!robotOrderDo.getCreator().equals(SessionContextUtil.getUser().getUserId())) {
            throw new BizException("该指令不属于你");
        }

        wrapper.set("deleted", 1);
        wrapper.set("deleted_time", currTime);
        robotOrderDao.update(null, wrapper);
    }

    public void editOrder(RobotOrderReq robotOrderReq) {
        RobotOrderDo orderDo = robotOrderDao.selectById(robotOrderReq.getId());
        if (Objects.isNull(orderDo)) {
            throw new BizException("指令不存在");
        }
        if (!SessionContextUtil.getLoginUser().getUserId().equals(orderDo.getCreator())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        //属性映射
        RobotOrderDo robotOrderDo = new RobotOrderDo();
        BeanUtil.copyProperties(robotOrderReq, robotOrderDo);
        robotOrderDao.updateById(robotOrderDo);
    }

    /**
     * 查询分页
     */
    public RobotOrderResp listAll(OrderPageParam pageParam) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");

        Long id = pageParam.getId();
        if (ObjectUtil.isNotNull(id) && id != 0) {
            //传的有id字段
            wrapper.eq("id", id);
        }
        try {
            String userId = pageParam.getUserId();
            if (StringUtils.isEmpty(userId)) {
                userId = SessionContextUtil.getUser().getUserId();
            }
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                wrapper.eq("creator", userId);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        wrapper.eq("order_type", 0);
        PageResult pageResult = robotOrderDao.selectPage(pageParam, wrapper);
        List<RobotOrderDo> lists = pageResult.getList();
        Long total = pageResult.getTotal();

        RobotOrderResp orderResp = new RobotOrderResp();
        List<RobotOrderBean> orderBeanList = new ArrayList<RobotOrderBean>();

        for (RobotOrderDo robotOrderDo : lists) {
            //属性映射
            RobotOrderBean orderBean = new RobotOrderBean();
            BeanUtil.copyProperties(robotOrderDo, orderBean);
            orderBeanList.add(orderBean);
        }
        orderResp.setList(orderBeanList);
        orderResp.setTotal(total);
        orderResp.setPageNo(pageParam.getPageNo());
        return orderResp;
    }

    public RobotOrderResp listOrderByName(String name) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        //如果指令名称不为空  加上判断条件
        wrapper.eq("order_name", name);

        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }

        List<RobotOrderDo> variableDos = robotOrderDao.selectList(wrapper);
        RobotOrderResp orderResp = new RobotOrderResp();
        List<RobotOrderBean> orderBeanList = new ArrayList<RobotOrderBean>();

        for (RobotOrderDo robotOrderDo : variableDos) {
            //属性映射
            RobotOrderBean orderBean = new RobotOrderBean();
            BeanUtil.copyProperties(robotOrderDo, orderBean);
            orderBeanList.add(orderBean);
        }
        orderResp.setList(orderBeanList);
        return orderResp;
    }

    @Override
    public RobotOrderBean queryOneById(Long id) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        wrapper.eq("id", id);
        RobotOrderDo robotOrderDo = robotOrderDao.selectOne(wrapper);
        return BeanUtil.copyProperties(robotOrderDo, RobotOrderBean.class);
    }

    @Override
    public List<Long> checkListUseTemp(Map<Long, RobotOrderReq> map) {
        Collection<RobotOrderReq> reqList = map.values();
        if (CollectionUtils.isEmpty(reqList)) return new ArrayList<>();
        //判断重复变量
        List<RobotOrderDo> existList = robotOrderDao.selectList(new LambdaQueryWrapper<RobotOrderDo>()
                .eq(RobotOrderDo::getDeleted, 0)
                .eq(BaseDo::getCreator, SessionContextUtil.getLoginUser().getUserId())
                .in(RobotOrderDo::getOrderName, reqList.stream().map(RobotOrderReq::getOrderName).collect(Collectors.toList())));

        if (!CollectionUtils.isEmpty(existList)) {
            Map<String, Long> nameMap = reqList.stream().collect(Collectors.toMap(RobotOrderReq::getOrderName, RobotOrderReq::getOldId));
            List<String> duName = existList.stream().map(RobotOrderDo::getOrderName).collect(Collectors.toList());
            List<Long> list = new ArrayList<>(duName.size());
            duName.forEach(name -> list.add(nameMap.get(name)));
            return list;
        }
        return new ArrayList<>();
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveListUseTemp(UseTempVariableOrder data) {
        Map<Long, RobotOrderReq> variableMap = data.getOrderMap();
        if (variableMap.isEmpty()) return;
        Collection<RobotOrderReq> reqList = variableMap.values();
        //判断重复变量
        List<RobotOrderDo> existList = robotOrderDao.selectList(new LambdaQueryWrapper<RobotOrderDo>()
                .eq(RobotOrderDo::getDeleted, 0)
                .eq(BaseDo::getCreator, SessionContextUtil.getLoginUser().getUserId())
                .in(RobotOrderDo::getOrderName, reqList.stream().map(RobotOrderReq::getOrderName).collect(Collectors.toList())));

        if (!CollectionUtils.isEmpty(existList)) {
            Map<String, Long> nameMap = reqList.stream().collect(Collectors.toMap(RobotOrderReq::getOrderName, RobotOrderReq::getOldId));
            List<String> duName = existList.stream().map(RobotOrderDo::getOrderName).collect(Collectors.toList());
            List<Long> list = new ArrayList<>(duName.size());
            duName.forEach(name -> list.add(nameMap.get(name)));
            data.setOrderDuplicate(list);
            throw new BizException("存在重复指令");
        }

        for (RobotOrderReq orderReq : reqList) {
            RobotOrderDo save = new RobotOrderDo();
            BeanUtils.copyProperties(orderReq, save);
            save.setId(null);
            robotOrderDao.insert(save);
            orderReq.setId(save.getId());
        }
    }


    @Override
    public List<RobotOrderBean> queryByTsOrderId(Long tsOrderId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("ts_order_id", tsOrderId);
        wrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        List list = robotOrderDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<RobotOrderBean> robotOrderBeans = BeanUtil.copyToList(list, RobotOrderBean.class);
            return robotOrderBeans;
        }
        return null;
    }

    @Override
    @Transactional
    public void removeIds(List<Long> ids) {
        robotOrderDao.deleteBatchIds(ids);
    }
}
