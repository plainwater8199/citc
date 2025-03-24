package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.vo.activity.PutAndRemove;
import com.citc.nce.materialSquare.vo.activity.LiPageQuery;
import com.citc.nce.materialSquare.vo.activity.LiPageResult;
import com.citc.nce.materialSquare.vo.activity.SummaryInfoForActivityContent;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsWorksLibraryStatus;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsActivityOptionalPage;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsPage;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsPageResult;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsPublisherVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @since 2024/5/14 16:17
 */
@RestController
@AllArgsConstructor
@Api(value = "MsManageActivityContentController", tags = "活动方案-作品列表")
public class MsManageActivityLiController {
    private final MsManageActivityLiApi msManageActivityLiApi;
    private final MsSummaryApi msSummaryApi;


    @GetMapping("/msManage/activityLi/msSummaryPagePublish")
    @ApiOperation("作品选择---分页查询--发布者列表")
    List<MsPublisherVo> msSummaryPagePublish(@RequestBody MsActivityOptionalPage pageQuery) {
        MsPage msPage = msSummaryPageParamBuild(pageQuery);
        return msSummaryApi.getPublishVo(msPage);
    }


    @GetMapping("/msManage/activityLi/msSummaryPage")
    @ApiOperation("作品选择---分页查询")
    PageResult<MsPageResult> msSummaryPage(@RequestBody MsActivityOptionalPage pageQuery) {
        MsPage msPage = msSummaryPageParamBuild(pageQuery);
        msPage.setIsCsp(0);
        return msSummaryApi.pageQuery(msPage);
    }

    @PostMapping("/msManage/activityLi/liPage")
    @ApiOperation("分页查询")
    PageResult<LiPageResult> liPage(@RequestBody LiPageQuery pageQuery) {
        return msManageActivityLiApi.liPage(pageQuery);
    }

    @PostMapping("/msManage/activityLi/putAndRemove")
    @ApiOperation("新增/编辑活动")
    void putAndRemove(@RequestBody @Valid PutAndRemove putAndRemove) {
        msManageActivityLiApi.putAndRemove(putAndRemove);
    }

    @PostMapping("/msManage/activityLi/delete/{msActivityLiId}")
    @ApiOperation("根据关联id删除指定作品与活动方案的关联关系")
    void delete(@PathVariable("msActivityLiId") Long msActivityLiId) {
        msManageActivityLiApi.delete(msActivityLiId);
    }

    @PostMapping("/msManage/activityLi/getSummaryInfoForActivity/{msActivityContentId}")
    @ApiOperation("根据关联id获取所有作品信息")
    public List<SummaryInfoForActivityContent> getSummaryInfoForActivity(@PathVariable("msActivityContentId") Long msActivityContentId) {
        return msManageActivityLiApi.getSummaryInfoForActivity(msActivityContentId);
    }

    /**
     * 转换参数
     */
    private static MsPage msSummaryPageParamBuild(MsActivityOptionalPage pageQuery) {
        MsPage msPage = new MsPage(pageQuery);
        msPage.setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_ON);
        if (Objects.nonNull(pageQuery.getMsTypes())) {
            msPage.setMsTypes(Collections.singletonList(pageQuery.getMsTypes()));
        }
        msPage.setName(pageQuery.getName());
        msPage.setMsSource(pageQuery.getMsSource());
        msPage.setCreator(pageQuery.getCreator());
        return msPage;
    }
}
