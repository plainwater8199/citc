package com.citc.nce.auth.meal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.meal.domain.CspMealContractAssociation;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * csp套餐合同详 关联表 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
public interface ICspMealContractAssociationService extends IService<CspMealContractAssociation> {

    /**
     * 根据套餐编号查询套餐使用量
     *
     * @param mealId 套餐编号
     */
    Long countOrderNum(@NotEmpty String mealId);

    Long countMealCount(Long contractId);

    void setMealEffectTime(Long contractId);

    void removeByMealContractId(Long id);
}
