package com.citc.nce.meal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.meal.CspMealContractApi;
import com.citc.nce.auth.meal.vo.MealCspHome;
import com.citc.nce.auth.meal.vo.contract.CspContractMeal;
import com.citc.nce.auth.meal.vo.contract.CspMealContractPageInfo;
import com.citc.nce.auth.meal.vo.contract.CspMealContractPageQuery;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * bydud
 * 2024/1/25
 **/
@RestController
@RequestMapping("/csp/mealContract")
@Api(tags = "csp-meal-contract-合同管理")
@AllArgsConstructor
public class CspMealContractController {
    private final CspMealContractApi cspMealContractApi;

    @GetMapping("/csp/mealContract/cspHome")
    @ApiOperation("csp个人首页信息")
    public MealCspHome cspHome() {
        SessionContextUtil.verifyCspLogin();
        return cspMealContractApi.cspHome();
    }

    @PostMapping("/csp/mealContract/cspPage")
    @ApiOperation("csp-meal合同-分页")
    public PageResult<CspMealContractPageInfo> cspPage(@RequestBody CspMealContractPageQuery query) {
        String cspId = SessionContextUtil.verifyCspLogin();
        query.setCspId(cspId);
        Page<CspMealContractPageInfo> page = cspMealContractApi.page(query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @PostMapping("/csp/mealContract/cspContractMealDetail")
    @ApiOperation("csp-meal合同-详情(展开列表)")
    public List<CspContractMeal> cspContractMealDetail(@RequestParam("contractId") Long contractId) {
        return cspMealContractApi.cspContractMealDetail(contractId);
    }
}
