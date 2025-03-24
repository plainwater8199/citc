package com.citc.nce.auth.meal.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.CspMealApi;
import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.service.ICspMealService;
import com.citc.nce.auth.meal.vo.meal.CspMealAddVo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageQuery;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * csp用户套餐表 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-19 04:01:13
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "csp-meal-套餐管理")
public class CspMealController implements CspMealApi {
    private final ICspMealService cspMealService;

    @PostMapping("/csp/meal/add")
    @Override
    public void addMeal(@RequestBody @Validated CspMealAddVo addVo) {
        cspMealService.addMeal(addVo.getType(), addVo.getName(), addVo.getCustomerNumber(), addVo.getPrice());
    }

    @PostMapping("/csp/meal/updateState")
    @Override
    public void updateState(@RequestParam Long id, @RequestParam Integer status) {
        cspMealService.updateState(id, CspMealStatus.byCode(status));
    }

    @PostMapping("/csp/meal/delete")
    @Override
    public void deleteMeal(@RequestParam Long id) {
        cspMealService.deleteMeal(id);
    }

    @PostMapping("/csp/meal/page")
    @Override
    public Page<CspMealPageInfo> page(@RequestBody @Validated CspMealPageQuery query) {
        return cspMealService.queryPage(query);
    }

    @Override
    public List<CspMealPageInfo> exportList(CspMealPageQuery query) {
        return cspMealService.exportList(query);
    }
}

