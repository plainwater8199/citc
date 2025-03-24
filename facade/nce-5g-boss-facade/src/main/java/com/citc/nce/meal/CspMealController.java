package com.citc.nce.meal;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.annotation.BossAuth;
import com.citc.nce.auth.meal.CspMealApi;
import com.citc.nce.auth.meal.vo.meal.CspMealAddVo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageQuery;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * bydud
 * 2024/1/22
 **/
@BossAuth("/chatbot-view/package/index")
@RestController
@AllArgsConstructor
@Slf4j
@Api(tags = "csp-meal-套餐")
public class CspMealController {
    private CspMealApi mealApi;

    /**
     * 新增套餐
     */
    @PostMapping("/csp/meal/add")
    public void addMeal(@RequestBody @Validated CspMealAddVo addVo) {
        mealApi.addMeal(addVo);
    }


    /**
     * 上下架
     *
     * @param id     套餐id
     * @param status 状态
     */
    @PostMapping("/csp/meal/updateState")
    public void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        mealApi.updateState(id, status);
    }

    /**
     * 删除套餐
     *
     * @param id 套餐id
     */
    @PostMapping("/csp/meal/delete")
    public void deleteMeal(@RequestParam("id") Long id) {
        mealApi.deleteMeal(id);
    }


    @BossAuth("/chatbot-view/contract/index")
    @PostMapping("/csp/meal/page")
    public PageResult<CspMealPageInfo> page(@RequestBody @Validated CspMealPageQuery query) {
        Page<CspMealPageInfo> page = mealApi.page(query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 查询条件导出
     *
     * @param query 条件
     */
    @PostMapping("/csp/meal/exportList")
    @UnWrapResponse
    public void exportList(HttpServletResponse response, @RequestBody @Validated CspMealPageQuery query) {
        List<CspMealPageInfo> list = mealApi.exportList(query);
        try (OutputStream os = response.getOutputStream()) {
            // 设置响应头信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 设置防止中文名乱码
            String fileName = "套餐导出.xlsx";
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
            // 使用EasyExcel进行导出
            ExcelWriter excelWriter = EasyExcel.write(os, CspMealPageInfo.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.write(list, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            log.error("meal套餐导出失败", e);
            throw new BizException("导出失败，请联系管理员");
        }
    }
}
