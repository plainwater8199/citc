package com.citc.nce.modulemanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.modulemanagement.dao.ModuleMapper;
import com.citc.nce.modulemanagement.entity.ModuleDo;
import com.citc.nce.modulemanagement.service.ModuleManagementService;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.modulemanagement.vo.ModuleManagementResp;
import com.citc.nce.modulemanagement.vo.req.QueryForMSReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModuleManagementServiceImpl extends ServiceImpl<ModuleMapper, ModuleDo> implements IService<ModuleDo>,
        ModuleManagementService {
    @Override
    public ModuleManagementResp moduleQuery() {
        ModuleManagementResp resp = new ModuleManagementResp();

        List<ModuleDo> moduleDoList = this.lambdaQuery().orderByDesc(ModuleDo::getCreateTime).list();
        List<ModuleManagementItem> items = moduleDoList.stream().map(item -> {
            ModuleManagementItem moduleManagementItem = new ModuleManagementItem();
            moduleManagementItem.setId(item.getId());
            moduleManagementItem.setModuleName(item.getModuleName());
            moduleManagementItem.setDescription(item.getDescription());
            moduleManagementItem.setModuleType(item.getModuleType());
            moduleManagementItem.setCreateTime(item.getCreateTime());
            return moduleManagementItem;
        }).collect(Collectors.toList());
        resp.setItems(items);
        return resp;
    }

    @Override
    public ModuleManagementItem queryById(Long id) {
        ModuleManagementItem item = new ModuleManagementItem();
        ModuleDo moduleDo = this.getById(id);
        if (Objects.isNull(moduleDo)) {
            return item;
        }
        BeanUtils.copyProperties(moduleDo, item);
        return item;
    }

    @Override
    public void updateMssID(Long id, Long mssId) {
        this.lambdaUpdate().eq(ModuleDo::getMssId, mssId).set(ModuleDo::getMssId, null).update();
        ModuleDo moduleDo = this.getById(id);
        if (!Objects.isNull(moduleDo)) {
            moduleDo.setMssId(mssId);
            this.updateById(moduleDo);
        }else{
            throw new BizException(500, "组件素材不存在！");
        }
    }

    @Override
    public ModuleManagementResp queryForMS(QueryForMSReq req) {
        ModuleManagementResp resp = new ModuleManagementResp();

        List<ModuleDo> moduleDoList = this.lambdaQuery().isNull(ModuleDo::getMssId).or().eq(req.getId()!= null ,ModuleDo::getId, req.getId()).orderByDesc(ModuleDo::getCreateTime).list();
        List<ModuleManagementItem> items = moduleDoList.stream().map(item -> {
            ModuleManagementItem moduleManagementItem = new ModuleManagementItem();
            moduleManagementItem.setId(item.getId());
            moduleManagementItem.setModuleName(item.getModuleName());
            moduleManagementItem.setDescription(item.getDescription());
            moduleManagementItem.setModuleType(item.getModuleType());
            moduleManagementItem.setCreateTime(item.getCreateTime());
            return moduleManagementItem;
        }).collect(Collectors.toList());
        resp.setItems(items);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMssIDForIds(List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)){
            baseMapper.updateMssIDNullById(ids);
        }
    }
}
