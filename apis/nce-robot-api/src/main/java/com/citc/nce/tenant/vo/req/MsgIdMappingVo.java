package com.citc.nce.tenant.vo.req;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

//平台msgId和本地扣费msgId中间表
@Data
@Accessors(chain = true)
public class MsgIdMappingVo {
    private String messageId;
    private Set<String> platformMsgIds = CollectionUtil.newHashSet();
    private String customerId;
}
