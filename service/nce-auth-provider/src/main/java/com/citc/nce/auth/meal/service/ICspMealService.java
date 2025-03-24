package com.citc.nce.auth.meal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.meal.domain.CspMeal;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.citc.nce.auth.meal.vo.contract.CspContractMeal;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageQuery;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * csp用户套餐表 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-19 04:01:13
 */
public interface ICspMealService extends IService<CspMeal> {

    void addMeal(CspMealType type, String name, Integer customerNumber, BigDecimal price);

    void updateState(Long id, CspMealStatus status);

    void deleteMeal(Long id);

    /**
     * 套餐分页查询
     *
     * @param query 查询条件
     */
    Page<CspMealPageInfo> queryPage(CspMealPageQuery query);

    List<CspMealPageInfo> exportList(CspMealPageQuery query);


    void checkAndSave(CspMealContract contract, List<String> mealList, boolean removeOld);

    /**
     * 根据合同id 查询套餐用户数量
     *
     * @param contractId 合同id
     * @return 所以套餐数量总和
     */
    Long countMealCount(Long contractId);

    /**
     * 查询合同所以套餐
     *
     * @param contractId 合同id
     * @return 第一个是基础套餐，后面得是扩展套餐
     */
    List<CspContractMeal> listByContractId(Long contractId);

    /**
     * 设置套餐生效时间
     * @param contractId 合同id
     */
    void setMealEffectTime(Long contractId);

    void removeByMealContractId(Long id);
}
