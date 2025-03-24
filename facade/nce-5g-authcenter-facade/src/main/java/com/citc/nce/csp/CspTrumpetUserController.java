package com.citc.nce.csp;

import com.citc.nce.authcenter.csp.CspTrumpetUserApi;
import com.citc.nce.authcenter.csp.domain.CspTrumpetAdd;
import com.citc.nce.authcenter.csp.domain.CspTrumpetEdit;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * bydud
 * 2024/1/26
 **/

@Api(tags = "CspTrumpetUserController 小号管理")
@RestController
public class CspTrumpetUserController {
    @Resource
    private CspTrumpetUserApi trumpetUserApi;
    @Resource
    private ECDHService ecdhService;

    @GetMapping("/csp/cspTrumpetUser/getById")
    @HasCsp
    public CspTrumpetUser getById(@RequestParam("ctuId") Long ctuId) {
        return trumpetUserApi.getById(ctuId);
    }

    @GetMapping("/csp/cspTrumpetUser/myTrumpetList")
    @HasCsp
    public PageResult<CspTrumpetUser> listByCspId() {
        String cspId = SessionContextUtil.verifyCspLogin();
        List<CspTrumpetUser> cspTrumpetUsers = trumpetUserApi.listByCspId(cspId);
        cspTrumpetUsers.forEach(s -> s.setPhone(ecdhService.encode(s.getPhone())));
        return new PageResult<>(cspTrumpetUsers, (long) cspTrumpetUsers.size());
    }

    @PostMapping("/csp/cspTrumpetUser/add")
    @HasCsp
    @Log(title = "新增小号")
    public void add(@RequestBody @Valid CspTrumpetAdd cspTrumpet) {
        notCspTrumpetUse();
        cspTrumpet.setCspId(SessionContextUtil.verifyCspLogin());
        trumpetUserApi.add(cspTrumpet);
    }

    @PostMapping("/csp/cspTrumpetUser/edit")
    @HasCsp
    @Log(title = "修改小号")
    public void edit(@RequestBody @Valid CspTrumpetEdit cspTrumpet) {
        notCspTrumpetUse();
        cspTrumpet.setCspId(SessionContextUtil.verifyCspLogin());
        trumpetUserApi.edit(cspTrumpet);
    }

    @PostMapping("/csp/cspTrumpetUser/del")
    @HasCsp
    @Log(title = "删除小号")
    public void del(@RequestParam("ctuId") Long ctuId) {
        notCspTrumpetUse();
        trumpetUserApi.del(ctuId);
    }

    private void notCspTrumpetUse() {
        if (SessionContextUtil.isCspTrumpetUse()) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
    }
}
