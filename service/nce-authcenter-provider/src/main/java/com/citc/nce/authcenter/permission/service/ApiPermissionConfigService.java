package com.citc.nce.authcenter.permission.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.permission.ApiPermissionApi;
import com.citc.nce.authcenter.permission.dao.ApiPermissionConfigMapper;
import com.citc.nce.authcenter.permission.entity.ApiPermissionConfig;
import com.citc.nce.authcenter.permission.enums.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiancheng
 */
@Service
@Slf4j
public class ApiPermissionConfigService extends ServiceImpl<ApiPermissionConfigMapper, ApiPermissionConfig> {
    /**
     * 设置接口权限
     *
     * @param url         接口路径
     * @param permissions 需要权限列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void config(String url, List<Permission> permissions) {
        //删除接口的原有配置
        baseMapper.delete(lambdaQuery().eq(ApiPermissionConfig::getUrl, url));
        List<ApiPermissionConfig> permissionConfigs = permissions.stream()
                .map(permission -> new ApiPermissionConfig().setUrl(url).setPermission(permission))
                .collect(Collectors.toList());
        saveBatch(permissionConfigs);
    }


    /**
     * 获取url的权限
     *
     * @param uri uri
     */
    public List<Permission> getUrlPermission(String uri) {
        return lambdaQuery()
                .select(ApiPermissionConfig::getPermission)
                .eq(ApiPermissionConfig::getUrl, uri)
                .list()
                .stream()
                .map(ApiPermissionConfig::getPermission)
                .collect(Collectors.toList());
    }


}
