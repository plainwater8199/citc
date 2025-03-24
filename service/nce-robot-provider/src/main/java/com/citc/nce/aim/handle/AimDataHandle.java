package com.citc.nce.aim.handle;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.dao.AimOrderDao;
import com.citc.nce.aim.dao.AimProjectDao;
import com.citc.nce.aim.dto.AimProjectOrderInfoDto;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/14 9:32
 */
//@Component
//public class AimDataHandle implements ApplicationRunner {
//
//    @Resource
//    private RedisTemplate redisTemplate;
//    @Resource
//    private AimProjectDao aimProjectDao;
//    @Resource
//    private AimOrderDao aimOrderDao;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("#################### UserDataHandle is start ####################");
//        List<AimProjectOrderInfoDto> EnabledOrderInfoList = aimProjectDao.queryProjectEnabledOrderInfo();
//        if (CollectionUtil.isNotEmpty(EnabledOrderInfoList)) {
//            for (AimProjectOrderInfoDto dto:
//            EnabledOrderInfoList) {
//                long differ = dto.getOrderAmount() - dto.getOrderConsumption();
//                if (differ > 0L) {
//                    redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + dto.getProjectId(), String.valueOf(differ));
//                    JSONObject info = new JSONObject();
//                    info.put("projectId", dto.getProjectId());
//                    info.put("orderId", dto.getOrderId());
//                    info.put("calling", dto.getCalling());
//                    info.put("smsTemplate", dto.getSmsTemplate());
//                    info.put("type", AimConstant.SMS_TYPE);
//                    info.put("key", dto.getPathKey());
//                    info.put("pw", dto.getSecret());
//                    // 中讯给的文档里的写的key sms:config: + calling
//                    redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_SMS_PREFIX + dto.getCalling(), info.toJSONString());
//                }
//            }
//        }
//        System.out.println("#################### UserDataHandle is end ####################");
//    }
//}
