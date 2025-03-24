package com.citc.nce.auth.cardstyle;

import com.citc.nce.auth.cardstyle.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:53
 * @Version: 1.0
 * @Description:卡片样式
 */

@FeignClient(value = "auth-service", contextId = "CardStyleApi", url = "${auth:}")
public interface CardStyleApi {
    /**
     * 卡片样式列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/pageList")
    PageResultResp getCardStyles(@RequestBody @Valid PageParam pageParam);

    /**
     * 新增卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/save")
    Long saveCardStyle(@RequestBody @Valid CardStyleReq cardStyleReq);

    /**
     * 修改卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/edit")
    int updateCardStyle(@RequestBody @Valid CardStyleEditReq cardStyleEditReq);

    /**
     * 删除卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/delete")
    int delCardStyleById(@RequestBody @Valid CardStyleOneReq cardStyleOneReq);

    /**
     * 获取单个卡片样式
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/getOne")
    CardStyleResp getCardStyleById(@RequestBody @Valid CardStyleOneReq cardStyleOneReq);

    @PostMapping("/card/style/getCardStyleByIdInner")
    CardStyleResp getCardStyleByIdInner(@RequestBody @Valid CardStyleOneReq cardStyleOneReq);

    /**
     * 获取卡片样式树
     *
     * @param
     * @return
     */
    @PostMapping("/card/style/getTreeList")
    List<CardStyleTreeResp> getTreeList();

}
