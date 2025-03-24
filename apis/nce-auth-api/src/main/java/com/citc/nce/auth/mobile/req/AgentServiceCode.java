package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 代理商服务代码
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class AgentServiceCode extends BaseRequest{

    /**
     * 服务代码
     * 必须归属非直签客户关联的代理商
     */
    private String serviceCode;
    /**
     * 扩展码（服务代码+扩展码不超过20位）
     */
    private String extCode;
    /**
     * 非直签客户编码
     */
    private String customerNum;
    /**
     * 操作类型
     * 1-分配 2-收回
     */
    private String type;

}
