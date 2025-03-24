package com.citc.nce.im.tempStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.H5TplApi;
import com.citc.nce.im.tempStore.mapper.ResourcesFormMapper;
import com.citc.nce.im.tempStore.service.IResourcesFormService;
import com.citc.nce.robot.api.materialSquare.vo.summary.H5TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.form.FormAdd;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 扩展商城—资源管理-表单管理 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-23 10:11:59
 */
@Service
public class ResourcesFormServiceImpl extends ServiceImpl<ResourcesFormMapper, ResourcesForm> implements IResourcesFormService {
    @Resource
    private H5Api h5Api;


    @Override
    @Transactional
    public void addOrUpdate(FormAdd formAdd) {
        ResourcesForm one = getOne(new LambdaUpdateWrapper<ResourcesForm>()
                .eq(ResourcesForm::getFormName, formAdd.getFormName())
                .ne(Objects.nonNull(formAdd.getId()), ResourcesForm::getId, formAdd.getId())
                .eq(ResourcesForm::getCreator, SessionContextUtil.verifyCspLogin())
        );
        if (Objects.nonNull(one)) {
            throw new BizException("名称不能重复");
        }

        ResourcesForm form = new ResourcesForm();
        BeanUtils.copyProperties(formAdd, form);
        if (Objects.nonNull(form.getId())) {
            ResourcesForm formDb = getById(form.getId());
            if (Objects.isNull(formDb)) {
                throw new BizException("表单不存在");
            }
            SessionContextUtil.sameCsp(formDb.getCreator());
            updateById(form);
        } else {
            save(form);
        }
    }

    @Override
    public List<ResourcesForm> listByIdsDel(Collection<Long> ids) {
        return getBaseMapper().listByIdsDel(ids);
    }

    @Override
    public List<H5TemplateInfo> h5TemplateListQuery(H5TemplateListQueryReq req) {
        //查询新的H5模版
        List<H5TemplateInfo> h5TemplateInfoList = h5Api.h5TemplateListQuery(req);

//        //查询历史H5模版
//        List<ResourcesForm> resourcesFormAll = new ArrayList<>();
//        if(req.getId() != null){
//            ResourcesForm byId = this.getById(req.getId());
//            if(Objects.nonNull(byId)){
//                resourcesFormAll.add(byId);
//            }
//        }
//        List<ResourcesForm> list = this.lambdaQuery().eq(ResourcesForm::getCreator, SessionContextUtil.getUserId()).isNull(ResourcesForm::getMssId).list();
//        if(!CollectionUtils.isEmpty(list)){
//            resourcesFormAll.addAll(list);
//        }
//        if(!CollectionUtils.isEmpty(resourcesFormAll)){
//            for(ResourcesForm item : resourcesFormAll){
//                H5TemplateInfo h5TemplateInfo = new H5TemplateInfo();
//                h5TemplateInfo.setId(item.getId());
//                h5TemplateInfo.setFormName(item.getFormName());
//                h5TemplateInfo.setFormCover(item.getFormCover());
//                BeanUtils.copyProperties(item, h5TemplateInfo);
//                h5TemplateInfoList.add(h5TemplateInfo);
//            }
//        }

        return h5TemplateInfoList;
    }
}
