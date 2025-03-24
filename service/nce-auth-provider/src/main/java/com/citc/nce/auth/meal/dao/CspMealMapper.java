package com.citc.nce.auth.meal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.domain.CspMeal;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * csp用户套餐表 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
public interface CspMealMapper extends BaseMapper<CspMeal> {

    /**
     * 分页查询
     *
     * @param page  分页查询
     * @param query 查询条件
     */
    Page<CspMealPageInfo> queryPage(@Param("page") Page<CspMealPageInfo> page, @Param("query") CspMealPageQuery query);

    /**
     * 根据条件查询导出数据
     *
     * @param query 导出数据
     */
    List<CspMealPageInfo> exportList(@Param("query") CspMealPageQuery query);

    Long countMealCount(@Param("contractId") Long contractId);
}
