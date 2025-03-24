package com.citc.nce.h5.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.FormManagementTreeResp;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserInfoResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.FormUrlConfigure;
import com.citc.nce.h5.dao.H5FormMapper;
import com.citc.nce.h5.dao.H5Mapper;
import com.citc.nce.h5.dao.H5OfSummeryMapper;
import com.citc.nce.h5.dto.H5FormCountDto;
import com.citc.nce.h5.entity.H5Do;
import com.citc.nce.h5.entity.H5FormDo;
import com.citc.nce.h5.entity.H5OfSummeryDo;
import com.citc.nce.h5.service.H5Service;
import com.citc.nce.h5.vo.H5InfoListQuery;
import com.citc.nce.h5.vo.H5FormInfo;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.req.H5FromSubmitReq;
import com.citc.nce.h5.vo.req.H5QueryVO;
import com.citc.nce.h5.vo.resp.H5CopyResp;
import com.citc.nce.h5.vo.resp.H5Resp;
import com.citc.nce.materialSquare.service.IMsManageActivityService;
import com.citc.nce.misc.shortUrl.ShortUrlApi;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.vo.summary.H5TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.form.FormInfoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.form.FormPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import com.citc.nce.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mac
 * @since 2023-05-21
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
@EnableConfigurationProperties({FormUrlConfigure.class})
public class H5ServiceImpl extends ServiceImpl<H5Mapper, H5Do> implements H5Service, IService<H5Do> {

    private final FormUrlConfigure formUrlConfigure;

    private final IMsManageActivityService activityService;

    private final H5FormMapper h5FormMapper;

    private final AdminAuthApi adminAuthApi;

    private final ShortUrlApi shortUrlApi;
    private final MsSummaryApi msSummaryApi;

    private final H5OfSummeryMapper h5OfSummeryMapper;

    @Override
    public PageResult<H5Info> selectH5Page(H5QueryVO h5Vo) {
        Page<H5Do> page = new Page<>(h5Vo.getPageNo(), h5Vo.getPageSize());
        page(page, new LambdaQueryWrapper<H5Do>()
                .eq(h5Vo.getStatus() != null, H5Do::getStatus, h5Vo.getStatus())
                .eq(H5Do::getCustomerId, SessionContextUtil.getUserId())
                .like(StringUtils.hasLength(h5Vo.getTitle()), H5Do::getTitle, h5Vo.getTitle())
                .like(StringUtils.hasLength(h5Vo.getKeyword()), H5Do::getH5Desc, h5Vo.getKeyword())
                .and(wrapper -> wrapper
                        .eq(H5Do::getMssIdForBuy, 0L) // mssIdForBuy = 0L
                        .or()
                        .isNull(H5Do::getMssIdForBuy) // mssIdForBuy IS NULL
                )
                .orderByDesc(H5Do::getCreateTime)
        );

        List<H5Info> result = page.getRecords().stream()
                .map(item -> {
                    H5Info info = new H5Info();
                    BeanUtils.copyProperties(item, info);
                    info.setCreateTime(DateUtils.obtainDateStr(item.getCreateTime()));
                    return info;
                }).collect(Collectors.toList());

        //查询是否有表单信息
        if (!CollectionUtils.isEmpty(result)) {
            List<Long> H5IdList = result.stream().map(H5Info::getId).collect(Collectors.toList());
            List<H5FormCountDto> countAndH5 = h5FormMapper.queryCountByH5Ids(H5IdList);
            Map<Long, Long> resultMap = countAndH5.stream().collect(Collectors.toMap(H5FormCountDto::getH5Id, H5FormCountDto::getCount));
            result.forEach(item -> {
                item.setHasForm(resultMap.get(item.getId()) != null ? 1 : 0);
            });
        }

        return new PageResult<>(result, page.getTotal());
    }


