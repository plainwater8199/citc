package com.citc.nce.modulemanagement.service;

import com.citc.nce.modulemanagement.entity.ModuleUsed;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;

/**
 * <p>
 * 组件管理表-客户侧被使用 服务类
 * </p>
 *
 * @author fsyud
 * @since 2024-09-26 04:09:29
 */
public interface IModuleUsedService extends IService<ModuleUsed> {

    void useModuleManagement(ModuleManagementItem item);

    Boolean queryUsedPermissionsById(Long moduleId);

    Boolean queryUsedPermissionsByName(String moduleName);

}
