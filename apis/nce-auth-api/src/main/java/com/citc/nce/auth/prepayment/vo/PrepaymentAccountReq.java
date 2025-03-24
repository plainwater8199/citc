package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author jcrenc
 * @since 2024/12/5 16:55
 */
@Data
public class PrepaymentAccountReq {
    @NotNull
    private MsgTypeEnum msgType;
    @NotEmpty
    private String account;
}
