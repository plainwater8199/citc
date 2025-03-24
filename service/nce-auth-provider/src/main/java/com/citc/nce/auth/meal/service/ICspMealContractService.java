package com.citc.nce.auth.meal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.citc.nce.auth.meal.enums.CspMealContractStatus;
import com.citc.nce.auth.meal.vo.MealCspHome;
import com.citc.nce.auth.meal.vo.contract.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * csp套餐合同表 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
public interface ICspMealContractService extends IService<CspMealContract> {

    void add(CspMealContractAddVo contractInfo);

    void edit(CspMealContractEditVo contractInfo);

    void save(CspMealContract contract, List<String> mealList);

    void update(CspMealContract contract, List<String> mealList);

    void del(Long id);

    Page<CspMealContractPageInfo> queryPage(CspMealContractPageQuery query);

    CspMealContractInfo queryById(Long id);

    void updateStatus(Long contractId, CspMealContractStatus cspMealContractStatus);

    Map<String, Integer> manageHome();

    /**
     * 查询csp正在生效得合同
     * @param cspId cspId
     */
    public CspMealContract getCspCurrentContract(String cspId);

    /**
     * csp 生效合同的套餐量
     *
     * @param cspId cspId
     */
    Long countCurrentMealNumMunByCspId(String cspId);

    /**
     * 生效合同
     * @param contractId 合同id
     */
    void effectContract(Long contractId);

    /**
     * 刷新csp meal状态 合同套餐和实际用户
     * @param cspId cspId
     */
    void refreshCspMealStatus(String cspId);

    //csp 页面侧==========================================================================================================
    MealCspHome cspHome();

    /**
     * 判断合同是否是指定csp的
     * @param cspId cspId
     * @param contractId contractId
     * @return CspMealContract
     */
    CspMealContract isUserContract(String cspId, Long contractId);

    List<CspContractMeal> cspContractMealDetail(Long contractId);

}
