package com.citc.nce.im.massSegment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.im.massSegment.entity.MassSegment;
import com.citc.nce.im.massSegment.mapper.MassSegmentMapper;
import com.citc.nce.im.massSegment.service.IMassSegmentService;
import com.citc.nce.robot.domain.massSegment.MassSegmentDetail;
import com.citc.nce.robot.domain.massSegment.MassSegmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 运营商号段关系表 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-06 04:05:59
 */
@Service
@Slf4j
public class MassSegmentServiceImpl extends ServiceImpl<MassSegmentMapper, MassSegment> implements IMassSegmentService {

    @Autowired
    private RedisService redisService;
    private final static String redisMapKey = "system:mass:segment:map";

    @Override
    public String getOperatorStringByPhone(String phone) {
        if (!StringUtils.hasLength(phone)) return null;
        return getCacheMap().get(phone.substring(0, 3));
    }

    @Override
    public CSPOperatorCodeEnum getOperatorByPhone(String phone) {
        return CSPOperatorCodeEnum.byName(getCacheMap().get(phone.substring(0, 3)));
    }

    @Override
    public Map<String, CSPOperatorCodeEnum> getOperatorByPhone(List<String> phones) {
        if (CollectionUtils.isEmpty(phones)) return new HashMap<>();
        HashMap<String, CSPOperatorCodeEnum> returnMap = new HashMap<>(phones.size());
        Map<String, String> cacheMap = getCacheMap();
        for (String phone : phones) {
            String operator = cacheMap.get(phone.substring(0, 3));
            returnMap.put(phone, CSPOperatorCodeEnum.byName(operator));
        }
        return returnMap;
    }

    @Override
    @Transactional
    public void saveCustom(MassSegmentVo entity) {
        if (count(new LambdaQueryWrapper<MassSegment>()
                .eq(MassSegment::getPhoneSegment, entity.getPhoneSegment())
        ) > 0) {
            throw new BizException("号段不能重复");
        }

        MassSegment massSegment = new MassSegment();
        massSegment.setPhoneSegment(entity.getPhoneSegment());
        massSegment.setOperator(entity.getOperator());
        massSegment.setMsType(CUSTOM);
        save(massSegment);
        redisService.deleteObject(redisMapKey);
    }


    @Override
    public void delById(Long id) {
        MassSegment segment = getById(id);
        if (Objects.isNull(segment)) return;
//        if (!CUSTOM.equals(segment.getMsType())) {
//            throw new BizException("只能删除自定义号段");
//        }
        removeById(id);
        redisService.deleteObject(redisMapKey);
    }


    @Override
    public Map<String, List<MassSegmentDetail>> listGroupOperator(String msType) {
        List<MassSegment> list = listByMsType(msType);
        return list.stream().map(this::changeToMassSegmentDetail).collect(Collectors.groupingBy(MassSegmentDetail::getOperator));
    }

    @Override
    public Map<String, String> queryAllSegment() {
        List<MassSegment> massSegments = this.list();
        return massSegments.stream().collect(Collectors.groupingBy(MassSegment::getOperator, Collectors.mapping(MassSegment::getPhoneSegment, Collectors.joining(","))));
    }

    private List<MassSegment> listByMsType(String msType) {
        return list(new LambdaQueryWrapper<MassSegment>().eq(StringUtils.hasLength(msType), MassSegment::getMsType, msType));
    }


    @PostConstruct
    public void init() {
        redisService.deleteObject(redisMapKey);
        saveCache();
        log.info("初始化号段完成");
    }

    private Map<String, String> getCacheMap() {
        Map<String, String> cacheMap = redisService.getCacheObject(redisMapKey);
        if (Objects.isNull(cacheMap) || cacheMap.isEmpty()) {
            cacheMap = saveCache();
        }
        return cacheMap;
    }

    private Map<String, String> saveCache() {
        List<MassSegment> list = list();
        if (CollectionUtils.isEmpty(list)) return new HashMap<>();
        Map<String, List<MassSegment>> map = list.stream().collect(Collectors.groupingBy(MassSegment::getPhoneSegment));
        Map<String, String> cacheMap = new HashMap<>(map.size());
        for (Map.Entry<String, List<MassSegment>> entry : map.entrySet()) {
            String operator = null;
            if (entry.getValue().size() == 1) {
                operator = entry.getValue().get(0).getOperator();
            } else {
                List<MassSegment> segmentList = entry.getValue().stream().filter(s -> CUSTOM.equals(s.getMsType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(segmentList)) {
                    operator = segmentList.get(0).getOperator();
                }
            }
            if (StringUtils.hasLength(operator)) {
                cacheMap.put(entry.getKey(), operator);
            }
        }
        redisService.setCacheObject(redisMapKey, cacheMap);
        return cacheMap;
    }

}

