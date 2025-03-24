package com.citc.nce.auth.csp.contract.vo;

import lombok.Data;

@Data
public class ContractStatusResp {
    private Integer telecomContractOptions; // 电信合同选项 （1-直连 2-代理 3-并集）
    private Integer mobileContractOptions; // 移动合同选项
    private Integer unicomContractOptions; // 联通合同选项
}
