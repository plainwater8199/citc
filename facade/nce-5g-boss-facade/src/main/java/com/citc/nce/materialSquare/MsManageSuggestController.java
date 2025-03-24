package com.citc.nce.materialSquare;

/**
 * @author bydud
 * @since 2024/5/24 11:32
 */

import com.citc.nce.materialSquare.vo.suggest.SuggestAdd;
import com.citc.nce.materialSquare.vo.suggest.SuggestChangeNum;
import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import com.citc.nce.materialSquare.vo.suggest.req.SuggestOrderReq;
import com.citc.nce.materialSquare.vo.suggest.resp.SuggestListResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "MsManageSuggestController", tags = "素材广场-首页推荐管理")
public class MsManageSuggestController {
    private final MsManageSuggestApi msManageSuggestApi;

    @ApiOperation("新增推荐")
    @PostMapping("/msManageSuggest/suggestAdd")
    void putSuggest(@RequestBody @Validated SuggestAdd suggestAdd) {
        msManageSuggestApi.putSuggest(suggestAdd);
    }

    @ApiOperation("删除推荐")
    @PostMapping("/msManageSuggest/remove/{msSuggestId}")
    void deleteSuggest(@PathVariable("msSuggestId") Long msSuggestId) {
        msManageSuggestApi.deleteSuggest(msSuggestId);
    }

    @ApiOperation("修改顺序")
    @PostMapping("/msManageSuggest/orderNum")
    void orderNum(@RequestBody @Valid SuggestOrderReq req) {
        msManageSuggestApi.orderNum(req);
    }

    @ApiOperation("获取推荐列表")
    @GetMapping("/msManageSuggest/listOrderNum")
    SuggestListResp listOrderNum() {
        return msManageSuggestApi.listOrderNum();
    }

    @ApiOperation("设置置顶")
    @PostMapping("/msManageSuggest/setTop")
    void setTop(@RequestBody @Valid SuggestAdd suggestAdd) {
        msManageSuggestApi.setTop(suggestAdd);
    }

    @ApiOperation("重置置顶")
    @PostMapping("/msManageSuggest/cleanTop")
    void cleanTop() {
        msManageSuggestApi.cleanTop();
    }
}
