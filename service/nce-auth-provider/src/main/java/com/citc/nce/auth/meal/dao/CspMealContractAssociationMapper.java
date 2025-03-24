package com.citc.nce.auth.meal.dao;

import com.citc.nce.auth.meal.domain.CspMealContractAssociation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * csp套餐合同详 关联表 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
public interface CspMealContractAssociationMapper extends BaseMapper<CspMealContractAssociation> {

    Long selectMealCount(@Param("contractId") Long contractId);
}
