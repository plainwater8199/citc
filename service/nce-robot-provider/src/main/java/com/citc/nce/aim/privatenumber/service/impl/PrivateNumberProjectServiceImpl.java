package com.citc.nce.aim.privatenumber.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.constant.EventTypeEnum;
import com.citc.nce.aim.dao.AimTestDataDao;
import com.citc.nce.aim.entity.AimTestDataDo;
import com.citc.nce.aim.privatenumber.Constants;
import com.citc.nce.aim.privatenumber.dao.PrivateNumberProjectDao;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberProjectDo;
import com.citc.nce.aim.privatenumber.service.PrivateNumberDealRedisCacheService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberOrderService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberProjectService;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderResp;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectEditReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectQueryListReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectQueryReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectResp;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectSaveReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectUpdateStatusReq;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberProjectTestReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberProjectTestResp;
import com.citc.nce.aim.utils.api.AimCallUtils;
import com.citc.nce.aim.vo.ResponseData;
import com.citc.nce.aim.vo.ResponseInfo;
import com.citc.nce.authcenter.captcha.CaptchaApi;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrivateNumberProjectServiceImpl implements PrivateNumberProjectService {

    @Resource
    private PrivateNumberProjectDao privateNumberProjectDao;

    @Resource
    private PrivateNumberOrderService privateNumberOrderService;
    @Lazy
    @Resource
    private PrivateNumberDealRedisCacheService privateNumberDealRedisCacheService;

    @Resource
    private CaptchaApi captchaApi;

    @Resource
    private AimCallUtils aimCallUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(PrivateNumberProjectSaveReq req) {
        // 校验名称
        checkProjectName(req.getProjectName(), null);
        // 校验客户appkey+事件类型是否唯一
        checkAppKeyAndEventType(req.getAppKey(), req.getEventType(), null);
        // 校验客户号码
        //checkAppKey(req.getAppKey(), null);
        checkOther(req);
        // 校验通道账号密码
        aimCallUtils.doValidate(req.getPathKey(), req.getSecret());
        // 插入数据
        PrivateNumberProjectDo insert = new PrivateNumberProjectDo();
        BeanUtils.copyProperties(req, insert);
        insert.setProjectId(aimCallUtils.generateProjectId());
        insert.setCreateTime(new Date());
        insert.setCreator(SessionContextUtil.getUserId());
        return privateNumberProjectDao.insert(insert);
    }

    void checkOther(PrivateNumberProjectSaveReq req) {
        String error = "";
        if (ObjectUtil.isNull(req.getYuexinLinkEnable())) {
            throw new BizException("请选择是否包含阅信短链");
        }
        if (StrUtil.isEmpty(req.getChannelType())) {
            throw new BizException("请选择通道类型");
        }
        if (StrUtil.equals(Constants.CHANNEL_TYPE_EMAY, req.getChannelType())) {
            if (ObjectUtil.isNull(req.getPathKey())) {
                error += ",通道账号";
            }
            if (ObjectUtil.isNull(req.getSecret())) {
                error += ",通道秘钥";
            }
        } else {
            if (ObjectUtil.isNull(req.getAppId())) {
                error += ",appId";
            }
            if (ObjectUtil.isNull(req.getAppSecret())) {
                error += ",appSecret";
            }
        }
        if (!EventTypeEnum.isValidCode(req.getEventType())) {
            error += ",事件类型";
        }
        if (StrUtil.isNotEmpty(error)) {
            throw new BizException(error.substring(1) + "未填写");
        }
    }

    void checkOther(PrivateNumberProjectEditReq req) {
        String error = "";
        if (ObjectUtil.isNull(req.getYuexinLinkEnable())) {
            throw new BizException("请选择是否包含阅信短链");
        }
        if (StrUtil.isEmpty(req.getChannelType())) {
            throw new BizException("请选择通道类型");
        }
        if (StrUtil.equals(Constants.CHANNEL_TYPE_EMAY, req.getChannelType())) {
            if (ObjectUtil.isNull(req.getPathKey())) {
                error += ",通道账号";
            }
            if (ObjectUtil.isNull(req.getSecret())) {
                error += ",通道秘钥";
            }
        } else {
            if (ObjectUtil.isNull(req.getAppId())) {
                error += ",appId";
            }
            if (ObjectUtil.isNull(req.getAppSecret())) {
                error += ",appSecret";
            }
        }
        if (StrUtil.isNotEmpty(error)) {
            throw new BizException(error.substring(1) + "未填写");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(PrivateNumberProjectEditReq req) {
        // 校验名称
        checkProjectName(req.getProjectName(), req.getId());
        // 校验客户appkey+事件类型是否唯一
        checkAppKeyAndEventType(req.getAppKey(), req.getEventType(), req.getId());

        checkOther(req);
        PrivateNumberProjectDo privateNumberProjectDo = privateNumberProjectDao.selectById(req.getId());
        if (null != privateNumberProjectDo) {
            // 如果用户没有改通道的数据，就不进行验证 ,亿美通道校验
            if (AimConstant.SMS_TYPE.equals(req.getChannelType()) && !StringUtils.equals(privateNumberProjectDo.getPathKey(), req.getPathKey()) || !StringUtils.equals(privateNumberProjectDo.getSecret(), req.getSecret())) {
                // 校验通道账号密码
                aimCallUtils.doValidate(req.getPathKey(), req.getSecret());
            }
        } else {
            throw new BizException(AimError.PROJECT_NOT_EXIST);
        }
        PrivateNumberProjectDo update = new PrivateNumberProjectDo();
        BeanUtils.copyProperties(req, update);
        update.setUpdater(SessionContextUtil.getUserId());
        update.setUpdateTime(new Date());
        int updateResult = privateNumberProjectDao.updateById(update);
        if (updateResult > 0) {
            privateNumberDealRedisCacheService.updateProjectRedisCache(update);
        }
        return updateResult;
    }

    @Override
    public PrivateNumberProjectResp queryProject(PrivateNumberProjectQueryReq req) {
        PrivateNumberProjectResp resp = new PrivateNumberProjectResp();
        LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (0 != req.getId()) {
            queryWrapperX.eq(PrivateNumberProjectDo::getId, req.getId());
        }
        if (StringUtils.isNotEmpty(req.getProjectId())) {
            queryWrapperX.eq(PrivateNumberProjectDo::getProjectId, req.getProjectId());
        }
        if (StringUtils.isNotEmpty(req.getAppKey())) {
            queryWrapperX.eq(PrivateNumberProjectDo::getAppKey, req.getAppKey());
        }
        if (1 == req.getProjectStatus()) {
            queryWrapperX.eq(PrivateNumberProjectDo::getProjectStatus, req.getProjectStatus());
        }
        List<PrivateNumberProjectDo> aimProjectDoList = privateNumberProjectDao.selectList(queryWrapperX);
        if (!CollectionUtils.isEmpty(aimProjectDoList)) {
            BeanUtils.copyProperties(aimProjectDoList.get(0), resp);
        }
        return resp;
    }

    @Override
    public PageResult<PrivateNumberProjectResp> queryProjectList(PrivateNumberProjectQueryListReq req) {
        PageResult<PrivateNumberProjectResp> resp = new PageResult<>();

        // 创建分页对象
        Page<PrivateNumberProjectDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (Arrays.asList(0, 1).contains(req.getProjectStatus())) {
            queryWrapperX.eq(PrivateNumberProjectDo::getProjectStatus, req.getProjectStatus());
        }
        if (StringUtils.isNotEmpty(req.getQueryString())) {
            queryWrapperX.like(PrivateNumberProjectDo::getProjectName, req.getQueryString());
        }
        if (StringUtils.isNotEmpty(req.getAppKey())) {
            queryWrapperX.like(PrivateNumberProjectDo::getAppKey, req.getAppKey());
        }
        queryWrapperX.orderByDesc(PrivateNumberProjectDo::getCreateTime);
        // 执行分页查询
        Page<PrivateNumberProjectDo> pageResult = privateNumberProjectDao.selectPage(page, queryWrapperX);

        // 将查询结果转换为响应对象
        List<PrivateNumberProjectResp> respList = pageResult.getRecords().stream()
                .map(record -> {
                    PrivateNumberProjectResp respItem = new PrivateNumberProjectResp();
                    BeanUtils.copyProperties(record, respItem);
                    return respItem;
                })
                .collect(Collectors.toList());

        // 设置分页结果
        resp.setList(respList);
        resp.setTotal(pageResult.getTotal());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(PrivateNumberProjectUpdateStatusReq req) {
        LambdaUpdateWrapper<PrivateNumberProjectDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrivateNumberProjectDo::getProjectId, req.getProjectId());
        List<PrivateNumberProjectDo> privateNumberProjectDos = privateNumberProjectDao.selectList(updateWrapper);
        if (!CollectionUtils.isEmpty(privateNumberProjectDos)) {
            PrivateNumberProjectDo privateNumberProjectDo = privateNumberProjectDos.get(0);
            if (req.getProjectStatus() != privateNumberProjectDo.getProjectStatus()) {
                PrivateNumberOrderResp orderInfo = privateNumberOrderService.queryEnabledOrderByProjectId(privateNumberProjectDo.getProjectId());
                // 项目被禁用时需要删除redis数据
                if (0 == req.getProjectStatus()) {
                    privateNumberDealRedisCacheService.deleteRedisCache(privateNumberProjectDo.getAppKey(), orderInfo.getId());
                }
                // 启用时重新设置redis数据
                if (1 == req.getProjectStatus()) {
                    privateNumberDealRedisCacheService.updateProjectRedisCache(privateNumberProjectDo);
                }
                privateNumberProjectDo.setProjectStatus(req.getProjectStatus());
                return privateNumberProjectDao.updateById(privateNumberProjectDo);
            }
        } else {
            throw new BizException(AimError.PROJECT_NOT_EXIST);
        }
        return 0;
    }

    @Override
    public PrivateNumberProjectTestResp privateNumberProjectTest(PrivateNumberProjectTestReq req) {
        PrivateNumberProjectTestResp resp = new PrivateNumberProjectTestResp();
        CaptchaCheckResp checkCaptcha = captchaApi.checkCaptcha(req.getCaptchaInfo());
        resp.setResult("false");
        if (checkCaptcha.getResult() != null && checkCaptcha.getResult()) {
            LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
            if (StringUtils.isNotEmpty(req.getProjectId())) {
                queryWrapperX.eq(PrivateNumberProjectDo::getProjectId, req.getProjectId());
            }
            List<PrivateNumberProjectDo> aimProjectDos = privateNumberProjectDao.selectList(queryWrapperX);
            if (!CollectionUtils.isEmpty(aimProjectDos)) {
                PrivateNumberProjectDo aimProjectDo = aimProjectDos.get(0);
                String appKey = aimProjectDo.getAppKey();
                List<String> phones = req.getPhones();
                if (!Strings.isNullOrEmpty(appKey) && !CollectionUtils.isEmpty(phones)) {
                    resp = callTest(appKey, phones);
                } else {
                    throw new BizException(AimError.PROJECT_CONFIG_ERROR);
                }
            } else {
                throw new BizException(AimError.PROJECT_NOT_EXIST);
            }
        }
        return resp;
    }

    @Override
    public PrivateNumberProjectDo queryValidProject(String appKey) {
        PrivateNumberProjectDo resp = new PrivateNumberProjectDo();
        if (!Strings.isNullOrEmpty(appKey)) {
            LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(PrivateNumberProjectDo::getAppKey, appKey);
            queryWrapperX.eq(PrivateNumberProjectDo::getProjectStatus, 1);
            List<PrivateNumberProjectDo> aimProjectDoList = privateNumberProjectDao.selectList(queryWrapperX);
            if (!CollectionUtils.isEmpty(aimProjectDoList)) {
                resp = aimProjectDoList.get(0);
            }
        }
        return resp;
    }

    @Override
    public PrivateNumberProjectDo queryByProjectId(String projectId) {
        PrivateNumberProjectDo resp = new PrivateNumberProjectDo();
        if (!Strings.isNullOrEmpty(projectId)) {
            LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(PrivateNumberProjectDo::getProjectId, projectId);
            List<PrivateNumberProjectDo> aimProjectDoList = privateNumberProjectDao.selectList(queryWrapperX);
            if (!CollectionUtils.isEmpty(aimProjectDoList)) {
                resp = aimProjectDoList.get(0);
            }
        }
        return resp;
    }

    private PrivateNumberProjectTestResp callTest(String appKey, List<String> phones) {
        PrivateNumberProjectTestResp resp = new PrivateNumberProjectTestResp();
        String status;
        String responseInfoStr;
        try {
            String result = aimCallUtils.callSMS(appKey, phones);
            String[] resultInfo = result.split("~");
            status = resultInfo[0];
            responseInfoStr = resultInfo[1];
        } catch (Exception e) {
            responseInfoStr = e.getMessage();
            status = "999";
        }
        saveDB(appKey, StringUtil.join(phones.toArray(), ","), status, responseInfoStr);
        if ("200".equals(status)) {
            ResponseInfo responseInfo = JSON.parseObject(responseInfoStr, ResponseInfo.class);
            resp.setResult("true");
            resp.setBody(responseInfo);
            //扣减次数
            ResponseData data = responseInfo.getData();
            if (data != null) {
                List<String> successList = data.getSuccessMobiles();
                if (!CollectionUtils.isEmpty(successList)) {
//                    privateNumberDealRedisCacheService.decrementAmount(appKey,successList.size());
                }
            }
        } else {
            throw new BizException(AimError.PROJECT_TEST_EXCEPTION);
        }
        return resp;
    }

    private void saveDB(String calling, String called, String status, String response) {
        BaseUser baseUser = SessionContextUtil.getUser();
        String userId = (baseUser != null) ? baseUser.getUserId() : "test";
        AimTestDataDo aimTestDataDo = AimTestDataDo.builder().
                called(called).
                calling(calling).
                status(Integer.valueOf(status)).
                response(response).deleted(0).deletedTime(0L).build();
        aimTestDataDo.setCreator(userId);
        aimTestDataDo.setUpdater(userId);
        aimTestDataDo.setCreateTime(new Date());
        aimTestDataDo.setUpdateTime(new Date());
        AimTestDataDao aimTestDataDao = SpringUtil.getBean(AimTestDataDao.class);
        aimTestDataDao.insert(aimTestDataDo);
    }

    private void checkAppKey(String appKey, Long id) {
        LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(PrivateNumberProjectDo::getAppKey, appKey);
        if (null != id && id > 0) {
            queryWrapperX.ne(PrivateNumberProjectDo::getId, id);
        }
        List<PrivateNumberProjectDo> aimProjectDoList = privateNumberProjectDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimProjectDoList)) {
            throw new BizException(AimError.PROJECT_CALLING_DUPLICATE);
        }
    }

    private void checkAppKeyAndEventType(String appKey, String eventType, Long id) {
        LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(PrivateNumberProjectDo::getAppKey, appKey);
        queryWrapperX.eq(PrivateNumberProjectDo::getEventType, eventType);
        if (null != id && id > 0) {
            queryWrapperX.ne(PrivateNumberProjectDo::getId, id);
        }
        List<PrivateNumberProjectDo> aimProjectDoList = privateNumberProjectDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimProjectDoList)) {
            throw new BizException(AimError.ERROR_DUPLICATE_ORDER_ITEM);
        }
    }

    private void checkProjectName(String projectName, Long id) {
        LambdaQueryWrapperX<PrivateNumberProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(PrivateNumberProjectDo::getProjectName, projectName);
        if (null != id && id > 0) {
            queryWrapperX.ne(PrivateNumberProjectDo::getId, id);
        }
        List<PrivateNumberProjectDo> privateNumberProjectDoList = privateNumberProjectDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(privateNumberProjectDoList)) {
            throw new BizException(AimError.PROJECT_NAME_DUPLICATE);
        }
    }

}