    @Override
    public H5Resp create(H5Info h5) {
        H5Resp resp = new H5Resp();
        checkREQ(h5);
        if (h5.getMsActivityId() != null) {
            checkH5ExistsForActivity(h5.getMsActivityId());
        }
        H5Do h5Do = new H5Do();
        BeanUtils.copyProperties(h5, h5Do);
        h5Do.setCustomerId(SessionContextUtil.getUserId());
        h5Do.setCreator(SessionContextUtil.getUserId());
        h5Do.setUpdater(SessionContextUtil.getUserId());
        h5Do.setCreateTime(new Date());
        h5Do.setUpdateTime(new Date());
        save(h5Do);
        if (h5.getMsActivityId() != null) {
            activityService.updateActivityH5Info(h5.getMsActivityId(), h5Do.getId());
        }
        // 生成短链接
        shortUrlApi.generateUrlByIdAndType(h5Do.getId(), "01");
        resp.setId(h5Do.getId());
        return resp;
    }

    private void checkH5ExistsForActivity(Long msActivityId) {
        List<H5Do> h5Dos = this.lambdaQuery().eq(H5Do::getMsActivityId, msActivityId).list();
        if (!CollectionUtils.isEmpty(h5Dos)) {
            throw new BizException("该活动已存在H5应用！");
        }
    }

    private void checkREQ(H5Info h5) {
        if (100 == h5.getPageDisplay()) {
            if (h5.getFlipType() == null) {
                throw new BizException("翻页方式不能为空");
            }
        }
    }

    @Override
    public H5Info previewData(Long id) {
        //1、检查H5是否存在
        H5Do h5Do = checkH5(id, SessionContextUtil.getUserId(), false);
        H5Info h5Info = new H5Info();
        BeanUtils.copyProperties(h5Do, h5Info);
        h5Info.setCreateTime(DateUtils.obtainDateStr(h5Do.getCreateTime()));
        return h5Info;

    }

    @Override
    public H5Info previewDataDetail(Long id) {
        //1、检查H5是否存在
        H5Do h5Do = checkH5(id);
        H5Info h5Info = new H5Info();
        BeanUtils.copyProperties(h5Do, h5Info);
        h5Info.setCreateTime(DateUtils.obtainDateStr(h5Do.getCreateTime()));
        return h5Info;
    }


    @Override
    public void update(H5Info h5Info) {
        //1、检查H5是否存在
        H5Do h5Do = checkH5(h5Info.getId(), SessionContextUtil.getUserId(), true);
        Long mssId = h5Do.getMssId();
        if (!h5Do.getStatus().equals(1)) {
            checkREQ(h5Info);
            BeanUtils.copyProperties(h5Info, h5Do);
            h5Do.setUpdateTime(new Date());
            h5Do.setUpdater(SessionContextUtil.getUserId());
            if (mssId != null) {
                MsSummary msSummary = msSummaryApi.getByMssId(mssId);
                msSummaryApi.notifyTemplateUpgrade(MsType.H5_FORM, msSummary.getMsId());
            }
            boolean success = updateById(h5Do);
        } else {
            throw new BizException("状态异常");
        }
    }

    @Override
    public void delete(Long id) {
        //1、检查H5是否存在
        H5Do h5Do = checkH5(id, SessionContextUtil.getUserId(), true);
        if (!h5Do.getStatus().equals(1)) {
            Long mssId = h5Do.getMssId();
            if (mssId != null) {
                MsSummary msSummary = msSummaryApi.getByMssId(mssId);
                if (Objects.nonNull(msSummary)) {
                    if (MsAuditStatus.WAIT.equals(msSummary.getAuditStatus())) {
                        throw new BizException(MallError.TEMPLATE_CAN_NOT_DELETE_WAIT);
                    }
                    if (MsAuditStatus.ACTIVE_ON.equals(msSummary.getAuditStatus())) {
                        throw new BizException(MallError.TEMPLATE_CAN_NOT_DELETE_ACTIVE_ON);
                    }
                }
                msSummaryApi.notifyTemplateDelete(MsType.H5_FORM, msSummary.getMsId());
            }
            removeById(id);
        } else {
            throw new BizException("状态异常，不能删除");
        }
    }

