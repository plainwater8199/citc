package com.citc.nce.authcenter.csp.trumpet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.authcenter.csp.domain.CspTrumpetAdd;
import com.citc.nce.authcenter.csp.domain.CspTrumpetEdit;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;

import java.util.List;

/**
 * <p>
 * csp小号 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 10:01:59
 */
public interface ICspTrumpetUserService extends IService<CspTrumpetUser> {

    List<CspTrumpetUser> listByCspId(String cspId);

    void add(CspTrumpetAdd cspTrumpet);

    CspTrumpetUser getByAccountName(String accountName);

    CspTrumpetUser getByPhone(String phone);

    void edit(CspTrumpetEdit cspTrumpet);

    void del(Long ctuId);

}
