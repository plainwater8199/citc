package com.citc.nce.developer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.entity.VideoDeveloperCustomerDo;
import com.citc.nce.developer.vo.*;

/**
 * @author ping chen
 */
public interface VideoDeveloperCustomerService extends IService<VideoDeveloperCustomerDo> {

    void generate();

    VideoDeveloperAuthVo details(String customerId);

    void saveCallbackUrl(VideoDeveloperAuthCallbackUrlVo videoDeveloperAuthCallbackUrlVo);

    PageResult<DeveloperCustomerVo> searchDeveloperSend(VideoDeveloperSendSearchVo videoDeveloperSendSearchVo);

    void setSwitch(VideoDeveloperSwitchVo videoDeveloperSwitchVo);

    PageResult<VideoDeveloperCustomerManagerVo> queryList(VideoDeveloperCustomerReqVo videoDeveloperCustomerReqVo);

    PageResult<DeveloperAccountVo> getVideoDeveloperCustomerOption();

    void appSecretEncode();
}
