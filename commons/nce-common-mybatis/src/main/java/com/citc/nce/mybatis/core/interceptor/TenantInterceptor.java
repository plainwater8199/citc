package com.citc.nce.mybatis.core.interceptor;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.citc.nce.common.core.saas.TenantContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/13 9:28
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class TenantInterceptor implements TenantLineHandler {

    private Set<String> ignoreTables;

    public TenantInterceptor(Set<String> ignoreTables) {
        log.info("com.citc.nce.mybatis plus多租户插件TenantInterceptor加载，忽略的表：{}", JSONUtil.toJsonStr(ignoreTables));
        this.ignoreTables = ignoreTables;
    }

    @Override
    public Expression getTenantId() {
        return new StringValue(TenantContext.getTenantId());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        if (!StringUtils.hasLength(TenantContext.getTenantId()) || ignoreTables.contains(tableName)) {
            return true;
        }
        return false;
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }
}
