package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.vo.activity.*;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-视频-封面资源表
 * </p>
 *
 * @author bydud
 * @since 2024/5/14 14:22
 */
@FeignClient(value = "rebot-service", contextId = "MsManageActivityContentApi", url = "${robot:}")
public interface MsManageActivityContentApi {

    @ApiOperation("新增活动方案")
    @PostMapping("msManage/activityContent/add")
    void addContent(@RequestBody @Valid ContentAdd contentAdd);

    @ApiOperation("更新活动方案")
    @PostMapping("msManage/activityContent/update")
    void updateContent(@RequestBody @Valid ContentUpdate contentUpdate);

    @ApiOperation("分页查询活动方案")
    @PostMapping("msManage/activityContent/page")
    PageResult<MsManageActivityContentResp> page(@RequestBody ContentPageQuery pageQuery);

    @GetMapping("msManage/activityContent/get/{msActivityContentId}")
    MsManageActivityContentResp queryContentById(@PathVariable("msActivityContentId")Long msActivityContentId);

    /**
     * 查询作品真是价格
     * @param mssId 作品id
     * @param pType 价格展示：
     *                   当前价格（original）: 最新的活动价格，和首页价格一样
     *                   活动价格（discount）: 如果通过活动页面进入作品，价格展示为该活动下该作品的价格
     * @param msActivityContentId  查出当前价格：-1 ，查询指定活动价格：活动id
     * @return 价格和活动方案
     */
    @PostMapping("msManage/activityContent/getPrice")
    MsPrice getPrice(@RequestParam("mssId") Long mssId, @RequestParam(name ="msActivityContentId" ,required = false)Long msActivityContentId, @RequestParam(name = "pType",required = false) String pType);
//    MsPrice getPrice(@RequestBody @Valid MsPriceParam price);

    @ApiOperation("分页查询活动方案")
    @PostMapping("/activityContent/List")
    List<MsManageActivityContentResp> list();
}
