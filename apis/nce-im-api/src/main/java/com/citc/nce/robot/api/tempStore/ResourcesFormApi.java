package com.citc.nce.robot.api.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.bean.form.FormAdd;
import com.citc.nce.robot.api.tempStore.bean.form.FormPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bydud
 * @since 15:25
 */
@FeignClient(value = "im-service", contextId = "resourcesFormApi", url = "${im:}")
public interface ResourcesFormApi {
    @PostMapping("/tempStore/resources/form/listByIds")
    @ApiOperation("listByIds")
    public List<ResourcesForm> listByIdsDel(@RequestBody List<Long> ids);

    @PostMapping("/tempStore/resources/form/page")
    @ApiOperation("表单列表查询")
    public PageResult<ResourcesForm> page(@RequestBody @Validated FormPageQuery query);

    @PostMapping("/tempStore/resources/form")
    @ApiOperation("新增/修改表单 封面图使用原来接口 chatbotApi/file/upload")
    public void addOrUpdate(@RequestBody @Validated FormAdd formAdd);

    @PostMapping("/tempStore/resources/form/removeById/{id}")
    @ApiOperation("删除表单")
    public boolean remove(@PathVariable("id") Long id);

    @GetMapping("/tempStore/resources/form/{formId}")
    @ApiOperation("查询表单数据")
    ResourcesForm getById(@PathVariable("formId") Long formId);
}
