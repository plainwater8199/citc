package com.citc.nce.materialSquare;

import com.citc.nce.materialSquare.vo.suggest.SuggestAdd;
import com.citc.nce.materialSquare.vo.suggest.SuggestChangeNum;
import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import com.citc.nce.materialSquare.vo.suggest.req.SuggestOrderReq;
import com.citc.nce.materialSquare.vo.suggest.resp.SuggestListResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bydud
 * @since 2024/5/15 10:47
 */
@FeignClient(value = "rebot-service", contextId = "MsManageSuggestApi", url = "${robot:}")
public interface MsManageSuggestApi {

    @ApiOperation("新增推荐")
    @PostMapping("/msManageSuggest/suggestAdd")
    void putSuggest(@RequestBody @Validated SuggestAdd suggestAdd);

    @ApiOperation("删除推荐")
    @PostMapping("/msManageSuggest/remove/{msSuggestId}")
    void deleteSuggest(@PathVariable("msSuggestId") Long msSuggestId);

    @ApiOperation("更新首页推荐排序")
    @PostMapping("/msManageSuggest/orderNum")
    void orderNum(@RequestBody @Valid SuggestOrderReq req);

    @ApiOperation("获取推荐列表")
    @GetMapping("/msManageSuggest/listOrderNum")
    SuggestListResp listOrderNum();

    @ApiOperation("设置置顶")
    @PostMapping("/msManageSuggest/setTop")
    void setTop(@RequestBody @Valid SuggestAdd suggestAdd);

    @ApiOperation("取消置顶")
    @PostMapping("/msManageSuggest/cleanTop")
    void cleanTop();

}
