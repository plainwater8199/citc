package com.citc.nce.auth.meal.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.CspMealContractApi;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.citc.nce.auth.meal.service.ICspMealContractService;
import com.citc.nce.auth.meal.vo.MealCspHome;
import com.citc.nce.auth.meal.vo.contract.*;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * csp套餐合同表 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
@RestController
@RequestMapping("/csp/mealContract")
@Api(tags = "csp-meal-contract-合同管理")
@AllArgsConstructor
public class CspMealContractController implements CspMealContractApi {
    private final ICspMealContractService contractService;

    @Override
    @PostMapping("/add")
    public void addContract(@RequestBody @Validated CspMealContractAddVo contractInfo) {
        contractService.add(contractInfo);
    }

    @Override
    @PostMapping("/edit")
    public void editContract(@RequestBody @Validated CspMealContractEditVo contractInfo) {
        contractService.edit(contractInfo);
    }

    @Override
    @GetMapping("/getById/{id}")
    public CspMealContractInfo getById(@PathVariable("id") Long id) {
        return contractService.queryById(id);
    }

    @Override
    @GetMapping("/del/{id}")
    public void del(Long id) {
        contractService.del(id);
    }

    @Override
    @PostMapping("/page")
    public Page<CspMealContractPageInfo> page(@RequestBody CspMealContractPageQuery query) {
        return contractService.queryPage(query);
    }

    @Override
    @GetMapping("/manageHome")
    public Map<String, Integer> manageHome() {
        return contractService.manageHome();
    }

    @Override
    @GetMapping("/countCurrentMealNumMunByCspId")
    public Long countCurrentMealNumMunByCspId(String cspId) {
        return contractService.countCurrentMealNumMunByCspId(cspId);
    }


    //==================csp 页面接口

    @Override
    @GetMapping("/cspHome")
    public MealCspHome cspHome() {
        return contractService.cspHome();
    }

    @Override
    @PostMapping("/cspContractMealDetail")
    public List<CspContractMeal> cspContractMealDetail(@RequestParam("contractId") Long contractId) {
        //检查合是否是当前csp数据
        contractService.isUserContract(SessionContextUtil.verifyCspLogin(), contractId);
        return contractService.cspContractMealDetail(contractId);
    }
}

