package com.citc.nce.misc.guide.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.guide.entity.SysGuide;
import com.citc.nce.misc.guide.mapper.SysGuideMapper;
import com.citc.nce.misc.guide.req.SysGuideAddReq;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.citc.nce.misc.guide.SysGuideConstants.SYS_GUIDE_STATUS_OFF;
import static com.citc.nce.misc.guide.SysGuideConstants.SYS_GUIDE_STATUS_ON;

/**
 * @author jcrenc
 * @since 2024/11/6 16:49
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SysGuideService extends ServiceImpl<SysGuideMapper, SysGuide> implements IService<SysGuide> {
    /**
     * 添加系统引导
     *
     * @param addReq 引导参数
     */
    public void add(SysGuideAddReq addReq) {
        if (!Objects.equals(addReq.getStatus(), SYS_GUIDE_STATUS_ON) && !Objects.equals(addReq.getStatus(), SYS_GUIDE_STATUS_OFF)) {
            throw new BizException("非法引导状态");
        }
        SysGuide sysGuide = new SysGuide();
        BeanUtils.copyProperties(addReq, sysGuide);
        save(sysGuide);
    }
}
