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
@TableName("chatbot_csp_customer")
public class CustomerManageDo extends BaseDo<CustomerManageDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long enterpriseId;

    private String userId;

    private String cspUserId;

    private String province;

    private String provinceCode;

    private String city;

    private String cityCode;

    private String area;

    private String areaCode;

    private Integer cspActive;

    private int deleted;

    private String permissions;

    private Integer isBinding;

    private Integer isBindingChatbot;
}
