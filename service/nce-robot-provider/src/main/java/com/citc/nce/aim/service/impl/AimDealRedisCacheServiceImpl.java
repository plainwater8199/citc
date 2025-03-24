package com.citc.nce.aim.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.constant.AimOrderStatusEnum;
import com.citc.nce.aim.dao.AimSentDataDao;
import com.citc.nce.aim.entity.AimProjectDo;
import com.citc.nce.aim.entity.AimSentDataDo;
import com.citc.nce.aim.service.AimDealRedisCacheService;
import com.citc.nce.aim.service.AimOrderService;
import com.citc.nce.aim.service.AimProjectService;
import com.citc.nce.aim.vo.AimOrderQueryListReq;
import com.citc.nce.aim.vo.AimOrderQueryReq;
import com.citc.nce.aim.vo.AimOrderResp;
import com.citc.nce.aim.vo.AimProjectQueryReq;
import com.citc.nce.aim.vo.AimProjectResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/28 18:12
 */
@Service
@Slf4j
public class AimDealRedisCacheServiceImpl implements AimDealRedisCacheService {

    @Resource
    private AimOrderService aimOrderService;
    @Resource
    private AimProjectService aimProjectService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private AimSentDataDao aimSentDataDao;


    /**
     * 处理redis缓存
     * 项目编辑时可以变更客户号码，中讯的操作以客户号码为基准
     * @param update
     * @param oldProject
     */
    public int setAmountRedisCache(AimProjectDo update, AimProjectResp oldProject) {
        int res = 1;
        // 如果项目状态是开启 && 客户号码发生变化
        // 去处理redis
        if (1 == oldProject.getProjectStatus() && !StringUtils.equals(update.getCalling(), oldProject.getCalling())) {
            try {
                redisTemplate.delete(AimConstant.REDIS_KEY_AMOUNT_PREFIX + update.getProjectId());
                // 通过projectId去获取当前生效的订单
                AimOrderQueryReq req = new AimOrderQueryReq();
                req.setProjectId(update.getProjectId());
                AimOrderResp aimOrderResp = aimOrderService.queryEnabledOrderByProjectId(req);
                long differ = aimOrderResp.getOrderAmount() - aimOrderResp.getOrderConsumption();
                if (differ > 0L) {
                    redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + aimOrderResp.getProjectId(), String.valueOf(differ));
                }
            }catch (Exception e) {
                log.info(e.getMessage());
                throw new BizException(AimError.REDIS_EXCEPTION);
            }
        }
        return res;
    }

    public int processRedisCache(long orderId) {
        int res = 0;
        AimOrderQueryReq req = new AimOrderQueryReq();
        req.setId(orderId);
        AimOrderResp aimOrderResp = aimOrderService.queryOrderById(req);
        if (null != aimOrderResp && AimOrderStatusEnum.ENABLED.getCode() == aimOrderResp.getOrderStatus()) {
            try {
                AimProjectQueryReq projectQueryReq = new AimProjectQueryReq();
                projectQueryReq.setProjectId(aimOrderResp.getProjectId());
                AimProjectResp aimProjectResp = aimProjectService.queryProject(projectQueryReq);
                long differ = aimOrderResp.getOrderAmount() - aimOrderResp.getOrderConsumption();
                redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + aimOrderResp.getProjectId(), String.valueOf(differ));
                res = updateProjectSetting(aimProjectResp, aimOrderResp);
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new BizException(AimError.REDIS_EXCEPTION);
            }
        }
        if (null != aimOrderResp && AimOrderStatusEnum.ENABLED.getCode() != aimOrderResp.getOrderStatus()) {
            res = deleteRedisCache(aimOrderResp.getProjectId());
        }
        return res;
    }

    @Override
    public int deleteRedisCache(String projectId) {
        try {
            AimProjectQueryReq queryReq = new AimProjectQueryReq();
            queryReq.setProjectId(projectId);
            AimProjectResp aimProjectResp = aimProjectService.queryProject(queryReq);
            redisTemplate.delete(AimConstant.REDIS_KEY_AMOUNT_PREFIX + projectId);
            redisTemplate.delete(AimConstant.REDIS_KEY_SMS_PREFIX + aimProjectResp.getCalling());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BizException(AimError.REDIS_EXCEPTION);
        }
        return 1;
    }

    @Override
    public int updateProjectRedisCache(AimProjectDo update) {
        int res = 0;
        try {
            AimProjectQueryReq query = new AimProjectQueryReq();
            query.setProjectId(update.getProjectId());
            AimProjectResp aimProjectResp = new AimProjectResp();
            aimProjectResp.setCalling(update.getCalling());
            aimProjectResp.setSmsTemplate(update.getSmsTemplate());
            aimProjectResp.setPathKey(update.getPathKey());
            aimProjectResp.setSecret(update.getSecret());
            AimOrderQueryReq req = new AimOrderQueryReq();
            req.setProjectId(update.getProjectId());
            AimOrderResp aimOrderResp = aimOrderService.queryEnabledOrderByProjectId(req);
            redisTemplate.delete(AimConstant.REDIS_KEY_SMS_PREFIX + update.getCalling());
            // 无订单时允许更新
            if (ObjectUtils.isEmpty(aimOrderResp)) {
                return 1;
            }
            res = updateProjectSetting(aimProjectResp, aimOrderResp);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return res;
    }

    @Override
    public void decrementAmount(String calling, long amount) {
        AimProjectQueryReq req = new AimProjectQueryReq();
        req.setCalling(calling);
        req.setProjectStatus(1);
        AimProjectResp aimProjectResp = aimProjectService.queryProject(req);
        if (null == aimProjectResp) {
            throw new BizException(AimError.PROJECT_NOT_EXIST);
        }
        AimOrderQueryReq orderQueryReq = new AimOrderQueryReq();
        orderQueryReq.setProjectId(aimProjectResp.getProjectId());
        AimOrderResp aimOrderResp = aimOrderService.queryEnabledOrderByProjectId(orderQueryReq);
        if (null == aimOrderResp) {
            throw new BizException(AimError.ENABLED_ORDER_NOT_EXIST);
        }
        Object redisAmount = redisTemplate.opsForValue().get(AimConstant.REDIS_KEY_AMOUNT_PREFIX + aimProjectResp.getProjectId());
        if (Objects.nonNull(redisAmount)) {
            redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + aimProjectResp.getProjectId(), Long.parseLong(redisAmount.toString()) - amount);
        }
        aimOrderService.updateOrderConsumption(aimOrderResp.getId(), aimOrderResp.getOrderConsumption() + amount);
        // 测试短信也要计入统计数据
        insertSentData(calling, amount, aimOrderResp);
    }

    private void insertSentData(String calling, long amount, AimOrderResp aimOrderResp) {
        List<AimSentDataDo> insertList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            AimSentDataDo dataDo = new AimSentDataDo();
            dataDo.setProjectId(aimOrderResp.getProjectId());
            dataDo.setCalling(calling);
            dataDo.setOrderId(aimOrderResp.getId());
            dataDo.setRecordId(UUID.randomUUID().toString());
            dataDo.setCreator(AimConstant.INSERT_USER);
            dataDo.setUpdater(AimConstant.INSERT_USER);
            insertList.add(dataDo);
        }
        aimSentDataDao.insertBatch(insertList);
    }

    @Override
    public int setRedisCache(String projectId) {
        int res = 0;
        AimProjectQueryReq queryReq = new AimProjectQueryReq();
        queryReq.setProjectId(projectId);
        AimProjectResp aimProjectResp = aimProjectService.queryProject(queryReq);
        if (null == aimProjectResp) {
            throw new BizException(AimError.PROJECT_NOT_EXIST);
        }
        AimOrderQueryListReq aimOrderQueryListReq = new AimOrderQueryListReq();
        aimOrderQueryListReq.setProjectId(aimProjectResp.getProjectId());
        PageResult<AimOrderResp> respPageResult = aimOrderService.queryOrderList(aimOrderQueryListReq);
        if (null == respPageResult) {
            throw new BizException(AimError.ORDER_NOT_EXIST);
        }
        AimOrderQueryReq orderQueryReq = new AimOrderQueryReq();
        orderQueryReq.setProjectId(aimProjectResp.getProjectId());
        AimOrderResp aimOrderResp = aimOrderService.queryEnabledOrderByProjectId(orderQueryReq);
        if (null == aimOrderResp) {
            throw new BizException(AimError.ENABLED_ORDER_NOT_EXIST);
        }
        try {
            long differ = aimOrderResp.getOrderAmount() - aimOrderResp.getOrderConsumption();
            redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + aimOrderResp.getProjectId(), String.valueOf(differ));
            res = updateProjectSetting(aimProjectResp, aimOrderResp);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return res;
    }

    private int updateProjectSetting(AimProjectResp aimProjectResp, AimOrderResp aimOrderResp) {
        int res = 0;
        JSONObject info = new JSONObject();
        info.put("projectId", aimOrderResp.getProjectId());
        info.put("orderId", aimOrderResp.getId());
        info.put("calling", aimProjectResp.getCalling());
        info.put("smsTemplate", aimProjectResp.getSmsTemplate());
        info.put("type", AimConstant.SMS_TYPE);
        info.put("key", aimProjectResp.getPathKey());
        info.put("pw", aimProjectResp.getSecret());
        // 中讯给的文档里的写的key sms:config: + calling
        redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_SMS_PREFIX + aimProjectResp.getCalling(), info.toJSONString());
        Object obj = redisTemplate.opsForValue().get(AimConstant.REDIS_KEY_SMS_PREFIX + aimProjectResp.getCalling());
        if (ObjectUtils.isNotEmpty(obj)) {
            res = 1;
        }
        return res;
    }
}
