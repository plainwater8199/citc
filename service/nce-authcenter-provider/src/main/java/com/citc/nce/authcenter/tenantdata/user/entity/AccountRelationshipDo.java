package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("chatbot_csp_account_relationship")
public class AccountRelationshipDo extends BaseDo<AccountRelationshipDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String customerId;

    private String cspUserId;

    private String cspId;

}
