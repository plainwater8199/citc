package com.citc.nce.mybatis.core.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/4/25 14:34
 * @Version: 1.0
 * @Description: 通用参数填充实现类
 * 如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值,具体字段见
 * com.iss.mag.fr.db.com.iss.mag.fr.db.mp.core.entity.BaseFillEntity
 */
@Slf4j
public class DefaultFieldHandler implements MetaObjectHandler {
    private final static String NOT_LOGIN = "not login";

    public DefaultFieldHandler() {

    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date()); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date()); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        String currentUserName = getCurrentUserName();
        if (StringUtils.isNotEmpty(currentUserName)) {
            this.strictInsertFill(metaObject, "creator", String.class, currentUserName); // 起始版本 3.3.0(推荐使用)
            this.strictInsertFill(metaObject, "updater", String.class, currentUserName); // 起始版本 3.3.0(推荐使用)
        }

        String cspId = getCspId();
        if (StringUtils.isNotEmpty(cspId)) {
            this.strictInsertFill(metaObject, "cspId", String.class, cspId);
            this.strictInsertFill(metaObject, "cspId", String.class, cspId);
        }
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date()); // 起始版本 3.3.3(推荐)
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        String currentUserName = getCurrentUserName();
        if (StringUtils.isNotEmpty(currentUserName)) {
            this.strictInsertFill(metaObject, "updater", String.class, currentUserName); // 起始版本 3.3.0(推荐使用)
        }
    }

    /**
     * 严格模式填充策略,默认有值不覆盖(重写方法改为fieldName包含update覆盖),如果提供的值为null也不填充
     */
    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        if (metaObject.getValue(fieldName) == null) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        return this;
    }

    /**
     * 处理平台多用户问题更新操作用户名获取
     *
     * @return
     */
    private String getCurrentUserName() {
        BaseUser user = SessionContextUtil.getUser();
        if (Objects.nonNull(user)) {
            if (!Boolean.TRUE.equals(user.getIsCustomer())) {
                if (StringUtils.isNotEmpty(user.getCspId())) {
                    //当csp登录时把creator填充为cspId
                    return user.getCspId();
                } else {
                    log.error("DefaultFieldHandler----发现代码异常cspId不正确:{}", JSON.toJSONString(user));
                }
            }
            return user.getUserId();
        }
        return "system";
    }

    /**
     * 客户和csp返回cspId
     * 管理员返回用户id
     * 没有登录返回 not login
     *
     * @return
     */
    private String getCspId() {
        BaseUser user = SessionContextUtil.getUser();
        if (Objects.nonNull(user)) {
            if (StringUtils.isNotEmpty(user.getCspId())) {
                //当csp登录时把creator填充为cspId
                return user.getCspId();
            } else {
                return user.getUserId();
            }
        }
        return NOT_LOGIN;
    }

}
