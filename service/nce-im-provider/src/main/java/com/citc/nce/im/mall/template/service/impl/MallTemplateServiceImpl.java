package com.citc.nce.im.mall.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.mall.template.entity.MallTemplateDo;
import com.citc.nce.im.mall.template.mapper.MallTemplateDao;
import com.citc.nce.im.mall.template.service.MallTemplateService;
import com.citc.nce.im.mall.utils.UUIDUtils;
import com.citc.nce.im.materialSquare.service.IMsSummaryService;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.robot.api.mall.constant.MallSnapshotTypeEnum;
import com.citc.nce.robot.api.mall.constant.MallTemplateStatusEnum;
import com.citc.nce.robot.api.mall.constant.MallTemplateTypeEnum;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryRobotListReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateSaveReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateUpdateReq;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryListResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateSimpleQueryListResp;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.vo.summary.TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.TemplateListQueryReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 17:15
 */
@Service
@Slf4j
public class MallTemplateServiceImpl implements MallTemplateService {

    @Resource
    private MallTemplateDao mallTemplateDao;

    @Resource
    @Lazy
    private IMsSummaryService summaryService;

    @Resource
    @Lazy
    private MallSnapshotService mallSnapshotService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(MallTemplateSaveReq req) {
        // 校验名称
        boolean checkOrderName = checkOrderName(req.getTemplateName(), req.getTemplateType().getCode(), null);
        if (!checkOrderName) {
            throw new BizException(MallError.TEMPLATE_NAME_DUPLICATE);
        }
        // 插入数据
        MallTemplateDo insert = new MallTemplateDo();
        BeanUtils.copyProperties(req, insert);
        insert.setTemplateType(req.getTemplateType().getCode());
        // 只有新增时才创建模板Id
        insert.setTemplateId(UUIDUtils.generateUUID());
        insert.setStatus(MallTemplateStatusEnum.NOT_BINDING.getCode());
        // 机器人是先有模板，再添加内容数据
        if (Objects.equals(MallTemplateTypeEnum.MSG.getCode(), req.getTemplateType().getCode())) {
            // 处理内容部分
            dealWithContent(insert, req.getMallCommonContent());
        } else {
            // 机器人模板，先塞个空。页面后期编辑的时候会更新这个字段
            insert.setSnapshotUuid("");
        }

        mallTemplateDao.insert(insert);
        return 0;
    }

    private void dealWithContent(MallTemplateDo mallTemplateDo, MallCommonContent content) {
        if (ObjectUtils.isNotEmpty(content)) {
            String snapshotUuid = mallSnapshotService.dealWithContent(content, MallSnapshotTypeEnum.TEMPLATE.getCode());
            mallTemplateDo.setSnapshotUuid(snapshotUuid);
        } else {
            mallTemplateDo.setSnapshotUuid("");
        }
    }

