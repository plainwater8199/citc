package com.citc.nce.common.core;

import com.citc.nce.common.core.pojo.BaseUser;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/25 17:20
 * @Version: 1.0
 * @Description:
 */
public interface SessionContext {
    /**
     * 获取当前操作的用户
     *
     * @return
     */
    BaseUser getUser();
}
