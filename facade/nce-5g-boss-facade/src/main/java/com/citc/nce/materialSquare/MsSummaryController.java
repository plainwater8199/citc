package com.citc.nce.materialSquare;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.api.materialSquare.ManageGoodsLogApi;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperate;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperateBatch;
import com.citc.nce.robot.api.tempStore.domain.GoodsManageLog;
import com.citc.nce.security.annotation.RepeatSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @since 10:53
 */
@Api(tags = "素材广场-发布管理")
@RestController
@Slf4j
@AllArgsConstructor
public class MsSummaryController {

    private MsSummaryApi msGoodsApi;
    private ManageGoodsLogApi logApi;

    @PostMapping("/ms/manage/wl/pagePublish")
    @ApiOperation("manage-发布人列表查询")
    List<MsPublisherVo> pagePublish(@RequestBody MsWlPage wlPage) {
        MsPage msPage = msWlPageParamBuild(wlPage);
        return msGoodsApi.getPublishVo(msPage);
    }


    @PostMapping("/ms/manage/wl/page")
    @ApiOperation("manage-作品库列表")
    PageResult<MsPageResult> wlPage(@RequestBody MsWlPage wlPage) {
        MsPage msPage = msWlPageParamBuild(wlPage);
        return msGoodsApi.pageQuery(msPage);
    }


    @PostMapping("/ms/manage/audit/auditPage")
    @ApiOperation("manage-上架审核列表")
    PageResult<MsPageResult> auditPage(@RequestBody MsAuditPage auditPage) {
        MsPage msPage = new MsPage(auditPage);
        msPage.setCspQuery(auditPage.getCspQuery());
        msPage.setName(auditPage.getName());
        msPage.setIsCsp(0);
        if (Objects.nonNull(auditPage.getMsType())) {
            msPage.setMsTypes(Collections.singletonList(auditPage.getMsType()));
        } else {
            msPage.setMsTypes(MsSummaryApi.cspMsTypeList);
        }
        if (Objects.nonNull(auditPage.getAuditStatus()) && !MsAuditStatus.ALL.equals(auditPage.getAuditStatus())) {
            msPage.setAuditStatusList(auditPage.getAuditStatus());
        }
        return msGoodsApi.pageQuery(msPage);
    }


    @PostMapping("/ms/manage/audit/activeOff")
    @ApiOperation("manage-下架商品")
    void activeOff(@RequestBody @Valid GoodsOperateBatch mssIds) {
        msGoodsApi.activeOff(mssIds);
    }

    @PostMapping("/ms/manage/audit/activeOn")
    @ApiOperation("manage-管理员上架商品")
    void activeMangeOn(@RequestBody @Valid GoodsOperateBatch mssIds) {
        msGoodsApi.activeMangeOn(mssIds);
    }

    @PostMapping("/ms/manage/audit/auditPass")
    @ApiOperation("manage-审核通过")
    void auditPass(@RequestBody @Valid GoodsOperate req) {
        msGoodsApi.auditPass(req);
    }

    @PostMapping("/ms/manage/audit/auditFail")
    @ApiOperation("manage-审核不通过")
    void auditFail(@RequestBody @Valid GoodsOperate req) {
        if (!StringUtils.hasLength(req.getRemark())) {
            throw new BizException("备注不能为空");
        }
        msGoodsApi.auditFail(req);
    }


    @PostMapping("/tempStore/goods/manage/delete")
    @ApiOperation("manage-删除商品")
    void deleteList(@RequestBody @Valid GoodsOperateBatch batch) {
        msGoodsApi.deleteList(batch);
    }


    @GetMapping("/tempStore/goods/manage/oplog/{goodsId}")
    @ApiOperation("日志")
    public List<GoodsManageLog> listByGoodsId(@PathVariable("goodsId") Long goodsId) {
        return logApi.listByGoodsId(goodsId);
    }


    @GetMapping("/ms/manage/getDetailManage/{mssId}")
    @ApiOperation("manage-查看作品详情")
    @XssCleanIgnore
    public MsSummaryDetailVO getDetailManage(@PathVariable("mssId") Long mssId) {
        return msGoodsApi.getDetailManage(mssId);
    }

    @PostMapping("/ms/manage/saveDraft")
    @ApiOperation(value = "manage-保存草稿", notes = "保存草稿")
    @RepeatSubmit
    @XssCleanIgnore
    public void saveDraftManage(@RequestBody @Valid MsSummarySaveDart dart) {
        msGoodsApi.saveDraftManage(dart);
    }

    @PostMapping("/ms/manage/updateDraft")
    @ApiOperation(value = "manage-更新草稿", notes = "更新草稿")
    @RepeatSubmit
    @XssCleanIgnore
    public void updateDraftManage(@RequestBody @Valid MsSummaryUpdateDart dart) {
        msGoodsApi.updateDraftManage(dart);
    }

    /**
     * 作品库构建参数
     */
    private static MsPage msWlPageParamBuild(MsWlPage wlPage) {
        MsPage msPage = new MsPage(wlPage);
        msPage.setName(wlPage.getName());
        msPage.setMsSource(wlPage.getMsSource());
//        msPage.setCspId(wlPage.getCspId());
        msPage.setIsCsp(0);
        msPage.setCreator(wlPage.getCreator());
        if (Objects.nonNull(wlPage.getMsType())) {
            msPage.setMsTypes(Collections.singletonList(wlPage.getMsType()));
        }
        if (Objects.nonNull(wlPage.getStatus()) && !MsAuditStatus.ALL.equals(wlPage.getStatus())) {
            msPage.setAuditStatus(Collections.singletonList(wlPage.getStatus()));
        }else{
            msPage.setAuditStatus(Arrays.asList(MsAuditStatus.ACTIVE_ON, MsAuditStatus.ACTIVE_OFF));//作品库只查询已上架的作品
        }
        return msPage;
    }
}
