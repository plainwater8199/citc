package com.citc.nce.authcenter.csp.platformDefinition.service;

import com.citc.nce.authcenter.csp.platformDefinition.domain.CspPlatformDefinition;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.authcenter.csp.vo.resp.PlatformDefinition;

/**
 * <p>
 * csp平台自定义数据 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-01-25 11:01:01
 */
public interface ICspPlatformDefinitionService extends IService<CspPlatformDefinition> {

    /**
     * 根据cpsId 获取平台定义
     * @param cspId cspId
     */
    PlatformDefinition platformDefinition(String cspId);

    void updatePlatformDefinition(PlatformDefinition platformDefinition);
}
