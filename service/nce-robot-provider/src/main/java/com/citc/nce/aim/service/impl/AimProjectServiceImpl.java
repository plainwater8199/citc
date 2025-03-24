package com.citc.nce.aim.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.dao.AimProjectDao;
import com.citc.nce.aim.dao.AimTestDataDao;
import com.citc.nce.aim.dto.AimProjectOrderInfoDto;
import com.citc.nce.aim.dto.AimProjectQueryDto;
import com.citc.nce.aim.entity.AimProjectDo;
import com.citc.nce.aim.entity.AimTestDataDo;
import com.citc.nce.aim.service.AimDealRedisCacheService;
import com.citc.nce.aim.service.AimProjectService;
import com.citc.nce.aim.utils.api.AimCallUtils;
import com.citc.nce.aim.vo.AimProjectEditReq;
import com.citc.nce.aim.vo.AimProjectQueryListReq;
import com.citc.nce.aim.vo.AimProjectQueryReq;
import com.citc.nce.aim.vo.AimProjectResp;
import com.citc.nce.aim.vo.AimProjectSaveReq;
import com.citc.nce.aim.vo.AimProjectUpdateStatusReq;
import com.citc.nce.aim.vo.ResponseData;
import com.citc.nce.aim.vo.ResponseInfo;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.authcenter.captcha.CaptchaApi;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:05
 */
@Service
@Slf4j
public class AimProjectServiceImpl implements AimProjectService {

    @Resource
    private AimProjectDao aimProjectDao;
    @Lazy
    @Resource
    private AimDealRedisCacheService aimDealRedisCacheService;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CaptchaApi captchaApi;
    @Resource
    private AimCallUtils aimCallUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(AimProjectSaveReq req) {
        // 校验名称
        boolean checkProjectName = checkProjectName(req.getProjectName(), null);
        if (!checkProjectName) {
            throw new BizException(AimError.PROJECT_NAME_DUPLICATE);
        }
        // 校验客户号码
        boolean checkCalling = checkCalling(req.getCalling(), null);
        if (!checkCalling) {
            throw new BizException(AimError.PROJECT_CALLING_DUPLICATE);
        }
        // 校验通道账号密码
//        doValidate(req.getPathKey(), req.getSecret());
        // 插入数据
        AimProjectDo insert = new AimProjectDo();
        BeanUtils.copyProperties(req, insert);
        String projectId = generateProjectId();
        insert.setProjectId(projectId);
//        // 项目设置为启用
//        insert.setProjectStatus(1);
        aimProjectDao.insert(insert);
        return 0;
    }

