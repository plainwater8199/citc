package com.citc.nce.auth.prepayment.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerAccountPrepaymentListVo {
    private Integer accountType;
    private String accountName;
    private String operator;
    private String availableAmount;
}
