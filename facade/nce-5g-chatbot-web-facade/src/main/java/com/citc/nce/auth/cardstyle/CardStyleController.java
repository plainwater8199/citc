package com.citc.nce.auth.cardstyle;

import com.citc.nce.auth.cardstyle.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 卡片样式
 */
@RestController
@RequestMapping("/card/style")
@Slf4j
@Api(value = "auth", tags = "卡片样式")
public class CardStyleController {
    @Resource
    private CardStyleApi cardStyleApi;

    /**
     * 卡片样式列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "卡片样式列表分页获取", notes = "卡片样式列表分页获取")
    @PostMapping("/pageList")
    public PageResultResp getCardStyles(@RequestBody @Valid PageParam pageParam) {
        return cardStyleApi.getCardStyles(pageParam);
    }

    /**
     * 新增卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增卡片样式", notes = "新增卡片样式")
    @PostMapping("/save")
    public Long saveCardStyle(@RequestBody  @Valid CardStyleReq CardStyleReq) {
        return cardStyleApi.saveCardStyle(CardStyleReq);
    }

    /**
     * 修改卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改卡片样式", notes = "修改卡片样式")
    @PostMapping("/edit")
    public int updateCardStyle(@RequestBody  @Valid CardStyleEditReq CardStyleEditReq) {
        return cardStyleApi.updateCardStyle(CardStyleEditReq);
    }

    /**
     * 删除卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除卡片样式", notes = "删除卡片样式")
    @PostMapping("/delete")
    public int delCardStyleById(@RequestBody  @Valid CardStyleOneReq cardStyleOneReq) {
        return cardStyleApi.delCardStyleById(cardStyleOneReq);
    }

    /**
     * 根据id获取卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据id获取卡片样式", notes = "根据id获取卡片样式")
    @PostMapping("/getOne")
    public CardStyleResp getCardStyleById(@RequestBody  @Valid CardStyleOneReq cardStyleOneReq) {
        return cardStyleApi.getCardStyleById(cardStyleOneReq);
    }

    /**
     * 获取卡片样式树
     *
     * @param
     * @return
     */
    @GetMapping("/getTreeList")
    public List<CardStyleTreeResp> getTreeList() {
        return cardStyleApi.getTreeList();
    }

}
