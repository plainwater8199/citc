package com.citc.nce.auth.identification.vo.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/14 18:22
 * @Version: 1.0
 * @Description:
 */
@ApiModel("实名认证信息返回对象")
@Data
public class BossIdentificationResp {

    private PersonIdentification personIdentification;

    private EnterpriseIdentification enterpriseIdentification;

    @Data
    public static class PersonIdentification {

        private Integer personAuthStatus;

        private String personName;

        private String idCard;

        /**
         * 个人实名认证申请时间
         */
        private Date personAuthTime;

        private List<IdentificationAuditResp> auditRemarkList;
    }

    @Data
    public static class EnterpriseIdentification {

        private Integer enterpriseAuthStatus;
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

        private String address;

        private String area;

        /**
         * 审核备注
         */
        private List<IdentificationAuditResp> auditRemarkList;
    }


}