    @Override
    public H5CopyResp copy(H5Info h5Temp) {
        H5CopyResp resp = new H5CopyResp();
        Long id = h5Temp.getId();
        H5Do h5 = checkH5(id, SessionContextUtil.getUserId(), false);

        H5Do newH5 = new H5Do();
        newH5.setTitle(h5.getTitle() + "-副本");
        newH5.setH5Desc(h5.getH5Desc());
        newH5.setTpl(h5.getTpl());
        newH5.setGlobalStyle(h5.getGlobalStyle());
        newH5.setCustomerId(SessionContextUtil.getUserId());
        newH5.setCreateTime(new Date());
        newH5.setFormCover(h5.getFormCover());
        newH5.setFlipType(h5.getFlipType());
        newH5.setPageDisplay(h5.getPageDisplay());
        boolean flag = this.save(newH5);
        if (flag) {
            resp.setId(id);
        } else {
            throw new BizException("复制失败");
        }
        return resp;
    }

    @Override
    public void fromSubmit(H5FromSubmitReq req) {
        if (StringUtils.hasLength(req.getContent()) && JSONUtil.isTypeJSON(req.getContent())) {
            H5FormDo h5FormDo = new H5FormDo();
            h5FormDo.setContent(req.getContent());
            h5FormDo.setH5Id(req.getH5Id());
            h5FormDo.setCreator("visitor");
            h5FormDo.setCreateTime(new Date());
            h5FormDo.setUpdateTime(new Date());
            h5FormMapper.insert(h5FormDo);
        } else {
            throw new BizException("content必须是json字符串");
        }

    }

    @Override
    public H5Info getByActivityId(Long mcActivityId) {
        H5Do h5Do = this.lambdaQuery().eq(H5Do::getMsActivityId, mcActivityId).one();
        if (h5Do != null) {
            H5Info h5Info = new H5Info();
            BeanUtils.copyProperties(h5Do, h5Info);
            h5Info.setCreateTime(DateUtils.obtainDateStr(h5Do.getCreateTime()));
            return h5Info;
        } else {
            throw new BizException("H5应用不存在！");
        }

    }

    @Override
    public List<H5TemplateInfo> h5TemplateListQuery(H5TemplateListQueryReq req) {
        List<H5Do> listAll = new ArrayList<>();
        List<H5TemplateInfo> result = new ArrayList<>();

        if (req.getId() != null) {
            H5Do h5GetById = this.getById(req.getId());
            if (h5GetById != null) {
                listAll.add(h5GetById);
            } else {
                H5OfSummeryDo h5OfSummeryDo = h5OfSummeryMapper.selectById(req.getId());
                if (h5OfSummeryDo != null) {
                    h5GetById = this.getById(h5OfSummeryDo.getH5Id());
                    if (h5GetById != null) {
                        h5GetById.setId(Long.valueOf(req.getId()));
                        listAll.add(h5GetById);
                    }
                }
            }
        }
        //查询列表
        List<H5Do> list = this.lambdaQuery().eq(H5Do::getCustomerId, SessionContextUtil.getUserId()).isNull(H5Do::getMssId).orderByDesc(H5Do::getCreateTime).list();
        if (!CollectionUtils.isEmpty(list)) {
            listAll.addAll(list);
        }

        listAll.forEach(item -> {
            H5TemplateInfo h5TemplateInfo = new H5TemplateInfo();
            h5TemplateInfo.setId(item.getId());
            h5TemplateInfo.setFormName(item.getTitle());
            h5TemplateInfo.setFormCover(item.getFormCover());
            h5TemplateInfo.setCreateTime(item.getCreateTime());
            BeanUtils.copyProperties(item, h5TemplateInfo);
            h5TemplateInfo.setFormShareUrl(formUrlConfigure.getShareUrl() + item.getId());
            h5TemplateInfo.setShortUrl(formUrlConfigure.getShortUrl() + getShortUrl(item.getId()));

            result.add(h5TemplateInfo);

        });
        return result;
    }

