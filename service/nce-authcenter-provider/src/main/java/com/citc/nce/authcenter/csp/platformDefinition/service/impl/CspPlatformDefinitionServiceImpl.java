package com.citc.nce.authcenter.csp.platformDefinition.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.csp.multitenant.utils.CspUtils;
import com.citc.nce.authcenter.csp.platformDefinition.dao.CspPlatformDefinitionMapper;
import com.citc.nce.authcenter.csp.platformDefinition.domain.CspPlatformDefinition;
import com.citc.nce.authcenter.csp.platformDefinition.service.ICspPlatformDefinitionService;
import com.citc.nce.authcenter.csp.vo.resp.PlatformDefinition;
import com.citc.nce.common.core.exception.BizException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * csp平台自定义数据 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-01-25 11:01:01
 */
@Service
public class CspPlatformDefinitionServiceImpl extends ServiceImpl<CspPlatformDefinitionMapper, CspPlatformDefinition> implements ICspPlatformDefinitionService {
    @Override
    public PlatformDefinition platformDefinition(String cspId) {
        PlatformDefinition definition = new PlatformDefinition();
        definition.setLoginUrl(String.format("/user/login/%s", CspUtils.encodeCspId(cspId)));
        CspPlatformDefinition cspPlatformDefinition = getByCspId(cspId);
        if (Objects.nonNull(cspPlatformDefinition)) {
            copyProperties(cspPlatformDefinition, definition);
        }
        return definition;
    }


    @Override
    public void updatePlatformDefinition(PlatformDefinition var) {
        String cspId = var.getCspId();
        CspPlatformDefinition definition = getByCspId(cspId);
        if (Objects.isNull(definition)) {
            //之前 没有修改过
            definition = new CspPlatformDefinition();
            copyProperties(var, definition);
            definition.setCspId(cspId);
            save(definition);
            return;
        }
        if (!definition.getCspId().equals(cspId)) {
            throw new BizException("不能其他csp的平台信息");
        }
        copyProperties(var, definition);
        updateById(definition);
    }

    private CspPlatformDefinition getByCspId(String cspId) {
        return lambdaQuery().eq(CspPlatformDefinition::getCspId, cspId).one();
    }

    private void copyProperties(PlatformDefinition vo, CspPlatformDefinition db) {
        BeanUtils.copyProperties(vo, db);
        db.setCarouselChart(JSON.toJSONString(vo.getCarouselChart()));
    }

    private void copyProperties(CspPlatformDefinition db, PlatformDefinition resp) {
        BeanUtils.copyProperties(db, resp);
        resp.setCarouselChart(JSON.parseArray(db.getCarouselChart(), String.class));
    }
}
