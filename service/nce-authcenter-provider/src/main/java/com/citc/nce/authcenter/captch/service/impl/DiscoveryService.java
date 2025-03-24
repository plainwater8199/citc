package com.citc.nce.authcenter.captch.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 注册中心相关内容获取
 *
 * @author: bydud
 * @createTime: 2023/03/28 15:02
 * @version: v1.0
 */

@Component
public class DiscoveryService {
    @Autowired
    private DiscoveryClient client;

    public List<String> containsServices() {
        return client.getServices();
    }

    public List<ServiceInstance> listServices() {
        List<ServiceInstance> list = new java.util.ArrayList<>();
        List<String> services = client.getServices();
        for (String serviceId : services) {
            list.addAll(client.getInstances(serviceId));
        }
        return list;
    }

    /**
     * @param serviceId 服务名称 如gateway
     * @return 是否包含此服务
     */
    public boolean hasServices(String serviceId) {
        return containsServices().contains(serviceId);
    }

    /**
     * @param serviceId 服务名称 如gateway
     * @return 服务列表
     */
    public List<ServiceInstance> listServices(String serviceId) {
        if (hasServices(serviceId)) {
            return client.getInstances(serviceId);
        }
        return Collections.emptyList();
    }

}
