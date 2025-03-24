package com.citc.nce.auth.meal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.vo.MealCspHome;
import com.citc.nce.auth.meal.vo.contract.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * bydud
 * 2024/1/22
 **/
@Api(tags = "csp-meal合同api")
@FeignClient(value = "auth-service", contextId = "cspMealContractApi", url = "${auth:}")
public interface CspMealContractApi {
    @PostMapping("/csp/mealContract/add")
    public void addContract(@RequestBody @Validated CspMealContractAddVo contractInfo);

    @PostMapping("/csp/mealContract/edit")
    public void editContract(@RequestBody @Validated CspMealContractEditVo contractInfo);

    @GetMapping("/csp/mealContract/getById/{id}")
    public CspMealContractInfo getById(@PathVariable("id") Long id);

    @GetMapping("/csp/mealContract/del/{id}")
    public void del(@PathVariable("id") Long id);

    @PostMapping("/csp/mealContract/page")
    public Page<CspMealContractPageInfo> page(@RequestBody CspMealContractPageQuery query);

    /**
     * 查询首页临期合同  异常账号数量
     * temporary  abnormalCount
     */
    @GetMapping("/csp/mealContract/manageHome")
    public Map<String, Integer> manageHome();

    /**
     * 根据cspId查询当前csp激活套餐可用量
     *
     * @param cspId cspid
     */
    @GetMapping("/csp/mealContract/countCurrentMealNumMunByCspId")
    Long countCurrentMealNumMunByCspId(@RequestParam("cspId") String cspId);


    //==================csp 页面接口

    @GetMapping("/csp/mealContract/cspHome")
     MealCspHome cspHome();

    @PostMapping("/csp/mealContract/cspContractMealDetail")
    @ApiOperation("csp-meal合同-详情")
     List<CspContractMeal> cspContractMealDetail(@RequestParam("contractId") Long contractId);
}
