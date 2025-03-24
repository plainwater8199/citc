package com.citc.nce.auth.meal;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.vo.meal.CspMealAddVo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageQuery;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 合同套餐
 *
 * @author jiancheng
 */
@Api(tags = "csp合同套餐api")
@FeignClient(value = "auth-service", contextId = "CspmealApi", url = "${auth:}")
public interface CspMealApi {

    /**
     * 新增套餐
     *
     * @param addVo
     */
    @PostMapping("/csp/meal/add")
    void addMeal(@RequestBody CspMealAddVo addVo);


    /**
     * 上下架
     *
     * @param id     套餐id
     * @param status 状态
     */
    @PostMapping("/csp/meal/updateState")
    void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status);

    /**
     * 删除套餐
     *
     * @param id 套餐id
     */
    @PostMapping("/csp/meal/delete")
    void deleteMeal(@RequestParam("id") Long id);


    @PostMapping("/csp/meal/page")
    Page<CspMealPageInfo> page(@RequestBody CspMealPageQuery query);

    /**
     * 查询条件导出
     *
     * @param query 条件
     */
    @PostMapping("/csp/meal/exportList")
    List<CspMealPageInfo> exportList(@RequestBody CspMealPageQuery query);
}
