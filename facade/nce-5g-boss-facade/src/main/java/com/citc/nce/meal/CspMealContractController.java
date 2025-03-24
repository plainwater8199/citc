package com.citc.nce.meal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.annotation.BossAuth;
import com.citc.nce.auth.meal.CspMealContractApi;
import com.citc.nce.auth.meal.vo.contract.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * bydud
 * 2024/1/23
 **/
@BossAuth("/chatbot-view/contract/index")
@RestController
@AllArgsConstructor
@Slf4j
@Api(tags = "csp-meal-套餐合同")
public class CspMealContractController {
    private final CspMealContractApi contractApi;

    @PostMapping("/csp/mealContract/add")
    @ApiOperation("新增合同")
    public void addContract(@RequestBody @Validated CspMealContractAddVo contractInfo) {
        contractApi.addContract(contractInfo);
    }

    @PostMapping("/csp/mealContract/edit")
    @ApiOperation("编辑合同")
    public void editContract(@RequestBody @Validated CspMealContractEditVo contractInfo) {
        contractApi.editContract(contractInfo);
    }

    @BossAuth("/chatbot-view/index")
    @GetMapping("/csp/mealContract/getById/{id}")
    @ApiOperation("合同详情")
    public CspMealContractInfo getById(@PathVariable("id") Long id) {
        return contractApi.getById(id);
    }

    @GetMapping("/csp/mealContract/del/{id}")
    @ApiOperation("删除合同")
    public void del(@PathVariable("id") Long id) {
        contractApi.del(id);
    }

    @PostMapping("/csp/mealContract/page")
    @ApiOperation("分页查询")
    public PageResult<CspMealContractPageInfo> page(@RequestBody CspMealContractPageQuery query) {
        Page<CspMealContractPageInfo> page = contractApi.page(query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @BossAuth("/chatbot-view/index")
    @GetMapping("/csp/mealContract/manageHome")
    @ApiOperation("管理首页合同展示信息")
    public Map<String, Integer> manageHome() {
        return contractApi.manageHome();
    }

}
