package com.citc.nce.authcenter.csp.trumpet.controller;


import com.citc.nce.authcenter.csp.CspTrumpetUserApi;
import com.citc.nce.authcenter.csp.domain.CspTrumpetAdd;
import com.citc.nce.authcenter.csp.domain.CspTrumpetEdit;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;
import com.citc.nce.authcenter.csp.trumpet.service.ICspTrumpetUserService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * csp小号 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 10:01:59
 */
@RestController
public class CspTrumpetUserController implements CspTrumpetUserApi {

    @Resource
    private ICspTrumpetUserService trumpetUserService;

    @Override
    public CspTrumpetUser getById(Long ctuId) {
        return trumpetUserService.getById(ctuId);
    }

    @Override
    public List<CspTrumpetUser> listByCspId(String cspId) {
        return trumpetUserService.listByCspId(cspId);
    }

    @Override
    public void add(CspTrumpetAdd cspTrumpet) {
        trumpetUserService.add(cspTrumpet);
    }

    @Override
    public void edit(CspTrumpetEdit cspTrumpet) {
        trumpetUserService.edit(cspTrumpet);
    }

    @Override
    public void del(Long ctuId) {
        trumpetUserService.del(ctuId);
    }

    @Override
    public CspTrumpetUser getByAccountName(String accountName) {
        return trumpetUserService.getByAccountName(accountName);
    }

    @Override
    public CspTrumpetUser getByPhone(String phone) {
        return trumpetUserService.getByPhone(phone);
    }
}

