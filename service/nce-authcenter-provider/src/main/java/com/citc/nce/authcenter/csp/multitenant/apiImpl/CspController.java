package com.citc.nce.authcenter.csp.multitenant.apiImpl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.csp.vo.CspInfoPage;
import com.citc.nce.authcenter.csp.vo.QueryAllCspIdResp;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspMealCspInfo;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiancheng
 */
@RestController
@Slf4j
@RequestMapping("csp")
public class CspController implements CspApi {
    @Autowired
    private CspService cspService;
    @Autowired
    private UserEnterpriseIdentificationDao ueiDao;
    @Autowired
    private CspCustomerService cspCustomerService;

    @PostMapping("/login/address")
    @Override
    public String getCspLoginAddress() {
        return cspService.getCspLoginAddress();
    }

    @PostMapping("queryCspId/{userId}")
    @Override
    public String queryCspId(String userId) {
        return cspService.obtainCspId(userId);
    }

    @Override
    @PostMapping("/queryAllCsp")
    public QueryAllCspIdResp queryAllCspId() {
        QueryAllCspIdResp resp = new QueryAllCspIdResp();
        resp.setCspIds(cspService.queryAllCspId());
        return resp;
    }

    @Override
    @GetMapping("/queryByNameOrPhone")
    public List<UserInfoVo> queryByNameOrPhone(String nameOrPhone) {
        return cspService.queryByNameOrPhone(nameOrPhone);
    }

    @GetMapping("/getByIdList")
    @Override
    public List<UserInfoVo> getByIdList(@RequestParam("idList") Collection<String> idList) {
        return cspService.getByIdList(idList);
    }

    @GetMapping("/obtainCspIds")
    @Override
    public List<String> obtainCspIds(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds))
            return Collections.emptyList();
        return cspService.lambdaQuery()
                .select(Csp::getCspId)
                .in(Csp::getUserId, userIds)
                .list()
                .stream()
                .map(Csp::getCspId)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("getCustomerIdByEnterpriseAccountName")
    public List<String> getCustomerIdLikeEnterpriseAccountName(String cspId, String enterpriseAccountName) {
        return ueiDao.selectList(Wrappers.<UserEnterpriseIdentificationDo>lambdaQuery()
                        .likeRight(UserEnterpriseIdentificationDo::getUserId, cspId)
                        .like(UserEnterpriseIdentificationDo::getEnterpriseAccountName, enterpriseAccountName)
                        .select(UserEnterpriseIdentificationDo::getUserId)
                )
                .stream()
                .map(UserEnterpriseIdentificationDo::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/refreshCspMealStatus")
    public void refreshCspMealStatus(String cspId) {
        cspCustomerService.refreshCspMealStatus(cspId);
    }

    @Override
    @GetMapping("/listMealStatus")
    public List<UserInfo> listMealStatus(@RequestParam(value = "cspId", required = false) String cspId,
                                         @RequestParam(value = "mealStatus", required = false) Integer mealStatus) {
        return cspCustomerService.listMealStatus(cspId, mealStatus);
    }

    @GetMapping("getCspStatus")
    @Override
    public boolean getCspStatus(String cspId) {
        return cspService.getCspStatus(cspId);
    }

    @Override
    @PostMapping("/account/allList")
    public Page<CspMealCspInfo> allList(@RequestBody @Validated CspInfoPage query) {
        Page<CspMealCspInfo> page = new Page<>(query.getPageNo(), query.getPageSize());
        return cspService.allList(page,query);
    }

    @Override
    @PostMapping("/checkUserPublishAuth")
    public void checkUserPublishAuth(String userId) {
        cspService.checkUserPublishAuth(userId);
    }

    @Override
    @GetMapping("/isCspInUser")
    public boolean isCspInUser(@RequestParam("userId") String userId) {
        return cspService.isCspInUser(userId);
    }
}
