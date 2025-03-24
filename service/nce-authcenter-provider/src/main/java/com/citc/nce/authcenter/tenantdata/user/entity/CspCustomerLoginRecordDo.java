package com.citc.nce.authcenter.tenantdata.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

@Data
@TableName("csp_customer_login_record")
public class CspCustomerLoginRecordDo extends BaseDo {

    private String customerId;


}
