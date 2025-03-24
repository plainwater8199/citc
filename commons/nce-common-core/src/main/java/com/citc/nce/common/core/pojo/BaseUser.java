package com.citc.nce.common.core.pojo;


import com.citc.nce.common.core.enums.CustomerPayType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/25 16:53
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUser {

    /**
     * 用户id,用uuid标识
     */
    private String userId;
    /**
     * 用户账户名
     */
    private String userName;
    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户平台类型,用于facade层做权限校验
     * 1 核能商城客户端
     * 2 chatbot客户端
     * 3 硬核桃社区
     * 4 管理平台
     */
    private Integer platformType;

    /**
     * 是否是csp客户
     */
    private Boolean IsCustomer;

    private String cspId;

    private CustomerPayType payType;

    private String permissions;

    private Boolean isAgentLogin;

    /**
     * 扩展商城权限(0禁用,1启用)
     */
    private Boolean tempStorePerm;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long ctuId;

    private boolean authStatus;
}
