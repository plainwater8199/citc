package com.citc.nce.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.ResourcesFormApi;
import com.citc.nce.robot.api.tempStore.bean.form.FormAdd;
import com.citc.nce.robot.api.tempStore.bean.form.FormPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bydud
 * @since 15:27
 */

@RestController
@Api(tags = "csp-模板商城-资源管理-表单管理")
@AllArgsConstructor
public class ResourcesFormController {
    private final ResourcesFormApi formApi;

    @PostMapping("/tempStore/resources/form/listByIds")
    @ApiOperation("listByIds")
    public List<ResourcesForm> listByIds(@RequestBody List<Long> ids) {
        return formApi.listByIdsDel(ids);
    }

    @GetMapping("/tempStore/resources/getById/{formId}")
    @ApiOperation("查询表单数据")
    public ResourcesForm getById(@PathVariable("formId") Long formId) {
        return formApi.getById(formId);
    }

    @PostMapping("/tempStore/resources/form/page")
    @ApiOperation("表单列表查询")
    public PageResult<ResourcesForm> page(@RequestBody @Validated FormPageQuery query) {
        return formApi.page(query);
    }

    @PostMapping("/tempStore/resources/form")
    @ApiOperation("新增/修改")
    public void addOrUpdate(@RequestBody @Validated FormAdd formAdd) {
        formApi.addOrUpdate(formAdd);
    }

    @PostMapping("/tempStore/resources/form/removeById/{id}")
    @ApiOperation("删除")
    public boolean remove(@PathVariable("id") Long id) {
        return formApi.remove(id);
    }
}
