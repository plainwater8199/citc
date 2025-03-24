package com.citc.nce.developer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.entity.DeveloperCustomer5gApplicationDo;
import com.citc.nce.developer.vo.Developer5gApplicationNameVo;
import com.citc.nce.developer.vo.Developer5gApplicationVo;
import com.citc.nce.developer.vo.DeveloperAccountVo;
import com.citc.nce.developer.vo.DeveloperCustomer5gManagerVo;
import com.citc.nce.developer.vo.DeveloperQueryApplicationVo;
import com.citc.nce.developer.vo.DeveloperSetStateApplicationVo;
import com.citc.nce.developer.vo.SmsDeveloperCustomerReqVo;

import java.util.List;

/**
 * @author ping chen
 */
public interface DeveloperCustomer5gApplicationService extends IService<DeveloperCustomer5gApplicationDo> {

    void saveApplication(Developer5gApplicationVo developerSaveApplicationVo);

    void editApplication(Developer5gApplicationVo developerSaveApplicationVo);

    void deleteApplication(String uniqueId);

    void setStateApplication(DeveloperSetStateApplicationVo developerSetStateApplicationVo);

    Developer5gApplicationVo queryApplicationInfo(String uniqueId);

    Developer5gApplicationVo queryApplicationWithoutLogin(String uniqueId);

    List<Developer5gApplicationNameVo> queryApplicationWithoutLogin(List<Long> ids);

    PageResult<Developer5gApplicationVo> queryApplicationList(DeveloperQueryApplicationVo developerQueryApplicationVo);

    PageResult<DeveloperCustomer5gManagerVo> queryList(SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo);

    PageResult<DeveloperAccountVo> get5gDeveloperCustomerOption();

    /**
     * 刷新模板状态
     *
     * @param entity 要刷新的对象
     */
    Integer refreshTemplateStatus(DeveloperCustomer5gApplicationDo entity);

    /**
     * appSecret加密
     */
    void appSecretEncode();
}
