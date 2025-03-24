package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.bean.RobotVariableBean;
import com.citc.nce.robot.dao.RobotVariableDao;
import com.citc.nce.robot.entity.RobotVariableDo;
import com.citc.nce.robot.service.RobotVariableService;
import com.citc.nce.robot.vo.RobotVariableCreateReq;
import com.citc.nce.robot.vo.RobotVariablePageReq;
import com.citc.nce.robot.vo.RobotVariableReq;
import com.citc.nce.robot.vo.RobotVariableResp;
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


@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
@Slf4j
public class RobotVariableServiceImpl implements RobotVariableService {

    @Resource
    RobotVariableDao robotVariableDao;


    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    public RobotVariableResp list(RobotVariablePageReq robotVariablePageReq) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isNotEmpty(robotVariablePageReq.getCreator())) {
            wrapper.eq("creator", robotVariablePageReq.getCreator());
        } else {
            BaseUser user = SessionContextUtil.getUser();
            String userId = null;
            if (null != user) {
                userId = user.getUserId();
            }
            wrapper.eq("creator", userId);
        }
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(robotVariablePageReq.getPageNo());
        pageParam.setPageSize(robotVariablePageReq.getPageSize());
        //根据创建时间排序
        wrapper.orderByDesc("create_time");
        PageResult pageResult = robotVariableDao.selectPage(pageParam, wrapper);
        List<RobotVariableDo> list = pageResult.getList();
        Long total = pageResult.getTotal();//总页数
        RobotVariableResp variableResp = new RobotVariableResp();
        List<RobotVariableBean> robotVariableBeans = new ArrayList<RobotVariableBean>();
        for (RobotVariableDo robotVariableDo : list) {
            //属性映射
            RobotVariableBean robotVariableBean = new RobotVariableBean();
            BeanUtil.copyProperties(robotVariableDo, robotVariableBean);
            robotVariableBeans.add(robotVariableBean);
        }
        variableResp.setList(robotVariableBeans);
        variableResp.setPageNo(robotVariablePageReq.getPageNo());
        variableResp.setTotal(total);
        return variableResp;
    }

    public RobotVariableBean queryById(RobotVariableReq robotVariableReq) {
        RobotVariableDo robotVariableDo = robotVariableDao.selectById(robotVariableReq.getId());
        RobotVariableBean robotVariableBean = new RobotVariableBean();
        BeanUtil.copyProperties(robotVariableDo, robotVariableBean);
        return robotVariableBean;
    }

    @Override
    public RobotVariableResp getList(RobotVariableCreateReq robotVariableCreateReq) {
        QueryWrapper<RobotVariableDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("creator", robotVariableCreateReq.getCreator());

        //根据创建时间排序
        wrapper.orderByDesc("create_time");

        List<RobotVariableDo> list = robotVariableDao.selectList(wrapper);
        RobotVariableResp variableResp = new RobotVariableResp();
        List<RobotVariableBean> robotVariableBeans = new ArrayList<RobotVariableBean>();
        for (RobotVariableDo robotVariableDo : list) {
            //属性映射
            RobotVariableBean robotVariableBean = new RobotVariableBean();
            BeanUtil.copyProperties(robotVariableDo, robotVariableBean);
            robotVariableBeans.add(robotVariableBean);
        }
        variableResp.setList(robotVariableBeans);

        return variableResp;
    }

    @Override
    public List<Long> checkListUseTemp(Map<Long, RobotVariableReq> variableMap) {
        if (variableMap.isEmpty()) return new ArrayList<>();
        Collection<RobotVariableReq> reqList = variableMap.values();
        List<RobotVariableDo> existList = robotVariableDao.selectList(new LambdaQueryWrapper<RobotVariableDo>()
                .eq(RobotVariableDo::getDeleted, 0)
                .eq(BaseDo::getCreator, SessionContextUtil.getLoginUser().getUserId())
                .in(RobotVariableDo::getVariableName, reqList.stream().map(RobotVariableReq::getVariableName).collect(Collectors.toSet())));
        if (!CollectionUtils.isEmpty(existList)) {
            Map<String, Long> nameMap = reqList.stream().collect(Collectors.toMap(RobotVariableReq::getVariableName, RobotVariableReq::getOldId));
            List<String> duName = existList.stream().map(RobotVariableDo::getVariableName).collect(Collectors.toList());
            List<Long> list = new ArrayList<>(duName.size());
            duName.forEach(name -> list.add(nameMap.get(name)));
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveListUseTemp(UseTempVariableOrder data) {
        Map<Long, RobotVariableReq> variableMap = data.getVariableMap();
        if (variableMap.isEmpty()) return;
        Collection<RobotVariableReq> reqList = variableMap.values();

        //一个一个保存并回写id

        for (RobotVariableReq variableReq : reqList) {
            RobotVariableDo save = new RobotVariableDo();
            BeanUtils.copyProperties(variableReq, save);
            save.setId(null);
            robotVariableDao.insert(save);
            variableReq.setId(save.getId());
        }
    }

    @Override
    public List<RobotVariableBean> queryByTsOrderId(Long tsOrderId) {
        QueryWrapper<RobotVariableDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        wrapper.eq("ts_order_id", tsOrderId);
        List<RobotVariableDo> robotVariableDos = robotVariableDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(robotVariableDos)) {
            List<RobotVariableBean> robotVariableBeans = BeanUtil.copyToList(robotVariableDos, RobotVariableBean.class);
            return robotVariableBeans;
        }
        return null;
    }

    @Override
    @Transactional
    public void removeIds(List<Long> ids) {
        robotVariableDao.deleteBatchIds(ids);
    }

    /**
     * 根据name 查询记录
     */
    public RobotVariableResp listByName(String name, BaseUser user) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        //如果有变量名，则根据变量名查询，没有则查询全部
        wrapper.eq("variable_name", name);
        wrapper.eq("creator", user.getUserId());
        List<RobotVariableDo> robotVariableDos = robotVariableDao.selectList(wrapper);
        RobotVariableResp variableResp = new RobotVariableResp();
        List<RobotVariableBean> robotVariableBeans = new ArrayList<RobotVariableBean>();
        for (RobotVariableDo robotVariableDo : robotVariableDos) {
            //属性映射
            RobotVariableBean robotVariableBean = new RobotVariableBean();
            BeanUtil.copyProperties(robotVariableDo, robotVariableBean);
            robotVariableBeans.add(robotVariableBean);
        }
        variableResp.setList(robotVariableBeans);
        return variableResp;
    }

    /**
     * 机器人变量保存
     */
    public void saveVariable(RobotVariableReq robotVariableReq, BaseUser user) {
        RobotVariableDo robotVariableDo = new RobotVariableDo();
        BeanUtil.copyProperties(robotVariableReq, robotVariableDo);
        robotVariableDao.insert(robotVariableDo);
    }

    /**
     * 机器人变量编辑
     */
    public void editVariable(RobotVariableReq robotVariableReq) {
        //属性映射
        RobotVariableDo robotVariableDo = new RobotVariableDo();
        BeanUtil.copyProperties(robotVariableReq, robotVariableDo);

        UpdateWrapper<RobotVariableDo> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", robotVariableReq.getId());
        RobotVariableDo variableDo = robotVariableDao.selectOne(wrapper);
        if (Objects.isNull(variableDo)) {
            throw new BizException("变量不存在");
        }
        if (!variableDo.getCreator().equals(SessionContextUtil.getUser().getUserId())) {
            throw new BizException("该变量不属于你");
        }
        robotVariableDao.update(robotVariableDo, wrapper);
    }


    /**
     * 机器人变量删除
     */
    public void removeVariable(long id, BaseUser user) {
        Date currTime = DateUtil.date();
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", id);
        RobotVariableDo variableDo = robotVariableDao.selectOne(wrapper);
        if (Objects.isNull(variableDo)) {
            return;
        }
        if (!variableDo.getCreator().equals(SessionContextUtil.getUser().getUserId())) {
            throw new BizException("该变量不属于你");
        }
        wrapper.set("deleted", 1);
        wrapper.set("deleted_time", currTime);
        robotVariableDao.update(null, wrapper);
    }
}
