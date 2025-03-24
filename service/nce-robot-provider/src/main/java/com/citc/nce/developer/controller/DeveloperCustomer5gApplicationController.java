package com.citc.nce.developer.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.DeveloperCustomer5gApplicationApi;
import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.developer.vo.Developer5gApplicationNameVo;
import com.citc.nce.developer.vo.Developer5gApplicationVo;
import com.citc.nce.developer.vo.DeveloperAccountVo;
import com.citc.nce.developer.vo.DeveloperCustomer5gManagerVo;
import com.citc.nce.developer.vo.DeveloperQueryApplicationVo;
import com.citc.nce.developer.vo.DeveloperSetStateApplicationVo;
import com.citc.nce.developer.vo.SmsDeveloperCustomerReqVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DeveloperCustomer5gApplicationController implements DeveloperCustomer5gApplicationApi {
    private final DeveloperCustomer5gApplicationService developerCustomer5gApplicationService;


    /**
     * 保存应用数据
     *
     * @param developerSaveApplicationVo
     */
    @Override
    public void saveApplication(Developer5gApplicationVo developerSaveApplicationVo) {
        developerCustomer5gApplicationService.saveApplication(developerSaveApplicationVo);
    }

    /**
     * 修改
     *
     * @param developerSaveApplicationVo
     */
    @Override
    public void editApplication(Developer5gApplicationVo developerSaveApplicationVo) {
        developerCustomer5gApplicationService.editApplication(developerSaveApplicationVo);
    }

    /**
     * 删除
     *
     * @param uniqueId
     */
    @Override
    public void deleteApplication(String uniqueId) {
        developerCustomer5gApplicationService.deleteApplication(uniqueId);
    }

    /**
     * 状态修改
     *
     * @param developerSetStateApplicationVo
     */
    @Override
    public void setStateApplication(DeveloperSetStateApplicationVo developerSetStateApplicationVo) {
        developerCustomer5gApplicationService.setStateApplication(developerSetStateApplicationVo);
    }

    /**
     * 详情查询
     *
     * @param uniqueId
     * @return
     */
    @Override
    public Developer5gApplicationVo queryApplicationInfo(String uniqueId) {
        return developerCustomer5gApplicationService.queryApplicationInfo(uniqueId);
    }

    /**
     * 列表查询
     *
     * @param developerQueryApplicationVo
     * @return
     */
    @Override
    public PageResult<Developer5gApplicationVo> queryApplicationList(DeveloperQueryApplicationVo developerQueryApplicationVo) {
        return developerCustomer5gApplicationService.queryApplicationList(developerQueryApplicationVo);
    }

    @Override
    public PageResult<DeveloperCustomer5gManagerVo> queryList(SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo) {
        return developerCustomer5gApplicationService.queryList(smsDeveloperCustomerReqVo);
    }

    @Override
    public PageResult<DeveloperAccountVo> get5gDeveloperCustomerOption() {
        return developerCustomer5gApplicationService.get5gDeveloperCustomerOption();
    }

    @Override
    public List<Developer5gApplicationNameVo> queryApplicationNameWithoutLogin(List<Long> ids) {
        return developerCustomer5gApplicationService.queryApplicationWithoutLogin(ids);
    }

}