    private String getShortUrl(Long h5Id) {
        //查询短连接
        String url = shortUrlApi.getShortUrlByIdAndType(h5Id, "01");
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(url)) {
            return url;
        } else {
            log.warn("短链接为空 表单id: {}", h5Id);
            return null;
        }
    }

    @Override
    public PageResult<ResourcesForm> pageListForCSP(FormPageQuery query) {
        Page<H5Do> page = new Page<>(query.getPageNo(), query.getPageSize());
        page(page, new LambdaQueryWrapper<H5Do>()
                .eq(H5Do::getCustomerId, SessionContextUtil.getUserId())
                .orderByDesc(H5Do::getCreateTime)
        );

        List<ResourcesForm> result = page.getRecords().stream()
                .map(item -> {
                    ResourcesForm info = new ResourcesForm();
                    info.setId(item.getId());
                    info.setFormName(item.getTitle());
                    info.setFormDetails(item.getTpl());
                    info.setFormCover(item.getFormCover());
                    info.setCreator(item.getCreator());
                    info.setCreateTime(item.getCreateTime());
                    info.setMssId(item.getMssId());
                    info.setUpdateTime(item.getUpdateTime());
                    info.setUpdater(item.getUpdater());
                    return info;
                }).collect(Collectors.toList());

        return new PageResult<>(result, page.getTotal());
    }

    @Override
    public List<FormManagementTreeResp> getTreeListForCustomer() {
        List<H5Do> list = this.lambdaQuery().eq(H5Do::getCustomerId, SessionContextUtil.getUserId())
                .orderByDesc(H5Do::getCreateTime).list();
        return list.stream().map(formManagementDo -> {
            FormManagementTreeResp formManagementTreeResp = new FormManagementTreeResp();
            formManagementTreeResp.setId(formManagementDo.getId());
            formManagementTreeResp.setFormName(formManagementDo.getTitle());
            formManagementTreeResp.setFormShareUrl(formUrlConfigure.getShareUrl() + formManagementDo.getId());
            //查询短连接
            formManagementTreeResp.setShortUrl(formUrlConfigure.getShortUrl() + getShortUrl(formManagementDo.getId()));
            return formManagementTreeResp;
        }).collect(Collectors.toList());

    }

    @Override
    public void updateMssID(String msId, Long mssId) {
        this.lambdaUpdate().eq(H5Do::getMssId, mssId).set(H5Do::getMssId, null).update();
        H5Do h5GetById = this.getById(msId);
        if (h5GetById == null) {
            H5OfSummeryDo h5OfSummeryDo = h5OfSummeryMapper.selectById(msId);
            if (h5OfSummeryDo != null) {
                h5GetById = this.getById(h5OfSummeryDo.getH5Id());
            }
        }

        if (h5GetById != null) {
            h5GetById.setMssId(mssId);
            updateById(h5GetById);
        } else {
            throw new BizException("H5模版不存在！");
        }
    }

    @Override
    public void saveForCustomer(Long msId, String userId, Long mssId) {
        H5OfSummeryDo h5OfSummeryDo = h5OfSummeryMapper.selectById(msId);
        if (h5OfSummeryDo != null) {
            H5Do goods = new H5Do();
            BeanUtils.copyProperties(h5OfSummeryDo, goods);
            goods.setCustomerId(userId);
            goods.setMssIdForBuy(mssId);
            goods.setId(null);
            goods.setMssId(null);
            goods.setUpdater(userId);
            goods.setUpdateTime(new Date());
            this.saveOrUpdate(goods);
        } else {
            throw new BizException("H5应用不存在！");
        }
    }

    @Override
    public void formDeleteInfo(Long id) {
        H5FormDo h5FormDo = h5FormMapper.selectById(id);
        if (h5FormDo != null) {
            h5FormDo.setUpdater(SessionContextUtil.getUserId());
            h5FormDo.setUpdateTime(new Date());
            h5FormMapper.updateById(h5FormDo);
            h5FormMapper.deleteById(id);
        } else {
            throw new BizException("表单内容不存在！");
        }
    }

    @Override
    public PageResult<H5FormInfo> formQueryList(FormInfoPageQuery query) {
        PageResult<H5FormInfo> result;
        Page<H5FormDo> page = new Page<>(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<H5FormDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(H5FormDo::getH5Id, query.getH5Id());
        queryWrapper.orderByDesc(H5FormDo::getCreateTime);
        Page<H5FormDo> h5FormDoPage = h5FormMapper.selectPage(page, queryWrapper);
        List<H5FormDo> records = h5FormDoPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<H5FormInfo> h5FormInfoList = records.stream().map(item -> {
                H5FormInfo h5FormInfo = new H5FormInfo();
                h5FormInfo.setId(item.getId());
                h5FormInfo.setContent(item.getContent());
                h5FormInfo.setCreateTime(item.getCreateTime());
                return h5FormInfo;
            }).collect(Collectors.toList());
            result = new PageResult<>(h5FormInfoList, records.size());
        } else {
            result = new PageResult<>(new ArrayList<>(), 0);
        }
        return result;
    }

    @Override
    public void deleteMssIDForIds(List<Long> h5OfSummeryIdList) {
        List<H5OfSummeryDo> h5OfSummeryDos = h5OfSummeryMapper.selectBatchIds(h5OfSummeryIdList);
        if (!CollectionUtils.isEmpty(h5OfSummeryDos)) {
            List<Long> h5IdList = h5OfSummeryDos.stream().map(H5OfSummeryDo::getH5Id).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(h5IdList)) {
                baseMapper.updateMssIDNullById(h5IdList);
            }
        }
    }

    @Override
    public List<H5Info> byIds(H5InfoListQuery query) {
        List<H5Info> result = new ArrayList<>();
        List<Long> ids = query.getIds();
        if (!CollectionUtils.isEmpty(ids)) {
            List<H5Do> h5Dos = this.lambdaQuery().in(H5Do::getId, ids).list();
            if (!CollectionUtils.isEmpty(h5Dos)) {
                result = h5Dos.stream().map(item -> {
                    H5Info info = new H5Info();
                    BeanUtils.copyProperties(item, info);
                    info.setCreateTime(DateUtils.obtainDateStr(item.getCreateTime()));
                    return info;
                }).collect(Collectors.toList());
            }
        }
        return result;
    }

    @Override
    public Long createSummeryH5(String h5Id) {
        H5Do one = this.lambdaQuery().eq(H5Do::getId, h5Id).one();
        if (one == null) {
            H5OfSummeryDo h5OfSummeryDo = h5OfSummeryMapper.selectById(h5Id);
            if (h5OfSummeryDo == null) {
                throw new BizException("H5应用不存在！");
            } else {
                return h5OfSummeryDo.getId();
            }
        }
        H5OfSummeryDo h5OfSummeryDo = new H5OfSummeryDo();
        BeanUtils.copyProperties(one, h5OfSummeryDo);
        h5OfSummeryDo.setH5Id(one.getId());
        h5OfSummeryDo.setId(null);
        h5OfSummeryMapper.insert(h5OfSummeryDo);
        return h5OfSummeryDo.getId();
    }

    @Override
    public H5Info getDetailForSummery(Long msId) {
        H5Info h5Info = new H5Info();
        H5OfSummeryDo h5OfSummeryDo = h5OfSummeryMapper.selectById(msId);
        if (h5OfSummeryDo != null) {
            BeanUtils.copyProperties(h5OfSummeryDo, h5Info);
            h5Info.setId(h5OfSummeryDo.getH5Id());
        }
        return h5Info;
    }

    @Override
    public void updateSummaryH5(String msId) {
        H5OfSummeryDo h5OfSummeryDo = h5OfSummeryMapper.selectById(msId);
        if (h5OfSummeryDo != null) {
            H5Do h5Do = this.lambdaQuery().eq(H5Do::getId, h5OfSummeryDo.getH5Id()).one();
            if (h5Do != null) {
                BeanUtils.copyProperties(h5Do, h5OfSummeryDo);
                h5OfSummeryDo.setH5Id(h5Do.getId());
                h5OfSummeryMapper.updateById(h5OfSummeryDo);
            } else {
                throw new BizException("H5应用不存在！");
            }
        } else {
            throw new BizException("未找到对应的H5备份记录！");
        }
    }

    @Override
    public void importH5ForCustomer(Long mssId, String userId, String name) {
        H5Do h5Do = this.lambdaQuery().eq(H5Do::getCustomerId, userId).eq(H5Do::getMssIdForBuy, mssId).one();
        if (h5Do != null) {
            H5Do customerH5 = new H5Do();
            BeanUtils.copyProperties(h5Do, customerH5);
            customerH5.setId(null);
            customerH5.setMssIdForBuy(0L);
            customerH5.setTitle(name);
            customerH5.setCreateTime(new Date());
            customerH5.setUpdater(userId);
            customerH5.setUpdateTime(new Date());
            this.saveOrUpdate(customerH5);
        } else {
            throw new BizException("H5应用不存在！");
        }
    }

    @Override
    public Map<Long, Csp4CustomerFrom> importH5ForRobotAndMSG(List<Long> h5Ids) {
        Map<Long, Csp4CustomerFrom> result = new HashMap<>();
        if(!CollectionUtils.isEmpty(h5Ids)){
            //复制每个h5
            for(Long h5Id : h5Ids){
                H5Do h5Do = this.lambdaQuery().eq(H5Do::getId, h5Id).one();
                if (h5Do != null) {
                    String userId = SessionContextUtil.getUserId();
                    H5Do customerH5 = new H5Do();
                    BeanUtils.copyProperties(h5Do, customerH5);
                    customerH5.setId(null);
                    customerH5.setMssIdForBuy(0L);
                    customerH5.setCustomerId(userId);
                    customerH5.setUpdater(userId);
                    customerH5.setUpdateTime(new Date());
                    this.saveOrUpdate(customerH5);
                    //返回结果
                    Long newId = customerH5.getId();
                    Csp4CustomerFrom csp4CustomerFrom = new Csp4CustomerFrom();
                    csp4CustomerFrom.setNewId(newId);
                    csp4CustomerFrom.setId(newId);
                    csp4CustomerFrom.setFormName(customerH5.getTitle());
                    csp4CustomerFrom.setFormCover(customerH5.getFormCover());
                    csp4CustomerFrom.setFormStatus(2);
                    csp4CustomerFrom.setFormDetails(customerH5.getTpl());
                    csp4CustomerFrom.setFormShareUrl(formUrlConfigure.getShareUrl() + newId);
                    //为每个h5生成短链
                    csp4CustomerFrom.setShortUrl(formUrlConfigure.getShortUrl() + shortUrlApi.generateUrlByIdAndType(newId, "01"));
                    result.put(h5Id, csp4CustomerFrom);
                }

            }
        }
        return result;
    }




    private H5Do checkH5(Long h5Id, String customerId, boolean isUpdateOrDelete) {
        //如果是管理员，则可以查看全部
        H5Do one;
        AdminUserInfoResp adminUserInfoByUserId = adminAuthApi.getAdminUserInfoByUserId(customerId);
        if (adminUserInfoByUserId.getPhone() != null) {
            one = this.lambdaQuery().eq(H5Do::getId, h5Id).one();
            if (isUpdateOrDelete) {//管理员只能去删除或者更新管理员创建的H5
                AdminUserInfoResp adminUserInfoForH5 = adminAuthApi.getAdminUserInfoByUserId(one.getCustomerId());
                if (adminUserInfoForH5.getPhone() == null) {
                    throw new BizException("越权操作！");
                }
            }
        } else {
            one = this.lambdaQuery().eq(H5Do::getId, h5Id).eq(H5Do::getCustomerId, customerId).one();
        }

        if (one == null) {
            throw new BizException("H5应用不存在！");
        }
        return one;
    }

    private H5Do checkH5(Long h5Id) {
        H5Do one = this.lambdaQuery().eq(H5Do::getId, h5Id).one();
        if (one == null) {
            throw new BizException("H5应用不存在！");
        }
        return one;
    }

}
