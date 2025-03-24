package com.citc.nce.authcenter.csp.multitenant.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_customer_login_record")
public class CspCustomerLoginRecord extends BaseDo {
    private String customerId;
    private String name;
    @TableLogic
    private Boolean deleted;
}
