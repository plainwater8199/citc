package com.citc.nce.developer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.entity.SmsDeveloperCustomerDo;
import com.citc.nce.developer.vo.*;

/**
 * @author ping chen
 */
public interface SmsDeveloperCustomerService extends IService<SmsDeveloperCustomerDo> {

    void generate();

    SmsDeveloperAuthVo details(String customerId);

    void saveCallbackUrl(SmsDeveloperAuthCallbackUrlVo smsDeveloperAuthCallbackUrlVo);

    PageResult<DeveloperCustomerVo> searchDeveloperSend(SmsDeveloperSendSearchVo smsDeveloperSendSearchVo);

    void setSwitch(SmsDeveloperSwitchVo smsDeveloperSwitchVo);
    PageResult<SmsDeveloperCustomerManagerVo> queryList(SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo);

    PageResult<DeveloperAccountVo> getSmsDeveloperCustomerOption();

    void appSecretEncode();
}
