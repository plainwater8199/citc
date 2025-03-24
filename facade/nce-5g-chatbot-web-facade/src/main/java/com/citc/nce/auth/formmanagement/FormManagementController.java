package com.citc.nce.auth.formmanagement;



import com.citc.nce.auth.formmanagement.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.h5.H5Api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 表单管理
 */
@RestController
@Slf4j
@Api(value = "auth", tags = "表单管理")
public class FormManagementController {
    @Resource
    private FormManagementApi formManagementApi;

    @Resource
    private H5Api h5Api;



    /**
     * 表单管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "表单管理列表分页获取", notes = "表单管理列表分页获取")
    @PostMapping("/form/management/pageList")
    public PageResultResp getFormManagements(@RequestBody @Valid PageParam pageParam) {
        return formManagementApi.getFormManagements(pageParam);
    }

    /**
     * 新增表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增表单管理", notes = "新增表单管理")
    @PostMapping("/form/management/save")
    public FormManagementSaveResp saveFormManagement(@RequestBody  @Valid FormManagementReq formManagementReq) {
        return formManagementApi.saveFormManagement(formManagementReq);
    }

    /**
     * 修改表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改表单管理", notes = "修改表单管理")
    @PostMapping("/form/management/edit")
    public int updateFormManagement(@RequestBody  @Valid FormManagementEditReq formManagementEditReq) {
        return formManagementApi.updateFormManagement(formManagementEditReq);
    }

    /**
     * 删除表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除表单管理", notes = "删除表单管理")
    @PostMapping("/form/management/delete")
    public int delFormManagementById(@RequestBody  @Valid FormManagementOneReq formManagementOneReq) {
        return formManagementApi.delFormManagementById(formManagementOneReq);
    }

    /**
     * 根据id获取表单管理
     *
     * @param
     * @return
     */
    @SkipToken
    @ApiOperation(value = "根据id获取表单管理", notes = "根据id获取表单管理")
    @PostMapping("/form/management/getOne")
    public FormManagementResp getFormManagementById(@RequestBody  @Valid FormManagementOneReq formManagementOneReq) {
        return formManagementApi.getFormManagementById(formManagementOneReq);
    }

    /**
     * 根据id获取表单管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据id获取表单管理编辑数据", notes = "根据id获取表单管理编辑数据")
    @PostMapping("/form/management/getEdit")
    public FormManagementResp getEdit(@RequestBody  @Valid FormManagementOneReq formManagementOneReq) {
        return formManagementApi.getEdit(formManagementOneReq);
    }

    /**
     * 获取表单管理树
     *
     * @param
     * @return
     */
    @GetMapping("/form/management/getTreeList")
    public List<FormManagementTreeResp> getTreeList() {
        return h5Api.getTreeListForCustomer();
    }



}
