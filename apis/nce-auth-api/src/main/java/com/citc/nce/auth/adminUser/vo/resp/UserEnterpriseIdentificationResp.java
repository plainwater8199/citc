package com.citc.nce.auth.adminUser.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserEnterpriseIdentificationResp {

    private Long id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 资质code
     */
    private Integer certificateId;
    /**
     * 企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer enterpriseAuthStatus;

    /**
     * 企业账户名
     */
    private String enterpriseAccountName;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 营业执照
     */
    private String enterpriseLicense;

    /**
     * 统一社会信用代码/注册号/组织机构代码
     */
    private String creditCode;

    /**
     * 企业认证申请时间
     */
    private Date enterpriseAuthTime;

    /**
     * 企业认证审核时间
     */
    private Date enterpriseAuthAuditTime;

    /**
     * 最新审核备注
     */
    private String auditRemark;

    /**
     * 详细地址
     */
    private String address;
    /**
     * 所在省 数据字典服务
     */
    private String province;
    /**
     * 所在市 数据字典服务
     */
    private String city;
    /**
     * 所在地区 数据字典服务
     */
    private String area;

    private String zone;

}
