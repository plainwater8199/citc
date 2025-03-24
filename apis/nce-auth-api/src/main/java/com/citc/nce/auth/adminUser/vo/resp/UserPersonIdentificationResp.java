package com.citc.nce.auth.adminUser.vo.resp;

import lombok.Data;

import java.util.Date;

@Data
public class UserPersonIdentificationResp {

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
     * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer personAuthStatus;

    /**
     * 姓名
     */
    private String personName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 身份证正面照片
     */
    private String idCardImgFront;

    /**
     * 身份证反面照片
     */
    private String idCardImgBack;

    /**
     * 个人实名认证申请时间
     */
    private Date personAuthTime;

    /**
     * 个人认证审核时间
     */
    private Date personAuthAuditTime;

    /**
     * 最新审核备注
     */
    private String auditRemark;

}
