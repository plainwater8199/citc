package com.citc.nce.auth.formmanagement.controller;


import com.citc.nce.auth.formmanagement.FormManagementApi;
import com.citc.nce.auth.formmanagement.service.FormManagementService;
import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 表单管理
 */
@RestController()
@Slf4j
public class FormManagementController implements FormManagementApi {
    @Resource
    private FormManagementService formManagementService;

    /**
     * 表单管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "表单管理列表分页获取", notes = "表单管理列表分页获取")
    @PostMapping("/form/management/pageList")
    @Override
    public PageResultResp getFormManagements(@RequestBody @Valid PageParam pageParam) {
        return formManagementService.getFormManagements(pageParam);
    }

    /**
     * 新增表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增表单管理", notes = "新增表单管理")
    @PostMapping("/form/management/save")
    @Override
    public FormManagementSaveResp saveFormManagement(@RequestBody @Valid FormManagementReq formManagementReq) {
        return formManagementService.saveFormManagement(formManagementReq);
    }

    /**
     * 修改表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改表单管理", notes = "修改表单管理")
    @PostMapping("/form/management/edit")
    @Override
    public int updateFormManagement(@RequestBody @Valid FormManagementEditReq formManagementEditReq) {
        return formManagementService.updateFormManagement(formManagementEditReq);
    }

    /**
     * 删除表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除表单管理", notes = "删除表单管理")
    @PostMapping("/form/management/delete")
    @Override
    public int delFormManagementById(@RequestBody @Valid FormManagementOneReq formManagementOneReq) {
        return formManagementService.delFormManagementById(formManagementOneReq);
    }

    /**
     * 根据id获取表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据id获取表单管理", notes = "根据id获取表单管理")
    @PostMapping("/form/management/getOne")
    @Override
    public FormManagementResp getFormManagementById(@RequestBody @Valid FormManagementOneReq formManagementOneReq) {
        return formManagementService.getFormManagementById(formManagementOneReq);
    }

    /**
     * 获取表单管理树
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/getTreeList")
    @Override
    public List<FormManagementTreeResp> getTreeList() {
        return formManagementService.getTreeList();
    }

    @ApiOperation(value = "根据id获取表单管理编辑数据", notes = "根据id获取表单管理编辑数据")
    @PostMapping("/form/management/getEdit")
    @Override
    public FormManagementResp getEdit(@RequestBody @Valid FormManagementOneReq formManagementOneReq) {
        return formManagementService.getEdit(formManagementOneReq);
    }

    @Override
    @PostMapping("/form/management/saveList")
    public int saveList(@RequestBody FormManagementSaveReq req) {
        return formManagementService.saveList(req.getList());
    }

    @Override
    @PostMapping("/form/management/saveList/csp4customer")
    public Map<Long, Csp4CustomerFrom> saveList(@RequestBody List<Csp4CustomerFrom> list) {
        return formManagementService.saveListCsp4CustomerFrom(list);
    }

}