    private void doValidate(String accessKey, String accessSecret) {
        String status;
        int resCode;
        try {
            String validateAccess = aimCallUtils.validateAccess(accessKey, accessSecret);
            log.info("#### AimProjectServiceImpl validateAccess {}", validateAccess);
            String[] resultInfo = validateAccess.split("~");
            status = resultInfo[0];
            String responseInfoStr = resultInfo[1];
            JSONObject jsonObject = JsonUtils.string2Obj(responseInfoStr, JSONObject.class);
            resCode = jsonObject.getInteger("code");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BizException(AimError.VALIDATE_ACCESS_SERV_ERROR);
        }
        if (AimConstant.VALIDATE_ACCESS_SUCCESS.equals(status)) {
            if (AimConstant.VALIDATE_ACCESS_ERROR == resCode) {
                throw new BizException(AimError.VALIDATE_ACCESS_ERROR);
            } else if (AimConstant.VALIDATE_ACCESS_LIMIT == resCode) {
                throw new BizException(AimError.VALIDATE_ACCESS_LIMIT);
            } else if (AimConstant.SMS_SERVER_FAILURE == resCode) {
                throw new BizException(AimError.SMS_SERVER_FAILURE);
            }
        } else {
            throw new BizException(AimError.VALIDATE_ACCESS_SERV_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(AimProjectEditReq req) {
        // 校验名称
        boolean checkProjectName = checkProjectName(req.getProjectName(), req.getId());
        if (!checkProjectName) {
            throw new BizException(AimError.PROJECT_NAME_DUPLICATE);
        }
        // 校验客户号码
        boolean checkCalling = checkCalling(req.getCalling(), req.getId());
        if (!checkCalling) {
            throw new BizException(AimError.PROJECT_CALLING_DUPLICATE);
        }
        AimProjectDo update = new AimProjectDo();
        BeanUtils.copyProperties(req, update);
        AimProjectResp aimProjectResp = new AimProjectResp();
        AimProjectQueryReq query = new AimProjectQueryReq();
        if (0 == update.getId()) {
            query.setProjectId(req.getProjectId());
            aimProjectResp = queryProject(query);
            update.setId(aimProjectResp.getId());
        } else {
            query.setId(req.getId());
            aimProjectResp = queryProject(query);
        }
        // 如果用户没有改通道的数据，就不进行验证
        if (!StringUtils.equals(update.getPathKey(), aimProjectResp.getPathKey()) || !StringUtils.equals(update.getSecret(), aimProjectResp.getSecret())) {
            // 校验通道账号密码
            doValidate(req.getPathKey(), req.getSecret());
        }

        // 用户要更改客户号码
        int deleteAndSetRedisCache;
        int updateProjectRedisCache;
        if (StringUtils.isNotEmpty(update.getCalling()) && !StringUtils.equals(update.getCalling(), aimProjectResp.getCalling())) {
            // 处理redis数据
            aimDealRedisCacheService.deleteRedisCache(update.getProjectId());
            deleteAndSetRedisCache = aimDealRedisCacheService.setAmountRedisCache(update, aimProjectResp);
            updateProjectRedisCache = aimDealRedisCacheService.updateProjectRedisCache(update);
            if (1 == deleteAndSetRedisCache && 1 == updateProjectRedisCache) {
                aimProjectDao.updateById(update);
            } else {
                throw new BizException(AimError.PROJECT_UPDATE_FAILURE);
            }
        } else {
            updateProjectRedisCache = aimDealRedisCacheService.updateProjectRedisCache(update);
            if (1 == updateProjectRedisCache) {
                aimProjectDao.updateById(update);
            } else {
                throw new BizException(AimError.PROJECT_UPDATE_FAILURE);
            }
        }
        return 0;
    }

    @Override
    public AimProjectResp queryProject(AimProjectQueryReq req) {
        LambdaQueryWrapperX<AimProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (0 != req.getId()) {
            queryWrapperX.eq(AimProjectDo::getId, req.getId());

        }
        if (StringUtils.isNotEmpty(req.getProjectId())) {
            queryWrapperX.eq(AimProjectDo::getProjectId, req.getProjectId());
        }
        if (StringUtils.isNotEmpty(req.getCalling())) {
            queryWrapperX.eq(AimProjectDo::getCalling, req.getCalling());
        }
        if (1 == req.getProjectStatus()) {
            queryWrapperX.eq(AimProjectDo::getProjectStatus, req.getProjectStatus());
        }
        AimProjectDo aimProjectDo = aimProjectDao.selectOne(queryWrapperX);
        if (null != aimProjectDo) {
            AimProjectResp resp = new AimProjectResp();
            BeanUtils.copyProperties(aimProjectDo, resp);
            return resp;
        }
        return null;
    }

    @Override
    public PageResult<AimProjectResp> queryProjectList(AimProjectQueryListReq req) {
        if (null == req.getPageSize()) {
            req.setPageSize(20);
        }
        if (null == req.getPageNo()) {
            req.setPageNo(1);
        }
        AimProjectQueryDto dto = new AimProjectQueryDto();
        BeanUtils.copyProperties(req, dto);
        int currentPage = Math.max((req.getPageNo() - 1) * req.getPageSize(), 0);
        dto.setCurrentPage(currentPage);

        int listCount = aimProjectDao.queryProjectListCount(dto);
        if (listCount > 0) {
            List<AimProjectOrderInfoDto> doList = aimProjectDao.queryProjectList(dto);
            List<AimProjectResp> projectResps = BeanUtil.copyToList(doList, AimProjectResp.class);
            return new PageResult(projectResps, Long.parseLong(String.valueOf(listCount)));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(AimProjectUpdateStatusReq req) {
        LambdaUpdateWrapper<AimProjectDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AimProjectDo::getProjectStatus, req.getProjectStatus());
        updateWrapper.eq(AimProjectDo::getProjectId, req.getProjectId());
        if (0 != req.getId()) {
            updateWrapper.eq(AimProjectDo::getId, req.getId());
        }
        // 项目被禁用时需要删除redis数据
        if (0 == req.getProjectStatus()) {
            aimDealRedisCacheService.deleteRedisCache(req.getProjectId());
        }
        // 启用时重新设置redis数据
        if (1 == req.getProjectStatus()) {
            int setRedisCache = aimDealRedisCacheService.setRedisCache(req.getProjectId());
            if (1 != setRedisCache) {
                throw new BizException(AimError.REDIS_EXCEPTION);
            }
        }
        aimProjectDao.update(null, updateWrapper);
        return 0;
    }

    @Override
    public AimProjectTestResp aimProjectTest(AimProjectTestReq req) {
        AimProjectTestResp resp = new AimProjectTestResp();
        CaptchaCheckResp checkCaptcha = captchaApi.checkCaptcha(req.getCaptchaInfo());
        resp.setResult("false");
        if (checkCaptcha.getResult() != null && checkCaptcha.getResult()) {
            LambdaQueryWrapperX<AimProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
            if (StringUtils.isNotEmpty(req.getProjectId())) {
                queryWrapperX.eq(AimProjectDo::getProjectId, req.getProjectId());
            }
            AimProjectDo aimProjectDo = aimProjectDao.selectOne(queryWrapperX);
            if (aimProjectDo != null) {
                String calling = aimProjectDo.getCalling();
                List<String> phones = req.getPhones();
                if (!Strings.isNullOrEmpty(calling) && !CollectionUtils.isEmpty(phones)) {
                    resp = callTest(calling, phones);
                } else {
                    throw new BizException(AimError.PROJECT_CONFIG_ERROR);
                }
            } else {
                throw new BizException(AimError.PROJECT_NOT_EXIST);
            }
        }
        return resp;
    }

    private AimProjectTestResp callTest(String calling, List<String> phones) {
        AimProjectTestResp resp = new AimProjectTestResp();
        String status;
        String responseInfoStr;
        try {
            String result = aimCallUtils.callSMS(calling, phones);
            String[] resultInfo = result.split("~");
            status = resultInfo[0];
            responseInfoStr = resultInfo[1];
        } catch (Exception e) {
            responseInfoStr = e.getMessage();
            status = "999";
        }
        saveDB(calling, StringUtil.join(phones.toArray(), ","), status, responseInfoStr);
        if ("200".equals(status)) {
            ResponseInfo responseInfo = JSON.parseObject(responseInfoStr, ResponseInfo.class);
            resp.setResult("true");
            resp.setBody(responseInfo);
            //扣减次数
            ResponseData data = responseInfo.getData();
            if(data != null){
                List<String> successList = data.getSuccessMobiles();
                if(!CollectionUtils.isEmpty(successList)){
                    aimDealRedisCacheService.decrementAmount(calling,successList.size());
                }
            }
        }else{
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

    private boolean checkCalling(String calling, Long id) {
        LambdaQueryWrapperX<AimProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AimProjectDo::getCalling, calling);
        if (null != id && id > 0) {
            queryWrapperX.ne(AimProjectDo::getId, id);
        }
        List<AimProjectDo> aimProjectDoList = aimProjectDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimProjectDoList)) {
            return false;
        }
        return true;
    }

    private boolean checkProjectName(String projectName, Long id) {
        LambdaQueryWrapperX<AimProjectDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AimProjectDo::getProjectName, projectName);
        if (null != id && id > 0) {
            queryWrapperX.ne(AimProjectDo::getId, id);
        }
        List<AimProjectDo> aimProjectDoList = aimProjectDao.selectList(queryWrapperX);
        if (CollectionUtil.isNotEmpty(aimProjectDoList)) {
            return false;
        }
        return true;
    }

    /**
     * 生成项目Id
     * AIM + yyyyMMdd + Id数字(9位）
     * AIM20230608000000000(20位）
     *
     * @return string
     */
    private String generateProjectId() {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localDate = LocalDateTime.now().format(pattern);
        Long maxId = aimProjectDao.getMaxId();
        if (null == maxId) {
            maxId = 0L;
        }
        String maxIdStr = String.valueOf(maxId);
        int idLength = 9;
        String str;
        if (maxIdStr.length() >= idLength) {
            str = maxIdStr.substring(maxIdStr.length() - idLength);
        } else {
            str = String.format("%0" + idLength + "d", maxId);
        }
        String projectId = AimConstant.PROJECT_ID_PREFIX + localDate + str;
        return projectId;
    }
}
