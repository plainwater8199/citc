package com.citc.nce.authcenter.tenantdata.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.service.impl
 * @Author: ljh
 * @CreateTime: 2024-03-15  10:22
 * @Description: 这张表储存了CSP使用的到底是直连还是供应商渠道.
 * @Version: 1.0
 */
@Data
@TableName("csp_channel")
public class CspChannelDo extends BaseDo {
    // 用户ID
    private String userId;

    // 电信通道（1-直连 2-蜂动）
    private Integer telecomChannel;

    // 移动通道（1-直连 2-蜂动）
    private Integer mobileChannel;

    // 联通通道（1-直连 2-蜂动）
    private Integer unicomChannel;
}
