package com.citc.nce.authcenter.userDataSyn.vo;

import lombok.Data;

@Data
public class CertificationInfo {
    private EnterpriseInfo enterprise;
    private ApplierInfo applier;
    private String applierState;
    private String enterpriseState;
    private String way;
    private String enterpriseVerifiedDate;
    private String enterpriseCreateDate;
    private String applierVerifiedDate;
    private String applierCreateDate;
    private String applierVerifier;
    private String applierReason;
    private String orgAccountName;
    private String enterpriseVerifier;
    private String enterpriseReason;
}
