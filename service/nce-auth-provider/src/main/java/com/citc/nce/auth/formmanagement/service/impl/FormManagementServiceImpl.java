package com.citc.nce.auth.formmanagement.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.configure.FormUrlConfigure;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.formmanagement.dao.FormManagementDao;
import com.citc.nce.auth.formmanagement.entity.FormManagementDo;
import com.citc.nce.auth.formmanagement.exception.FormManagementCode;
import com.citc.nce.auth.formmanagement.service.FormManagementService;
import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.*;
import com.citc.nce.auth.submitdata.dao.SubmitDataDao;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.shortUrl.ShortUrlApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:32
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({SuperAdministratorUserIdConfigure.class, FormUrlConfigure.class})
public class FormManagementServiceImpl implements FormManagementService {

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    private final FormUrlConfigure formUrlConfigure;
    @Resource
    private FormManagementDao formManagementDao;

    @Resource
    private SubmitDataDao submitDataDao;


    @Autowired
    private ShortUrlApi shortUrlApi;

    @Override
    public PageResultResp getFormManagements(PageParam pageParam) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        PageResult<FormManagementDo> FormManagementDoPageResult = formManagementDao.selectPage(pageParam, wrapper);
        List<FormManagementResp> FormManagementResps = BeanUtil.copyToList(FormManagementDoPageResult.getList(), FormManagementResp.class);
        List<Map<String, Long>> maps = submitDataDao.selectCountByFormId();
        HashMap<Long, Long> vMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Long> map : maps) {
                Long formId = map.get("formId");
                Long nums = map.get("nums");
                vMap.put(formId, nums);
            }
        }
        for (FormManagementResp formManagementResp : FormManagementResps) {
            if (vMap.get(formManagementResp.getId()) == null) {
                formManagementResp.setSubmitNum(0L);
            } else {
                formManagementResp.setSubmitNum(vMap.get(formManagementResp.getId()));
            }
        }
        return new PageResultResp(FormManagementResps, FormManagementDoPageResult.getTotal(), pageParam.getPageNo());
    }

    @Override
    public FormManagementSaveResp saveFormManagement(FormManagementReq formManagementReq) {
        /**
         * 2023-12-6
         * 模板与订单的需求 现在允许表单名称重复
         */
        FormManagementDo formManagementDo = new FormManagementDo();
        BeanUtil.copyProperties(formManagementReq, formManagementDo);
        int insert = formManagementDao.insert(formManagementDo);
        FormManagementSaveResp formManagementSaveResp = new FormManagementSaveResp();
        formManagementSaveResp.setId(formManagementDo.getId());
        formManagementSaveResp.setSuccess(insert);
        // 生成短链接
        shortUrlApi.generateUrlByIdAndType(formManagementDo.getId(), "01");
        return formManagementSaveResp;
    }

    @Override
    public int updateFormManagement(FormManagementEditReq formManagementEditReq) {
        /**
         * 2023-12-6
         * 模板与订单的需求 现在允许表单名称重复
         */
        FormManagementDo from = formManagementDao.selectById(formManagementEditReq.getId());
        if (Objects.isNull(from)) {
            throw new BizException("表单不存在或已删除");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(from.getCreator())) {
            throw new BizException("不能修改别人的表单");
        }
        FormManagementDo formManagementDo = new FormManagementDo();
        BeanUtil.copyProperties(formManagementEditReq, formManagementDo);
        return formManagementDao.updateById(formManagementDo);
    }

    @Override
    public int delFormManagementById(FormManagementOneReq formManagementOneReq) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", formManagementOneReq.getId());
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return formManagementDao.delFormManagementById(map);
    }

    @Override
    public FormManagementResp getFormManagementById(FormManagementOneReq formManagementOneReq) {
        FormManagementDo formManagementDo = formManagementDao.selectOne(Wrappers.<FormManagementDo>lambdaQuery()
                .eq(FormManagementDo::getId, formManagementOneReq.getId()));
        if (formManagementDo == null) {
            return null;
        }
        FormManagementResp FormManagementResp = BeanUtil.copyProperties(formManagementDo, FormManagementResp.class);
        return FormManagementResp;
    }

    @Override
    public List<FormManagementTreeResp> getTreeList() {

        QueryWrapper<FormManagementDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.eq("form_status", 2);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        List<FormManagementDo> FormManagementDos = formManagementDao.selectList(wrapper);

        return FormManagementDos.stream().map(formManagementDo -> {
            FormManagementTreeResp formManagementTreeResp = new FormManagementTreeResp();
            formManagementTreeResp.setId(formManagementDo.getId());
            formManagementTreeResp.setFormName(formManagementDo.getFormName());
            formManagementTreeResp.setFormShareUrl(formUrlConfigure.getShareUrl() + formManagementDo.getId());
            //查询短连接
            String url = shortUrlApi.getShortUrlByIdAndType(formManagementDo.getId(), "01");
            if (StringUtils.isNotBlank(url)) {
                formManagementTreeResp.setShortUrl(formUrlConfigure.getShortUrl() + url);
            } else {
                log.warn("短链接为空 表单id: {}", formManagementDo.getId());
            }
            return formManagementTreeResp;
        }).collect(Collectors.toList());
    }

    @Override
    public FormManagementResp getEdit(FormManagementOneReq formManagementOneReq) {
        FormManagementDo formManagementDo = formManagementDao.selectOne(Wrappers.<FormManagementDo>lambdaQuery()
                .eq(FormManagementDo::getId, formManagementOneReq.getId()));
        if (Objects.isNull(formManagementDo)) return null;
        if (!SessionContextUtil.getLoginUser().getUserId().equals(formManagementDo.getCreator())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

        FormManagementResp FormManagementResp = BeanUtil.copyProperties(formManagementDo, FormManagementResp.class);
        return FormManagementResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveList(List<FormManagementReq> list) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
            return 0;
        }
        List<FormManagementDo> formManagementDos = BeanUtil.copyToList(list, FormManagementDo.class);
        formManagementDao.insertBatch(formManagementDos);
//        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(formManagementDos)) {
//            for (FormManagementDo formManagementDo:
//                    formManagementDos) {
//                // 生成短链接
//                shortUrlApi.generateUrlByIdAndType(formManagementDo.getId(), "01");
//            }
//        }
        return 0;
    }

    @Override
    public Map<Long, Csp4CustomerFrom> saveListCsp4CustomerFrom(List<Csp4CustomerFrom> list) {
        if (CollectionUtils.isEmpty(list)) return new HashMap<>();
        List<FormManagementDo> insertList = list.stream().map(s -> {
            FormManagementDo var = new FormManagementDo();
            var.setFormName(s.getFormName());
            var.setFormCover(s.getFormCover());
            var.setFormDetails(s.getFormDetails());
            var.setFormDraft(s.getFormDraft());
            var.setFormStatus(2); // 2已发布，1待发布，0未发布
            var.setOldId(s.getId()); // 备份老id
            return var;
        }).collect(Collectors.toList());

        HashMap<Long, String> shortUrlMap = new HashMap<>();
        insertList.forEach(s -> {
            formManagementDao.insert(s);
            // 生成短链接
            shortUrlMap.put(s.getId(), formUrlConfigure.getShortUrl() + shortUrlApi.generateUrlByIdAndType(s.getId(), "01"));
        });
        Map<Long, Csp4CustomerFrom> map = list.stream().collect(Collectors.toMap(Csp4CustomerFrom::getId, Function.identity()));
        for (FormManagementDo var : insertList) {
            Csp4CustomerFrom csp4CustomerFrom = map.get(var.getOldId());
            Long newId = var.getId();
            csp4CustomerFrom.setNewId(newId);
            csp4CustomerFrom.setFormShareUrl(formUrlConfigure.getShareUrl() + newId);
            csp4CustomerFrom.setShortUrl(shortUrlMap.get(newId));
        }
        return map;
    }
}
