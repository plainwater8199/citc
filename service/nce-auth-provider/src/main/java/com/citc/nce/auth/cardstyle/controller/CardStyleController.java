package com.citc.nce.auth.cardstyle.controller;



import com.citc.nce.auth.cardstyle.CardStyleApi;
import com.citc.nce.auth.cardstyle.service.CardStyleService;
import com.citc.nce.auth.cardstyle.vo.*;
import com.citc.nce.auth.contactlist.vo.ImportContactListResp;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 卡片样式
 */
@RestController()
@Slf4j
public class CardStyleController implements CardStyleApi {
    @Resource
    private CardStyleService cardStyleService;

    /**
     * 卡片样式列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "卡片样式列表分页获取", notes = "卡片样式列表分页获取")
    @PostMapping("/card/style/pageList")
    @Override
    public PageResultResp getCardStyles(@RequestBody @Valid PageParam pageParam) {
        return cardStyleService.getCardStyles(pageParam);
    }

    /**
     * 新增卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增卡片样式", notes = "新增卡片样式")
    @PostMapping("/card/style/save")
    @Override
    public Long saveCardStyle(@RequestBody  @Valid CardStyleReq CardStyleReq) {
        return cardStyleService.saveCardStyle(CardStyleReq);
    }

    /**
     * 修改卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改卡片样式", notes = "修改卡片样式")
    @PostMapping("/card/style/edit")
    @Override
    public int updateCardStyle(@RequestBody  @Valid CardStyleEditReq CardStyleEditReq) {
        return cardStyleService.updateCardStyle(CardStyleEditReq);
    }

    /**
     * 删除卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除卡片样式", notes = "删除卡片样式")
    @PostMapping("/card/style/delete")
    @Override
    public int delCardStyleById(@RequestBody  @Valid CardStyleOneReq cardStyleOneReq) {
        return cardStyleService.delCardStyleById(cardStyleOneReq);
    }

    /**
     * 根据id获取卡片样式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据id获取卡片样式", notes = "根据id获取卡片样式")
    @PostMapping("/card/style/getOne")
    @Override
    public CardStyleResp getCardStyleById(@RequestBody  @Valid CardStyleOneReq cardStyleOneReq) {
        return cardStyleService.getCardStyleById(cardStyleOneReq);
    }

    @Override
    public CardStyleResp getCardStyleByIdInner(CardStyleOneReq cardStyleOneReq) {
        return cardStyleService.getCardStyleByIdInner(cardStyleOneReq);
    }

    /**
     * 获取卡片样式树
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/getTreeList")
    @Override
    public List<CardStyleTreeResp> getTreeList() {
        return cardStyleService.getTreeList();
    }



}
