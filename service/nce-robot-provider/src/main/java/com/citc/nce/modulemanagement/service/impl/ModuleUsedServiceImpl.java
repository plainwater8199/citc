package com.citc.nce.modulemanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.modulemanagement.dao.ModuleUsedMapper;
import com.citc.nce.modulemanagement.entity.ModuleUsed;
import com.citc.nce.modulemanagement.service.IModuleUsedService;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组件管理表-客户侧被使用 服务实现类
 * </p>
 *
 * @author fsyud
 * @since 2024-09-26 04:09:29
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ModuleUsedServiceImpl extends ServiceImpl<ModuleUsedMapper, ModuleUsed> implements IModuleUsedService {

    @Override
    public void useModuleManagement(ModuleManagementItem item) {
        boolean exists = this.lambdaQuery().eq(ModuleUsed::getModuleId, item.getId())
                .eq(ModuleUsed::getCreator, item.getCustomerId()).exists();
        if (Boolean.TRUE.equals(exists)) {
            return;
        }
        System.out.println("更新用户的组件权限："+item.getCustomerId()+"------"+item.getId()+"------"+item.getModuleName());
        ModuleUsed used = new ModuleUsed();
        BeanUtils.copyProperties(item, used);
        used.setModuleId(item.getId());
        used.setId(null);
        used.setCreator(item.getCustomerId());
        save(used);
    }

    @Override
    public Boolean queryUsedPermissionsById(Long moduleId) {
        return this.lambdaQuery().eq(ModuleUsed::getModuleId, moduleId)
                .eq(ModuleUsed::getCreator, SessionContextUtil.getUserId()).exists();
    }

    @Override
    public Boolean queryUsedPermissionsByName(String moduleName) {
        return this.lambdaQuery().eq(ModuleUsed::getModuleName, moduleName)
                .eq(ModuleUsed::getCreator, SessionContextUtil.getUserId()).exists();
    }
}
