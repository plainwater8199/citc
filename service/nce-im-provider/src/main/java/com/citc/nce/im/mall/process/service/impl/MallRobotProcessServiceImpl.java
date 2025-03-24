package com.citc.nce.im.mall.process.service.impl;

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
import com.citc.nce.im.mall.process.entity.MallRobotProcessDo;
import com.citc.nce.im.mall.process.entity.MallRobotProcessTriggerDo;
import com.citc.nce.im.mall.process.mapper.MallRobotProcessDao;
import com.citc.nce.im.mall.process.mapper.MallRobotProcessTriggerDao;
import com.citc.nce.im.mall.process.service.MallRobotProcessService;
import com.citc.nce.im.mall.utils.UUIDUtils;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessUpdateReq;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessResp;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessTriggerDetailResp;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MallRobotProcessServiceImpl implements MallRobotProcessService {

    @Resource
    MallRobotProcessDao dao;

    @Resource
    MallRobotProcessTriggerDao mallRobotProcessTriggerDao;

    /**
     * 根据name 查询记录
     */
    public List<MallRobotProcessDo> listByName(String name, String userId, String processId) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("process_name", name);
        if (StringUtils.isNotEmpty(userId)) {
            wrapper.eq("creator", userId);
        }
        if (StringUtils.isNotEmpty(processId)) {
            wrapper.ne("process_id", processId);
        }
        List<MallRobotProcessDo> list = dao.selectList(wrapper);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(MallRobotProcessSaveReq req) {
        List<MallRobotProcessDo> listByName = listByName(req.getProcessName(), SessionContextUtil.getUser().getUserId(), null);
        if (checkName(listByName)) {
            // 名称已经存在
            throw new BizException(MallError.PROCESS_NAME_DUPLICATE);
        }
        MallRobotProcessDo MallRobotProcessDo = new MallRobotProcessDo();
        BeanUtil.copyProperties(req, MallRobotProcessDo);
        MallRobotProcessDo.setProcessId(UUIDUtils.generateUUID());
        dao.insert(MallRobotProcessDo);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(MallRobotProcessUpdateReq req) {
        List<MallRobotProcessDo> MallRobotProcessDos = listByName(req.getProcessValue(), SessionContextUtil.getUser().getUserId(), req.getProcessId());
        if (checkName(MallRobotProcessDos)) {
            // 名称已经存在
            throw new BizException(MallError.PROCESS_NAME_DUPLICATE);
        }
        //属性映射
        MallRobotProcessDo order = new MallRobotProcessDo();
        BeanUtil.copyProperties(req, order);

        UpdateWrapper<MallRobotProcessDo> wrapper = new UpdateWrapper<MallRobotProcessDo>();
        wrapper.eq("process_id", req.getProcessId());
        dao.update(order, wrapper);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(String processId) {
        LambdaUpdateWrapper<MallRobotProcessDo> update = new LambdaUpdateWrapper<>();
        update.eq(MallRobotProcessDo::getProcessId, processId);
        update.set(MallRobotProcessDo::getDeleted, 1);
        dao.update(new MallRobotProcessDo(), update);
        return 0;
    }

    @Override
    public MallRobotProcessResp queryDetail(String processId) {
        LambdaQueryWrapperX<MallRobotProcessDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallRobotProcessDo::getProcessId, processId);
        MallRobotProcessDo MallRobotProcessDo = dao.selectOne(queryWrapperX);
        return BeanUtil.copyProperties(MallRobotProcessDo, MallRobotProcessResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTrigger(MallRobotProcessTriggerSaveReq req) {
        LambdaQueryWrapperX<MallRobotProcessTriggerDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallRobotProcessTriggerDo::getProcessId, req.getProcessId())
                .eq(MallRobotProcessTriggerDo::getTemplateId, req.getTemplateId())
                .eq(MallRobotProcessTriggerDo::getDeleted, 0);
        List<MallRobotProcessTriggerDo> processTriggerDos = mallRobotProcessTriggerDao.selectList(queryWrapperX);
        MallRobotProcessTriggerDo mallRobotProcessTriggerDo = new MallRobotProcessTriggerDo();
        if (CollectionUtils.isNotEmpty(processTriggerDos) && 1 == processTriggerDos.size()) {
            MallRobotProcessTriggerDo update = processTriggerDos.get(0);
            update.setPrimaryCodeList(req.getPrimaryCodeList());
            update.setRegularCode(req.getRegularCode());
            mallRobotProcessTriggerDao.updateById(update);
        } else {
            BeanUtil.copyProperties(req, mallRobotProcessTriggerDo);
            mallRobotProcessTriggerDao.insert(mallRobotProcessTriggerDo);
        }
        return 0;
    }

    @Override
    public MallRobotProcessTriggerDetailResp queryTriggerDetail(String processId, String templateId) {
        MallRobotProcessTriggerDetailResp resp = new MallRobotProcessTriggerDetailResp();
        LambdaQueryWrapperX<MallRobotProcessTriggerDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallRobotProcessTriggerDo::getDeleted, 0);
        if (StringUtils.isNotEmpty(processId)) {
            queryWrapperX.eq(MallRobotProcessTriggerDo::getProcessId, processId);
        }
        if (StringUtils.isNotEmpty(templateId)) {
            queryWrapperX.eq(MallRobotProcessTriggerDo::getTemplateId, templateId);
        }
        List<MallRobotProcessTriggerDo> robotProcessTriggerDos = mallRobotProcessTriggerDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(robotProcessTriggerDos) && 1 == robotProcessTriggerDos.size()) {
            BeanUtil.copyProperties(robotProcessTriggerDos.get(0), resp);
            return resp;
        }
        return resp;
    }

    @Override
    public PageResult<MallRobotProcessResp> queryList(Integer pageNo, Integer pageSize, String userId, String templateId) {
        PageResult<MallRobotProcessResp> res = new PageResult<>();
        res.setTotal(0L);
        IPage<MallRobotProcessDo> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapperX<MallRobotProcessDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (StringUtils.isEmpty(userId)) {
            userId = SessionContextUtil.getUser().getUserId();
        }
        queryWrapperX.eq(MallRobotProcessDo::getCreator, userId);
        if (StringUtils.isNotEmpty(templateId)) {
            queryWrapperX.eq(MallRobotProcessDo::getTemplateId, templateId);
        }
        queryWrapperX.eq(MallRobotProcessDo::getDeleted, 0);
        IPage<MallRobotProcessDo> robotVariableDoIPage = dao.selectPage(page, queryWrapperX);
        if (CollectionUtils.isNotEmpty(robotVariableDoIPage.getRecords())) {
            /**
             * 触发器
             */
            LambdaQueryWrapperX<MallRobotProcessTriggerDo> triggerWrapper = new LambdaQueryWrapperX<MallRobotProcessTriggerDo>();
            triggerWrapper.eq(MallRobotProcessTriggerDo::getTemplateId, templateId)
                    .eq(MallRobotProcessTriggerDo::getCreator, userId);
            List<MallRobotProcessTriggerDo> mallRobotProcessTriggerDos = mallRobotProcessTriggerDao.selectList(triggerWrapper);

            List<MallRobotProcessResp> mallRobotProcessResps = BeanUtil.copyToList(robotVariableDoIPage.getRecords(), MallRobotProcessResp.class);
            if (CollectionUtils.isNotEmpty(mallRobotProcessTriggerDos)) {
                Map<String, MallRobotProcessTriggerDo> collect = mallRobotProcessTriggerDos.stream().collect(Collectors.toMap(MallRobotProcessTriggerDo::getProcessId, Function.identity(), (key1, key2) -> key2));
                for (MallRobotProcessResp mallRobotProcessDo :
                        mallRobotProcessResps) {
                    MallRobotProcessTriggerDo robotProcessTriggerDo = collect.get(mallRobotProcessDo.getProcessId());
                    if (null != robotProcessTriggerDo) {
                        mallRobotProcessDo.setPrimaryCodeList(robotProcessTriggerDo.getPrimaryCodeList());
                        mallRobotProcessDo.setRegularCode(robotProcessTriggerDo.getRegularCode());
                    }
                }
            }
            res.setTotal(robotVariableDoIPage.getTotal());
            res.setList(mallRobotProcessResps);
        }
        return res;
    }

    private boolean checkName(List<MallRobotProcessDo> MallRobotProcessDos) {
        if (CollectionUtil.isEmpty(MallRobotProcessDos)) {
            return false;
        }
        return true;
    }
}
