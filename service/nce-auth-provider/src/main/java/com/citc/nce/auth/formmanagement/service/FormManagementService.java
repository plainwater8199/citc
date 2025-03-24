package com.citc.nce.auth.formmanagement.service;


import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.*;
import com.citc.nce.common.core.pojo.PageParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:30
 * @Version: 1.0
 * @Description:
 */
public interface FormManagementService {


    PageResultResp getFormManagements(PageParam pageParam);

    FormManagementSaveResp saveFormManagement(FormManagementReq formManagementReq);

    int updateFormManagement(FormManagementEditReq formManagementEditReq);

    int delFormManagementById(FormManagementOneReq formManagementOneReq);

    FormManagementResp getFormManagementById(FormManagementOneReq formManagementOneReq);

    List<FormManagementTreeResp> getTreeList();

    FormManagementResp getEdit(FormManagementOneReq formManagementOneReq);

    int saveList(List<FormManagementReq> list);

    Map<Long, Csp4CustomerFrom> saveListCsp4CustomerFrom(List<Csp4CustomerFrom> list);
}
