package com.citc.nce.authcenter.csp.multitenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.authcenter.csp.vo.CspInfoPage;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspMealCspInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author jiancheng
 */
public interface CspService extends IService<Csp> {

    /**
     * 创建csp，为csp分表
     * @param userId csp用户ID
     * @return cspId
     */
    void createCsp(String userId);

    void createCspChannel(String userId);

    /**
     * 返回csp专属登录地址
     * @return
     */
    String getCspLoginAddress();


    String obtainCspId(String userId);

    List<String> queryAllCspId();

    /**
     * 根据csp账号或手机号查询csp详情
     */
    List<UserInfoVo> queryByNameOrPhone(String nameOrPhone);

    /**
     * 根据csp id查询csp信息
     * @param idList
     * @return
     */
    List<UserInfoVo> getByIdList(Collection<String> idList);

    /**
     * csp 是否可以登录（平台禁用和csp表csp_active）
     * @param cspId
     * @return
     */
    boolean getCspStatus(String cspId);

    Csp getCspByCspId(String cspId);

    Page<CspMealCspInfo> allList(Page<CspMealCspInfo> page, CspInfoPage query);

    boolean isCspInUser(String userId);

    void checkUserPublishAuth(String userId);
}
