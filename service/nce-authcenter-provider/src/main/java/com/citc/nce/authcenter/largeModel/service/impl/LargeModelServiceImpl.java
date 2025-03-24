package com.citc.nce.authcenter.largeModel.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.largeModel.dao.LargeModelDao;
import com.citc.nce.authcenter.largeModel.dao.LargeModelPromptDao;
import com.citc.nce.authcenter.largeModel.entity.LargeModelDo;
import com.citc.nce.authcenter.largeModel.entity.LargeModelPromptDo;
import com.citc.nce.authcenter.largeModel.service.LargeModelService;
import com.citc.nce.authcenter.largeModel.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class LargeModelServiceImpl implements LargeModelService {

    @Resource
    private LargeModelDao largeModelDao;

    @Resource
    private LargeModelPromptDao largeModelPromptDao;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<LargeModelResp> getLargeModelList(LargeModelReq req) {
        Page<LargeModelDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<LargeModelDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LargeModelDo::getDeleted, 0);
        wrapper.orderByDesc(LargeModelDo::getCreateTime);
        Page<LargeModelDo> selectPage = largeModelDao.selectPage(page, wrapper);
        List<LargeModelDo> list = selectPage.getRecords();
        List<LargeModelResp> result = new ArrayList<>();
        list.forEach(largeModelDo -> {
            LargeModelResp resp = new LargeModelResp();
            BeanUtils.copyProperties(largeModelDo, resp);
            result.add(resp);
        });
        PageResult<LargeModelResp> pageResult = new PageResult<>();
        pageResult.setList(result);
        pageResult.setTotal(selectPage.getTotal());
        return pageResult;
    }

    @Override
    public LargeModelResp getLargeModeDetail(Long id) {
        LargeModelResp resp = new LargeModelResp();
        LargeModelDo largeModelDo = largeModelDao.selectById(id);
        BeanUtils.copyProperties(largeModelDo, resp);
        return resp;
    }

    @Override
    public void createLargeModel(LargeModelCreateReq req) {
        LargeModelDo largeModelDo = new LargeModelDo();
        // 校验api-key以及secret-key
        Boolean status = testWenxinSetting(req.getApiKey(), req.getSecretKey());
        if (!status) {
            throw new BizException(820401002, "APIKey, SecretKey填写错误，保存失败");
        }
        BeanUtils.copyProperties(req, largeModelDo);
        largeModelDao.insert(largeModelDo);
    }

    @Override
    public void updateLargeModel(LargeModelUpdateReq req) {
        LargeModelDo largeModelDo = new LargeModelDo();
        Boolean status = testWenxinSetting(req.getApiKey(), req.getSecretKey());
        if (!status) {
            throw new BizException(820401002, "APIKey, SecretKey填写错误，保存失败");
        }
        BeanUtils.copyProperties(req, largeModelDo);
        largeModelDao.updateById(largeModelDo);
        Boolean delete = stringRedisTemplate.delete("im:baiduWenxinToken:" + largeModelDo.getId());
        int num = Boolean.TRUE.equals(delete) ?1:0;
        log.info("百度token删除{}个", num);
    }

    @Override
    public void deleteLargeModel(LargeModelDetailReq req) {
        // 查询是否可以删除
        LambdaQueryWrapper<LargeModelPromptDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LargeModelPromptDo::getModelId, req.getId());
        boolean haveModel = largeModelPromptDao.exists(queryWrapper);
        if (haveModel) {
            throw new BizException(820401001, "当前模型已被绑定，无法删除");
        }
        largeModelDao.deleteById(req.getId());
        Boolean delete = stringRedisTemplate.delete("im:baiduWenxinToken:"+req.getId());
        int num = Boolean.TRUE.equals(delete) ?1:0;
        log.info("百度token删除{}个", num);
    }

    @Override
    public PageResult<PromptResp> getPromptList(PromptReq req) {
        req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
        Long total = largeModelPromptDao.queryListCount(req);
        PageResult<PromptResp> pageResult = new PageResult<>();
        if (total > 0) {
            List<PromptResp> list = largeModelPromptDao.queryList(req);
            pageResult.setList(list);
        }
        pageResult.setTotal(total);
        return pageResult;
    }

    @Override
    public PromptDetailResp getPromptDetail(Long id) {
        return largeModelPromptDao.queryDetail(id);
    }

    @Override
    public void updatePrompt(PromptUpdateReq req) {
        LargeModelPromptDo largeModelPromptDo = new LargeModelPromptDo();
        LambdaQueryWrapper<LargeModelDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LargeModelDo::getId, req.getModelId());
        boolean haveModel = largeModelDao.exists(queryWrapper);
        if (!haveModel) {
            throw new BizException(820401001, "模型已被删除，无法保存");
        }
        BeanUtils.copyProperties(req, largeModelPromptDo);
        largeModelPromptDao.updateById(largeModelPromptDo);
    }

    @Override
    public void updatePromptStatus(PromptUpdateStatusReq req) {
        LargeModelPromptDo largeModelPromptDo = new LargeModelPromptDo();
        largeModelPromptDo.setId(req.getId());
        largeModelPromptDo.setStatus(req.getStatus());
        largeModelPromptDao.updateById(largeModelPromptDo);
    }

    @Override
    public LargeModelPromptSettingResp getLargeModelPromptSettingByChatbotAccountId() {
        //查询大模型promote
        LambdaQueryWrapper<LargeModelPromptDo> eq = new LambdaQueryWrapper<LargeModelPromptDo>()
                .eq(LargeModelPromptDo::getStatus, 1)
                .eq(LargeModelPromptDo::getDeleted, 0);
        LargeModelPromptDo largeModelPromptDo = largeModelPromptDao.selectOne(eq);
        //如果已经禁用
        if (Objects.isNull(largeModelPromptDo) || Objects.isNull(largeModelPromptDo.getModelId())) {
            return null;
        }
        LargeModelPromptSettingResp largeModelPromptSettingResp = new LargeModelPromptSettingResp();
        LargeModelResp largeModelResp = new LargeModelResp();
        //查询大模型设置
        LambdaQueryWrapper<LargeModelDo> largeModelEq = new LambdaQueryWrapper<LargeModelDo>()
                .eq(LargeModelDo::getId, largeModelPromptDo.getModelId())
                .eq(LargeModelDo::getDeleted, 0);
        LargeModelDo largeModelDo = largeModelDao.selectOne(largeModelEq);
        if (Objects.nonNull(largeModelDo)) {
            largeModelResp.setApiKey(largeModelDo.getApiKey());
            largeModelResp.setId(largeModelDo.getId());
            largeModelResp.setSecretKey(largeModelDo.getSecretKey());
            largeModelResp.setApiUrl(largeModelDo.getApiUrl());
        }
        largeModelPromptSettingResp.setSettings(largeModelPromptDo.getPromptSetting());
        largeModelPromptSettingResp.setExamples(largeModelPromptDo.getPromptExample());
        largeModelPromptSettingResp.setRuleAndFormats(largeModelPromptDo.getPromptRule());
        largeModelPromptSettingResp.setLargeModelResp(largeModelResp);
        return largeModelPromptSettingResp;
    }

    @Override
    public PromptStatusResp getPromptStatus() {
        PromptStatusResp resp = new PromptStatusResp();
        // 默认不展示
        resp.setTextRecognitionStatus(0);
        List<LargeModelPromptDo> promptDoList = largeModelPromptDao.selectList();
        promptDoList.forEach(prompt -> {
            if (prompt.getType() == 1) {
                resp.setTextRecognitionStatus(prompt.getStatus());
            }
        });
        return resp;
    }

    private Boolean testWenxinSetting(String apiKey, String secretKey) {
        String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" +
                apiKey +
                "&client_secret=" +
                secretKey;
        HttpResponse execute = HttpUtil.createPost(tokenUrl)
                .header("Content-Type", "application/json")
                .execute();
        int status = execute.getStatus();
        return status == 200;
    }

}
