package com.citc.nce.auth.unicomAndTelecom.req;


import lombok.Data;

@Data
public class CspCustomerReq {
    /**
     * 客户基本信息
     */
    private RcsRegisterInfo rcsRegisterInfo;

    /**
     * 客户企业信息
     */
    private RcsInfo rcsInfo;

    /**
     * 合同信息
     */
    private RcsContractInformation rcsContractInformation;

    /**
     * 客户法人信息
     */
    private RcsLegalP rcsLegalP;


}
