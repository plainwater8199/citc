package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import com.citc.nce.materialSquare.vo.activity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @author bydud
 * @since 2024/5/14 16:10
 */
@FeignClient(value = "rebot-service", contextId = "MsManageActivityLiApi", url = "${robot:}")
public interface MsManageActivityLiApi {

    @PostMapping("/msManage/activityLi/liPage")
    PageResult<LiPageResult> liPage(@RequestBody LiPageQuery pageQuery);

    @PostMapping("/msManage/activityLi/putAndRemove")
    void putAndRemove(@RequestBody PutAndRemove putAndRemove);

    @PostMapping("/msManage/activityLi/delete/{msActivityLiId}")
    void delete(@PathVariable("msActivityLiId") Long msActivityLiId);

    @PostMapping("/msManage/activityLi/getActivityContentByMssId/{mssId}")
    MsManageActivityContent getActivityContentByMssId(@PathVariable("mssId") Long mssId);

    @PostMapping("/msManage/activityLi/getSummaryInfoForActivity/{msActivityContentId}")
    List<SummaryInfoForActivityContent> getSummaryInfoForActivity(@PathVariable("msActivityContentId") Long msActivityContentId);

    @PostMapping("/activityLi/List")
    List<LiDetailResult> listByLiId(@RequestBody MssForLiReq req);

    @PostMapping("/msManage/activityLi/deleteSummaryForActivity")
    void deleteSummaryForActivity(@RequestParam("mssIds") List<Long> mssIds);

    @PostMapping("/msManage/activityLi/getActivityContentById/{msActivityContentId}")
    MsManageActivityContent getActivityContentById(@PathVariable("msActivityContentId") Long msActivityContentId);
}
