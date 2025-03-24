package com.citc.nce.developer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.entity.DeveloperCustomer5gDo;
import com.citc.nce.developer.vo.DeveloperCustomerInfoVo;
import com.citc.nce.developer.vo.DeveloperCustomerVo;
import com.citc.nce.developer.vo.DeveloperSaveCallbackUrlVo;
import com.citc.nce.developer.vo.SmsDeveloperSendSearchVo;

/**
 * @author ping chen
 */
public interface DeveloperCustomer5gService extends IService<DeveloperCustomer5gDo> {

    DeveloperCustomerInfoVo queryInfo();

    void saveCallbackUrl(DeveloperSaveCallbackUrlVo developerSaveCallbackUrlVo);

    PageResult<DeveloperCustomerVo> searchDeveloperSend(SmsDeveloperSendSearchVo smsDeveloperSendSearchVo);
}
