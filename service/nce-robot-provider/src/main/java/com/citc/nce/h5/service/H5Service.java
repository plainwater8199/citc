package com.citc.nce.h5.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.FormManagementTreeResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.entity.H5Do;
import com.citc.nce.h5.vo.H5InfoListQuery;
import com.citc.nce.h5.vo.H5FormInfo;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.req.H5FromSubmitReq;
import com.citc.nce.h5.vo.req.H5QueryVO;
import com.citc.nce.h5.vo.resp.H5CopyResp;
import com.citc.nce.h5.vo.resp.H5Resp;
import com.citc.nce.robot.api.materialSquare.vo.summary.H5TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.form.FormInfoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.form.FormPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;


import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mac
 * @since 2023-05-21
 */
public interface H5Service extends IService<H5Do> {

    PageResult<H5Info> selectH5Page(H5QueryVO h5Vo);

    H5Resp create(H5Info h5);

    H5Info previewData(Long pid);

    H5Info previewDataDetail(Long pid);

    void update(H5Info h5Info);

    void delete(Long id);

    H5CopyResp copy(H5Info h5Temp);

    void fromSubmit(H5FromSubmitReq req);

    H5Info getByActivityId(Long mcActivityId);

    List<H5TemplateInfo> h5TemplateListQuery(H5TemplateListQueryReq req);

    PageResult<ResourcesForm> pageListForCSP(FormPageQuery query);

    List<FormManagementTreeResp> getTreeListForCustomer();

    void updateMssID(String msId, Long mssId);

    void saveForCustomer(Long msId, String userId,Long mssId);

    void formDeleteInfo(Long id);

    PageResult<H5FormInfo> formQueryList(FormInfoPageQuery query);

    void deleteMssIDForIds(List<Long> h5IdList);

    List<H5Info> byIds(H5InfoListQuery query);

    Long createSummeryH5(String msId);

    H5Info getDetailForSummery(Long msId);

    void updateSummaryH5(String msId);

    void importH5ForCustomer(Long mssId, String userId, String name);

    Map<Long, Csp4CustomerFrom> importH5ForRobotAndMSG(List<Long> h5Ids);
}
