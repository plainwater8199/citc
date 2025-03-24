package com.citc.nce.common.core.saas;

import com.citc.nce.common.thread.ThreadTaskUtils;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/13 9:20
 * @Version: 1.0
 * @Description: saas化租户信息上下文工具，用于在请求，
 * 建议所有相关theadlocal操作都放在此类多个租户信息需要传递时可以将tenantIdLocal的泛型替换为map并提供对应的方法
 */
public class TenantContext {
    //token tenantId traceId
    /**
     *
     */
    public static ThreadLocal<String> tenantIdLocal = ThreadTaskUtils.newThreadLocal();

    public static String getTenantId() {
        return tenantIdLocal.get();
    }

    public static void setTenantId(String tenantId) {
        tenantIdLocal.set(tenantId);
    }

    public static void cleanTenant() {
        tenantIdLocal.remove();
    }

    public static void main(String[] args) {

    }

}
