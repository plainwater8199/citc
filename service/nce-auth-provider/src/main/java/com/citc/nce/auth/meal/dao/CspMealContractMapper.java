package com.citc.nce.auth.meal.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.meal.vo.contract.CspMealContractPageInfo;
import com.citc.nce.auth.meal.vo.contract.CspMealContractPageQuery;
import com.citc.nce.auth.meal.vo.contract.CspMealCspInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * csp套餐合同表 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
public interface CspMealContractMapper extends BaseMapper<CspMealContract> {

    Page<CspMealContractPageInfo> queryPage(@Param("page") Page<CspMealContractPageInfo> page,
                                            @Param("query") CspMealContractPageQuery query);

    /**
     * 查询cspInfo
     *
     * @param cspId cspId
     * @return csp个人的信息
     */
    CspMealCspInfo selectMealCspInfo(String cspId);
}
