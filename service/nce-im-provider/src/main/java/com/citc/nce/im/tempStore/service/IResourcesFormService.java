package com.citc.nce.im.tempStore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.robot.api.materialSquare.vo.summary.H5TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.form.FormAdd;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城—资源管理-表单管理 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-23 10:11:59
 */
public interface IResourcesFormService extends IService<ResourcesForm> {

    /**
     * 新增或修改表单
     * @param formAdd 数据
     */
    void addOrUpdate(FormAdd formAdd);

    List<ResourcesForm> listByIdsDel(Collection<Long> ids);

    List<H5TemplateInfo> h5TemplateListQuery(H5TemplateListQueryReq req);
}
