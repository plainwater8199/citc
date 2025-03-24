package com.citc.nce.auth.csp.recharge.vo;

import lombok.Data;

@Data
public class TariffOptions {
    private Integer payType; // 支付方式
    private Boolean enableEdit;
}
