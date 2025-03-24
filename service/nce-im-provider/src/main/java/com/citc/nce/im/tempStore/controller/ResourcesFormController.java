package com.citc.nce.im.tempStore.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.h5.H5Api;
import com.citc.nce.im.tempStore.service.IResourcesFormService;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.robot.api.tempStore.ResourcesFormApi;
import com.citc.nce.robot.api.tempStore.bean.form.FormAdd;
import com.citc.nce.robot.api.tempStore.bean.form.FormPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 扩展商城—资源管理-表单管理 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-23 10:11:59
 */
@RestController
@Api(tags = "csp-模板商城-资源管理-表单管理")
@AllArgsConstructor
@Slf4j
public class ResourcesFormController implements ResourcesFormApi {
    private final IResourcesFormService formService;

    private final H5Api h5Api;

    @Override
    public List<ResourcesForm> listByIdsDel(List<Long> ids) {
        return formService.listByIdsDel(ids);
    }

    @ApiOperation("表单列表查询")
    public PageResult<ResourcesForm> page(@RequestBody @Validated FormPageQuery query) {

        return h5Api.pageListForCSP(query);


//        Page<ResourcesForm> page = PageSupport.getPage(ResourcesForm.class, query.getPageNo(), query.getPageSize());
//        formService.page(page, new LambdaQueryWrapper<ResourcesForm>()
//                .eq(ResourcesForm::getCreator, SessionContextUtil.verifyCspLogin())
//                .orderByDesc(ResourcesForm::getCreateTime)
//        );
//        return new PageResult<>(page.getRecords(), page.getTotal());
    }


    @ApiOperation("新增/修改表单 封面图使用原来接口 chatbotApi/file/upload")
    public void addOrUpdate(@RequestBody @Validated FormAdd formAdd) {
        formService.addOrUpdate(formAdd);
    }


    @ApiOperation("删除表单")
    public boolean remove(@PathVariable("id") Long id) {
        return formService.removeById(id);
    }

    @Override
    public ResourcesForm getById(@PathVariable("formId") Long formId) {
        ResourcesForm form = formService.getById(formId);
        SessionContextUtil.sameCsp(form.getCreator());
        return form;
    }
}

