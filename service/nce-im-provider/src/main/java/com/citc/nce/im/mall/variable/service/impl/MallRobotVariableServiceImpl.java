package com.citc.nce.im.mall.variable.service.impl;

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
import com.citc.nce.im.mall.variable.entity.MallRobotVariableDo;
import com.citc.nce.im.mall.variable.mapper.MallRobotVariableDao;
import com.citc.nce.im.mall.variable.service.MallRobotVariableService;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableSaveReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableUpdateReq;
import com.citc.nce.robot.api.mall.variable.vo.resp.MallRobotVariableDetailResp;
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
public class MallRobotVariableServiceImpl implements MallRobotVariableService {

    @Resource
    MallRobotVariableDao robotVariableDao;

    /**
     * 根据name 查询记录
     */
    public List<MallRobotVariableDo> listByName(String name, String userId, Long id) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("variable_name", name);
        if (StringUtils.isNotEmpty(userId)) {
            wrapper.eq("creator", userId);
        }
        if (null != id) {
            wrapper.ne("id", id);
        }
        List<MallRobotVariableDo> robotVariableDos = robotVariableDao.selectList(wrapper);
        return robotVariableDos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(MallRobotVariableSaveReq req) {
        List<MallRobotVariableDo> mallRobotVariableDos = listByName(req.getVariableName(), SessionContextUtil.getUser().getUserId(), null);
        if (checkName(mallRobotVariableDos)) {
            // 变量名称已经存在
            throw new BizException(MallError.VARIABLE_NAME_DUPLICATE);
        }
        MallRobotVariableDo robotVariableDo = new MallRobotVariableDo();
        BeanUtil.copyProperties(req, robotVariableDo);
        robotVariableDao.insert(robotVariableDo);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(MallRobotVariableUpdateReq req) {
        List<MallRobotVariableDo> mallRobotVariableDos = listByName(req.getVariableName(), SessionContextUtil.verifyCspLogin(), req.getId());
        if (checkName(mallRobotVariableDos)) {
            // 变量名称已经存在
            throw new BizException(MallError.VARIABLE_NAME_DUPLICATE);
        }
        MallRobotVariableDo variableDo = robotVariableDao.selectById(req.getId());
        if (Objects.isNull(variableDo)) {
            throw new BizException("变量不存在");
        }
        SessionContextUtil.sameCsp(variableDo.getCreator());
        //属性映射
        MallRobotVariableDo robotVariableDo = new MallRobotVariableDo();
        BeanUtil.copyProperties(req, robotVariableDo);
        UpdateWrapper<MallRobotVariableDo> wrapper = new UpdateWrapper<MallRobotVariableDo>();
        wrapper.eq("id", req.getId());
        robotVariableDao.update(robotVariableDo, wrapper);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        MallRobotVariableDo variableDo = robotVariableDao.selectById(id);
        if (Objects.isNull(variableDo)) return 0;
        SessionContextUtil.sameCsp(variableDo.getCreator());
        LambdaUpdateWrapper<MallRobotVariableDo> update = new LambdaUpdateWrapper<>();
        update.eq(MallRobotVariableDo::getId, id);
        update.set(MallRobotVariableDo::getDeleted, 1);
        robotVariableDao.update(new MallRobotVariableDo(), update);
        return 0;
    }

    @Override
    public MallRobotVariableDetailResp queryDetail(Long id) {
        LambdaQueryWrapperX<MallRobotVariableDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallRobotVariableDo::getId, id);
        MallRobotVariableDo mallRobotVariableDo = robotVariableDao.selectOne(queryWrapperX);
        return BeanUtil.copyProperties(mallRobotVariableDo, MallRobotVariableDetailResp.class);
    }

    @Override
    public PageResult<MallRobotVariableDetailResp> queryList(Integer pageNo, Integer pageSize, String userId) {
        PageResult<MallRobotVariableDetailResp> res = new PageResult<>();
        res.setTotal(0L);
        IPage<MallRobotVariableDo> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapperX<MallRobotVariableDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (StringUtils.isNotEmpty(userId)) {
            queryWrapperX.eq(MallRobotVariableDo::getCreator, userId);
        } else {
            queryWrapperX.eq(MallRobotVariableDo::getCreator, SessionContextUtil.getUser().getUserId());
        }
        queryWrapperX.eq(MallRobotVariableDo::getDeleted, 0);
        queryWrapperX.orderByDesc(MallRobotVariableDo::getCreateTime);
        IPage<MallRobotVariableDo> robotVariableDoIPage = robotVariableDao.selectPage(page, queryWrapperX);
        if (CollectionUtils.isNotEmpty(robotVariableDoIPage.getRecords())) {
            res.setTotal(robotVariableDoIPage.getTotal());
            res.setList(BeanUtil.copyToList(robotVariableDoIPage.getRecords(), MallRobotVariableDetailResp.class));
            return res;
        }else{
            res.setList(new ArrayList<>());
        }
        return res;
    }

    @Override
    public List<MallRobotVariableDetailResp> listByIdsDel(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            List<Long> selectIds = new ArrayList<>();
            for (String id : ids) {
                selectIds.add(Long.parseLong(id));
            }
            List<MallRobotVariableDo> variableDos = robotVariableDao.listByIdsDel(selectIds);
            return BeanUtil.copyToList(variableDos, MallRobotVariableDetailResp.class);
        }
        return null;
    }

    @Override
    public List<MallRobotVariableDetailResp> listByIds(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            List<Long> selectIds = new ArrayList<>();
            for (String id : ids) {
                selectIds.add(Long.parseLong(id));
            }
            List<MallRobotVariableDo> variableDos = robotVariableDao.listByIds(selectIds);
            return BeanUtil.copyToList(variableDos, MallRobotVariableDetailResp.class);
        }
        return null;
    }

    private boolean checkName(List<MallRobotVariableDo> mallRobotVariableDos) {
        if (CollectionUtil.isEmpty(mallRobotVariableDos)) {
            return false;
        }
        return true;
    }
}
