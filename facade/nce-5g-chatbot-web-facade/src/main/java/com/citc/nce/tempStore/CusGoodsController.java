package com.citc.nce.tempStore;

import com.citc.nce.common.core.pojo.EsPageResult;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsWorksLibraryStatus;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.security.annotation.IsCustomer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @since 11:32
 */
@RestController
@Api(tags = "客户侧-商品api")
@AllArgsConstructor
@RequestMapping("/tempStore/goods")
public class CusGoodsController {
    MsSummaryApi msGoodsApi;

    //客户端
    @IsCustomer
    @PostMapping("/customer/homePage")
    @ApiOperation(value = "客户-素材广场首页查询")
    public EsPageResult<MsEsPageResult> pageQueryEs(@RequestBody MsEsPageQuery msPage) {
        return msGoodsApi.pageQueryEs(msPage);
    }

    @IsCustomer
    @PostMapping("/customer/getTagList")
    @ApiOperation(value = "客户-素材广场首页--标签查询")
    public List<String> getTagList() {
        return msGoodsApi.getTagList();
    }

    private static MsPage getMsHomePageParamsBuild(MsCspPage cspPage) {
        MsPage msPage = new MsPage(cspPage);
        msPage.setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_ON);
        msPage.setAuditStatusList(MsAuditStatus.ACTIVE_ON);
        if (Objects.nonNull(cspPage.getMsType())) {
            msPage.setMsTypes(Collections.singletonList(cspPage.getMsType()));
        }
        if (StringUtils.hasLength(cspPage.getName())) {
            msPage.setName(cspPage.getName());
        }
        return msPage;
    }


    @IsCustomer
    @GetMapping("/customer/homePage/getPublishVo")
    @ApiOperation("客户-素材广场首页查询--发布者列表")
    List<MsPublisherVo> msSummaryPagePublish(@RequestBody MsCspPage cspPage) {
        MsPage msPage = getMsHomePageParamsBuild(cspPage);
        return msGoodsApi.getPublishVo(msPage);
    }

    @IsCustomer
    @GetMapping("/customer/getSummaryDetail/{mssId}")
    @ApiOperation("客户-查看作品详情")
    public MsSummaryDetailVO getSummaryDetail(@PathVariable("mssId") Long mssId,@RequestParam(name = "msActivityContentId" ,required = false) Long msActivityContentId,@RequestParam(name =  "pType", required = false) String pType) {
        return msGoodsApi.getSummaryDetail(mssId,msActivityContentId,pType);
    }
}
