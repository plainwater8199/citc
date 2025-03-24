package com.citc.nce.authcenter.csp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.csp.vo.CspInfoPage;
import com.citc.nce.authcenter.csp.vo.QueryAllCspIdResp;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspMealCspInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author jiancheng
 */
@FeignClient(value = "authcenter-service", contextId = "csp", url = "${authCenter:}")
public interface CspApi {

    @PostMapping("/csp/login/address")
    String getCspLoginAddress();

    @PostMapping("/csp/queryCspId/{userId}")
    String queryCspId(@PathVariable("userId") String userId);

    @PostMapping("/csp/queryAllCsp")
    QueryAllCspIdResp queryAllCspId();

    @GetMapping("/csp/queryByNameOrPhone")
    List<UserInfoVo> queryByNameOrPhone(@RequestParam("nameOrPhone") String nameOrPhone);

    /**
     * @param idList csp ID list
     */
    @GetMapping("/csp/getByIdList")
    List<UserInfoVo> getByIdList(@RequestParam("idList") Collection<String> idList);

    @GetMapping("/csp/obtainCspIds")
    List<String> obtainCspIds(@RequestParam("userIds") Collection<String> userIds);

    @GetMapping("/csp/getCustomerIdByEnterpriseAccountName")
    List<String> getCustomerIdLikeEnterpriseAccountName(@RequestParam("cspId") String cspId, @RequestParam("enterpriseAccountName") String enterpriseAccountName);

    /**
     * 刷新指定csp的合同用户数和客户数 状态
     */
    @GetMapping("/csp/refreshCspMealStatus")
    void refreshCspMealStatus(@RequestParam("cspId") String cspId);

    @GetMapping("/csp/listMealStatus")
    List<UserInfo> listMealStatus(@RequestParam(value = "cspId", required = false) String cspId ,
                                  @RequestParam(value = "mealStatus", required = false) Integer mealStatus);

    @GetMapping("/csp/getCspStatus")
    boolean getCspStatus(@RequestParam("cspId") String cspId);

    /**
     * 检查在user表中，但是是不是CSP，是否在csp表中，如果不在说明是新注册的用户
     * @param userId 用户ID
     * @return 是否是csp
     */
    @GetMapping("/csp/isCspInUser")
    boolean isCspInUser(@RequestParam("userId") String userId);

    @PostMapping("/csp/account/allList")
    Page<CspMealCspInfo> allList(@RequestBody @Validated CspInfoPage query);

    @PostMapping("/csp/checkUserPublishAuth")
    void checkUserPublishAuth(@RequestParam("cspId") String userId);
}
