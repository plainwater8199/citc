package com.citc.nce.authcenter.csp;

import com.citc.nce.authcenter.csp.domain.CspTrumpetAdd;
import com.citc.nce.authcenter.csp.domain.CspTrumpetEdit;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * csp 小号 子账号管理
 * bydud
 * 2024/1/26
 **/
@FeignClient(value = "authcenter-service", contextId = "cspTrumpetUserApi", url = "${authCenter:}")
public interface CspTrumpetUserApi {

    @GetMapping("/csp/cspTrumpetUser/getById")
    CspTrumpetUser getById(@RequestParam("ctuId") Long ctuId);

    @GetMapping("/csp/cspTrumpetUser/cspId")
    List<CspTrumpetUser> listByCspId(@RequestParam("cspId") String cspId);

    @PostMapping("/csp/cspTrumpetUser/add")
    void add(@RequestBody @Valid CspTrumpetAdd cspTrumpet);

    @PostMapping("/csp/cspTrumpetUser/edit")
    void edit(@RequestBody @Valid CspTrumpetEdit cspTrumpet);

    @PostMapping("/csp/cspTrumpetUser/del")
    void del(@RequestParam("ctuId") Long ctuId);

    @GetMapping("/csp/cspTrumpetUser/accountName")
    CspTrumpetUser getByAccountName(@RequestParam("accountName") String accountName);

    @GetMapping("/csp/cspTrumpetUser/phone")
    CspTrumpetUser getByPhone(@RequestParam("phone") String phone);
}
