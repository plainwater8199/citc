package com.citc.nce.auth.formmanagement;

import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:53
 * @Version: 1.0
 * @Description:卡片样式
 */

@FeignClient(value = "auth-service", contextId = "FormManagementApi", url = "${auth:}")
public interface FormManagementApi {
    /**
     * 卡片样式列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/pageList")
    PageResultResp getFormManagements(@RequestBody @Valid PageParam pageParam);

    /**
     * 新增卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/save")
    FormManagementSaveResp saveFormManagement(@RequestBody @Valid FormManagementReq FormManagementReq);

    /**
     * 修改卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/edit")
    int updateFormManagement(@RequestBody @Valid FormManagementEditReq FormManagementEditReq);

    /**
     * 删除卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/delete")
    int delFormManagementById(@RequestBody @Valid FormManagementOneReq FormManagementOneReq);

    /**
     * 获取单个卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/getOne")
    FormManagementResp getFormManagementById(@RequestBody @Valid FormManagementOneReq FormManagementOneReq);

    /**
     * 获取卡片样式树
     *
     * @param
     * @return
     */
    @PostMapping("/form/management/getTreeList")
    List<FormManagementTreeResp> getTreeList();

    @PostMapping("/form/management/getEdit")
    FormManagementResp getEdit(FormManagementOneReq formManagementOneReq);

    @PostMapping("/form/management/saveList")
    int saveList(@RequestBody FormManagementSaveReq req);

    @PostMapping("/form/management/saveList/csp4customer")
    Map<Long,Csp4CustomerFrom> saveList(@RequestBody  List<Csp4CustomerFrom> list);
}