    /**
     * 获取模板详情
     *
     * @param templateId
     * @return MallGoodsDetailResp
     * @author zy.qiu
     * @createdTime 2023/11/22 19:15
     */
    @Override
    public MallTemplateQueryDetailResp queryDetail(String templateId) {
        MallTemplateDo mallTemplateDo = baseQuery(templateId);
        if (ObjectUtils.isEmpty(mallTemplateDo)) {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);
        }
        SessionContextUtil.sameCsp(mallTemplateDo.getCreator());
        MallTemplateQueryDetailResp resp = new MallTemplateQueryDetailResp();
        BeanUtils.copyProperties(mallTemplateDo, resp);
        MallCommonContent content = mallSnapshotService.queryContent(mallTemplateDo.getSnapshotUuid());
        resp.setMallCommonContent(content);
        return resp;
    }

    @Override
    public MallCommonContent getMallCommonContent(String templateId) {
        MallTemplateDo mallTemplateDo = baseQuery(templateId);
        if (ObjectUtils.isEmpty(mallTemplateDo)) {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);
        }
        SessionContextUtil.sameCsp(mallTemplateDo.getCreator());
        MallCommonContent content = mallSnapshotService.queryContent(mallTemplateDo.getSnapshotUuid());
        if (content != null) {
            content.setCreateTime(mallTemplateDo.getCreateTime());
            content.setUpdateTime(mallTemplateDo.getUpdateTime());
        }
        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(MallTemplateUpdateReq req) {
        boolean checkOrderName = checkOrderName(req.getTemplateName(), req.getTemplateType().getCode(),
                req.getTemplateId());
        if (!checkOrderName) {
            throw new BizException(MallError.TEMPLATE_NAME_DUPLICATE);
        }

        MallTemplateDo mallTemplateDo = baseQuery(req.getTemplateId());
        if (ObjectUtils.isEmpty(mallTemplateDo)) {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);
        }
        SessionContextUtil.sameCsp(mallTemplateDo.getCreator());

        BeanUtil.copyProperties(req, mallTemplateDo);
        // 处理内容部分
        dealWithContent(mallTemplateDo, req.getMallCommonContent());
        mallTemplateDo.setUpdater(SessionContextUtil.getUser().getUserId());
        mallTemplateDo.setUpdateTime(null);
        mallTemplateDao.updateById(mallTemplateDo);
        // 如果模板已经绑定了商品
        summaryService.notifyTemplateUpgrade(MsType.fromCode(mallTemplateDo.getTemplateType()),
                mallTemplateDo.getTemplateId());
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(String templateId) {
        MallTemplateDo mallTemplateDo = baseQuery(templateId);
        if (ObjectUtils.isEmpty(mallTemplateDo)) {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);
        }
        SessionContextUtil.sameCsp(mallTemplateDo.getCreator());

        MsSummary msSummary = summaryService.getByMsTypeAndMsId(MsType.fromCode(mallTemplateDo.getTemplateType()),
                mallTemplateDo.getTemplateId());
        if (Objects.nonNull(msSummary)) {
            // 当模板对应的商品为待审核或已上架状态时，
            // 根据商品状态分别提示：“该模板对应的商品状态为待审核，不可删除”  “该模板对应的商品状态为已上架，不可删除”
            if (MsAuditStatus.WAIT.equals(msSummary.getAuditStatus())) {
                throw new BizException(MallError.TEMPLATE_CAN_NOT_DELETE_WAIT);
            }
            if (MsAuditStatus.ACTIVE_ON.equals(msSummary.getAuditStatus())) {
                throw new BizException(MallError.TEMPLATE_CAN_NOT_DELETE_ACTIVE_ON);
            }
        }
        summaryService.notifyTemplateDelete(MsType.fromCode(mallTemplateDo.getTemplateType()),
                mallTemplateDo.getTemplateId());

        LambdaUpdateWrapper<MallTemplateDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MallTemplateDo::getTemplateId, mallTemplateDo.getTemplateId())
                .eq(MallTemplateDo::getId, mallTemplateDo.getId());
        updateWrapper.set(MallTemplateDo::getDeleted, 1)
                .set(MallTemplateDo::getDeletedTime, new Date().getTime());
        mallTemplateDao.update(null, updateWrapper);
        return 0;
    }

    @Override
    public int addSnapshotUuid(String templateId, String snapshotUuid) {
        MallTemplateDo mallTemplateDo = baseQuery(templateId);
        if (ObjectUtils.isEmpty(mallTemplateDo)) {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);
        }
        mallTemplateDo.setSnapshotUuid(snapshotUuid);
        mallTemplateDao.updateById(mallTemplateDo);
        return 0;
    }


    @Override
    public PageResult<MallTemplateQueryListResp> queryRobotList(String templateName, Integer pageNo, Integer pageSize) {
        PageResult<MallTemplateQueryListResp> res = new PageResult<>();
        IPage<MallTemplateDo> mallTemplateDoIPage = queryList(templateName, null, pageNo, pageSize,
                MallTemplateTypeEnum.ROBOT.getCode(), null);
        if (null != mallTemplateDoIPage) {
            List<MallTemplateDo> records = mallTemplateDoIPage.getRecords();
            List<MallTemplateQueryListResp> templateQueryListResps = BeanUtil.copyToList(records,
                    MallTemplateQueryListResp.class);
            res.setList(templateQueryListResps);
            res.setTotal(mallTemplateDoIPage.getTotal());
        } else {
            res.setTotal(0L);
        }
        return res;
    }

    @Override
    public PageResult<MallTemplateQueryListResp> query5GMsgList(String templateName, Integer messageType,
                                                                Integer pageNo, Integer pageSize) {
        PageResult<MallTemplateQueryListResp> res = new PageResult<>();
        IPage<MallTemplateDo> mallTemplateDoIPage = queryList(templateName, messageType, pageNo, pageSize,
                MallTemplateTypeEnum.MSG.getCode(), null);
        if (null != mallTemplateDoIPage) {
            List<MallTemplateDo> records = mallTemplateDoIPage.getRecords();
            List<MallTemplateQueryListResp> templateQueryListResps = BeanUtil.copyToList(records,
                    MallTemplateQueryListResp.class);
            res.setList(templateQueryListResps);
            res.setTotal(mallTemplateDoIPage.getTotal());
        } else {
            res.setTotal(0L);
        }
        return res;
    }

    @Override
    public PageResult<MallTemplateQueryListResp> queryAvailableList(MallTemplateQueryRobotListReq req) {
        req.setCspId(SessionContextUtil.verifyCspLogin());
        Page<MallTemplateDo> page = PageSupport.getPage(MallTemplateDo.class, req);
        mallTemplateDao.selectPageUnused(page, req);
        PageResult<MallTemplateQueryListResp> res = new PageResult<>();
        List<MallTemplateDo> list = page.getRecords();
        res.setTotal(page.getTotal());
        res.setList(BeanUtil.copyToList(list, MallTemplateQueryListResp.class));
        //查询5g消息缩略图
        if (CollectionUtil.isEmpty(list)) {
            return res;
        }
        if (1 == req.getTemplateType()) {
            //5g消息 设置缩略图id
            List<String> snapshotUuids =
                    list.stream().map(MallTemplateDo::getSnapshotUuid).collect(Collectors.toList());
            Map<String, String> map = mallSnapshotService.get5GThumbnailBySnapshotUuid(snapshotUuids);
            res.getList().forEach(i -> i.setThumbnail(map.get(i.getSnapshotUuid())));
        }
        return res;
    }

    @Override
    public List<MallTemplateSimpleQueryListResp> simpleRobotList(String templateName) {
        IPage<MallTemplateDo> mallTemplateDoIPage = queryList(templateName, null, null, null,
                MallTemplateTypeEnum.ROBOT.getCode(), null);
        if (null != mallTemplateDoIPage) {
            List<MallTemplateDo> records = mallTemplateDoIPage.getRecords();
            List<MallTemplateSimpleQueryListResp> templateQueryListResps = BeanUtil.copyToList(records,
                    MallTemplateSimpleQueryListResp.class);
            return templateQueryListResps;
        }
        return null;
    }

    @Override
    public List<MallTemplateSimpleQueryListResp> simple5GMsgList(String templateName, Integer messageType) {
        IPage<MallTemplateDo> mallTemplateDoIPage = queryList(templateName, messageType, null, null,
                MallTemplateTypeEnum.MSG.getCode(), null);
        if (null != mallTemplateDoIPage) {
            List<MallTemplateDo> records = mallTemplateDoIPage.getRecords();
            List<MallTemplateSimpleQueryListResp> templateQueryListResps = BeanUtil.copyToList(records,
                    MallTemplateSimpleQueryListResp.class);
            return templateQueryListResps;
        }
        return null;
    }

    @Override
    public List<TemplateInfo> templateListQuery(TemplateListQueryReq req) {

        List<TemplateInfo> result = new ArrayList<>();
        List<MallTemplateDo> mallTemplateDos = new ArrayList<>();
        if (req.getId() != null) {
            List<MallTemplateDo> mallTemplateDoDBs = mallTemplateDao.selectList(new LambdaUpdateWrapper<MallTemplateDo>().eq(MallTemplateDo::getTemplateId, req.getId()));
            if (CollectionUtil.isNotEmpty(mallTemplateDoDBs)) {
                MallTemplateDo mallTemplateDo = mallTemplateDoDBs.get(0);
                if (Objects.equals(mallTemplateDo.getTemplateType(), req.getTemplateType())) {
                    mallTemplateDos.add(mallTemplateDo);
                }
            }
        }
        LambdaQueryWrapperX<MallTemplateDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallTemplateDo::getCreator, SessionContextUtil.getUserId())
                .eq(MallTemplateDo::getTemplateType, req.getTemplateType())
                .eq(MallTemplateDo::getDeleted, 0)
                .isNull(MallTemplateDo::getMssId)
                .orderByDesc(MallTemplateDo::getCreateTime);
        List<MallTemplateDo> mallTemplateDoElse = mallTemplateDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(mallTemplateDoElse)) {
            mallTemplateDos.addAll(mallTemplateDoElse);
        }

        if (CollectionUtil.isNotEmpty(mallTemplateDos)) {
            for (MallTemplateDo mallTemplateDo : mallTemplateDos) {
                TemplateInfo templateInfo = new TemplateInfo();
                BeanUtil.copyProperties(mallTemplateDo, templateInfo);
                result.add(templateInfo);
            }

            //查询5g消息缩略图
            List<String> snapshotUuids =
                    mallTemplateDos.stream().map(MallTemplateDo::getSnapshotUuid).collect(Collectors.toList());
            Map<String, String> map = mallSnapshotService.get5GThumbnailBySnapshotUuid(snapshotUuids);
            if (CollectionUtil.isNotEmpty(map)) {
                result.forEach(i -> i.setThumbnail(map.get(i.getSnapshotUuid())));
            }
        }
        return result;
    }

    @Override
    public void updateMssID(String msId, Long mssId) {
        List<MallTemplateDo> mallTemplateDoDBOlds = mallTemplateDao.selectList(new LambdaUpdateWrapper<MallTemplateDo>().eq(MallTemplateDo::getMssId, mssId));
        if(CollectionUtil.isNotEmpty(mallTemplateDoDBOlds)) {
            mallTemplateDao.updateMssIDNullById(mallTemplateDoDBOlds.stream().map(MallTemplateDo::getTemplateId).collect(Collectors.toList()));
        }
        List<MallTemplateDo> mallTemplateDoDBs = mallTemplateDao.selectList(new LambdaUpdateWrapper<MallTemplateDo>().eq(MallTemplateDo::getTemplateId, msId));
        if (CollectionUtil.isNotEmpty(mallTemplateDoDBs)) {
            MallTemplateDo mallTemplateDo = mallTemplateDoDBs.get(0);
            mallTemplateDo.setMssId(mssId);
            mallTemplateDao.updateById(mallTemplateDo);
        } else {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);

        }
    }

    @Override
    public void deleteMssIDForIds(List<String> templateIDList) {
        if (!CollectionUtils.isEmpty(templateIDList)) {
            mallTemplateDao.updateMssIDNullById(templateIDList);
        }
    }


    private MallTemplateDo baseQuery(String templateId) {
        LambdaQueryWrapperX<MallTemplateDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallTemplateDo::getTemplateId, templateId);
        return mallTemplateDao.selectOne(queryWrapperX);
    }

    public IPage<MallTemplateDo> queryList(String templateName, Integer messageType, Integer pageNo, Integer pageSize,
                                           Integer templateType, Integer status) {
        QueryWrapper<MallTemplateDo> queryWrapper = new QueryWrapper<>();
        IPage<MallTemplateDo> page = new Page<>(-1, 999999);
        if (StringUtils.isNotEmpty(templateName)) {
            queryWrapper.like("template_name", templateName);
        }
        if (null != messageType) {
            queryWrapper.eq("message_type", messageType);
        }
        if (null != templateType) {
            queryWrapper.eq("template_type", templateType);
        }
        if (null != status) {
            queryWrapper.eq("status", status);
        }
        if (null != pageNo && null != pageSize) {
            page = new Page<>(pageNo, pageSize);
        }
        queryWrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("create_time");

        IPage<MallTemplateDo> mallGoodsDoIPage = mallTemplateDao.selectPage(page, queryWrapper);
        if (CollectionUtil.isNotEmpty(mallGoodsDoIPage.getRecords())) {
            return mallGoodsDoIPage;
        }
        return null;
    }

    private boolean checkOrderName(String templateName, Integer type, String tempUUId) {
        LambdaQueryWrapperX<MallTemplateDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.ne(StringUtils.isNotEmpty(tempUUId), MallTemplateDo::getTemplateId, tempUUId);
        queryWrapperX.eq(MallTemplateDo::getTemplateName, templateName);
        queryWrapperX.eq(MallTemplateDo::getTemplateType, type);
        queryWrapperX.eq(BaseDo::getCreator, SessionContextUtil.verifyCspLogin());
        List<MallTemplateDo> mallTemplateDos = mallTemplateDao.selectList(queryWrapperX);
        return !CollectionUtil.isNotEmpty(mallTemplateDos);
    }
}
