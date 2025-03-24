package com.citc.nce.materialSquare;


import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.materialSquare.vo.activity.LiDetailResult;
import com.citc.nce.materialSquare.vo.activity.MssForLiReq;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsWorksLibraryStatus;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryViewRecord;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.TemplateListQueryReq;
import com.citc.nce.security.annotation.RepeatSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 19:35
 */
@RestController
@Api(value = "MsSummaryController", tags = "素材广场-csp")
public class MsSummaryController {


    @Resource
    private MsSummaryApi msSummaryApi;
    @Resource
    private MsManageActivityLiApi msManageActivityLiApi;

    @PostMapping("/ms/summary/saveDraft")
    @ApiOperation(value = "csp-保存作品", notes = "csp-保存作品")
    @RepeatSubmit
    @XssCleanIgnore
    public void saveDraft(@RequestBody @Valid MsSummarySaveDart dart) {
        msSummaryApi.saveDraft(dart);
    }

    @PostMapping("/ms/summary/updateDraft")
    @ApiOperation(value = "csp-更新作品", notes = "csp-更新作品")
    @RepeatSubmit
    @XssCleanIgnore
    public void updateDraft(@RequestBody @Valid MsSummaryUpdateDart dart) {
        msSummaryApi.updateDraft(dart);
    }

    @PostMapping("/ms/summary/publishByMssId/{mssId}")
    @ApiOperation(value = "csp-上架作品", notes = "csp-上架作品")
    @RepeatSubmit
    public void publishByMssId(@PathVariable("mssId") Long mssId) {
        msSummaryApi.publishByMssId(mssId);
    }

    @PostMapping("/ms/summary/activeOffCsp/{mssId}")
    @ApiOperation(value = "csp-下架作品", notes = "csp-下架作品")
    @RepeatSubmit
    public void activeOffCsp(@PathVariable("mssId") Long mssId) {
        msSummaryApi.activeOffByCsp(mssId);
    }

    @PostMapping("/ms/summary/delete/{mssId}")
    @ApiOperation(value = "csp-删除作品", notes = "csp-删除作品")
    @RepeatSubmit
    public void delete(@PathVariable("mssId") Long mssId) {
        msSummaryApi.delete(mssId);
    }

    @PostMapping("/ms/summary/getDetail/{mssId}")
    @ApiOperation(value = "csp-查看作品详情", notes = "csp-查看作品详情")
    @RepeatSubmit
    @XssCleanIgnore
    public MsSummaryDetailVO getDetail(@PathVariable("mssId") Long mssId) {
        return msSummaryApi.getDetail(mssId);
    }


    @PostMapping("/ms/summary/cspPage")
    @ApiOperation(value = "csp分页查询")
    public PageResult<MsPageResult> cspPage(@RequestBody MsCspPage cspPage) {
        MsPage msPage = new MsPage(cspPage);
        msPage.setCspId(Collections.singletonList(SessionContextUtil.verifyCspLogin()));
        msPage.setName(cspPage.getName());
        msPage.setAuditStatusList(cspPage.getAuditStatus());
        msPage.setTemplateStatusList(cspPage.getTemplateStatus());
        msPage.setIsCsp(1);

        if (Objects.nonNull(cspPage.getMsType())) {
            //csp传递作品类型
            if (!MsSummaryApi.cspMsTypeList.contains(cspPage.getMsType())) {
                throw new BizException("当前用户不支持查询当前作品类型");
            }
            msPage.setMsTypes(Collections.singletonList(cspPage.getMsType()));
        } else {
            //csp未传递作品类型
            msPage.setMsTypes(MsSummaryApi.cspMsTypeList);
        }
        return msSummaryApi.pageQuery(msPage);
    }

    @PostMapping("/ms/summary/like/addLike/{msId}")
    @ApiOperation("点赞作品")
    void addLike(@PathVariable("msId") Long msId) {
        msSummaryApi.addLike(msId);
    }

    @PostMapping("/ms/summary/like/cancelLike/{msId}")
    @ApiOperation("取消点赞")
    void cancelLike(@PathVariable("msId") Long msId) {
        msSummaryApi.cancelLike(msId);
    }

    @PostMapping("/ms/summary/view/addView/{msId}")
    @ApiOperation("预览数加1")
    void addView(@PathVariable("msId") Long msId) {
        msSummaryApi.addView(msId);
    }

    @GetMapping("/ms/summary/like/list/{msId}")
    @ApiOperation("点赞列表")
    List<MsSummaryLikeRecord> likeListByMsId(@PathVariable("msId") Long msId) {
        return msSummaryApi.likeListByMsId(msId);
    }

    @GetMapping("/ms/summary/view/list/{msId}")
    @ApiOperation("浏览列表")
    List<MsSummaryViewRecord> viewListByMsId(@PathVariable("msId") Long msId) {
        return msSummaryApi.viewListByMsId(msId);
    }

    @PostMapping("/ms/summary/template/list")
    @ApiOperation(value = "机器人、5G消息模版列表查询")
    public List<TemplateInfo> templateListQuery(@RequestBody @Valid  TemplateListQueryReq req){
        return msSummaryApi.templateListQuery(req);
    }

    @PostMapping("/ms/summary/h5/template/list")
    @ApiOperation(value = "H5列表查询")
    public List<H5TemplateInfo> h5TemplateListQuery(@RequestBody H5TemplateListQueryReq req){
        return msSummaryApi.h5TemplateListQuery(req);
    }


    @PostMapping("h5/activityLi/List")
    @ApiOperation("分页查询")
    List<LiDetailResult> listByLiId(@RequestBody MssForLiReq req) {
        return msManageActivityLiApi.listByLiId(req);
    }
}
