package com.citc.nce.auth.prepayment.service;

import com.citc.nce.auth.prepayment.vo.CustomerAccountPrepaymentListVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentAccountReq;

import java.util.List;

public interface IPrepaymentAccountService {

    List<CustomerAccountPrepaymentListVo> selectCustomerAllAccount(Long planId);

    List<CustomerAccountPrepaymentListVo> selectCustomerAccount(PrepaymentAccountReq req);
}
