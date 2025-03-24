package com.citc.nce.authcenter.csp.multitenant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.ActivateEmailResp;
import com.citc.nce.authcenter.auth.vo.resp.LoginResp;
import com.citc.nce.authcenter.auth.vo.resp.ModifyUserPhoneOrEmailResp;
import com.citc.nce.authcenter.auth.vo.resp.QueryUserInfoDetailResp;
import com.citc.nce.authcenter.auth.vo.resp.SetUseImgOrNameResp;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomer;
import com.citc.nce.authcenter.csp.vo.CspAgentLoginReq;
import com.citc.nce.authcenter.csp.vo.CustomerAddReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.CustomerProvinceResp;
import com.citc.nce.authcenter.csp.vo.CustomerSearchReq;
import com.citc.nce.authcenter.csp.vo.CustomerSearchResp;
import com.citc.nce.authcenter.csp.vo.CustomerUpdateReq;
import com.citc.nce.authcenter.csp.vo.ReduceBalanceResp;
import com.citc.nce.authcenter.csp.vo.UserEnterpriseInfoVo;
import com.citc.nce.authcenter.csp.vo.UserInfoForDropdownVo;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspInfoResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;

/**
 * @author jiancheng
 */
public interface CspCustomerService extends IService<CspCustomer> {
    /**
     * csp客户登录
     */
    LoginResp cusLogin(String cspId, LoginReq req);

    /**
     * 客户第三方登录
     */
    LoginResp thirdLogin(thirdLoginReq req);

    /**
     * 客户第三方验证
     */
    LoginResp thirdAuth(thirdAuthReq req);

    /**
     * 添加csp客户
     */
    void addCustomer(CustomerAddReq req);

    /**
     * 更新用户认证状态
     */
    //更新用户认证状态
    void updateAuthStatus(String customerId, Long id);

    /**
     * 分页查询客户列表
     */
    PageResult<CustomerSearchResp> queryList(CustomerSearchReq req);

    /**
     * 查询csp下所有客户
     *
     * @return
     */
    public List<UserInfoForDropdownVo> queryCustomerOfCSPForDropdown(Integer payType);

    /**
     * 获取客户省份分布情况
     */
    List<CustomerProvinceResp> getCustomerDistribution();

    /**
     * 更新csp客户启用状态
     */
    int updateCspActive(String customerId, Integer customerActive);

    /**
     * 查询客户详情
     */
    CustomerDetailResp getCustomerDetail(String customerId);


    /**
     * 修改客户信息
     */
    int updateCustomer(CustomerUpdateReq req);

    /**
     * 获取用户权限字符串
     *
     * @param customerId 用户ID
     */
    String getUserPermission(String customerId);

    /**
     * 获取csp 信息
     *
     * @param userId 有可能是cspId也有可能是customerId
     * @return
     */
    CspInfoResp getCspInfo(String userId);

    /**
     * 查询用户详情
     */
    QueryUserInfoDetailResp getUserInfoDetail(QueryUserInfoDetailReq req);

    /**
     * 查询csp的客户ID集合
     */
    List<String> queryCustomerIdsByCspId(String cspId);

    /**
     * csp客户修改名称、头像
     */
    SetUseImgOrNameResp setCusImgOrName(SetUseImgOrNameVReq req);

    /**
     * 激活邮箱
     */
    ActivateEmailResp activateEmail(ActivateEmailReq req);

    /**
     * 根据客户ID获取用户基本信息
     *
     * @param customerId 客户ID
     * @return 客户基本信息
     */
    UserInfoVo queryById(String customerId);

    /**
     * 客户充值
     *
     * @param customerId
     * @param chargeAmount
     * @return
     */
    Boolean recharge(String customerId, Long chargeAmount);

    Long getBalance(String customerId);

    Long addBalance(String customerId, Long addMoney);

    ReduceBalanceResp reduceBalance(String customerId, Long price, Long num, boolean isTryMax);

    /**
     * 客户自己修改手机号或邮箱
     */
    ModifyUserPhoneOrEmailResp updatePhoneOrEmail(ModifyUserPhoneOrEmailVReq req);

    List<UserInfoVo> checkLoadNameExist(String value);

    boolean checkUnique(String checkValue, Integer checkType);

    /**
     * 套餐超过限制时禁用用户
     *
     * @param cspId cspId
     */
    void disableCustomerMealCsp(String cspId);

    /**
     * cspId当前启用客户数
     *
     * @param cspId
     * @return 客户数
     */
    Long countActiveCustomerByCspId(String cspId);

    Long countCustomerByCspId(String cspId);

    void refreshCspMealStatus(String cspId);

    void refreshCspMealStatus(String cspId, Long mealNumMun, Long customerNum);

    List<UserInfo> listMealStatus(String cspId, Integer mealStatus);

    void changeCustomerToFormal(String customerId);

    void deleteCustomer(String customerId);

    String agentLogin(CspAgentLoginReq req);

    LoginResp getAgentLoginInfo(String token);

    Collection<UserEnterpriseInfoVo> getUserEnterpriseInfoByUserIds(Collection<String> userIds);

    Collection<UserEnterpriseInfoVo> getUserUserIdsLikeEnterpriseAccountName(String enterpriseAccountName);

    CustomerDetailResp getDetailByCustomerIdForDontCheck(String customerId);

    void changeEnableAgentLogin(@RequestBody EnableAgentLoginReq req);
}
